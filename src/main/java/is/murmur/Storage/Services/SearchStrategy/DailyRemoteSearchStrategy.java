package is.murmur.Storage.Services.SearchStrategy;

import is.murmur.Storage.DAO.*;
import is.murmur.Storage.Helpers.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementazione della strategia di ricerca remota giornaliera.
 * <p>
 * Questa classe implementa l'interfaccia {@link SearchStrategy} e definisce la logica
 * per eseguire una ricerca di lavoratori (worker) che soddisfano i criteri giornalieri
 * definiti, come la professione, il range di tariffa oraria e l'intervallo orario.
 * </p>
 */
public class DailyRemoteSearchStrategy implements SearchStrategy {

    /**
     * Esegue la ricerca dei lavoratori in base ai criteri specificati.
     * <p>
     * Il metodo esegue una query sul database utilizzando JPA per recuperare
     * utenti di tipo "WORKER" insieme alle loro informazioni di carriera, filtrando
     * in base alla professione e alla tariffa oraria. Successivamente, controlla
     * eventuali collisioni con l'orario specificato e ordina i risultati in base alla priorità.
     * Infine, i risultati vengono formattati in un oggetto JSON.
     * </p>
     *
     * @param criteria I criteri di ricerca che includono informazioni quali la professione,
     *                 la tariffa oraria minima e massima, il giorno e l'intervallo orario.
     * @return Una stringa in formato JSON contenente i criteri di ricerca e i risultati, oppure
     *         un messaggio di errore in caso di eccezioni.
     */
    @Override
    public String search(Criteria criteria) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        String json = "";
        try {
            // Estrae i parametri di ricerca dai criteri
            String profession = criteria.getProfession();
            double hourlyRateMin = criteria.getHourlyRateMin();
            double hourlyRateMax = criteria.getHourlyRateMax();
            LocalDate day = criteria.getDay();
            LocalTime startHour = criteria.getDailyStartHour();
            LocalTime endHour = criteria.getDailyEndHour();

            // Validazione della professione
            if (profession == null || profession.isEmpty()) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "profession is required");
                return errorJson.toString(2);
            } else {
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

            // Validazione delle tariffe orarie
            if (hourlyRateMax < hourlyRateMin) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "the hourlyRateMax must be greater than or equal to the hourlyRateMin");
                return errorJson.toString(2);
            }

            // Validazione del giorno
            if (day == null) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "Day cannot be null");
                return errorJson.toString(2);
            }

            // Validazione degli orari giornalieri
            if (startHour == null) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "StartHour cannot be null");
                return errorJson.toString(2);
            }
            if (endHour == null) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "EndHour cannot be null");
                return errorJson.toString(2);
            }
            if (!endHour.isAfter(startHour)) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "the endHour must be after the StartHour");
                return errorJson.toString(2);
            }

            // Crea la query per selezionare gli utenti e le loro carriere
            Query query = em.createQuery(
                            "select u, c from User u " +
                                    "join Planner p on p.user.id = u.id " +
                                    "join Career c on c.worker.id = u.id " +
                                    "join Daily d on d.schedule.id = p.schedule.id " +
                                    "join Activityarea aa on aa.worker.id = u.id " +
                                    "where u.type = 'WORKER' " +
                                    "and c.profession.name = :profession " +
                                    "and c.hourlyRate > :hourlyRateMin " +
                                    "and c.hourlyRate < :hourlyRateMax")
                    .setParameter("profession", profession)
                    .setParameter("hourlyRateMin", hourlyRateMin)
                    .setParameter("hourlyRateMax", hourlyRateMax);

            // Esegue la query e ottiene i risultati
            @SuppressWarnings("unchecked")
            List<Object[]> queryResults = query.getResultList();
            List<Result> results = new ArrayList<>();

            // Itera sui risultati della query e controlla le collisioni nell'orario specificato
            for (Object[] row : queryResults) {
                User worker = (User) row[0];
                if (Collision.detect(worker, day, new TimeInterval(startHour, endHour)))
                    continue;
                Career career = (Career) row[1];
                results.add(new Result(worker, career));
            }

            // Se non sono stati trovati risultati, restituisce un messaggio di errore
            if (results.isEmpty()) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "No Results Found");
                return errorJson.toString(2);
            }

            // Ordina i risultati in base alla priorità del lavoratore (in ordine decrescente)
            results.sort(new Comparator<Result>() {
                @Override
                public int compare(Result o1, Result o2) {
                    return (int) (o2.getWorker().getWorker().getPriority() - o1.getWorker().getWorker().getPriority());
                }
            });

            // Crea l'oggetto JSON per l'output
            JSONObject output = new JSONObject();
            JSONObject criteriaJson = new JSONObject();
            criteriaJson.put("scheduleType", criteria.getScheduleType());
            criteriaJson.put("serviceMode", criteria.getServiceMode());
            criteriaJson.put("profession", profession);
            criteriaJson.put("hourlyRateMin", hourlyRateMin);
            criteriaJson.put("hourlyRateMax", hourlyRateMax);
            criteriaJson.put("day", day.toString());
            criteriaJson.put("startHour", startHour.toString());
            criteriaJson.put("endHour", endHour.toString());
            output.put("searchCriteria", criteriaJson);

            JSONArray resultsArray = new JSONArray();
            for (Result result : results) {
                JSONObject resultJson = new JSONObject();
                resultJson.put("user", result.getWorker());
                resultJson.put("career", result.getCareer());
                resultsArray.put(resultJson);
            }

            if(resultsArray.isEmpty()){
                JSONObject errorJson = new JSONObject();
                errorJson.put("results", "No Results Found");
                return errorJson.toString(2);
            }

            output.put("results", resultsArray);

            json = output.toString(2);
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject errorJson = new JSONObject();
            errorJson.put("error", "Server error occurred: " + e.getMessage());
            json = errorJson.toString();
        } finally {
            em.close();
        }
        return json;
    }
}