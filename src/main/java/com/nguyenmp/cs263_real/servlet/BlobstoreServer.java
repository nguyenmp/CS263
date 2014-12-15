package com.nguyenmp.cs263_real.servlet;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * <p>Returns the persisted JSON API data.  After 24 hours, usage data
 * can no longer change, so they are persisted as strings to the blob store.</p>
 *
 * <p>This means there's less data to process for indexing and enumerating.</p>
 */
public class BlobstoreServer extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hostname = req.getParameter("hostname");
        String dateString = req.getParameter("date");

        if (hostname == null || hostname.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"hostname\" was not provided or is empty.");
        } else if (dateString == null || dateString.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"date\" was not provided or is empty.");
        } else {
            try {
                Long date = Long.valueOf(dateString);

                // Lookup and serve the json blob persisted from the API for the given hostname and date
                BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
                BlobKey blobKey = DatastoreToBlobstoreConverter.getBlobkey(hostname, date);
                blobstoreService.serve(blobKey, resp);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"date\" is not a number representing milliseconds since epoch.");
            }
        }
    }
}
