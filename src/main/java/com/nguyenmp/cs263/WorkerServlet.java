package com.nguyenmp.cs263;

import com.google.appengine.api.datastore.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Path("/worker")
public class WorkerServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: Fill this stub
        String name = request.getParameter("name");
        String value = request.getParameter("value");

        DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();
        TaskData.putValues(dataStore, name, value);
    }
}
