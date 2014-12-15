package com.nguyenmp.cs263_real.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nguyenmp.cs263_real.dao.UsageDao;
import com.nguyenmp.cs263_real.model.UsageModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 */
public class BackgroundUsageServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String usageJson = req.getParameter("data");
        if (usageJson == null || usageJson.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"data\" was not specified or was empty.");
        } else {
            try {
                UsageModel[] usageParams = new Gson().fromJson(usageJson, UsageModel[].class);
                UsageDao.put(usageParams);
            } catch (JsonSyntaxException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "The JSON of usages provided was malformed in syntax.");
            }
        }
    }
}
