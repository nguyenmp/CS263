<%@ page import="com.google.gson.Gson" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="java.util.Map" %>
<%@ page import="static com.nguyenmp.cs263_real.servlet.DatastoreToBlobstoreConverter.Interval" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String hostname = (String) request.getAttribute("hostname");
    Map<String, LinkedList<Interval>> usages = (Map<String, LinkedList<Interval>>) request.getAttribute("usages");
%>

<html>
<head>
    <title>Users Who Have Logged Into CSIL</title>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
    <link href="http://visjs.org/dist/vis.css" rel="stylesheet" type="text/css" />
    <script src="http://visjs.org/dist/vis.js"></script>
    <link rel="stylesheet" href="/stylesheets/main.css">
</head>
<body>

<h1 id="hostname"><%= StringEscapeUtils.escapeHtml(hostname) %></h1>
<div id="mytimeline"></div>

<script type="text/javascript">
    // DOM element where the Timeline will be attached
    var hostname = "<%= hostname %>";
    var container = document.getElementById('mytimeline');
    var dataArr = [];
    var id = 1;
    var dataset = new vis.DataSet(dataArr);
    var oldest_time = new Date().valueOf();

    // Configuration for the Timeline
    var options = {};

    // Create a Timeline
    var timeline = new vis.Timeline(container, dataset, options);
    timeline.setWindow(new Date().valueOf() - (28 * 60 * 60 * 1000), new Date().valueOf() + 2 * 60 * 60 * 1000);
    timeline.on('rangechange', function(properties) {
        if (oldest_time > properties.start.valueOf()) {
            while (oldest_time > properties.start.valueOf()) {
                oldest_time -= 24 * 60 * 60 * 1000;

                $.getJSON("http://astral-casing-728.appspot.com/blobstore_server?hostname=" + hostname + "&date=" + oldest_time, loadedDate);
            }
        }
    });
    timeline.on('select', function(properties) {
        var name = dataArr[properties.items[0] - 1].content;
        window.open("http://astral-casing-728.appspot.com/user?name=" + name, "_self")
    });

    var json = '<%=new Gson().toJson(usages)%>';
    var data = JSON.parse(json);
    loadedDate(data);
    function loadedDate(data) {

        // Create a DataSet with data (enables two way data binding)
        for (var key in data) {
            var intervals = data[key];
            console.log(intervals);
            for (var i = 0; i < intervals.length; i++) {
                var interval = intervals[i];
                console.log(interval);

                var start = new Date(0);
                start.setUTCMilliseconds(interval.start);
                var end = new Date(0);
                end.setUTCMilliseconds(interval.end);
                if (interval.start === interval.end) {
                    dataArr.push({id: id++, content: key, start: start});
                } else {
                    dataArr.push({id: id++, content: key, start: start, end: end});
                }
            }
        }

        dataset.update(dataArr);
    }
</script>
</body>
</html>
