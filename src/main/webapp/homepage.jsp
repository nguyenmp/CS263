<%@ page import="com.nguyenmp.cs263_real.dao.UsageDao" %>
<%@ page import="com.nguyenmp.cs263_real.model.UsageModel" %>
<%@ page import="com.nguyenmp.cs263_real.servlet.Homepage" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%
    String[] usernames = UsageDao.getUsersCached();
    String[] hostnames = UsageDao.getComputersCached();

    UsageModel[] recents = UsageDao.getRecentCached();
    Map<String, Set<String>> recentByComputer = Homepage.countUsagesByComputer(recents);
    Homepage.Count[] counts = Homepage.count(recentByComputer);
    Arrays.sort(counts);
    HashSet<String> empties = new HashSet<String>(Arrays.<String>asList(usernames));
    empties.removeAll(recentByComputer.keySet());
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
    <ul id="users">
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
    <ul id="computers">
        <% for (String hostname : hostnames) { %>
        <li>
            <a href="${pageContext.request.contextPath}/computer?hostname=<%= URLEncoder.encode(hostname) %>" class="hostname">
                <%= StringEscapeUtils.escapeHtml(hostname) %> (<%= recentByComputer.containsKey(hostname) ? recentByComputer.get(hostname).size() : 0 %>)
            </a>
        </li>
        <% } %>
    </ul>
</div>
</body>
</html>