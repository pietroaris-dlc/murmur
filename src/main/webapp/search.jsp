<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Search</title>
    <link rel="stylesheet" type="text/css" href="CSS/Pages/search.css">
</head>
<body>
<%@ include file="JSPF/header.jspf" %>

<div id="body">
    <form id="searchForm">
        <!-- RIGA 1: Schedule Type -->
        <div class="form-row">
            <label for="searchScheduleType">Schedule type:</label>
            <select id="searchScheduleType" name="searchScheduleType" required>
                <option value="">-- Select type--</option>
                <option value="daily">Daily</option>
                <option value="weekly">Weekly</option>
            </select>
        </div>

        <!-- RIGA 2: Service Mode -->
        <div class="form-row">
            <label for="searchServiceMode">Service mode:</label>
            <select id="searchServiceMode" name="searchServiceMode" required>
                <option value="">-- Select mode--</option>
                <option value="remote">Remote</option>
                <option value="onSite">On site</option>
                <option value="homeDelivery">Home Delivery</option>
            </select>
        </div>

        <!-- RIGA 3: Profession -->
        <div class="form-row">
            <label for="professionCriteria">Profession:</label>
            <input type="text" id="professionCriteria" name="professionCriteria" required>
        </div>

        <!-- RIGA 4: Hourly Rate Min -->
        <div class="form-row">
            <label for="hourlyRateMin">Hourly Rate Min:</label>
            <input type="number" id="hourlyRateMin" name="hourlyRateMin" required>
        </div>

        <!-- RIGA 5: Hourly Rate Max -->
        <div class="form-row">
            <label for="hourlyRateMax">Hourly Rate Max:</label>
            <input type="number" id="hourlyRateMax" name="hourlyRateMax" required>
        </div>

        <!-- Schedule Criteria (injected via JavaScript) -->
        <div id="scheduleCriteria"></div>

        <!-- Service Mode Criteria (injected via JavaScript) -->
        <div id="serviceModeCriteria"></div>

        <!-- RIGA ULTIMA: pulsante Submit -->
        <div class="form-row submit-row">
            <input type="submit" value="Search">
        </div>
    </form>

    <div id="results">
        <ul id="resultsList"></ul>
    </div>
</div>

<%@ include file="JSPF/footer.jspf" %>

</body>
<script type="text/javascript" src="JS/search.js"></script>
</html>