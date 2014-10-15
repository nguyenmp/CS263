package com.nguyenmp.cs263;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ReadData extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/tasks.jsp").forward(request, response);
//        response.setStatus(HttpServletResponse.SC_OK);
//        Writer writer = response.getWriter();
//        writer.write("<p>Hello, this is a testing servlet.</p>\n");
//
//        Query allTasks = new Query("TaskData");
//        DatastoreService service = DatastoreServiceFactory.getDatastoreService();
//        Iterable<Entity> entities = service.prepare(allTasks).asIterable(FetchOptions.Builder.withLimit(10));
//        for (Entity entity : entities) {
//            writer.write("<p>Key: " + entity.getKey().getKind() + "</p>");
//            writer.write("<p>Name: " + entity.getKey().getName() + "</p>");
//            writer.write("<p>Value: " + entity.getProperty("value") + "</p>");
//        }
//        writer.flush();
    }
}
