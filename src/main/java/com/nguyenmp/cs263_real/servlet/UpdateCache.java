package com.nguyenmp.cs263_real.servlet;

import com.nguyenmp.cs263_real.dao.UsageDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UpdateCache extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UsageDao.cacheComputer("csil.cs.ucsb.edu");
    }
}
