<%--
  Created by IntelliJ IDEA.
  User: pietr
  Date: 22/01/2025
  Time: 01:02
  To change this template use File | Settings | File Templates.
--%>


<style>
    /* This CSS will only affect text inputs on this page */
    .form-group {
        display: flex;
        align-items: center;
        gap: 20px; /* Adjusts spacing between label and input */
        margin-bottom: 10px; /* Adds spacing between rows */



    }

    .form-group label {
        width: 120px; /* Adjust label width for consistency */
        font-weight: bold;
    }

    .form-group input {
        width: 250px; /* Set a consistent input width */
        height: 42px;
        padding-left: 10px;
        border: 1px solid #777;
        border-radius: 10px;
    }

    .submit-button {
        background-color: #00c853; /* Green background */
        color: white; /* White text */
        border: none; /* No border */
        padding: 10px 20px; /* Button size */
        font-size: 16px;
        font-weight: bold;
        border-radius: 20px; /* Rounded edges */
        cursor: pointer;
        transition: 0.3s ease-in-out;
        display: block;
        margin: 20px auto; /* Centers the button */
    }

    .submit-button:hover {
        background-color: #009624; /* Darker green on hover */
    }



    .output-box {
        width: 100%;
        max-width: 250px;  /* Match the width of the other boxes */

        border: 1px solid #ccc;
        border-radius: 10px;
        background-color: #d9d9d9;
        font-family: Arial, sans-serif;
        font-size: 14px;
        color: #333;
        white-space: pre-wrap;  /* Ensures newlines are respected */
        word-wrap: break-word;  /* Allows long words to break into the next line */

        display: inline-block;  /* Makes the box adapt its height based on content */

        word-spacing: -3; /* Adjusts the space between words */
        border: 1px solid #777;

        border-radius: 20px;

    }

</style>




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



<div id = rightAllign >
    <div id="infoDiv"> <!-- UPGRADE APPLICATION -->

            <h3> Upgrade Application </h3>

        <div id="aboutUsDiv">
            <p id="aboutUsParagraph">

            </p>
        </div>
    </div>


    <div id="infoDiv"> <!-- JOB APPLICATION -->

        <h3> Job Application </h3>

        <div id="aboutUsDiv">
            <p id="aboutUsParagraph">

            </p>
        </div>
    </div>

    <div id="infoDiv"> <!-- COLLAB APPLICATION -->

        <h3> Collab Application </h3>

        <div id="aboutUsDiv">
            <p id="aboutUsParagraph">

            <div class="output-box" >
                NOME: Giacomo   <br>
                COGNOME: Bertinetti
            </div>



            <div class="output-box" >
                NOME: Giacomo   <br>
                COGNOME: Bertinetti
            </div>

            <div class="output-box" >
                NOME: Bizzo   <br>
                COGNOME: Munto
            </div>


            <div class="output-box" >
                NOME: Bizzo   <br>
                COGNOME: Munto
            </div>

            </p>
        </div>
    </div>







</div>

<%@include file="JSPF/footer.jspf"%>

</body>
</html>
