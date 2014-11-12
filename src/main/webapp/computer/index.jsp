<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.nguyenmp.cs263_real.model.UsageModel" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    UsageModel[] usages = (UsageModel[]) request.getAttribute("usages");
%>

<html>
<head>
    <title>Users Who Have Logged Into CSIL</title>
</head>
<body>
<ul>
    <% for (UsageModel usage : usages) { %>
        <li>
            <div>
                <a href="${pageContext.request.contextPath}/user?name=<%= URLEncoder.encode(usage.username) %>" class="username">
                    <%= StringEscapeUtils.escapeHtml(usage.username) %>
                </a>
            </div>
        </li>
    <% } %>
</ul>
</body>
</html>
