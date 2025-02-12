package is.murmur.Model.Services;

import is.murmur.Model.Entities.*;
import is.murmur.Model.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class WorkerSide {

    public static List<Career> getCareers(Registereduser worker) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Career> query = em.createQuery(
                    "SELECT c FROM Career c WHERE c.worker.id = :workerId", Career.class);
            query.setParameter("workerId", worker.getId());
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Application jobApplication(Registereduser user, String docsUrl, String type) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
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

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static boolean addCareer(Registereduser user, Career career) {

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            if (!"Worker".equals(user.getType())) {
                return false;
            }

            career.setWorker(user);

            em.persist(career);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static boolean deleteCareer(Career career) {

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            Career managedCareer = em.find(Career.class, career.getId());
            if (managedCareer == null) {
                return false;
            }

            em.remove(managedCareer);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static List<Activityarea> getActivityArea(Registereduser user) {

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Activityarea> query = em.createQuery(
                    "SELECT a FROM Activityarea a WHERE a.worker = :worker", Activityarea.class);
            query.setParameter("worker", user);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public static Activityarea addToActivityArea(Registereduser user, String[] locationInputs) {

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            if (!"Worker".equals(user.getType())) {
                throw new IllegalStateException("Solo un Worker può aggiungere un'area di attività.");
            }

            Location location = new Location();
            location.setCity(locationInputs[0]);
            location.setStreet(locationInputs[1]);
            location.setStreetNumber(Short.parseShort(locationInputs[2]));
            location.setDistrict(locationInputs[3]);
            location.setRegion(locationInputs[4]);
            location.setCountry(locationInputs[5]);

            Location existingLocation = em.createQuery(
                            "SELECT l FROM Location l WHERE l.city = :city AND l.street = :street AND l.streetNumber = :streetNumber " +
                                    "AND l.district = :district AND l.region = :region AND l.country = :country", Location.class)
                    .setParameter("city", location.getCity())
                    .setParameter("street", location.getStreet())
                    .setParameter("streetNumber", location.getStreetNumber())
                    .setParameter("district", location.getDistrict())
                    .setParameter("region", location.getRegion())
                    .setParameter("country", location.getCountry())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (existingLocation == null) {
                em.persist(location);
            } else {
                location = existingLocation;
            }

            Activityarea activityArea = new Activityarea();
            activityArea.setWorker(user);
            activityArea.setLocation(location);

            em.persist(activityArea);
            transaction.commit();
            return activityArea;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }


    public static boolean deleteFromActivityArea(Registereduser user, Location location) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            TypedQuery<Activityarea> query = em.createQuery(
                    "SELECT a FROM Activityarea a WHERE a.worker = :worker AND a.location = :location", Activityarea.class);
            query.setParameter("worker", user);
            query.setParameter("location", location);

            List<Activityarea> results = query.getResultList();

            if (results.isEmpty()) {
                return false;
            }

            for (Activityarea activityarea : results) {
                em.remove(activityarea);
            }

            transaction.commit();
            return true;

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static Contract signContract(Contract contract) {
        // aggiungere schedule worker e client
        // trovare worker e client con l'alias
        // aggiungi al planner dei due utenti lo schedule del contratto
        // return null in caso di errore

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            Alias clientAlias = contract.getClientAlias();
            Alias workerAlias = contract.getWorkerAlias();

            Registereduser client = em.createQuery("SELECT u FROM Registereduser u WHERE u.id = :clientId", Registereduser.class)
                    .setParameter("clientId", clientAlias.getId())
                    .getSingleResult();

            Registereduser worker = em.createQuery("SELECT u FROM Registereduser u WHERE u.id = :workerId", Registereduser.class)
                    .setParameter("workerId", workerAlias.getId())
                    .getSingleResult();

            Schedule schedule = em.find(Schedule.class, contract.getScheduleId());
            if (schedule == null) {
                return null;
            }

            Planner clientPlanner = new Planner();
            PlannerId clientPlannerId = new PlannerId();
            clientPlannerId.setUserId(client.getId());
            clientPlannerId.setScheduleId(schedule.getId());
            clientPlanner.setId(clientPlannerId);
            clientPlanner.setUser(client);
            clientPlanner.setSchedule(schedule);

            Planner workerPlanner = new Planner();
            PlannerId workerPlannerId = new PlannerId();
            workerPlannerId.setUserId(worker.getId());
            workerPlannerId.setScheduleId(schedule.getId());
            workerPlanner.setId(workerPlannerId);
            workerPlanner.setUser(worker);
            workerPlanner.setSchedule(schedule);

            em.persist(clientPlanner);
            em.persist(workerPlanner);

            em.persist(contract);

            contract.setStatus("ACTIVE");

            transaction.commit();

            return contract;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }


}
