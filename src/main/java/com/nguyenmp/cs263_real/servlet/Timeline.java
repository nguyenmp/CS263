package com.nguyenmp.cs263_real.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Timeline extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hostname = req.getParameter("hostname");
        if (hostname == null || hostname.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"hostname\" missing.");
            return;
        }

        String dateString = req.getParameter("date");
        if (dateString == null || dateString.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"date\" missing.");
            return;
        }

        Long date;
        try {
            date = Long.valueOf(dateString);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"date\" is not a long decimal representing the date in milliseconds since epoch.");
            return;
        }

        // Render all users
        req.setAttribute("date", date);
        req.setAttribute("hostname", hostname);
        req.getRequestDispatcher("/computer/timeline.jsp").forward(req, resp);
    }
}
