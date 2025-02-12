package is.murmur.Controller.ClientSide;

import is.murmur.Model.Services.ClientSide;
import is.murmur.Model.Helpers.Criteria;
import is.murmur.Model.Helpers.TimeInterval;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "searchServlet", value = "/searchServlet")
public class SearchServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Estrae i parametri base dal form
            String scheduleType = request.getParameter("searchScheduleType"); // "daily" o "weekly"
            String serviceMode = request.getParameter("searchServiceMode");     // "remote", "onSite", "homeDelivery"
            Criteria criteria = null;

            // Costruzione dei criteri in base al tipo di ricerca
            if (scheduleType.equalsIgnoreCase("daily")) {
                // Parametri specifici per la ricerca DAILY
                String profession = request.getParameter("professionCriteria");
                double hourlyRateMin = Double.parseDouble(request.getParameter("hourlyRateMin"));
                double hourlyRateMax = Double.parseDouble(request.getParameter("hourlyRateMax"));
                LocalDate day = LocalDate.parse(request.getParameter("day"));
                LocalTime startHour = LocalTime.parse(request.getParameter("startHour"));
                LocalTime endHour = LocalTime.parse(request.getParameter("endHour"));

                // Creazione del builder per Daily
                Criteria.Builder builder = new Criteria.Builder("DAILY", serviceMode.toUpperCase(),
                        profession, hourlyRateMin, hourlyRateMax)
                        .daily(day, startHour, endHour);

                // Aggiunta dei parametri di location se richiesti
                if (serviceMode.equalsIgnoreCase("onSite") ||
                        serviceMode.equalsIgnoreCase("homeDelivery")) {
                    String city = request.getParameter("city");
                    String street = request.getParameter("street");
                    String district = request.getParameter("district");
                    String region = request.getParameter("region");
                    String country = request.getParameter("country");
                    builder.location(city, street, district, region, country);
                }
                criteria = builder.build();
            } else { // Weekly
                // Parametri specifici per la ricerca WEEKLY
                String profession = request.getParameter("professionCriteria");
                double hourlyRateMin = Double.parseDouble(request.getParameter("hourlyRateMin"));
                double hourlyRateMax = Double.parseDouble(request.getParameter("hourlyRateMax"));
                LocalDate startDate = LocalDate.parse(request.getParameter("startDate"));
                LocalDate endDate = LocalDate.parse(request.getParameter("endDate"));

                // Estrae la lista dei giorni e gli intervalli orari associati
                String dayOfWeekListStr = request.getParameter("dayOfWeekList"); // es. "MONDAY,TUESDAY"
                String[] days = dayOfWeekListStr.split(",");
                Map<String, TimeInterval> weeklyIntervals = new HashMap<>();
                for (String d : days) {
                    d = d.trim().toUpperCase();
                    // I campi del form sono convenzionalmente nominati: "mondayStart", "mondayEnd", etc.
                    LocalTime dayStart = LocalTime.parse(request.getParameter(d.toLowerCase() + "Start"));
                    LocalTime dayEnd = LocalTime.parse(request.getParameter(d.toLowerCase() + "End"));
                    weeklyIntervals.put(d, new TimeInterval(dayStart, dayEnd));
                }

                // Creazione del builder per Weekly
                Criteria.Builder builder = new Criteria.Builder("WEEKLY", serviceMode.toUpperCase(),
                        profession, hourlyRateMin, hourlyRateMax)
                        .weekly(startDate, endDate, weeklyIntervals);

                // Aggiunta dei parametri di location se richiesti
                if (serviceMode.equalsIgnoreCase("onSite") ||
                        serviceMode.equalsIgnoreCase("homeDelivery")) {
                    String city = request.getParameter("city");
                    String street = request.getParameter("street");
                    String district = request.getParameter("district");
                    String region = request.getParameter("region");
                    String country = request.getParameter("country");
                    builder.location(city, street, district, region, country);
                }
                criteria = builder.build();
            }

            // Invoca il servizio ClientSide con i criteri costruiti
            String resultJson = ClientSide.search(criteria);

            // Imposta il content type e restituisce il JSON al client
            response.setContentType("application/json");
            response.getWriter().write(resultJson);
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject errorJson = new JSONObject();
            errorJson.put("error", "Server error occurred: " + e.getMessage());
            response.setContentType("application/json");
            response.getWriter().write(errorJson.toString());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }
}