package com.nguyenmp.cs263_real.servlet;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.nguyenmp.cs263_real.dao.UsageDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

public class PersistTask extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] computers = UsageDao.getComputers();
        Queue queue = QueueFactory.getDefaultQueue();

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        for (String computer : computers) {
            queue.add(withUrl("/datastore_to_blobstore")
                    .param("computer", computer)
                    .param("date", Long.toString(calendar.getTimeInMillis())));
        }
    }
}