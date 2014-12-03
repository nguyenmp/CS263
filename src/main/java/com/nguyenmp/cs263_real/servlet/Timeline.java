package com.nguyenmp.cs263_real.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Timeline extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hostname = req.getParameter("hostname");
        Long date = Long.valueOf(req.getParameter("date"));

        // Render all users
        req.setAttribute("date", date);
        req.setAttribute("hostname", hostname);
        req.getRequestDispatcher("/computer/timeline.jsp").forward(req, resp);
    }
}
