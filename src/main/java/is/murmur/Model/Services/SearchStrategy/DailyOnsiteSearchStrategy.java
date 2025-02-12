package is.murmur.Model.Services.SearchStrategy;

import is.murmur.Model.Entities.Alias;
import is.murmur.Model.Entities.AliasId;
import is.murmur.Model.Entities.Registereduser;
import is.murmur.Model.Helpers.Criteria;
import is.murmur.Model.Helpers.JPAUtil;
import is.murmur.Model.Helpers.Result;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DailyOnsiteSearchStrategy implements SearchStrategy {

    @Override
    public String search(Criteria criteria) {
        // I criteri attesi per Daily Onsite sono:
        // - Campi comuni: profession, hourlyRateMin, hourlyRateMax.
        // - Campi Daily: day, dailyStartHour, dailyEndHour.
        // - Campi Location: city, street, district, region, country.
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        String json = "";
        try {
            // Estrazione dei parametri dalla Criteria
            String profession = criteria.getProfession();
            double hourlyRateMin = criteria.getHourlyRateMin();
            double hourlyRateMax = criteria.getHourlyRateMax();
            LocalDate day = criteria.getDay();
            LocalTime startHour = criteria.getDailyStartHour();
            LocalTime endHour = criteria.getDailyEndHour();
            String dayOfWeek = day.getDayOfWeek().toString();
            String city = criteria.getCity();
            String street = criteria.getStreet();
            String district = criteria.getDistrict();
            String region = criteria.getRegion();
            String country = criteria.getCountry();

            // Costruzione della query JPQL con l'aggiunta di streetNumber
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
                    "       SELECT p FROM Planner p, Daily d " +
                    "       WHERE p.user.id = u.id " +
                    "         AND p.schedule.id = d.id " +
                    "         AND d.day = :day " +
                    "         AND d.startHour < :endHour " +
                    "         AND d.endHour > :startHour " +
                    "  ) " +
                    "  AND NOT EXISTS ( " +
                    "       SELECT p FROM Planner p, Weekly w, Weekday wd, Daily d " +
                    "       WHERE p.user.id = u.id " +
                    "         AND p.schedule.id = w.id " +
                    "         AND wd.weekly.id = w.id " +
                    "         AND d.id = wd.daily.id " +
                    "         AND :day BETWEEN w.startDate AND w.endDate " +
                    "         AND wd.id.dayOfWeek = :dayOfWeek " +
                    "         AND d.startHour < :endHour " +
                    "         AND d.endHour > :startHour " +
                    "  )";
            Query query = em.createQuery(jpql);
            query.setParameter("profession", profession);
            query.setParameter("hourlyRateMin", hourlyRateMin);
            query.setParameter("hourlyRateMax", hourlyRateMax);
            query.setParameter("day", day);
            query.setParameter("startHour", startHour);
            query.setParameter("endHour", endHour);
            query.setParameter("dayOfWeek", dayOfWeek);
            query.setParameter("city", city);
            query.setParameter("street", street);
            query.setParameter("district", district);
            query.setParameter("region", region);
            query.setParameter("country", country);

            @SuppressWarnings("unchecked")
            List<Object[]> queryResults = query.getResultList();
            List<Result> resultsList = new ArrayList<>();
            for (Object[] row : queryResults) {
                Long workerId = (Long) row[0];
                String profVal = (String) row[1];
                Double hrRate = (Double) row[2];
                Double priority = (Double) row[3];
                Integer seniority = (Integer) row[4];
                Short streetNumber = (Short) row[5];  // Estrazione di streetNumber
                resultsList.add(new Result(workerId, profVal, hrRate, streetNumber, priority, seniority));
            }

            // Creazione dei record alias per ciascun worker trovato
            em.getTransaction().begin();
            for (Result res : resultsList) {
                Registereduser worker = em.find(Registereduser.class, res.getWorkerId());
                Alias newAlias = new Alias();
                AliasId aliasId = new AliasId();
                aliasId.setUserId(worker.getId());
                aliasId.setId(null); // Il valore verrà generato automaticamente (SERIAL)
                newAlias.setId(aliasId);
                newAlias.setUser(worker);
                em.persist(newAlias);
                em.flush();
                res.setAliasId(newAlias.getId().getId());
            }
            em.getTransaction().commit();

            // Costruzione dell'output JSON
            JSONObject output = new JSONObject();
            JSONObject criteriaJson = new JSONObject();
            // Impostazione dei parametri di ricerca, con serviceMode "ONSITE"
            criteriaJson.put("scheduleType", "DAILY");
            criteriaJson.put("serviceMode", "ONSITE");
            criteriaJson.put("profession", profession);
            criteriaJson.put("hourlyRateMin", hourlyRateMin);
            criteriaJson.put("hourlyRateMax", hourlyRateMax);
            criteriaJson.put("day", day.toString());
            criteriaJson.put("startHour", startHour.toString());
            criteriaJson.put("endHour", endHour.toString());
            criteriaJson.put("dayOfWeek", dayOfWeek);
            criteriaJson.put("city", city);
            criteriaJson.put("street", street);
            criteriaJson.put("district", district);
            criteriaJson.put("region", region);
            criteriaJson.put("country", country);
            output.put("searchCriteria", criteriaJson);

            JSONArray resultsArray = new JSONArray();
            for (Result res : resultsList) {
                JSONObject workerJson = new JSONObject();
                workerJson.put("alias", "workerAlias" + res.getAliasId());
                workerJson.put("profession", res.getProfession());
                workerJson.put("hourlyRate", res.getHourlyRate());
                workerJson.put("streetNumber", res.getStreetNumber());  // Aggiunto streetNumber nell'output
                workerJson.put("priority", res.getPriority());
                workerJson.put("seniority", res.getSeniority());
                resultsArray.put(workerJson);
            }
            output.put("results", resultsArray);
            json = output.toString(2);
        } catch(Exception e) {
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