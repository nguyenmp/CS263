package com.nguyenmp.cs263_real.servlet;

import com.google.gson.Gson;
import com.nguyenmp.cs263_real.dao.UsageDao;
import com.nguyenmp.cs263_real.model.UsageModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RecentUsersApiServlet extends HttpServlet {

    /** Returns the recent users as an array of strings of those who are logged in in the last 15 minutes */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UsageModel[] recent = UsageDao.getRecentCached();
        Set<String> users = new HashSet<>();
        for (UsageModel usage : recent) {
            users.add(usage.username);
        }
        response.getWriter().print(new Gson().toJson(users.toArray(new String[users.size()])));
    }
}
