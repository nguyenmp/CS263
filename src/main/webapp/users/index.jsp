<%@ page import="com.nguyenmp.cs263_real.model.UserModel" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    UserModel[] users = (UserModel[]) request.getAttribute("users");
%>

<html>
<head>
    <title>Users Who Have Logged Into CSIL</title>
</head>
<body>
<ul>
    <% for (UserModel user : users) { %>
        <li>
            <div>
                <span class="username"><%= user.username %></span>
            </div>
        </li>
    <% } %>
</ul>
</body>
</html>
