package com.nguyenmp.cs263_real.servlet;

import com.nguyenmp.cs263_real.dao.UsageDao;
import com.nguyenmp.cs263_real.model.UsageModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: Implement getting specific user's usage
        String name = request.getParameter("name");
        UsageModel[] byUser = UsageDao.getByUserCached(name);
        // This will contain which computers the user is currently on and have used in the past
        request.setAttribute("byUser", byUser);
        request.getRequestDispatcher("/user/index.jsp").forward(request, response);
    }
}
