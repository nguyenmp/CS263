package com.nguyenmp.cs263_real.servlet;

import com.nguyenmp.cs263_real.dao.UsageDao;
import com.nguyenmp.cs263_real.model.UsageModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DatastorePruner extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String computer = req.getParameter("computer");
        long date = Long.parseLong(req.getParameter("date"));
        UsageModel[] usages = UsageDao.getByComputerInDay(computer, date);
        UsageDao.delete(usages);
    }
}
