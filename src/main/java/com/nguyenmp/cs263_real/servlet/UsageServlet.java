package com.nguyenmp.cs263_real.servlet;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nguyenmp.cs263_real.dao.UsageDao;
import com.nguyenmp.cs263_real.model.UsageModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

/**
 * This servlet is used to handle the processing of new check-in data.
 * Any computer that wants to say users are logged in posts their data here.
 */
public class UsageServlet extends HttpServlet {

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String usageJson = req.getParameter("data");
        if (usageJson == null || usageJson.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"data\" was not specified or was empty.");
        } else {
            // Push to background servlet to decrease front end hours (json deserialization takes forever)
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(withUrl("/background_usage").param("data", usageJson));
        }
    }
}
