<%@ page import="java.util.List" %>
<%@ page import="is.murmur.Model.Beans.Career" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/Pages/activityArea.css">
    <title>Career</title>
</head>

<%
    List<Career> careers = new ArrayList<>();
    if(session.getAttribute("career") != null)
        careers = (List<Career>) session.getAttribute("career");
%>

<body>
<%@include file="JSPF/header.jspf"%>

<div id="activityAreaView">
    <ul id="activityAreaList">
        <%if(!careers.isEmpty()){%>
        <%for(Career c : careers){%>
        <li id="aCareer"><%=c.getProfession().getName()%>: <%=c.getHourlyRate()%> - <%=c.getSeniority()%></li>
        <%
        }
            }else {
        %>
        <li>No Career Found.</li>
        <%}%>
    </ul>
</div>

<%@include file="JSPF/footer.jspf"%>
</body>
</html>