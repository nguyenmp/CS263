<%@ page import="com.google.appengine.api.datastore.*" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html><body>
<p>Hello, this is a testing servlet.</p>
<%
    Query allTasks = new Query("TaskData");
    DatastoreService service = DatastoreServiceFactory.getDatastoreService();
    Iterable<Entity> entities = service.prepare(allTasks).asIterable(FetchOptions.Builder.withLimit(10));

    for (Entity entity : entities) {
%>
<p><% entity.getKind(); %>("<% entity.getKey().getName(); %>") Tue Oct 14 17:07:14 UTC 2014 <% entity.getProperty("value"); %></p>
<%
    }
%>
</body>
</html>
