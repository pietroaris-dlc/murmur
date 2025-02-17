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
 * Implementazione della strategia di ricerca onsite giornaliera.
 * <p>
 * Questa classe implementa l'interfaccia {@link SearchStrategy} e definisce la logica
 * per eseguire una ricerca di lavoratori (worker) che offrono il servizio onsite,
 * filtrando in base a criteri quali la professione, il range di tariffa oraria,
 * l'orario specificato e la localizzazione.
 * </p>
 *
 
 */
public class DailyOnsiteSearchStrategy implements SearchStrategy {

    /**
     * Esegue la ricerca dei lavoratori in base ai criteri specificati per il servizio onsite.
     * <p>
     * Il metodo esegue una query sul database utilizzando JPA per recuperare
     * utenti di tipo "WORKER" insieme alle loro informazioni di carriera e l'indirizzo,
     * filtrando in base alla professione, alla tariffa oraria e alla localizzazione.
     * Successivamente, verifica la disponibilità oraria del lavoratore e ordina i risultati
     * in base alla priorità. I risultati vengono quindi formattati in un oggetto JSON.
     * </p>
     *
     * @param criteria I criteri di ricerca che includono informazioni quali la professione,
     *                 la tariffa oraria minima e massima, il giorno, l'intervallo orario e
     *                 i dettagli della localizzazione (città, via, distretto, regione e paese).
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
            String city = criteria.getCity();
            String street = criteria.getStreet();
            String district = criteria.getDistrict();
            String region = criteria.getRegion();
            String country = criteria.getCountry();

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
                    .setParameter("profession", criteria.getProfession())
                    .setParameter("hourlyRateMin", criteria.getHourlyRateMin())
                    .setParameter("hourlyRateMax", criteria.getHourlyRateMax())
                    .setParameter("city", criteria.getCity())
                    .setParameter("street", criteria.getStreet())
                    .setParameter("district", criteria.getDistrict())
                    .setParameter("region", criteria.getRegion())
                    .setParameter("country", criteria.getCountry());

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
                Short streetNumber = (Short) row[2];
                // Aggiunge il risultato alla lista
                results.add(new Result(worker, career, streetNumber));
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
            criteriaJson.put("city", city);
            criteriaJson.put("street", street);
            criteriaJson.put("district", district);
            criteriaJson.put("region", region);
            criteriaJson.put("country", country);

            // Aggiunge i criteri di ricerca all'output
            output.put("searchCriteria", criteriaJson);

            // Crea l'array JSON per contenere i risultati
            JSONArray resultsArray = new JSONArray();
            for (Result result : results) {
                JSONObject resultJson = new JSONObject();
                resultJson.put("user", result.getWorker());
                resultJson.put("career", result.getCareer());
                resultJson.put("streetNumber", result.getStreetNumber());
                resultsArray.put(resultJson);
            }
            // Aggiunge l'array dei risultati all'output
            output.put("results", resultsArray);

            // Converte l'oggetto JSON in stringa con indentazione (2 spazi)
            json = output.toString(2);
        } catch(Exception e) {
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