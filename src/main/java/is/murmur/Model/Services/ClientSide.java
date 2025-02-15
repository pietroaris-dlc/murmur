package is.murmur.Model.Services;

import is.murmur.Model.Beans.*;
import is.murmur.Model.Helpers.*;
import is.murmur.Model.Services.SearchStrategy.SearchStrategyFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static is.murmur.Model.Helpers.ScheduleHandler.*;

public class ClientSide{

    public static String search(Criteria criteria) {
        return SearchStrategyFactory.getStrategy(criteria.getScheduleType(), criteria.getServiceMode()).search(criteria);
    }

    public static Contract saveDraft(Registereduser user, Contract draft) {
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Alias clientAlias = new Alias();
        clientAlias.setUser(user);
        entityManager.persist(clientAlias);
        entityManager.flush();
        draft.setClientAlias(clientAlias);
        entityManager.persist(draft);
        transaction.commit();
        entityManager.close();
        return draft;
    }

    public static Contract draftResult(Registereduser user, Result result, Criteria criteria) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            String scheduleType = criteria.getScheduleType();
            if(!criteria.getServiceMode().equals("REMOTE")){
                if(scheduleType.equals("DAILY")){
                    Daily daily = ScheduleHandler.saveDaily(
                            em, user,
                            ScheduleHandler.createDaily(
                                    criteria.getDay(),
                                    new TimeInterval(criteria.getDailyStartHour(), criteria.getDailyEndHour())
                            )
                    );
                    assert daily != null;
                    Contract draft = ContractsManagement.writeDraft(
                            user,
                            result.getProfession(),
                            result.getHourlyRate(),
                            result.getWorkerAlias(),
                            daily.getId(),
                            BigDecimal.ZERO
                    );
                    if(draft == null) {
                        em.getTransaction().rollback();
                        return null;
                    } else{
                        transaction.commit();
                        return draft;
                    }
                } else {
                    Weekly weekly = ScheduleHandler.saveWeekly(
                            em, user, ScheduleHandler.createWeekly(
                                    criteria.getStartDate(),
                                    criteria.getEndDate()
                            )
                    );
                    List<Weekday> weekdays = ScheduleHandler.saveWeekdays(
                            em, user,
                            createWeekdays(
                                    weekly,
                                    criteria.getWeeklyIntervals()
                            )
                    );
                    Contract draft = ContractsManagement.writeDraft(
                            user,
                            result.getProfession(),
                            result.getHourlyRate(),
                            result.getWorkerAlias(),
                            weekly.getId(),
                            BigDecimal.ZERO
                    );
                    if(draft== null) {
                        em.getTransaction().rollback();
                        return null;
                    } else{
                        transaction.commit();
                        return draft;
                    }
                }
            } else {
                if(scheduleType.equals("DAILY")){
                    Daily daily = ScheduleHandler.saveDaily(
                            em, user,
                            ScheduleHandler.createDaily(
                                    criteria.getDay(),
                                    new TimeInterval(criteria.getDailyStartHour(), criteria.getDailyEndHour())
                            )
                    );
                    assert daily != null;
                    Contract draft = ContractsManagement.writeDraft(
                            user,
                            result.getProfession(),
                            result.getHourlyRate(),
                            result.getWorkerAlias(),
                            daily.getId(),
                            BigDecimal.ZERO,
                            criteria.getCity(),
                            criteria.getStreet(),
                            result.getStreetNumber(),
                            criteria.getDistrict(),
                            criteria.getRegion(),
                            criteria.getCountry()
                    );
                    if(draft == null) {
                        em.getTransaction().rollback();
                        return null;
                    } else {
                        transaction.commit();
                        return draft;
                    }
                } else {
                    Weekly weekly = ScheduleHandler.saveWeekly(
                            em, user, ScheduleHandler.createWeekly(
                                    criteria.getStartDate(),
                                    criteria.getEndDate()
                            )
                    );
                    List<Weekday> weekdays = ScheduleHandler.saveWeekdays(
                            em, user,
                            createWeekdays(
                                    weekly,
                                    criteria.getWeeklyIntervals()
                            )
                    );
                    Contract draft = ContractsManagement.writeDraft(
                            user,
                            result.getProfession(),
                            result.getHourlyRate(),
                            result.getWorkerAlias(),
                            weekly.getId(),
                            BigDecimal.ZERO,
                            criteria.getCity(),
                            criteria.getStreet(),
                            result.getStreetNumber(),
                            criteria.getDistrict(),
                            criteria.getRegion(),
                            criteria.getCountry()
                    );
                    if(draft == null) {
                        em.getTransaction().rollback();
                        return null;
                    } else{
                        transaction.commit();
                        return draft;
                    }
                }
            }
        } finally {
            em.close();
        }
    }

    public static Contract offerResult(Registereduser user, Result result, Criteria criteria, String specialRequests) {
        return offerDraft(user, draftResult(user, result, criteria), specialRequests);
    }

    public static Contract offerDraft(Registereduser user, Contract draft, String specialRequests) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            if (draft == null) {
                return null;
            } else if (draft.getId() == null) {
                draft = saveDraft(user, draft);
            }
            if (Collision.detect(user, draft.getScheduleId())) {
                em.remove(draft);
                em.flush();
                return null;
            }
            if (Collision.detect(draft.getWorkerAlias().getUser(), draft.getScheduleId())) {
                em.remove(draft);
                em.flush();
                return null;
            }
            draft.setStatus("OFFER");
            em.merge(draft);
            em.flush();
            Offercomponent offercomponent = new Offercomponent();
            offercomponent.setId(draft.getId());
            offercomponent.setContract(draft);
            offercomponent.setSpecialRequests(specialRequests);
            em.persist(offercomponent);
            transaction.commit();
            return draft;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Review doReview(Contract expired, String description, int rating) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Review review = new Review();
            review.setDescription(description);
            review.setContract(expired);
            review.setRating((byte) rating);
            review.setId(expired.getId());
            em.persist(review);
            em.flush();
            murmur(expired.getWorkerAlias().getUser());
            transaction.commit();
            return review;
        } finally {
            em.close();
        }
    }

    private static void murmur(Registereduser worker) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            double lastMonthWorkMinutes = 0.0;
            double averageRating = 0.0;
            LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
            LocalDate lastMonthDate = lastMonth.toLocalDate();
            LocalTime lastMonthTime = lastMonth.toLocalTime();
            List<Daily> dailies = em.createQuery("select d from Contract e join Daily d on e.scheduleId = d.schedule.id where e.status = :expired and d.day > :lastMonth and d.endHour > :lastMonthTime and e.workerAlias.user = :worker", Daily.class)
                    .setParameter("expired", "EXPIRED")
                    .setParameter("lastMonth", lastMonthDate)
                    .setParameter("lastMonthTime", lastMonthTime)
                    .setParameter("worker", worker)
                    .getResultList();
            for (Daily daily : dailies) {
                Duration duration = Duration.between(daily.getStartHour(), daily.getEndHour());
                lastMonthWorkMinutes += duration.toMinutes();
            }
            List<Review> reviews = em.createQuery("select r from Review r join Contract e on r.contract = e where e.workerAlias.user = :worker", Review.class)
                    .setParameter("worker", worker)
                    .getResultList();
            for (Review review : reviews) {
                averageRating += review.getRating();
            }
            if (!reviews.isEmpty()) {
                averageRating /= reviews.size();
            } else {
                averageRating = 0.0;
            }
            Workercomponent workercomponent = em.find(Workercomponent.class, worker.getId());
            workercomponent.setAverageRating(averageRating);
            int workDays = (int) (lastMonthWorkMinutes / 1440.0);
            workercomponent.setLastMonthWorkdays(workDays);
            double scaleFactor = 1_000_000.0;
            double priority = (averageRating * scaleFactor) / Math.pow(lastMonthWorkMinutes + 1, 2);
            workercomponent.setPriority(priority);
            transaction.commit();
        } finally {
            em.close();
        }
    }
}