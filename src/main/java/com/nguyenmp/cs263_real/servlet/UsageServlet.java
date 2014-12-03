package com.nguyenmp.cs263_real.servlet;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

public class UsageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
        // TODO: Implement getting general overall usage of CSIL
        // This will probably just show usage levels for computers
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO: Rate limit on our end
        String usageJson = req.getParameter("data");

        // Push to background servlet to decrease front end hours (json deserialization takes forever)
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(withUrl("/background_usage").param("data", usageJson));
    }
}
