package com.nguyenmp.cs263_real.servlet;

import com.nguyenmp.cs263_real.dao.UsageDao;
import com.nguyenmp.cs263_real.model.UsageModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ComputerServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hostname = req.getParameter("hostname");
        UsageModel[] usage = UsageDao.getByComputer(hostname);

        // Render all users
        req.setAttribute("usages", usage);
        req.getRequestDispatcher("/computer/index.jsp").forward(req, resp);
    }
}
