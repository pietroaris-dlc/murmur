<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Signin</title>
    <link rel="stylesheet" type="text/css" href="CSS/Pages/signin.css">
</head>
<body>
<%@ include file="JSPF/header.jspf" %>

<form id="loginForm" method="post" action="" onsubmit="validate()">
    <label for="email">Email:</label><input type="email" id="email" name="email" required>
    <label for="password">Password:</label><input type="password" id="password" name="password" required>
    <label for="firstName">First Name:</label><input type="text" id="firstName" name="firstName" required>
    <label for="lastName">Last Name:</label><input type="text" id="lastName" name="lastName" required>
    <label for="taxCode">Tax Code:</label><input type="text" id="taxCode" name="taxCode" required>
    <label for="birthDate">Birth Date:</label><input type="date" id="birthDate" name="birthDate" required>
    <label for="birthCity">Birth City:</label><input type="text" id="birthCity" name="birthCity" required>
    <label for="birthDistrict">Birth District:</label><input type="text" id="birthDistrict" name="birthDistrict" required>
    <label for="birthCountry">BirthCountry:</label><input type="text" id="birthCountry" name="birthCountry" required>
    <input type="submit" value="Sign in">
</form>

<%@ include file="JSPF/footer.jspf" %>

</body>
<script type="text/javascript" src="JS/signin.js"></script>
</html>