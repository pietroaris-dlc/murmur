package is.murmur.Services.SearchStrategy;

import is.murmur.Model.Beans.*;
import is.murmur.Model.Helpers.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Implementazione della strategia di ricerca onsite settimanale.
 * <p>
 * Questa classe implementa l'interfaccia {@link SearchStrategy} e definisce la logica
 * per eseguire una ricerca di lavoratori (worker) che offrono servizi onsite su base settimanale.
 * I criteri di ricerca includono la professione, il range di tariffa oraria, le date di inizio e fine,
 * gli intervalli orari per ciascun giorno della settimana, e i dettagli di localizzazione.
 * </p>
 *
 */
public class WeeklyOnsiteSearchStrategy implements SearchStrategy {

    /**
     * Esegue la ricerca dei lavoratori in base ai criteri settimanali specificati per il servizio onsite.
     * <p>
     * Il metodo estrae i parametri di ricerca dai criteri forniti, compresi gli intervalli settimanali
     * e i dettagli di localizzazione. Esegue una query sul database per recuperare gli utenti di tipo "WORKER",
     * le loro carriere e il numero civico associato alla localizzazione, filtrando in base a professione,
     * tariffa oraria e localizzazione. Per ogni lavoratore, viene controllata la presenza di collisioni
     * con gli intervalli orari specificati per ogni giorno compreso tra la data di inizio e quella di fine.
     * I lavoratori senza collisioni vengono aggiunti ai risultati, che vengono infine ordinati per priorità
     * e formattati in un oggetto JSON.
     * </p>
     *
     * @param criteria I criteri di ricerca che includono professione, tariffa oraria, date, intervalli settimanali e localizzazione.
     * @return Una stringa in formato JSON contenente i criteri di ricerca e i risultati, oppure un messaggio di errore.
     */
    @Override
    public String search(Criteria criteria) {
        // Ottiene l'EntityManager per interagire con il database
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        String json = "";
        try {
            // Estrae i parametri di ricerca dai criteri
            String profession = criteria.getProfession();
            double hourlyRateMin = criteria.getHourlyRateMin();
            double hourlyRateMax = criteria.getHourlyRateMax();
            LocalDate startDate = criteria.getStartDate();

            if (profession == null || profession.isEmpty()) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "profession is required");
                return errorJson.toString(2);
            } else {
                // Controllo per digits e caratteri speciali (solo lettere e spazi ammessi)
                for (char c : profession.toCharArray()) {
                    if (Character.isDigit(c)) {
                        JSONObject errorJson = new JSONObject();
                        errorJson.put("results", "profession contains digits");
                        return errorJson.toString(2);
                    }
                    if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                        JSONObject errorJson = new JSONObject();
                        errorJson.put("results", "profession contains special characters");
                        return errorJson.toString(2);
                    }
                }
            }

            if (hourlyRateMax < hourlyRateMin) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "the hourlyRateMax must be greater than or equal to the hourlyRateMin");
                return errorJson.toString(2);
            }

            if (startDate == null) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "StartDate cannot be null");
                return errorJson.toString(2);
            }

            LocalDate endDate = criteria.getEndDate();
            if (endDate == null) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "EndDate cannot be null");
                return errorJson.toString(2);
            }

            if (endDate.isBefore(startDate)) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "EndDate cannot be before startDate");
                return errorJson.toString(2);
            }

            Map<String, TimeInterval> weeklyIntervals = criteria.getWeeklyIntervals();
            if (weeklyIntervals == null) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "WeeklyIntervals cannot be null");
                return errorJson.toString(2);
            }

            List<String> dayOfWeekList = new ArrayList<>();
            List<LocalTime> dayOfWeekStartTimes = new ArrayList<>();
            List<LocalTime> dayOfWeekEndTimes = new ArrayList<>();
            for (Map.Entry<String, TimeInterval> entry : weeklyIntervals.entrySet()) {
                String dayOfWeek = entry.getKey().toUpperCase();
                TimeInterval interval = entry.getValue();
                dayOfWeekList.add(dayOfWeek);
                dayOfWeekStartTimes.add(interval.getStart());
                dayOfWeekEndTimes.add(interval.getEnd());
                if (interval.getEnd().isAfter(interval.getStart())) {
                    JSONObject errorJson = new JSONObject();
                    errorJson.put("results", "the endHour must be after the StartHour");
                    return errorJson.toString(2);
                }
            }
            // Verifica che gli intervalli settimanali non siano vuoti
            if (dayOfWeekList.isEmpty()) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "WeeklyIntervals cannot be empty");
                return errorJson.toString(2);
            }

            for (List<LocalTime> localTimes : Arrays.asList(dayOfWeekStartTimes, dayOfWeekEndTimes)) {
                if (localTimes.isEmpty()) {
                    JSONObject errorJson = new JSONObject();
                    errorJson.put("results", "WeeklyIntervals cannot be empty");
                    return errorJson.toString(2);
                }
            }

            String city = criteria.getCity();
            if (city == null) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "City cannot be null");
                return errorJson.toString(2);
            } else {
                // Controllo per digits e caratteri speciali
                for (char c : city.toCharArray()) {
                    if (Character.isDigit(c)) {
                        JSONObject errorJson = new JSONObject();
                        errorJson.put("results", "City contains digits");
                        return errorJson.toString(2);
                    }
                    if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                        JSONObject errorJson = new JSONObject();
                        errorJson.put("results", "City contains special characters");
                        return errorJson.toString(2);
                    }
                }
            }

            String street = criteria.getStreet();
            if (street == null) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "Street cannot be null");
                return errorJson.toString(2);
            } else if (street.isEmpty()) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "Street cannot be empty");
                return errorJson.toString(2);
            } else if (street.length() > 128) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "Street contains more than 128 characters");
                return errorJson.toString(2);
            }

            String district = criteria.getDistrict();
            if (district == null) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "District cannot be null");
                return errorJson.toString(2);
            } else if (district.isEmpty()) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "District cannot be empty");
                return errorJson.toString(2);
            } else {
                // Controllo per digits e caratteri speciali
                for (char c : district.toCharArray()) {
                    if (Character.isDigit(c)) {
                        JSONObject errorJson = new JSONObject();
                        errorJson.put("results", "District contains digits");
                        return errorJson.toString(2);
                    }
                    if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                        JSONObject errorJson = new JSONObject();
                        errorJson.put("results", "District contains special characters");
                        return errorJson.toString(2);
                    }
                }
            }

            String region = criteria.getRegion();
            if (region == null) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "Region cannot be null");
                return errorJson.toString(2);
            } else if (region.isEmpty()) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "Region cannot be empty");
                return errorJson.toString(2);
            } else {
                // Controllo per digits e caratteri speciali
                for (char c : region.toCharArray()) {
                    if (Character.isDigit(c)) {
                        JSONObject errorJson = new JSONObject();
                        errorJson.put("results", "Region contains digits");
                        return errorJson.toString(2);
                    }
                    if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                        JSONObject errorJson = new JSONObject();
                        errorJson.put("results", "Region contains special characters");
                        return errorJson.toString(2);
                    }
                }
            }

            String country = criteria.getCountry();
            if (country == null) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "Country cannot be null");
                return errorJson.toString(2);
            } else if (country.isEmpty()) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "Country cannot be empty");
                return errorJson.toString(2);
            } else {
                // Controllo per digits e caratteri speciali
                for (char c : country.toCharArray()) {
                    if (Character.isDigit(c)) {
                        JSONObject errorJson = new JSONObject();
                        errorJson.put("results", "Country contains digits");
                        return errorJson.toString(2);
                    }
                    if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                        JSONObject errorJson = new JSONObject();
                        errorJson.put("results", "Country contains special characters");
                        return errorJson.toString(2);
                    }
                }
            }

            // Crea la query per selezionare gli utenti, le loro carriere e il numero civico
            Query query = em.createQuery(
                            "select u, c, aa.location.streetNumber from User u " +
                                    "join Planner p on p.user.id = u.id " +
                                    "join Career c on c.worker.id = u.id " +
                                    "join Daily d on d.schedule.id = p.schedule.id " +
                                    "join Activityarea aa on aa.worker.id = u.id " +
                                    "where u.type = 'WORKER' " +
                                    "and c.profession.name = :profession " +
                                    "and c.hourlyRate > :hourlyRateMin " +
                                    "and c.hourlyRate < :hourlyRateMax " +
                                    "and aa.location.city = :city " +
                                    "and aa.location.street = :street " +
                                    "and aa.location.district = :district " +
                                    "and aa.location.region = :region " +
                                    "and aa.location.country = :country")
                    .setParameter("profession", profession)
                    .setParameter("hourlyRateMin", hourlyRateMin)
                    .setParameter("hourlyRateMax", hourlyRateMax)
                    .setParameter("city", city)
                    .setParameter("street", street)
                    .setParameter("district", district)
                    .setParameter("region", region)
                    .setParameter("country", country);

            // Esegue la query e ottiene i risultati
            @SuppressWarnings("unchecked")
            List<Object[]> queryResults = query.getResultList();
            List<Result> results = new ArrayList<>();

            if (queryResults != null) {
                for (Object[] row : queryResults) {
                    User worker = (User) row[0];
                    Career career = (Career) row[1];
                    Short streetNumber = (Short) row[2];

                    boolean hasCollision = false;

                    // Controlla ogni giorno compreso tra startDate ed endDate
                    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                        String dayOfWeek = date.getDayOfWeek().name();

                        // Se esiste un intervallo per il giorno corrente, verifica la collisione
                        if (weeklyIntervals.containsKey(dayOfWeek)) {
                            TimeInterval interval = weeklyIntervals.get(dayOfWeek);
                            if (Collision.detect(worker, date, interval)) {
                                hasCollision = true;
                                break;
                            }
                        }
                    }

                    // Aggiunge il lavoratore ai risultati se non sono state rilevate collisioni
                    if (!hasCollision) {
                        results.add(new Result(worker, career, streetNumber));
                    }
                }
            } else {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "QueryResults cannot be null");
                return errorJson.toString(2);
            }

            // Ordina i risultati in base alla priorità del lavoratore (in ordine decrescente)
            results.sort(new Comparator<Result>() {
                @Override
                public int compare(Result o1, Result o2) {
                    return (int) (o2.getWorker().getWorker().getPriority() - o1.getWorker().getWorker().getPriority());
                }
            });

            // Crea l'oggetto JSON per l'output finale
            JSONObject output = new JSONObject();

            // Crea e popola l'oggetto JSON con i criteri di ricerca
            JSONObject criteriaJson = new JSONObject();
            criteriaJson.put("scheduleType", criteria.getScheduleType());
            criteriaJson.put("serviceMode", criteria.getServiceMode());
            criteriaJson.put("profession", profession);
            criteriaJson.put("hourlyRateMin", hourlyRateMin);
            criteriaJson.put("hourlyRateMax", hourlyRateMax);
            criteriaJson.put("startDate", startDate.toString());
            criteriaJson.put("endDate", endDate.toString());

            // Aggiunge gli intervalli settimanali in formato JSON
            JSONObject weeklyIntervalsJson = new JSONObject();
            for (Map.Entry<String, TimeInterval> entry : weeklyIntervals.entrySet()) {
                JSONObject intervalJson = new JSONObject();
                intervalJson.put("start", entry.getValue().getStart().toString());
                intervalJson.put("end", entry.getValue().getEnd().toString());
                weeklyIntervalsJson.put(entry.getKey(), intervalJson);
            }
            criteriaJson.put("weeklyIntervals", weeklyIntervalsJson);

            // Aggiunge i dettagli di localizzazione
            criteriaJson.put("city", city);
            criteriaJson.put("street", street);
            criteriaJson.put("district", district);
            criteriaJson.put("region", region);
            criteriaJson.put("country", country);
            output.put("searchCriteria", criteriaJson);

            // Crea un array JSON per contenere i risultati della ricerca
            JSONArray resultsArray = new JSONArray();
            if (!results.isEmpty()) {
                for (Result result : results) {
                    JSONObject resultJson = new JSONObject();
                    resultJson.put("user", result.getWorker());
                    resultJson.put("career", result.getCareer());
                    resultJson.put("streetNumber", result.getStreetNumber());
                    resultsArray.put(resultJson);
                }
            } else {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "No Results Found");
                return errorJson.toString(2);
            }
            output.put("results", resultsArray);

            // Converte l'oggetto JSON in una stringa formattata con indentazione (2 spazi)
            json = output.toString(2);
        } catch (Exception e) {
            // Stampa lo stack trace in caso di errore
            e.printStackTrace();
            // Crea un oggetto JSON per rappresentare l'errore
            JSONObject errorJson = new JSONObject();
            errorJson.put("results", "Server error occurred: " + e.getMessage());
            json = errorJson.toString(2);
        } finally {
            // Chiude l'EntityManager per liberare le risorse
            em.close();
        }
        return json;
    }
}