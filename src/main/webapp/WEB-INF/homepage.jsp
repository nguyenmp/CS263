<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="java.net.URLEncoder" %>
<%
    String[] usernames = (String[]) request.getAttribute("usernames");
    String[] hostnames = (String[]) request.getAttribute("hostnames");
%>

<!doctype html>
<html>
<head>
    <title>App Engine Demo</title>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <link rel="stylesheet" href="stylesheets/main.css">
</head>
<body>
<div class="two-column" >
    <h1><a href="${pageContext.request.contextPath}/users">Users:</a></h1>
    <ul>
        <% for (String username : usernames) { %>
            <li>
                <a href="${pageContext.request.contextPath}/user?name=<%= URLEncoder.encode(username) %>" class="username">
                    <%= StringEscapeUtils.escapeHtml(username) %>
                </a>
            </li>
        <% } %>
    </ul>
</div>
<div class="two-column" >
    <h1><a href="${pageContext.request.contextPath}/computers">Computers:</a></h1>
    <ul>
        <% for (String hostname : hostnames) { %>
        <li>
            <a href="${pageContext.request.contextPath}/computer?hostname=<%= URLEncoder.encode(hostname) %>" class="hostname">
                <%= StringEscapeUtils.escapeHtml(hostname) %>
            </a>
        </li>
        <% } %>
    </ul>
</div>
</body>
</html>