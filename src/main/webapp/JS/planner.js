document.addEventListener('DOMContentLoaded', function() {
    const scheduleSelect = document.getElementById('searchScheduleType');
    scheduleSelect.addEventListener('change', loadScheduleCriteria);
});

function loadScheduleCriteria() {
    const scheduleType = document.getElementById('searchScheduleType').value;
    const scheduleDiv = document.getElementById('scheduleCriteria');

    scheduleDiv.innerHTML = '';

    if (scheduleType === "daily") {
        // Struttura a form-row per day, startHour, endHour
        scheduleDiv.innerHTML = `
            <div class="form-row">
                <label for="day">Day:</label>
                <input type="date" id="day" name="day" required />
            </div>
            <div class="form-row">
                <label for="startHour">Start Hour:</label>
                <input type="time" class="timeField" id="startHour" name="startHour" required />
            </div>
            <div class="form-row">
                <label for="endHour">End Hour:</label>
                <input type="time" class="timeField" id="endHour" name="endHour" required />
            </div>
        `;
    } else if (scheduleType === "weekly") {
        // Manteniamo la tabella per l'inserimento dei giorni
        scheduleDiv.innerHTML = `
            <table style="margin: 0 auto;">
                <thead>
                  <tr>
                    <th>Week Day</th>
                    <th>Start Hour</th>
                    <th>End Hour</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>
                      <input type="checkbox" id="monday" name="monday">
                      <label for="monday">Monday</label>
                    </td>
                    <td><input type="time" name="mondayStart" placeholder="Start Hour"></td>
                    <td><input type="time" name="mondayEnd" placeholder="End Hour"></td>
                  </tr>
                  <tr>
                    <td>
                      <input type="checkbox" id="tuesday" name="tuesday">
                      <label for="tuesday">Tuesday</label>
                    </td>
                    <td><input type="time" name="tuesdayStart" placeholder="Start Hour"></td>
                    <td><input type="time" name="tuesdayEnd" placeholder="End Hour"></td>
                  </tr>
                  <tr>
                    <td>
                      <input type="checkbox" id="wednesday" name="wednesday">
                      <label for="wednesday">Wednesday</label>
                    </td>
                    <td><input type="time" name="wednesdayStart" placeholder="Start Hour"></td>
                    <td><input type="time" name="wednesdayEnd" placeholder="End Hour"></td>
                  </tr>
                  <tr>
                    <td>
                      <input type="checkbox" id="thursday" name="thursday">
                      <label for="thursday">Thursday</label>
                    </td>
                    <td><input type="time" name="thursdayStart" placeholder="Start Hour"></td>
                    <td><input type="time" name="thursdayEnd" placeholder="End Hour"></td>
                  </tr>
                  <tr>
                    <td>
                      <input type="checkbox" id="friday" name="friday">
                      <label for="friday">Friday</label>
                    </td>
                    <td><input type="time" name="fridayStart" placeholder="Start Hour"></td>
                    <td><input type="time" name="fridayEnd" placeholder="End Hour"></td>
                  </tr>
                  <tr>
                    <td>
                      <input type="checkbox" id="saturday" name="saturday">
                      <label for="saturday">Saturday</label>
                    </td>
                    <td><input type="time" name="saturdayStart" placeholder="Start Hour"></td>
                    <td><input type="time" name="saturdayEnd" placeholder="End Hour"></td>
                  </tr>
                  <tr>
                    <td>
                      <input type="checkbox" id="sunday" name="sunday">
                      <label for="sunday">Sunday</label>
                    </td>
                    <td><input type="time" name="sundayStart" placeholder="Start Hour"></td>
                    <td><input type="time" name="sundayEnd" placeholder="End Hour"></td>
                  </tr>
                </tbody>
            </table>
        `;
    }
}