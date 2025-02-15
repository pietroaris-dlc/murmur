package is.murmur.Model.Services.SearchStrategy;

import is.murmur.Model.Beans.Alias;
import is.murmur.Model.Beans.AliasId;
import is.murmur.Model.Beans.Registereduser;
import is.murmur.Model.Helpers.Criteria;
import is.murmur.Model.Helpers.JPAUtil;
import is.murmur.Model.Helpers.Result;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DailyHomeDeliverySearchStrategy implements SearchStrategy {

    @Override
    public String search(Criteria criteria) {
        // I criteri attesi per Daily HomeDelivery sono:
        // - Campi comuni: profession, hourlyRateMin, hourlyRateMax.
        // - Campi Daily: day, dailyStartHour, dailyEndHour.
        // - Campi Location: city, street, district, region, country.
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        String json = "";
        try {
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

            Query query = em.createQuery(
                    "select u.id, c.profession, c.hourlyRate, wc.priority, c.seniority, aa.location.streetNumber " +
                            "from Registereduser u " +
                            "join Career c on u.id = c.worker.id " +
                            "join Workercomponent wc on u.id = wc.id " +
                            "join Activityarea aa on u.id = aa.worker.id " +
                            "join Location l on aa.location.id = l.id " +
                            "where u.type = 'WORKER' " +
                            "  and c.profession = :profession " +
                            "  and c.hourlyRate between :hourlyRateMin and :hourlyRateMax " +
                            "  and l.city = :city " +
                            "  and l.district = :district " +
                            "  and l.region = :region " +
                            "  and l.country = :country " +
                            "  and l.street = :street " +
                            "  and not exists ( " +
                            "       select p from Planner p " +
                            "       join Daily d on p.schedule = d.schedule"+
                            "       where p.user.id = u.id " +
                            "         and d.day = :day " +
                            "         and d.startHour < :endHour " +
                            "         and d.endHour > :startHour " +
                            "  ) " +
                            "  and not exists ( " +
                            "       select p from Planner p, Weekly w, Weekday wd, Daily d " +
                            "       where p.user.id = u.id " +
                            "         and p.schedule.id = w.id " +
                            "         and wd.weekly.id = w.id " +
                            "         and d.id = wd.weekly.id " +
                            "         and :day between w.startDate and w.endDate " +
                            "         and d.startHour < :endHour " +
                            "         and d.endHour > :startHour " +
                            "  )"
            );
            query.setParameter("profession", criteria.getProfession());
            query.setParameter("hourlyRateMin", criteria.getHourlyRateMin());
            query.setParameter("hourlyRateMax", criteria.getHourlyRateMax());
            query.setParameter("day", criteria.getDay());
            query.setParameter("startHour", criteria.getDailyStartHour());
            query.setParameter("endHour", criteria.getDailyEndHour());
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
                BigDecimal hrRate = (BigDecimal) row[2];
                Double priority = (Double) row[3];
                Integer seniority = (Integer) row[4];
                Short streetNumber = (Short) row[5];  // Estratto anche il valore streetNumber
                resultsList.add(new Result(workerId,null, profVal, hrRate, streetNumber, priority, seniority));
            }

            em.getTransaction().begin();
            for (Result res : resultsList) {
                Registereduser worker = em.find(Registereduser.class, res.getWorkerId());
                Alias newAlias = new Alias();
                AliasId aliasId = new AliasId();
                aliasId.setUserId(worker.getId());
                aliasId.setId(null); // Lascia a null per generare automaticamente il valore (SERIAL)
                newAlias.setId(aliasId);
                newAlias.setUser(worker);
                em.persist(newAlias);
                em.flush();
                res.setWorkerAlias(newAlias);
            }
            em.getTransaction().commit();

            // Costruzione dell'output JSON
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
                workerJson.put("alias", "workerAlias" + res.getWorkerAlias().getId());
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