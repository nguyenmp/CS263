package com.nguyenmp.cs263;

import com.google.appengine.api.datastore.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

public class TasksServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        Writer writer = response.getWriter();
        writer.write("<p>Hello, this is a testing servlet.</p>\n");

        Query allTasks = new Query("Task");
        DatastoreService service = DatastoreServiceFactory.getDatastoreService();
        Iterable<Entity> entities = service.prepare(allTasks).asIterable(FetchOptions.Builder.withLimit(10));
        for (Entity entity : entities) {
            writer.write("<p>Key: " + entity.getKey().getKind() + "</p>");
            writer.write("<p>Name: " + entity.getKey().getName() + "</p>");
            writer.write("<p>Value: " + entity.getProperty("value") + "</p>");
        }
        writer.flush();
    }
}
