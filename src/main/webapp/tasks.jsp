<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="com.google.appengine.api.memcache.Expiration" %>
<%@ page import="com.nguyenmp.cs263.TaskData" %>
<%@ page import="java.util.logging.Level" %>
<%@ page import="com.google.appengine.api.memcache.ErrorHandlers" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheServiceFactory" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheService" %>
<%@ page import="java.util.ArrayList" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html><body>
<p>Hello, this is a testing servlet.</p>
<%
    // Check the memcache
    MemcacheService memCache = MemcacheServiceFactory.getMemcacheService(TaskData.TYPE_NAME);
    memCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
    List<TaskData> data = (List<TaskData>) memCache.get(TaskData.TYPE_NAME);

    // If memcache does not have data...
    if (data == null) {
        // Get data from the datastore
        Query allTasks = new Query("TaskData");
        DatastoreService service = DatastoreServiceFactory.getDatastoreService();
        List<Entity> entities = service.prepare(allTasks).asList(FetchOptions.Builder.withLimit(100000));
        data = new ArrayList<TaskData>(entities.size());
        for (Entity entity : entities) {
            data.add(TaskData.fromEntity(entity));
        }

        // Persist the data to the memcache
        memCache.put(TaskData.TYPE_NAME, data, Expiration.byDeltaSeconds(30));
    }

    for (TaskData task : data) {
        Date date = new Date(task.date);
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss z YYYY");
%>
<p><%= TaskData.TYPE_NAME %>("<%= StringEscapeUtils.escapeHtml(task.name) %>") <%= format.format(date) %> <%= StringEscapeUtils.escapeHtml(task.value) %></p>
<%
    }
%>
</body>
</html>
