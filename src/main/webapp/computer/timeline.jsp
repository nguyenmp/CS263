<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String hostname = (String) request.getAttribute("hostname");
    Long date = (Long) request.getAttribute("date");
%>
<!doctype html>
<html>
<head>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
    <link href="http://visjs.org/dist/vis.css" rel="stylesheet" type="text/css" />
    <script src="http://visjs.org/dist/vis.js"></script>
</head>
<body>
<div id="mytimeline"></div>

<script type="text/javascript">
    $.getJSON("http://astral-casing-728.appspot.com/blobstore_server?hostname=<%=hostname%>&date=<%=date%>", function(data) {
        console.log(data);

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
    });
</script>
</body>
</html>