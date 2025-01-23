<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/Pages/dbAccess.css">
    <title>Database Access</title>
</head>
<body>
<h1>Database Access</h1>
<form method="post" action="<%= request.getContextPath() %>/connectDB">
    <label for="developer">Who are you?</label>
    <select id="developer" name="developer" required>
        <option value="" disabled selected>-- Select a developer --</option>
        <option value="pietro">Pietro</option>
        <option value="gerardo">Gerardo</option>
        <option value="mattia">Mattia</option>
    </select>
    <button type="submit" id="submit">Access to the platform</button>
</form>
<%
    String developerError = (String) session.getAttribute("developerError");
    if(developerError != null){
%>
<p id="dbPasswordError"><%=developerError%></p>
<%}%>
</body>
</html>