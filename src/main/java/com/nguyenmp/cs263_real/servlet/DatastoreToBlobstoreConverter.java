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
    private static final String ENCODING = "UTF-8";
    private static final long TIMEOUT = 11 * 60 * 1000; // 11 minutes in milliseconds

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String computer = req.getParameter("computer");
        long date_in_day = Long.parseLong(req.getParameter("date"));

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

        GcsFileMetadata metadata = gcsService.getMetadata(filename);
        if (metadata == null) {
            GcsOutputChannel outputChannel = gcsService.createOrReplace(filename, GcsFileOptions.getDefaultInstance());
            OutputStream outputStream = Channels.newOutputStream(outputChannel);
            byte[] data = json.getBytes(ENCODING);
            outputStream.write(data);
            outputStream.close();
        }

        if (usages.length > 0) {
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(withUrl("/delete_datastore_date_computer")
                    .param("computer", computer)
                    .param("date", Long.toString(date_in_day)));
        }

        resp.getWriter().println(objectName);
    }

    public static Map<String, LinkedList<Interval>> convertToIntervalsByUser(UsageModel[] usages) {

        HashMap<String, LinkedList<Interval>> users = new HashMap<>();
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
    public static Map<String, LinkedList<Interval>> convertToIntervalsByComputer(UsageModel[] usages) {

        HashMap<String, LinkedList<Interval>> users = new HashMap<>();
        for (UsageModel usage : usages) {
            LinkedList<Interval> intervals = users.get(usage.hostname);
            if (intervals == null) {
                intervals = new LinkedList<>();
                users.put(usage.hostname, intervals);
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

    public static class Interval implements Serializable {
        public long start, end;
    }

    public static GcsFilename getFilename(String computer, long date) {
        String objectName = String.format("%s/%s", computer, new SimpleDateFormat("yyyy/MM/dd").format(new Date(date)));
        String bucketName = "mark_nguyen_foo";
        return new GcsFilename(bucketName, objectName);
    }

    public static BlobKey getBlobkey(String computer, long date) {
        GcsFilename filename = getFilename(computer, date);
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        return blobstoreService.createGsBlobKey(String.format("/gs/%s/%s", filename.getBucketName(), filename.getObjectName()));
    }
}
