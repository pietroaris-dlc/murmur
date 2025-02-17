<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/Pages/admin.css">
    <title>Admin</title>
</head>

<body>
<%@include file="JSPF/header.jspf"%>

<div id="applicationsDiv">
    <%@include file="JSPF/ulChecked.jspf"%>
    <%@include file="JSPF/ulDefinite.jspf"%>
    <%@include file="JSPF/ulUpgrade.jspf"%>
    <%@include file="JSPF/ulJob.jspf"%>
    <%@include file="JSPF/ulCollab.jspf"%>
</div>

<%@include file="JSPF/footer.jspf"%>
</body>
</html>
<script type="text/javascript" src="JS/admin.js"></script>
