package com.nguyenmp.cs263_real.servlet;

import com.google.gson.Gson;
import com.nguyenmp.cs263_real.dao.UsageDao;
import com.nguyenmp.cs263_real.model.UsageModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BackgroundUsageServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO: Rate limit on our end
        String usageJson = req.getParameter("data");
        UsageModel[] usageParams = new Gson().fromJson(usageJson, UsageModel[].class);
        UsageDao.put(usageParams);
    }
}
