<%@ page import="com.google.gson.Gson" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="java.util.Map" %>
<%@ page import="static com.nguyenmp.cs263_real.servlet.DatastoreToBlobstoreConverter.Interval" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    Map<String, LinkedList<Interval>> usages = (Map<String, LinkedList<Interval>>) request.getAttribute("usages");
%>

<html>
<head>
    <title>Users Who Have Logged Into CSIL</title>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
    <link href="http://visjs.org/dist/vis.css" rel="stylesheet" type="text/css" />
    <script src="http://visjs.org/dist/vis.js"></script>
</head>
<body>

<div id="mytimeline"></div>

<script type="text/javascript">
    var json = '<%=new Gson().toJson(usages)%>';
    var data = JSON.parse(json);

    // DOM element where the Timeline will be attached
    var container = document.getElementById('mytimeline');

    // Create a DataSet with data (enables two way data binding)
    var dataArr = [];
    var id = 1;
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
            dataArr.push({id: id++, content: key, start: start, end: end})
        }
    }


    var data = new vis.DataSet(dataArr);

    // Configuration for the Timeline
    var options = {};

    // Create a Timeline
    var timeline = new vis.Timeline(container, data, options);
    timeline.on('select', function (properties) {
        console.log(properties);
    });
</script>
</body>
</html>
