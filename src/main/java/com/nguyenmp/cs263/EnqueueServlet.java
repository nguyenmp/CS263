package com.nguyenmp.cs263;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import java.io.IOException;
import java.net.URLEncoder;

@Path("/enqueue")
public class EnqueueServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String value = request.getParameter("value");

        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(withUrl("/worker").param("name", name).param("value", value));


        response.sendRedirect("/tqueue.jsp?key_name=" + URLEncoder.encode(name));
    }
}
