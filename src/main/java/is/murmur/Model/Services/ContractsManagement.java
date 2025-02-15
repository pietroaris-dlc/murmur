package is.murmur.Model.Services;

import is.murmur.Model.Beans.*;
import is.murmur.Model.Helpers.Collision;
import is.murmur.Model.Helpers.JPAUtil;
import is.murmur.Model.Helpers.ScheduleHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ContractsManagement {
    public static List<Contract> getContracts(Registereduser user, String status) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            List<Alias> aliases = em.createQuery(
                    "SELECT a FROM Alias a WHERE a.user = :user",
                    Alias.class).setParameter("user", user).getResultList();

            if (aliases.isEmpty()) {
                return new ArrayList<Contract>();
            }

            return em.createQuery(
                            "SELECT c FROM Contract c WHERE c.status = :status AND c.clientAlias IN :aliases",
                            Contract.class)
                    .setParameter("status", status)
                    .setParameter("aliases", aliases)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public static Review getReview(Contract expired){
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            if (!"EXPIRED".equals(expired.getStatus()))
                return null;

            return em.createQuery(
                            "SELECT r FROM Review r WHERE r.contract = :contract",
                            Review.class)
                    .setParameter("contract", expired)
                    .getSingleResult();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static boolean defineOffer(Contract offer, boolean approval){
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            if (!"OFFER".equals(offer.getStatus()))
                return false;

            if (approval){
                Schedule schedule = em.find(Schedule.class, offer.getScheduleId());
                if(
                        Collision.detect(offer.getClientAlias().getUser(), schedule.getId())
                                || Collision.detect(offer.getWorkerAlias().getUser(), schedule.getId())
                ) {
                    transaction.rollback();
                    return false;
                }
                if(schedule.getType().equals("DAILY")){
                    Daily daily = em.find(Daily.class, offer.getScheduleId());
                    ScheduleHandler.addDailyToPlanner(em, offer.getClientAlias().getUser(), daily);
                    ScheduleHandler.addDailyToPlanner(em, offer.getWorkerAlias().getUser(), daily);
                } else if (schedule.getType().equals("WEEKLY")){
                    Weekly weekly = em.find(Weekly.class, offer.getScheduleId());
                    List<Weekday> weekday = em.createQuery(
                            "select wd from Weekday wd where wd.weekly = :weekly"
                            ,Weekday.class
                    )
                            .setParameter("weekly", weekly)
                            .getResultList();
                    for(Weekday wd : weekday){
                        ScheduleHandler.addWeekdayToPlanner(em, offer.getClientAlias().getUser(), wd);
                        ScheduleHandler.addWeekdayToPlanner(em, offer.getWorkerAlias().getUser(), wd);
                    }
                }
                offer.setStatus("ACTIVE");
            } else{
                offer.setStatus("REJECTED");
            }
            em.merge(offer);
            transaction.commit();
            return true;
        } finally {
            em.close();
        }
    }

    public static Cancellationrequest sendCancellationRequest(Contract contract, String description) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            if (!contract.getStatus().equals("ACTIVE")) {
                return null;
            }

            List<Cancellationrequest> existingRequests = em.createQuery(
                    "SELECT cr FROM Cancellationrequest cr WHERE cr.contract = :contract and cr.status = 'PENDING'",
                    Cancellationrequest.class)
                    .setParameter("contract", contract)
                    .getResultList();

            if (!existingRequests.isEmpty()) {
                return null;
            }

            Cancellationrequest cancellationRequest = new Cancellationrequest();
            cancellationRequest.setContract(contract);
            cancellationRequest.setId(contract.getId());
            cancellationRequest.setSubmissionDate(Instant.now());
            cancellationRequest.setDescription(description);
            cancellationRequest.setStatus("PENDING");

            em.persist(cancellationRequest);
            transaction.commit();
            return cancellationRequest;
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

    public static Cancellationrequest getCancellationRequest(Contract contract) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT cr FROM Cancellationrequest cr WHERE cr.contract = :contract",
                            Cancellationrequest.class
                    )
                    .setParameter("contract", contract)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public static boolean defineCancellationRequests(Cancellationrequest request, boolean approval) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            Cancellationrequest managedRequest = em.find(Cancellationrequest.class, request.getId());
            if (managedRequest == null) return false;

            if (approval) {
                managedRequest.setStatus("APPROVED");
                Contract contract = managedRequest.getContract();
                if (contract != null) {
                    contract.setStatus("EXPIRED");
                    Schedule schedule = em.find(Schedule.class, contract.getScheduleId());
                    if (schedule.getType().equals("DAILY")) {
                        Daily daily = em.find(Daily.class, schedule.getId());
                        if (daily.getDay().isBefore(LocalDate.now())) {
                            contract.setStatus("REJECTED");
                            em.merge(contract);
                            em.flush();
                        } else if (daily.getDay().equals(LocalDate.now())) {
                            if (daily.getStartHour().isBefore(LocalTime.now())) {
                                contract.setStatus("REJECTED");
                                em.merge(contract);
                                em.flush();
                            } else if (
                                    daily.getEndHour().isBefore(LocalTime.now())
                                            || daily.getEndHour().equals(LocalTime.now())
                            ) {
                                daily.setEndHour(LocalTime.now());
                                em.merge(daily);
                                em.flush();
                            }
                        }
                    } else if (schedule.getType().equals("WEEKLY")) {
                        Weekly weekly = em.find(Weekly.class, schedule.getId());
                        if (weekly.getStartDate().isBefore(LocalDate.now())) {
                            contract.setStatus("REJECTED");
                            em.merge(contract);
                            em.flush();
                        } else if (
                                weekly.getStartDate().isAfter(LocalDate.now())
                                        && weekly.getEndDate().isBefore(LocalDate.now())
                        ) {
                            LocalDate oldEnd = weekly.getEndDate();
                            weekly.setEndDate(LocalDate.now());
                            em.merge(weekly);

                            List<Weekday> weekdays = em.createQuery(
                                            "select wd from Weekday wd where wd.weekly = :weekly"
                                            , Weekday.class
                                    )
                                    .setParameter("weekly", weekly)
                                    .getResultList();

                            List<Daily> dailyPlannerClient = ScheduleHandler.getDailyPlanner(em, contract.getClientAlias().getUser());
                            List<Daily> dailyPlannerWorker = ScheduleHandler.getDailyPlanner(em, contract.getWorkerAlias().getUser());

                            for (Weekday weekday : weekdays) {
                                for (Daily daily : dailyPlannerClient) {
                                    if (
                                            daily.getDay().getDayOfWeek().equals(weekday.getId().getDayOfWeek())
                                                    && (daily.getDay().isBefore(oldEnd) && daily.getDay().isAfter(weekly.getEndDate()))
                                                    && daily.getStartHour().equals(weekday.getStartHour()) && daily.getEndHour().equals(weekday.getEndHour())
                                    ) {
                                        em.remove(daily.getSchedule());
                                        em.flush();
                                    }
                                }
                            }
                            for (Weekday weekday : weekdays) {
                                for (Daily daily : dailyPlannerWorker) {
                                    if (
                                            daily.getDay().getDayOfWeek().equals(weekday.getId().getDayOfWeek())
                                                    && (daily.getDay().isBefore(oldEnd) && daily.getDay().isAfter(weekly.getEndDate()))
                                                    && daily.getStartHour().equals(weekday.getStartHour()) && daily.getEndHour().equals(weekday.getEndHour())
                                    ) {
                                        em.remove(daily.getSchedule());
                                        em.flush();
                                    }
                                }
                            }
                        } else if (weekly.getEndDate().equals(LocalDate.now())) {
                            return false;
                        }
                    }
                    em.merge(contract);
                    em.flush();
                }
            } else managedRequest.setStatus("REJECTED");

            em.merge(managedRequest);
            transaction.commit();
            return true;
        }
    }

    public static boolean deleteDraft(Contract draft) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            if (!"DRAFT".equals(draft.getStatus())) {
                return false;
            }
            Contract managedContract = em.find(Contract.class, draft.getId());
            if (managedContract != null) {
                em.remove(managedContract);
            }
            transaction.commit();
            return true;
        }
    }

    public static Contract writeDraft(
            Registereduser user,
            String profession,
            BigDecimal hourlyRate,
            Alias workerAlias,
            Long scheduleId,
            BigDecimal totalFee
    ) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            Contract draft = new Contract();
            draft.setWorkerAlias(workerAlias);
            draft.setHourlyRate(hourlyRate);
            draft.setTotalFee(totalFee);
            draft.setProfession(profession);
            draft.setServiceMode("REMOTE");
            draft.setStatus("DRAFT");
            if (Collision.detect(user, scheduleId)) {
                transaction.rollback();
                return null;
            }
            if (Collision.detect(draft.getWorkerAlias().getUser(), scheduleId)) {
                transaction.rollback();
                return null;
            }
            em.persist(draft);
            transaction.commit();
            return draft;
        }
    }

    public static Contract writeDraft(
            Registereduser user,
            String profession,
            BigDecimal hourlyRate,
            Alias workerAlias,
            Long scheduleId,
            BigDecimal totalFee,
            String city,
            String street,
            Short streetNumber,
            String district,
            String region,
            String country
    ) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            Contract draft = new Contract();
            draft.setWorkerAlias(workerAlias);
            draft.setHourlyRate(hourlyRate);
            draft.setTotalFee(totalFee);
            draft.setProfession(profession);
            draft.setServiceMode("REMOTE");
            draft.setStatus("DRAFT");
            if (Collision.detect(user, scheduleId)) {
                transaction.rollback();
                return null;
            }
            if (Collision.detect(draft.getWorkerAlias().getUser(), scheduleId)) {
                transaction.rollback();
                return null;
            }
            em.persist(draft);
            em.flush();

            Notremotecomponent notremotecomponent;
            notremotecomponent = new Notremotecomponent();
            notremotecomponent.setId(draft.getId());
            notremotecomponent.setContract(draft);
            notremotecomponent.setCity(city);
            notremotecomponent.setStreet(street);
            notremotecomponent.setStreetNumber(streetNumber);
            notremotecomponent.setDistrict(district);
            notremotecomponent.setRegion(region);
            notremotecomponent.setCountry(country);
            em.persist(notremotecomponent);

            transaction.commit();
            return draft;
        }
    }

    public static boolean sendOffer(Registereduser user, Contract draft, String specialRequests) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            if (!draft.getStatus().equals("DRAFT")) return false;
            if(Collision.detect(user,draft.getScheduleId())){
                transaction.rollback();
                em.remove(draft);
                em.flush();
                return false;
            }
            if(Collision.detect(draft.getWorkerAlias().getUser(), user.getId())){
                transaction.rollback();
                em.remove(draft);
                em.flush();
                return false;
            }
            draft.setStatus("OFFER");

            Offercomponent offercomponent = new Offercomponent();
            offercomponent.setId(draft.getId());
            offercomponent.setSpecialRequests(specialRequests);
            offercomponent.setContract(draft);
            em.persist(offercomponent);
            em.flush();

            em.merge(draft);
            transaction.commit();
            return true;
        } finally {
            em.close();
        }
    }

}
