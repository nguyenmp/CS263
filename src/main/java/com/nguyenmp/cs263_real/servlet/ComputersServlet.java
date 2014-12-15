package com.nguyenmp.cs263_real.servlet;

import com.nguyenmp.cs263_real.dao.UsageDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ComputersServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] computers = UsageDao.getComputersCached();

        // Render all users
        req.setAttribute("computers", computers);
        req.getRequestDispatcher("/computers/index.jsp").forward(req, resp);
    }
}
