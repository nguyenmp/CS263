package com.nguyenmp.cs263_real.servlet;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.tools.cloudstorage.*;
import com.google.gson.Gson;
import com.nguyenmp.cs263_real.dao.UsageDao;
import com.nguyenmp.cs263_real.model.UsageModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.channels.Channels;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

/**
 * This class converts the "check-in" style data to an interval set.
 * This hopefully reduces entries for computers that have a lot of
 * activity and reduce the overall size of the datastore.  We will
 * prune the datastore to only store 1 day's worth of data.
 */
public class DatastoreToBlobstoreConverter extends HttpServlet {
    /** The string encoding to use to convert the json string into a binary byte stream */
    private static final String ENCODING = "UTF-8";

    /** The constant timeout where if a user hasn't checked in
     *  for this amount of time, the user probably logged out. */
    private static final long TIMEOUT = 11 * 60 * 1000; // 11 minutes in milliseconds

    /**
     * Takes in a computer and a date and persists that computer's data and the date to the blobstore as a
     * json mapping of login intervals by users.  We then delete this information from the datastore.
     */
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String computer = req.getParameter("computer");
        String dateString = req.getParameter("date");

        if (computer == null || computer.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"computer\" was not specified");
        } else if (dateString == null || dateString.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"date\" was not specified");
        } else {
            try {
                long date_in_day = Long.parseLong(dateString);

                // Convert our usages into an interval set
                UsageModel[] usages = UsageDao.getByComputerInDay(computer, date_in_day);
                long[] interval = UsageDao.getTimeIntervalDay(date_in_day);
                Map<String, LinkedList<Interval>> intervals = convertToIntervalsByUser(usages);
                String json = new Gson().toJson(intervals);

                // Persist that interval set into the blobstore
                GcsService gcsService = GcsServiceFactory.createGcsService();
                String objectName = String.format("%s/%s", computer, new SimpleDateFormat("yyyy/MM/dd").format(new Date(interval[0])));
                String bucketName = "mark_nguyen_foo";
                GcsFilename filename = new GcsFilename(bucketName, objectName);

                // If the metadata doesn't exist,
                GcsFileMetadata metadata = gcsService.getMetadata(filename);
                if (metadata == null) {
                    GcsOutputChannel outputChannel = gcsService.createOrReplace(filename, GcsFileOptions.getDefaultInstance());
                    OutputStream outputStream = Channels.newOutputStream(outputChannel);
                    byte[] data = json.getBytes(ENCODING);
                    outputStream.write(data);
                    outputStream.close();
                }

                // Prune the database of the data we persisted
                if (usages.length > 0) {
                    Queue queue = QueueFactory.getDefaultQueue();
                    queue.add(withUrl("/delete_datastore_date_computer")
                            .param("computer", computer)
                            .param("date", Long.toString(date_in_day)));
                }
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"date\" was malformed.");
            }
        }
    }

    /**
     * <p>Converts usage data points into sets of intervals by username.</p>
     * <p>Note that this doesn't discriminate against remote or local check-ins.</p>
     * @param usages the array of data points that represent check-ins for users on a computer
     * @return a compacted mapping of usernames to list of time intervals of when the user is logged-in.
     */
    @Nonnull public static Map<String, LinkedList<Interval>> convertToIntervalsByUser(@Nullable UsageModel[] usages) {
        HashMap<String, LinkedList<Interval>> users = new HashMap<>();
        if (usages == null) return users;

        for (UsageModel usage : usages) {
            LinkedList<Interval> intervals = users.get(usage.username);
            if (intervals == null) {
                intervals = new LinkedList<>();
                users.put(usage.username, intervals);
            }

            Interval tail = intervals.size() == 0 ? null : intervals.getLast();
            if (tail == null || tail.end + TIMEOUT < usage.timestamp) {
                // Either the last interval didn't exist or it timed out.
                // So now we start a new interval
                tail = new Interval();
                tail.start = usage.timestamp;
                tail.end = usage.timestamp;
                intervals.add(tail);
            } else {
                // Extend the old interval
                tail.end = usage.timestamp;
            }
        }

        return users;
    }

    /**
     * Generates a map of computers to time intervals
     * @param usages the usage data for a user
     * @return a mapping of hostnames to time intervals logged by the given user
     */
    @Nonnull public static Map<String, LinkedList<Interval>> convertToIntervalsByComputer(@Nonnull UsageModel[] usages) {
        // Create the resulting mapping
        HashMap<String, LinkedList<Interval>> mapping = new HashMap<>();

        // For each usage entry, extend the interval for the computer
        for (UsageModel usage : usages) {

            // If this computer hasn't been added to the mapping yet,
            // initialize it with an empty list of intervals
            LinkedList<Interval> intervals = mapping.get(usage.hostname);
            if (intervals == null) {
                intervals = new LinkedList<>();
                mapping.put(usage.hostname, intervals);
            }

            // Get the last element of the list (or null if the list is empty)
            Interval tail = intervals.size() == 0 ? null : intervals.getLast();

            if (tail == null || tail.end + TIMEOUT < usage.timestamp) {
                // If there are no intervals yet, or the last interval is too old,
                // abandon the last interval and create a new one, appending it to the list
                // Either the last interval didn't exist or it timed out.
                // So now we start a new interval
                tail = new Interval();
                tail.start = usage.timestamp;
                tail.end = usage.timestamp;
                intervals.add(tail);
            } else {
                // Otherwise, extend the old interval
                tail.end = usage.timestamp;
            }
        }

        return mapping;
    }

    /** A simple POJO that represents an interval of time */
    public static class Interval implements Serializable {

        /** the inclusive start of this interval (time since epoch in milliseconds) */
        public long start;

        /** the inclusive end of this interval (time since epoch in milliseconds) */
        public long end;
    }

    /**
     * Converts a computer name and a date into a Google Cloud Service filename
     * for looking up in the blob store (historical data). Can be used to find metadata about the file.
     * @param computer the computer's data to look up (hostname) e.g., "csil.cs.ucsb.edu"
     * @param date the time in milliseconds since epoch for the date to look up (any time in the date's interval is fine)
     * @return the blobkey to the persisted json result for the historical data
     */
    @Nonnull public static GcsFilename getFilename(@Nonnull String computer, long date) {
        String objectName = String.format("%s/%s", computer, new SimpleDateFormat("yyyy/MM/dd").format(new Date(date)));
        String bucketName = "mark_nguyen_foo";
        return new GcsFilename(bucketName, objectName);
    }

    /**
     * Converts a computer name and a date into a Blobkey for looking up in the blob store (historical data) for serving
     * @param computer the computer's data to look up (hostname) e.g., "csil.cs.ucsb.edu"
     * @param date the time in milliseconds since epoch for the date to look up (any time in the date's interval is fine)
     * @return the blobkey to the persisted json result for the historical data
     */
    @Nonnull public static BlobKey getBlobkey(@Nonnull String computer, long date) {
        GcsFilename filename = getFilename(computer, date);
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

        // The /gs/bucketname/filename is exactly what is stated in the documentation
        return blobstoreService.createGsBlobKey(String.format("/gs/%s/%s", filename.getBucketName(), filename.getObjectName()));
    }
}
