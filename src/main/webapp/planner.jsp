<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="is.murmur.Model.Beans.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/Pages/activityArea.css">
    <title>Activity Area</title>
</head>

<%
    List<Daily> dailyPlanner = new ArrayList<>();
    if(session.getAttribute("dailyPlanner") != null)
        dailyPlanner = (List<Daily>) session.getAttribute("dailyPlanner");
    List<Weekday> weeklyPlanner = new ArrayList<>();
    if(session.getAttribute("weeklyPlanner") != null)
        weeklyPlanner = (List<Weekday>) session.getAttribute("weeklyPlanner");
%>

<body>
<%@include file="JSPF/header.jspf"%>

<div id="dailyPlannerView">
    <ul id="dailyPlannerList">
        <%if(!dailyPlanner.isEmpty()){%>
        <%for(Daily daily : dailyPlanner){ %>
        <li id="aDaily"><%=daily.getDay()%>,<%=daily.getStartHour()%> - <%=daily.getEndHour()%></li>
        <%
            }
        } else {
        %>
        <li>No Daily Schedule Found</li>
        <%}%>
    </ul>
</div>
<div id="weeklyPlannerView">
    <ul id="weeklyPlannerList">
        <%if(!weeklyPlanner.isEmpty()){%>
        <%for(Weekday wd : weeklyPlanner){ %>
        <li id="aWeekly"><%=wd.getWeekly().getStartDate()%>,<%=wd.getWeekly().getEndDate()%><%=wd.getStartHour()%>,<%=wd.getEndHour()%>,<%=wd.getId().getDayOfWeek()%></li>
        <%
            }
        } else {
        %>
        <li>No Weekly Schedule Found</li>
        <%}%>
    </ul>
</div>

<form id="searchForm">
    <div class="form-row">
        <label for="searchScheduleType">Schedule type:</label>
        <select id="searchScheduleType" name="searchScheduleType" required>
            <option value="">-- Select type--</option>
            <option value="daily">Daily</option>
            <option value="weekly">Weekly</option>
        </select>
    </div>
</form>
<form>
    <div id="scheduleCriteria"></div>
    <input type="submit" value="addSchedule">
</form>

<%@include file="JSPF/footer.jspf"%>
</body>
</html>