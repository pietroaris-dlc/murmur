package is.murmur.Model.Services;

import is.murmur.Model.Beans.*;
import is.murmur.Model.Helpers.Collision;
import is.murmur.Model.Helpers.JPAUtil;
import is.murmur.Model.Helpers.ScheduleHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class WorkerSide implements Collision {

    public static List<Career> getCareers(Registereduser worker) {
        try (EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery(
                            "select c from Career c where c.worker.id = :workerId", Career.class)
                    .setParameter("workerId", worker.getId())
                    .getResultList();
        }
    }

    public static Application jobApplication(Registereduser user, String docsUrl, String type) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            Application application = new Application();
            application.setUser(user);
            application.setSubmissionDate(LocalDate.now());
            application.setSubmissionHour(LocalTime.now());
            application.setDocsUrl(docsUrl);
            application.setStatus("PENDING");
            application.setType(type);

            em.persist(application);
            transaction.commit();
            return application;

        }
    }

    public static Career addCareer(Registereduser worker, Profession toAdd, BigDecimal hourlyRate, Integer seniority) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            Profession profession = em.find(Profession.class, toAdd.getName());
            if (profession == null) {
                profession = toAdd;
            }

            Career career = em.createQuery(
                            "select c from Career c where c.worker = :worker and c.profession = :profession",
                            Career.class)
                    .setParameter("worker", worker)
                    .setParameter("profession", profession)
                    .getSingleResult();
            if (career != null) {
                career.setHourlyRate(hourlyRate);
                career.setSeniority(seniority);
                em.persist(career);
                transaction.commit();
                return career;
            } else {
                Career newCareer = new Career();
                newCareer.setWorker(worker);
                newCareer.setProfession(profession);
                newCareer.setHourlyRate(hourlyRate);
                newCareer.setSeniority(seniority);
                em.persist(newCareer);
                transaction.commit();
                return career;
            }
        }
    }

    public static boolean deleteCareer(Career career) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Career managedCareer = em.find(Career.class, career.getId());
            if (managedCareer == null) {
                transaction.rollback();
                return false;
            }
            em.remove(managedCareer);
            transaction.commit();
            return true;
        } finally {
            em.close();
        }
    }

    public static List<Activityarea> getActivityArea(Registereduser user) {
        try (EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery(
                            "SELECT a FROM Activityarea a WHERE a.worker = :worker", Activityarea.class)
                    .setParameter("worker", user)
                    .getResultList();
        }
    }

    public static Activityarea addToActivityArea(
            Registereduser user,
            String city,
            String street,
            Short streetNumber,
            String district,
            String region,
            String country
    ) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            if (!user.getType().equals("WORKER")) {
                transaction.rollback();
                return null;
            }

            Location location = em.createQuery(
                    "select l from Location l where l.city = :city and l.street = :street and l.streetNumber = :streetNumber " +
                            "and l.district = :district and l.region = :region and l.country = :country",
                    Location.class)
                    .setParameter("city", city)
                    .setParameter("street", street)
                    .setParameter("streetNumber", streetNumber)
                    .setParameter("district", district)
                    .setParameter("region", region)
                    .setParameter("country", country)
                    .getSingleResult();

            if (location == null) {
                location = new Location();
                location.setCity(city);
                location.setStreet(street);
                location.setStreetNumber(streetNumber);
                location.setDistrict(district);
                location.setRegion(region);
                location.setCountry(country);
                em.persist(location);
                em.flush();
            }
            Activityarea activityArea = new Activityarea();
            activityArea.setWorker(user);
            activityArea.setLocation(location);
            em.persist(activityArea);
            transaction.commit();
            return activityArea;
        } finally {
            em.close();
        }
    }

    public static boolean deleteFromActivityArea(Registereduser user, Location location) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            Activityarea activityarea = em.createQuery(
                            "SELECT a FROM Activityarea a WHERE a.worker = :worker AND a.location = :location",
                            Activityarea.class)
                    .setParameter("worker", user)
                    .setParameter("location", location)
                    .getSingleResult();

            if (activityarea == null) {
                transaction.rollback();
                return false;
            }
            em.remove(activityarea);
            transaction.commit();
            return true;
        }
    }

    public static Contract signContract(Contract contract) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try (em) {
            transaction.begin();

            Alias clientAlias = contract.getClientAlias();
            Alias workerAlias = contract.getWorkerAlias();

            if (clientAlias.getUser() == null || workerAlias.getUser() == null) {
                transaction.rollback();
                return null;
            }

            Schedule schedule = em.find(Schedule.class, contract.getScheduleId());
            if (schedule == null) {
                transaction.rollback();
                return null;
            }

            if(schedule.getType().equals("DAILY")){
                Daily daily = em.find(Daily.class, contract.getScheduleId());
                if(daily == null){
                    transaction.rollback();
                    contract.setStatus("REJECTED");
                    em.merge(contract);
                    em.flush();
                    return null;
                }
                if(ScheduleHandler.addDailyToPlanner(em,  clientAlias.getUser(), daily) == null || ScheduleHandler.addDailyToPlanner(em,  workerAlias.getUser(), daily) == null){
                    transaction.rollback();
                    contract.setStatus("REJECTED");
                    em.merge(contract);
                    em.flush();
                    return null;
                }
            } else if(schedule.getType().equals("WEEKLY")){
                Weekly weekly = em.find(Weekly.class, contract.getScheduleId());
                List<Weekday> weekdays = em.createQuery(
                        "select wd from Weekday wd where wd.weekly = :weekly",
                        Weekday.class)
                        .setParameter("weekly", weekly)
                        .getResultList();
                for(Weekday wd : weekdays){
                    if(ScheduleHandler.addWeekdayToPlanner(em,  clientAlias.getUser(), wd) == null || ScheduleHandler.addWeekdayToPlanner(em,  workerAlias.getUser(), wd) == null){
                        transaction.rollback();
                        contract.setStatus("REJECTED");
                        em.merge(contract);
                        em.flush();
                        return null;
                    }
                }
            }
            em.persist(contract);
            transaction.commit();
            return contract;
        } finally {
            em.close();
        }
    }
}