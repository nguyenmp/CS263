package com.nguyenmp.cs263_real.servlet;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.nguyenmp.cs263_real.dao.UsageDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

public class PersistTask extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Populate backend queue for each computer
        UsageDao.prune();

        String[] computers = UsageDao.getComputers();

        for (String computer : computers) {
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(withUrl("/datastore_to_blobstore").param("computer", computer));
        }
    }
}
