package is.murmur.Model.Services.SearchStrategy;

import is.murmur.Model.Beans.*;
import is.murmur.Model.Helpers.*;
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
 *
 
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
        // Ottiene l'EntityManager per interagire con il database
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
                                    "and c.hourlyRate < :hourlyRateMax ")
                    .setParameter("profession", criteria.getProfession())
                    .setParameter("hourlyRateMin", criteria.getHourlyRateMin())
                    .setParameter("hourlyRateMax", criteria.getHourlyRateMax());

            // Esegue la query e ottiene i risultati
            @SuppressWarnings("unchecked")
            List<Object[]> queryResults = query.getResultList();
            List<Result> results = new ArrayList<>();

            // Itera sui risultati della query
            for (Object[] row : queryResults) {
                User worker = (User) row[0];
                // Se viene rilevata una collisione nell'orario specificato, salta questo worker
                if (Collision.detect(worker, criteria.getDay(), new TimeInterval(criteria.getDailyStartHour(), criteria.getDailyEndHour())))
                    continue;
                Career career = (Career) row[1];
                // Aggiunge il risultato alla lista
                results.add(new Result(worker, career));
            }

            // Ordina i risultati in base alla priorità del lavoratore in ordine decrescente
            results.sort(new Comparator<Result>() {
                @Override
                public int compare(Result o1, Result o2) {
                    return (int) (o2.getWorker().getWorker().getPriority() - o1.getWorker().getWorker().getPriority());
                }
            });

            // Crea l'oggetto JSON per l'output
            JSONObject output = new JSONObject();

            // Crea e popola l'oggetto JSON con i criteri di ricerca
            JSONObject criteriaJson = new JSONObject();
            criteriaJson.put("scheduleType", criteria.getScheduleType());
            criteriaJson.put("serviceMode", criteria.getServiceMode());
            criteriaJson.put("profession", profession);
            criteriaJson.put("hourlyRateMin", hourlyRateMin);
            criteriaJson.put("hourlyRateMax", hourlyRateMax);
            criteriaJson.put("day", day.toString());
            criteriaJson.put("startHour", startHour.toString());
            criteriaJson.put("endHour", endHour.toString());

            // Aggiunge i criteri di ricerca all'output
            output.put("searchCriteria", criteriaJson);

            // Crea l'array JSON per contenere i risultati
            JSONArray resultsArray = new JSONArray();
            for (Result result : results) {
                JSONObject resultJson = new JSONObject();
                resultJson.put("user", result.getWorker());
                resultJson.put("career", result.getCareer());
                resultsArray.put(resultJson);
            }
            // Aggiunge l'array dei risultati all'output
            output.put("results", resultsArray);

            // Converte l'oggetto JSON in stringa con indentazione (2 spazi)
            json = output.toString(2);
        } catch (Exception e) {
            // Stampa lo stack trace in caso di eccezione
            e.printStackTrace();
            // Crea un oggetto JSON per rappresentare l'errore
            JSONObject errorJson = new JSONObject();
            errorJson.put("error", "Server error occurred: " + e.getMessage());
            json = errorJson.toString();
        } finally {
            // Chiude l'EntityManager per liberare le risorse
            em.close();
        }
        return json;
    }
}