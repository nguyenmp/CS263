package com.nguyenmp.cs263_real.servlet;

import com.nguyenmp.cs263_real.model.UserModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** /users */
public class UsersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO: Do database query here
        UserModel[] users = new UserModel[0];

        // Render all users
        req.setAttribute("users", users);
        req.getRequestDispatcher("/users/index.jsp").forward(req, resp);
    }
}
