package com.nguyenmp.cs263_real.servlet;

import com.nguyenmp.cs263_real.dao.UsageDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This servlet takes in computers and caches them in memory so that responses will be faster.
 * This can be seen as an invalidation method or a method of keeping the memcache fresh.
 */
public class UpdateCache extends HttpServlet {

    /**
     * Update the memcache for the given computers
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // At the moment, we only update csil's cache because that's the query that takes the longest
        // Updating the cache causes database hits on computers that aren't hot.
        // The cost of hitting the database for those computers that aren't hot is minimal
        // because the amount of users logged into anything but csil is small (less data)
        // This means that we don't need to cache cold computers.
        // However, if we get a cache miss on csil, that means the user is
        // waiting up to 30 seconds for us to do the processing and query
        // Hence, we are aggressive with csil (no wait time).
        // (Lets waste database reads on this for faster response times).
        UsageDao.cacheComputer("csil.cs.ucsb.edu");
    }
}
