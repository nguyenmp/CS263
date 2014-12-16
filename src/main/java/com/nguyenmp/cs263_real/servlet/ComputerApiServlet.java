package com.nguyenmp.cs263_real.servlet;

import com.google.gson.Gson;
import com.nguyenmp.cs263_real.dao.UsageDao;
import com.nguyenmp.cs263_real.model.UsageModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ComputerApiServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hostname = req.getParameter("hostname");
        if (hostname == null || hostname.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"hostname\" was not specified or was empty.");
        } else {
            UsageModel model = new UsageModel();
            model.timestamp = System.currentTimeMillis();
            model.hostname = hostname;
            model.isRemote = true;
            model.username = "test";
            UsageModel[] result = UsageDao.put(model);
            resp.getWriter().print(new Gson().toJson(result[0]));
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String hostname = request.getParameter("hostname");
        if (hostname == null || hostname.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"hostname\" is null or empty.");
        } else {
            response.getWriter().print(new Gson().toJson(UsageDao.getByComputerCached(hostname)));
        }
    }
}
