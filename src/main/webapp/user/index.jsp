<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.nguyenmp.cs263_real.model.UsageModel" %>
<%@ page import="com.nguyenmp.cs263_real.servlet.DatastoreToBlobstoreConverter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    UsageModel[] usages = (UsageModel[]) request.getAttribute("byUser");
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
    // DOM element where the Timeline will be attached
    var container = document.getElementById('mytimeline');
    var dataArr = [];
    var id = 1;
    var dataset = new vis.DataSet(dataArr);

    // Configuration for the Timeline
    var options = {};

    // Create a Timeline
    var timeline = new vis.Timeline(container, dataset, options);
    timeline.on('rangechange', function(properties) {
        tmp = properties;
        console.log(tmp);
    });
    timeline.on('select', function(properties) {
        var hostname = dataArr[properties.items[0] - 1].content;
        window.open("http://astral-casing-728.appspot.com/computer?hostname=" + hostname, "_blank")
    });

    var json = '<%=new Gson().toJson(DatastoreToBlobstoreConverter.convertToIntervalsByComputer(usages))%>';
    loadedDate(json);
    function loadedDate(json) {
        var data = JSON.parse(json);

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
