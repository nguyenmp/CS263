package com.nguyenmp.cs263_real.servlet;

import com.nguyenmp.cs263_real.dao.UsageDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Forwards to the homepage.
 */
public class Homepage extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] usernames = UsageDao.getUsersCached();
        String[] hostnames = UsageDao.getComputersCached();

        request.setAttribute("usernames", usernames);
        request.setAttribute("hostnames", hostnames);
        request.getRequestDispatcher("/homepage.jsp").forward(request, response);
    }
}
