<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" type="text/css" href="CSS/Pages/login.css">
</head>
<body>
<%@ include file="JSPF/header.jspf" %>

<form id="loginForm" method="post" action="" onsubmit="validate()">
    <label for="email">Email:</label><input type="email" id="email" name="email" required>
    <label for="password">Password:</label><input type="password" id="password" name="password" required>
    <input type="submit" value="Login">
</form>

<%@ include file="JSPF/footer.jspf" %>

</body>
<script type="text/javascript" src="JS/login.js"></script>
</html>