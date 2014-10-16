<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="com.nguyenmp.cs263.TaskData" %>
<html>
<head>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
</head>

<body>

<% String keyName = request.getParameter("key_name"); %>
<%
    // TODO: Handle when keyName is null or empty
    Key key = KeyFactory.createKey("TaskData", keyName);
    DatastoreService db = DatastoreServiceFactory.getDatastoreService();
    TaskData data = null;
    try {
        data = TaskData.fromEntity(db.get(key));
    } catch (EntityNotFoundException e) {
        // Do nothing.  The resulting entity will be set to null
    }

    if (data != null) {
%>
<p>The value in keyname <%= StringEscapeUtils.escapeHtml(keyName) %> is <%= StringEscapeUtils.escapeHtml(data.value) %>!</p>
<% } else { %>
<p>The value in keyname <%= StringEscapeUtils.escapeHtml(keyName) %> is -2!</p>
<% } %>
</body>
</html>
