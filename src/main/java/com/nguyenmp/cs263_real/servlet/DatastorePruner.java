package com.nguyenmp.cs263_real.servlet;

import com.nguyenmp.cs263_real.dao.UsageDao;
import com.nguyenmp.cs263_real.model.UsageModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Cleans up the datastore by deleting old data.  Keeping the datastore small makes queries run faster.
 */
public class DatastorePruner extends HttpServlet {

    /**
     * Given a computer and a date, this method will delete all data entries for that date and computer.
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String computer = req.getParameter("computer");
        if (computer == null || computer.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramter \"computer\" not provided.");
            return;
        }

        String dateString = req.getParameter("date");
        if (dateString == null || dateString.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"date\" not provided.");
            return;
        }

        long date;
        try {
            date = Long.parseLong(dateString);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter \"date\" is not a decimal long representing milliseconds since epoch.");
            return;
        }

        // Get all data entries for that day/computer and delete them
        UsageModel[] usages = UsageDao.getByComputerInDay(computer, date);
        UsageDao.delete(usages);
    }
}
