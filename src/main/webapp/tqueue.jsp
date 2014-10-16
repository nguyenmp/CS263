<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.nguyenmp.cs263.TaskData" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<html>
<head>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
</head>

<body>

<%
    String keyName = request.getParameter("key_name");
    TaskData data = TaskData.fromDataStore(DatastoreServiceFactory.getDatastoreService(), keyName);
    if (data != null) {
%>
<p>The value in keyname <%= StringEscapeUtils.escapeHtml(keyName) %> is <%= StringEscapeUtils.escapeHtml(data.value) %>!</p>
<% } else { %>
<p>The value in keyname <%= StringEscapeUtils.escapeHtml(keyName) %> is -2!</p>
<% } %>
</body>
</html>
