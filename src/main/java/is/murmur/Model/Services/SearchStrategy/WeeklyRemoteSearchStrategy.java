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
import java.util.Map;

/**
 * Implementazione della strategia di ricerca remota settimanale.
 * <p>
 * Questa classe implementa l'interfaccia {@link SearchStrategy} e definisce la logica
 * per eseguire una ricerca di lavoratori (worker) che offrono servizi remoti su base settimanale.
 * I criteri di ricerca includono la professione, il range di tariffa oraria, le date di inizio e fine,
 * e gli intervalli orari per ciascun giorno della settimana.
 * </p>
 *
 
 */
public class WeeklyRemoteSearchStrategy implements SearchStrategy {

    /**
     * Esegue la ricerca dei lavoratori in base ai criteri settimanali specificati.
     * <p>
     * Il metodo estrae i parametri di ricerca dai criteri forniti, compresi gli intervalli settimanali.
     * Esegue una query sul database per recuperare utenti di tipo "WORKER" e le loro carriere,
     * filtrando in base alla professione e alla tariffa oraria. Per ogni lavoratore, controlla
     * se ci sono collisioni con gli intervalli orari specificati per ogni giorno compreso tra le date
     * di inizio e fine. Se non viene rilevata alcuna collisione, il lavoratore viene aggiunto ai risultati.
     * I risultati sono ordinati per priorità e infine formattati in un oggetto JSON.
     * </p>
     *
     * @param criteria I criteri di ricerca che includono professione, tariffa oraria, date e intervalli settimanali.
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
            LocalDate endDate = criteria.getEndDate();
            Map<String, TimeInterval> weeklyIntervals = criteria.getWeeklyIntervals();

            // Prepara liste per i giorni della settimana e relativi intervalli (opzionale)
            List<String> dayOfWeekList = new ArrayList<>();
            List<LocalTime> dayOfWeekStartTimes = new ArrayList<>();
            List<LocalTime> dayOfWeekEndTimes = new ArrayList<>();
            for (Map.Entry<String, TimeInterval> entry : weeklyIntervals.entrySet()) {
                String dayOfWeek = entry.getKey().toUpperCase();
                TimeInterval interval = entry.getValue();
                dayOfWeekList.add(dayOfWeek);
                dayOfWeekStartTimes.add(interval.getStart());
                dayOfWeekEndTimes.add(interval.getEnd());
            }
            // Verifica che gli intervalli settimanali non siano vuoti
            if (dayOfWeekList.isEmpty()) {
                throw new IllegalArgumentException("Weekly intervals cannot be empty for a weekly search.");
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
                                    "and c.hourlyRate < :hourlyRateMax ")
                    .setParameter("profession", profession)
                    .setParameter("hourlyRateMin", hourlyRateMin)
                    .setParameter("hourlyRateMax", hourlyRateMax);

            // Esegue la query e ottiene i risultati
            @SuppressWarnings("unchecked")
            List<Object[]> queryResults = query.getResultList();
            List<Result> results = new ArrayList<>();

            // Itera sui risultati della query per controllare le collisioni
            for (Object[] row : queryResults) {
                User worker = (User) row[0];
                Career career = (Career) row[1];

                boolean hasCollision = false;

                // Controlla per ogni giorno compreso tra startDate ed endDate
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

                // Se non ci sono collisioni, aggiunge il lavoratore ai risultati
                if (!hasCollision) {
                    results.add(new Result(worker, career));
                }
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

            // Crea un oggetto JSON per rappresentare gli intervalli settimanali
            JSONObject weeklyIntervalsJson = new JSONObject();
            for (Map.Entry<String, TimeInterval> entry : weeklyIntervals.entrySet()) {
                JSONObject intervalJson = new JSONObject();
                intervalJson.put("start", entry.getValue().getStart().toString());
                intervalJson.put("end", entry.getValue().getEnd().toString());
                weeklyIntervalsJson.put(entry.getKey(), intervalJson);
            }
            criteriaJson.put("weeklyIntervals", weeklyIntervalsJson);
            output.put("searchCriteria", criteriaJson);

            // Crea un array JSON per contenere i risultati della ricerca
            JSONArray resultsArray = new JSONArray();
            for (Result result : results) {
                JSONObject resultJson = new JSONObject();
                resultJson.put("user", result.getWorker());
                resultJson.put("career", result.getCareer());
                resultsArray.put(resultJson);
            }
            output.put("results", resultsArray);

            // Converte l'oggetto JSON in una stringa con indentazione (2 spazi)
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