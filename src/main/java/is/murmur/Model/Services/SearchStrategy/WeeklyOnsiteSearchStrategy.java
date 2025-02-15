package is.murmur.Model.Services.SearchStrategy;

import is.murmur.Model.Beans.Alias;
import is.murmur.Model.Beans.AliasId;
import is.murmur.Model.Beans.Registereduser;
import is.murmur.Model.Helpers.Criteria;
import is.murmur.Model.Helpers.JPAUtil;
import is.murmur.Model.Helpers.Result;
import is.murmur.Model.Helpers.TimeInterval;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeeklyOnsiteSearchStrategy implements SearchStrategy {

    @Override
    public String search(Criteria criteria) {
        // Questa strategia esegue ricerche settimanali per il servizio ONSITE
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        String json = "";
        try {
            // === Estrazione dei campi comuni ===
            String profession = criteria.getProfession();
            double hourlyRateMin = criteria.getHourlyRateMin();
            double hourlyRateMax = criteria.getHourlyRateMax();

            // === Estrazione dei campi specifici per la ricerca Weekly ===
            LocalDate startDate = criteria.getStartDate();
            LocalDate endDate = criteria.getEndDate();
            Map<String, TimeInterval> weeklyIntervals = criteria.getWeeklyIntervals();

            // Costruzione della lista dei giorni della settimana (in uppercase)
            List<String> dayOfWeekList = new ArrayList<>();
            for (String day : weeklyIntervals.keySet()) {
                dayOfWeekList.add(day.toUpperCase());
            }
            if (dayOfWeekList.isEmpty()) {
                throw new IllegalArgumentException("Weekly intervals cannot be empty for a weekly search.");
            }
            // Per il controllo nella query si utilizza un intervallo rappresentativo,
            // preso dal primo giorno disponibile nella mappa.
            String representativeDay = dayOfWeekList.get(0);
            TimeInterval representativeInterval = weeklyIntervals.get(representativeDay);
            LocalTime startHour = representativeInterval.getStart();
            LocalTime endHour = representativeInterval.getEnd();

            // === Estrazione dei campi relativi alla location ===
            String city = criteria.getCity();
            String street = criteria.getStreet();
            String district = criteria.getDistrict();
            String region = criteria.getRegion();
            String country = criteria.getCountry();

            // === Costruzione della query JPQL con streetNumber ===
            String jpql = "SELECT u.id, c.profession, c.hourlyRate, wc.priority, c.seniority, aa.location.streetNumber " +
                    "FROM Registereduser u " +
                    "JOIN Career c ON u.id = c.worker.id " +
                    "JOIN Workercomponent wc ON u.id = wc.id " +
                    "JOIN Activityarea aa ON u.id = aa.worker.id " +
                    "JOIN Location l ON aa.location.id = l.id " +
                    "WHERE u.type = 'WORKER' " +
                    "  AND c.profession = :profession " +
                    "  AND c.hourlyRate BETWEEN :hourlyRateMin AND :hourlyRateMax " +
                    "  AND l.city = :city " +
                    "  AND l.district = :district " +
                    "  AND l.region = :region " +
                    "  AND l.country = :country " +
                    "  AND (:street = '' OR l.street = :street) " +
                    "  AND NOT EXISTS ( " +
                    "       SELECT p FROM Planner p, Weekly w, Weekday wd, Daily d " +
                    "       WHERE p.user.id = u.id " +
                    "         AND p.schedule.id = w.id " +
                    "         AND wd.weekly.id = w.id " +
                    "         AND :startDate BETWEEN w.startDate AND w.endDate " +
                    "         AND :endDate BETWEEN w.startDate AND w.endDate " +
                    "         AND wd.id.dayOfWeek IN :dayOfWeekList " +
                    "         AND d.startHour < :endHour " +
                    "         AND d.endHour > :startHour " +
                    "  )";
            Query query = em.createQuery(jpql);
            query.setParameter("profession", profession);
            query.setParameter("hourlyRateMin", hourlyRateMin);
            query.setParameter("hourlyRateMax", hourlyRateMax);
            query.setParameter("city", city);
            query.setParameter("street", street);
            query.setParameter("district", district);
            query.setParameter("region", region);
            query.setParameter("country", country);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            query.setParameter("dayOfWeekList", dayOfWeekList);
            query.setParameter("startHour", startHour);
            query.setParameter("endHour", endHour);

            @SuppressWarnings("unchecked")
            List<Object[]> queryResults = query.getResultList();
            List<Result> resultsList = new ArrayList<>();
            for (Object[] row : queryResults) {
                Long workerId = (Long) row[0];
                String profVal = (String) row[1];
                BigDecimal hrRate = (BigDecimal) row[2];
                Double priority = (Double) row[3];
                Integer seniority = (Integer) row[4];
                Short streetNumber = (Short) row[5];  // Estrazione di streetNumber
                resultsList.add(new Result(workerId, null,profVal, hrRate, streetNumber, priority, seniority));
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
                res.setWorkerAlias(newAlias);
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
            // Per ciascun giorno della settimana, se è presente un intervallo specifico lo usa,
            // altrimenti usa quello rappresentativo.
            for (String day : dayOfWeekList) {
                TimeInterval interval = weeklyIntervals.getOrDefault(day, representativeInterval);
                JSONObject dayObj = new JSONObject();
                dayObj.put("day", day);
                dayObj.put("startHour", interval.getStart().toString());
                dayObj.put("endHour", interval.getEnd().toString());
                dayTimes.put(dayObj);
            }
            criteriaJson.put("dayTimes", dayTimes);
            criteriaJson.put("serviceMode", "ONSITE");
            criteriaJson.put("city", city);
            criteriaJson.put("street", street);
            criteriaJson.put("district", district);
            criteriaJson.put("region", region);
            criteriaJson.put("country", country);
            output.put("searchCriteria", criteriaJson);

            JSONArray resultsArray = new JSONArray();
            for (Result res : resultsList) {
                JSONObject workerJson = new JSONObject();
                workerJson.put("alias", "workerAlias" + res.getWorkerAlias().getId());
                workerJson.put("profession", res.getProfession());
                workerJson.put("hourlyRate", res.getHourlyRate());
                workerJson.put("streetNumber", res.getStreetNumber()); // Aggiunto streetNumber nell'output
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