package is.murmur.Model.Services.SearchStrategy;

import is.murmur.Model.Entities.Alias;
import is.murmur.Model.Entities.AliasId;
import is.murmur.Model.Entities.Registereduser;
import is.murmur.Model.Helpers.Criteria;
import is.murmur.Model.Helpers.JPAUtil;
import is.murmur.Model.Helpers.Result;
import is.murmur.Model.Helpers.TimeInterval;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeeklyRemoteSearchStrategy implements SearchStrategy {

    @Override
    public String search(Criteria criteria) {
        // Questa strategia esegue ricerche settimanali per il servizio REMOTE
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        String json = "";
        try {
            // === Estrazione dei campi comuni ===
            String profession = criteria.getProfession();
            double hourlyRateMin = criteria.getHourlyRateMin();
            double hourlyRateMax = criteria.getHourlyRateMax();
            LocalDate startDate = criteria.getStartDate();
            LocalDate endDate = criteria.getEndDate();

            // === Estrazione dei campi specifici per la ricerca Weekly ===
            // Otteniamo la mappa degli intervalli settimanali (giorno -> TimeInterval)
            Map<String, TimeInterval> weeklyIntervals = criteria.getWeeklyIntervals();
            List<String> dayOfWeekList = new ArrayList<>();
            List<LocalTime> dayStartTimes = new ArrayList<>();
            List<LocalTime> dayEndTimes = new ArrayList<>();
            for (Map.Entry<String, TimeInterval> entry : weeklyIntervals.entrySet()) {
                String day = entry.getKey().toUpperCase();
                TimeInterval interval = entry.getValue();
                dayOfWeekList.add(day);
                dayStartTimes.add(interval.getStart());
                dayEndTimes.add(interval.getEnd());
            }
            if (dayOfWeekList.isEmpty()) {
                throw new IllegalArgumentException("Weekly intervals cannot be empty for a weekly search.");
            }

            // === Costruzione della condizione dinamica per ogni giorno ===
            // Per ogni giorno, viene creato un blocco di condizione da utilizzare nella sottoquery
            StringBuilder dayConditions = new StringBuilder();
            for (int i = 0; i < dayOfWeekList.size(); i++) {
                if (i > 0) {
                    dayConditions.append(" OR ");
                }
                dayConditions.append("(wd.id.dayOfWeek = :dayOfWeek").append(i)
                        .append(" AND d.startHour < :endHour").append(i)
                        .append(" AND d.endHour > :startHour").append(i)
                        .append(")");
            }

            // === Costruzione della query JPQL ===
            String jpql = "SELECT u.id, c.profession, c.hourlyRate, wc.priority, c.seniority " +
                    "FROM Registereduser u " +
                    "JOIN Career c ON u.id = c.worker.id " +
                    "JOIN Workercomponent wc ON u.id = wc.id " +
                    "WHERE u.type = 'WORKER' " +
                    "  AND c.profession = :profession " +
                    "  AND c.hourlyRate BETWEEN :hourlyRateMin AND :hourlyRateMax " +
                    "  AND NOT EXISTS ( " +
                    "       SELECT p FROM Planner p, Weekly w, Weekday wd, Daily d " +
                    "       WHERE p.user.id = u.id " +
                    "         AND p.schedule.id = w.id " +
                    "         AND wd.weekly.id = w.id " +
                    "         AND d.id = wd.daily.id " +
                    "         AND :startDate BETWEEN w.startDate AND w.endDate " +
                    "         AND :endDate BETWEEN w.startDate AND w.endDate " +
                    "         AND (" + dayConditions + ") " +
                    "  )";

            Query query = em.createQuery(jpql);
            query.setParameter("profession", profession);
            query.setParameter("hourlyRateMin", hourlyRateMin);
            query.setParameter("hourlyRateMax", hourlyRateMax);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            for (int i = 0; i < dayOfWeekList.size(); i++) {
                query.setParameter("dayOfWeek" + i, dayOfWeekList.get(i));
                query.setParameter("startHour" + i, dayStartTimes.get(i));
                query.setParameter("endHour" + i, dayEndTimes.get(i));
            }

            @SuppressWarnings("unchecked")
            List<Object[]> queryResults = query.getResultList();
            List<Result> resultsList = new ArrayList<>();
            for (Object[] row : queryResults) {
                Long workerId = (Long) row[0];
                String profVal = (String) row[1];
                Double hrRate = (Double) row[2];
                Double priority = (Double) row[3];
                Integer seniority = (Integer) row[4];
                resultsList.add(new Result(workerId, profVal, hrRate, null ,priority, seniority));
            }

            // === Creazione dei record alias per ciascun worker trovato ===
            em.getTransaction().begin();
            for (Result res : resultsList) {
                Registereduser worker = em.find(Registereduser.class, res.getWorkerId());
                Alias newAlias = new Alias();
                AliasId aliasId = new AliasId();
                aliasId.setUserId(worker.getId());
                aliasId.setId(null); // Valore generato automaticamente (ad esempio SERIAL)
                newAlias.setId(aliasId);
                newAlias.setUser(worker);
                em.persist(newAlias);
                em.flush();
                res.setAliasId(newAlias.getId().getId());
            }
            em.getTransaction().commit();

            // === Costruzione dell'output JSON ===
            JSONObject output = new JSONObject();
            JSONObject criteriaJson = new JSONObject();
            criteriaJson.put("scheduleType", criteria.getScheduleType());
            criteriaJson.put("serviceMode", criteria.getServiceMode());
            criteriaJson.put("profession", profession);
            criteriaJson.put("hourlyRateMin", hourlyRateMin);
            criteriaJson.put("hourlyRateMax", hourlyRateMax);
            criteriaJson.put("startDate", startDate.toString());
            criteriaJson.put("endDate", endDate.toString());
            criteriaJson.put("dayOfWeek", dayOfWeekList);

            JSONArray dayTimes = new JSONArray();
            for (int i = 0; i < dayOfWeekList.size(); i++) {
                JSONObject dayObj = new JSONObject();
                dayObj.put("day", dayOfWeekList.get(i));
                dayObj.put("startHour", dayStartTimes.get(i).toString());
                dayObj.put("endHour", dayEndTimes.get(i).toString());
                dayTimes.put(dayObj);
            }
            criteriaJson.put("dayTimes", dayTimes);
            criteriaJson.put("serviceMode", "REMOTE");
            output.put("searchCriteria", criteriaJson);

            JSONArray resultsArray = new JSONArray();
            for (Result res : resultsList) {
                JSONObject workerJson = new JSONObject();
                workerJson.put("alias", "workerAlias" + res.getAliasId());
                workerJson.put("profession", res.getProfession());
                workerJson.put("hourlyRate", res.getHourlyRate());
                workerJson.put("priority", res.getPriority());
                workerJson.put("seniority", res.getSeniority());
                resultsArray.put(workerJson);
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