<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/Pages/activityArea.css">
    <title>Admin</title>
</head>

<body>
<%@include file="JSPF/header.jspf"%>

<div id="applicationsDiv">
    <%@include file="JSPF/ulChecked.jspf"%>
</div>

<%@include file="JSPF/footer.jspf"%>
</body>
</html>
