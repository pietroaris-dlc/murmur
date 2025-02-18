document.addEventListener('DOMContentLoaded', function() {
    const scheduleSelect = document.getElementById('searchScheduleType');
    scheduleSelect.addEventListener('change', loadScheduleCriteria);

    const serviceSelect = document.getElementById('searchServiceMode');
    serviceSelect.addEventListener('change', loadServiceModeCriteria);
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
            <div class="form-row">
                <label for="StartDate">Start Date:</label>
                <input type="date" id="startDate" name="startDate" required />
            </div>
            <div class="form-row">
                <label for="EndDate">End Date:</label>
                <input type="date" id="endDate" name="endDate" required />
            </div>
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

function loadServiceModeCriteria() {
    const serviceMode = document.getElementById('searchServiceMode').value;
    const serviceDiv = document.getElementById('serviceModeCriteria');
    serviceDiv.innerHTML = '';

    if (serviceMode === "remote") {
        // Nessun campo aggiuntivo
        serviceDiv.innerHTML = ``;
    } else if (serviceMode === "onSite") {
        // Creiamo una serie di div .form-row per city, street, district, region, country
        serviceDiv.innerHTML = `
            <div class="form-row">
                <label for="city">City:</label>
                <input type="text" id="city" name="city" required />
            </div>
            <div class="form-row">
                <label for="street">Street:</label>
                <input type="text" id="street" name="street" />
            </div>
            <div class="form-row">
                <label for="district">District:</label>
                <input type="text" id="district" name="district" required />
            </div>
            <div class="form-row">
                <label for="region">Region:</label>
                <input type="text" id="region" name="region" required />
            </div>
            <div class="form-row">
                <label for="country">Country:</label>
                <input type="text" id="country" name="country" required />
            </div>
        `;
    } else if (serviceMode === "homeDelivery") {
        // Stessa struttura con .form-row
        serviceDiv.innerHTML = `
            <div class="form-row">
                <label for="city">City:</label>
                <input type="text" id="city" name="city" required />
            </div>
            <div class="form-row">
                <label for="street">Street:</label>
                <input type="text" id="street" name="street" />
            </div>
            <div class="form-row">
                <label for="district">District:</label>
                <input type="text" id="district" name="district" required />
            </div>
            <div class="form-row">
                <label for="region">Region:</label>
                <input type="text" id="region" name="region" required />
            </div>
            <div class="form-row">
                <label for="country">Country:</label>
                <input type="text" id="country" name="country" required />
            </div>
        `;
    }
}

document.getElementById("searchForm").addEventListener("submit", function(e) {
    e.preventDefault();

    console.log("Form submitted");

    // Se il tipo di schedule è "weekly", raccogli i giorni selezionati
    const scheduleType = document.getElementById('searchScheduleType').value;
    if (scheduleType === "weekly") {
        const daysNames = ["monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"];
        let selectedDays = [];
        daysNames.forEach(day => {
            const checkbox = document.getElementById(day);
            if (checkbox && checkbox.checked) {
                // Inseriamo il nome in maiuscolo per essere compatibile col server (es. "MONDAY")
                selectedDays.push(day.toUpperCase());
            }
        });

        // Crea (o aggiorna) un input hidden con il nome "dayOfWeekList"
        let hiddenInput = document.querySelector("input[name='dayOfWeekList']");
        if (!hiddenInput) {
            hiddenInput = document.createElement("input");
            hiddenInput.type = "hidden";
            hiddenInput.name = "dayOfWeekList";
            this.appendChild(hiddenInput);
        }
        hiddenInput.value = selectedDays.join(",");
    }

    const formData = new FormData(this);

    fetch("searchServlet", {
        method: "POST",
        body: new URLSearchParams(formData)
    })
        .then(data => {
            const resultsList = document.getElementById("resultsList");
            resultsList.innerHTML = "";
            console.log("Risposta dal server:", data.error);
            console.log("Risposta dal server:", JSON.stringify(data));

            if (data.error) {
                // Se c'è un messaggio di errore, lo mostriamo
                resultsList.innerHTML = `<li>Error: ${data.error}</li>`;
            } else if (data.results && data.results.length > 0) {
                data.results.forEach(result => {
                    const form = document.createElement("form");
                    form.className = "resultForm";

                    for (const key in result) {
                        if (result.hasOwnProperty(key)) {
                            const label = document.createElement("label");
                            label.textContent = key + ": ";
                            const input = document.createElement("input");
                            input.type = "text";
                            input.name = key;
                            input.value = result[key];
                            input.readOnly = true;
                            form.appendChild(label);
                            form.appendChild(input);
                            form.appendChild(document.createElement("br"));
                        }
                    }
                    const li = document.createElement("li");
                    li.appendChild(form);
                    resultsList.appendChild(li);
                });
            } else {
                resultsList.innerHTML = "<li>No Results Error</li>";
            }
        })
        .catch(error => {
            console.error("Errore durante la ricerca:", error);
            document.getElementById("resultsList").innerHTML = "<li>Error retrieving results. Check console.</li>";
        });
});