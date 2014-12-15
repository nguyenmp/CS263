package com.nguyenmp.cs263_real.servlet;

import com.nguyenmp.cs263_real.dao.UsageDao;
import com.nguyenmp.cs263_real.model.UsageModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

import static com.nguyenmp.cs263_real.servlet.DatastoreToBlobstoreConverter.Interval;

public class ComputerServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hostname = req.getParameter("hostname");
        if (hostname == null || hostname.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"hostname\" was not provided or is empty.");
        } else {
            // Get the mapping of users on the computer.
            Map<String, LinkedList<Interval>> map = UsageDao.getByComputerCached(hostname);

            // Render all users
            req.setAttribute("hostname", hostname);
            req.setAttribute("usages", map);
            req.getRequestDispatcher("/computer/index.jsp").forward(req, resp);
        }
    }
}
