<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String[] computers = (String[]) request.getAttribute("computers");
%>

<html>
<head>
    <title>Users Who Have Logged Into CSIL</title>
</head>
<body>
<ul>
    <% for (String computer : computers) { %>
        <li>
            <div>
                <a href="${pageContext.request.contextPath}/computer?hostname=<%= URLEncoder.encode(computer) %>" class="hostname">
                    <%= StringEscapeUtils.escapeHtml(computer) %>
                </a>
            </div>
        </li>
    <% } %>
</ul>
</body>
</html>
