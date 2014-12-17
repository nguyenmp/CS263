package com.nguyenmp.cs263_real.servlet;

import com.google.gson.Gson;
import com.nguyenmp.cs263_real.dao.UsageDao;
import com.nguyenmp.cs263_real.model.UsageModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserServlet extends HttpServlet {
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        if (name == null || name.equals("")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"name\" cannot be null or empty string.");
        } else {
            UsageModel[] byUser = UsageDao.getByUserCached(name);
            // This will contain which computers the user is currently on and have used in the past
            request.setAttribute("username", name);
            request.setAttribute("byUser", byUser);
            request.getRequestDispatcher("/user/index.jsp").forward(request, response);
        }
    }
}
