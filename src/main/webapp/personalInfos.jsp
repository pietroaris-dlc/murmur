<%@ page import="is.murmur.Model.Beans.Registereduser" %>
<%@ page import="is.murmur.Model.Beans.Workercomponent" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Signin</title>
    <link rel="stylesheet" type="text/css" href="CSS/Pages/signin.css">
</head>

<%
    Registereduser user = (Registereduser) session.getAttribute("user");
    Workercomponent wc = null;
    if(session.getAttribute("workerComponent") != null)
        wc = (Workercomponent)session.getAttribute("workerComponent");
%>
<body>
<%@ include file="JSPF/header.jspf" %>
    <ul>
        <li><%=user.getEmail()%></li>
        <li><%=user.getFirstName()%></li>
        <li><%=user.getLastName()%></li>
        <li><%=user.getBirthDate()%></li>
        <li><%=user.getBirthCity()%></li>
        <li><%=user.getBirthDistrict()%></li>
        <li><%=user.getBirthCountry()%></li>
        <li><%=user.getTaxCode()%></li>
        <%if(wc != null){%>
        <li><%=wc.getPriority()%></li>
        <li><%=wc.getAverageRating()%></li>
        <li><%=wc.getLastMonthWorkdays()%></li>
        <li><%=wc.getTotalProfit()%></li>
        <%}%>
    </ul>
<%@ include file="JSPF/footer.jspf" %>
</body>
<script type="text/javascript" src="JS/signin.js"></script>
</html>