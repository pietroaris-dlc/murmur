<%@ page import="java.util.List" %>
<%@ page import="is.murmur.Model.Beans.Activityarea" %>
<%@ page import="is.murmur.Model.Beans.Location" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/Pages/activityArea.css">
    <title>Activity Area</title>
</head>

<%
    List<Activityarea> activityarea = new ArrayList<>();
    if(session.getAttribute("activityArea") != null)
        activityarea = (List<Activityarea>) session.getAttribute("activityArea");
%>

<body>
<%@include file="JSPF/header.jspf"%>

<div id="activityAreaView">
    <ul id="activityAreaList">
        <%if(!activityarea.isEmpty()){%>
        <%for(Activityarea aa : activityarea){ Location l = aa.getLocation();%>
        <li id="aLocation"><%=l.getStreet()%> <%=l.getStreetNumber()%>,<%=l.getCity()%>(<%=l.getRegion()%>) - <%=l.getCountry()%></li>
        <%
        }
            } else {
        %>
        <li>No Location Found</li>
        <%}%>
    </ul>
</div>

<form id="addLocationForm">
    <label for="cityField">Password:</label><input type="text" id="cityField" name="cityField" required>
    <label for="streetField">Password:</label><input type="text" id="streetField" name="streetField" required>
    <label for="streetNumberField">Password:</label><input type="number" id="streetNumberField" name="streetNumberField" required>
    <label for="districtField">Password:</label><input type="text" id="districtField" name="districtField" required>
    <label for="regionField">Password:</label><input type="text" id="regionField" name="regionField" required>
    <label for="countryField">Password:</label><input type="text" id="countryField" name="countryField" required>
    <input type="submit" value="addSchedule">
</form>

<%@include file="JSPF/footer.jspf"%>
</body>
</html>