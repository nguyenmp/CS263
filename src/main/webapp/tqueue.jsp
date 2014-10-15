<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
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
    Entity entity = null;
    try {
        entity = db.get(key);
    } catch (EntityNotFoundException e) {
        // Do nothing.  The resulting entity will be set to null
    }

    if (entity != null) {
%>
<p>The value in keyname <%= StringEscapeUtils.escapeHtml(keyName) %> is <%= StringEscapeUtils.escapeHtml((String) entity.getProperty("value")) %>!</p>
<% } else { %>
<p>The value in keyname <%= StringEscapeUtils.escapeHtml(keyName) %> is -2!</p>
<% } %>
</body>
</html>
