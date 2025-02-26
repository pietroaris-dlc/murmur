<%@ page import="java.util.List" %>
<%@ page import="is.murmur.Storage.DAO.Career" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/Pages/activityArea.css">
    <title>Contracts</title>
</head>

<body>
<%@include file="JSPF/header.jspf"%>

<div id="contractsDiv">
    <%@include file="JSPF/ulDraft.jspf"%>
    <%@include file="JSPF/ulOffer.jspf"%>
    <%@include file="JSPF/ulActive.jspf"%>
    <%@include file="JSPF/ulExpired.jspf"%>
    <%@include file="JSPF/ulCancReq.jspf"%>
</div>

<%@include file="JSPF/footer.jspf"%>
</body>
</html>