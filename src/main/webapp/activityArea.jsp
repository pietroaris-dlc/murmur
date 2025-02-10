<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/Pages/activityArea.css">
    <title>Activity Area</title>


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


    </style>



</head>
<body>
<%@include file="JSPF/header.jspf"%>
<div id = rightAllign>
    <div id="infoDiv">
        <br>
        <h1> Locations </h1>

        <div id="aboutUsDiv">
        <p id="aboutUsParagraph">

        <div class="form-group">
            <input type="text" value = "VIA DI CASA MIA" name="insidein" disabled   />
        </div>

        <div class="form-group">
            <input type="text" value = "VIA DI CASA TUA" name="insidein" disabled   />
        </div>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <br>
        <div class="form-group">
            <input type="text" value = "VIA DI CASA NOSTRA" name="insidein" disabled   />
        </div>
        <br>
        <br>
        <br>
        <br>
    </p>
        </div>
    </div>
    <%-- <div id=""> --%>
    <div id="infoDiv">
        <br>
        <h1>Add a Location</h1>
        <form action="#" method="post">
            <div class="form-group">
                <label>City</label>
                <input type="text" name="City" required>
            </div>

            <div class="form-group">
                <label>Street</label>
                <input type="text" name="Street1" required>
            </div>

            <div class="form-group">
                <label>Street</label>
                <input type="text" name="Street2">
            </div>

            <div class="form-group">
                <label>Street Number</label>
                <input type="text" name="StreetNumber" required>
            </div>

            <div class="form-group">
                <label>District</label>
                <input type="text" name="District" required>
            </div>

            <div class="form-group">
                <label>Region</label>
                <input type="text" name="Region" required>
            </div>

            <div class="form-group">
                <label>Country</label>
                <input type="text" name="Country" required>
            </div>
            <div class="form-group">
                <button type="submit" class="submit-button">Submit</button>
            </div>

            <br>
            <br>
        </form>

        <%-- </div> --%>
    </div>
</div>

<%@include file="JSPF/footer.jspf"%>
</body>
</html>