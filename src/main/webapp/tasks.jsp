<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html><body>
<p>Hello, this is a testing servlet.</p>
<%
    Query allTasks = new Query("TaskData");
    DatastoreService service = DatastoreServiceFactory.getDatastoreService();
    List<Entity> entities = service.prepare(allTasks).asList(FetchOptions.Builder.withLimit(100000));

    System.out.println(entities.size());

    for (Entity entity : entities) {
        Long epoche = (Long) entity.getProperty("date");
        Date date = new Date(epoche);
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss z YYYY");
        System.out.println(format.format(date));
%>
<p><%= entity.getKind() %>("<%= StringEscapeUtils.escapeHtml(entity.getKey().getName()) %>") <%= format.format(date) %> <%= StringEscapeUtils.escapeHtml((String) entity.getProperty("value")) %></p>
<%
    }
%>
</body>
</html>
