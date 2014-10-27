<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String[] users = (String[]) request.getAttribute("users");
%>

<html>
<head>
    <title>Users Who Have Logged Into CSIL</title>
</head>
<body>
<ul>
    <% for (String user : users) { %>
        <li>
            <div>
                <a href="${pageContext.request.contextPath}/user?name=<%= URLEncoder.encode(user) %>" class="username">
                    <%= StringEscapeUtils.escapeHtml(user) %>
                </a>
            </div>
        </li>
    <% } %>
</ul>
</body>
</html>
