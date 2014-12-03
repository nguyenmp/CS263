package com.nguyenmp.cs263_real.servlet;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.nguyenmp.cs263_real.dao.UsageDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

public class PersistTask extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] computers = UsageDao.getComputers();
        Queue queue = QueueFactory.getDefaultQueue();

        for (String computer : computers) {

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar.setTime(new Date(1415456188127l));

            Calendar startOfThisDay = (Calendar) calendar.clone();
            startOfThisDay.setTime(new Date());
            startOfThisDay.set(Calendar.HOUR_OF_DAY, 0);
            startOfThisDay.set(Calendar.MINUTE, 0);
            startOfThisDay.set(Calendar.SECOND, 0);
            startOfThisDay.set(Calendar.MILLISECOND, 0);

            while (calendar.before(startOfThisDay)) {
                queue.add(withUrl("/datastore_to_blobstore")
                        .param("computer", computer)
                        .param("date", Long.toString(calendar.getTimeInMillis())));

                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
    }
}