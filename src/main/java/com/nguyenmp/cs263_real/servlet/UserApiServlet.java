package com.nguyenmp.cs263_real.servlet;

import com.google.gson.Gson;
import com.nguyenmp.cs263_real.dao.UsageDao;
import com.nguyenmp.cs263_real.model.UsageModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserApiServlet extends HttpServlet {

    /** creates a new user with the given name defaulting to the current time, remote, and hostname = test */
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("name");
        if (username == null || username.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"username\" was not specified or was empty.");
        } else {
            UsageModel model = new UsageModel();
            model.timestamp = System.currentTimeMillis();
            model.hostname = "test.cs.ucsb.edu";
            model.isRemote = true;
            model.username = username;
            UsageModel[] result = UsageDao.put(model);
            resp.getWriter().print(new Gson().toJson(result[0]));
        }
    }

    /** gets the usage data for the given users */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("name");
        if (username == null || username.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"name\" is null or empty.");
        } else {
            response.getWriter().print(new Gson().toJson(UsageDao.getByUserCached(username)));
        }
    }
}
