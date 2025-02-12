package is.murmur.Model.Services;

import is.murmur.Model.Entities.*;
import is.murmur.Model.Enums.ContractStatus;
import is.murmur.Model.Enums.ScheduleType;
import is.murmur.Model.Enums.ServiceMode;
import is.murmur.Model.Helpers.*;
import is.murmur.Model.Services.SearchStrategy.SearchStrategyFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

public class ClientSide {

    // Metodo principale di ricerca
    public static String search(Criteria criteria) {
        return SearchStrategyFactory.getStrategy(criteria.getScheduleType(), criteria.getServiceMode()).search(criteria);
    }

    public static boolean draftResult(Registereduser user, Result result, Criteria criteria) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            // Creazione e impostazione del contratto
            Contract contract = new Contract();
            contract.setProfession(criteria.getProfession());
            contract.setHourlyRate(BigDecimal.valueOf(result.getHourlyRate()));
            Alias alias = em.find(Alias.class, result.getAliasId());
            contract.setWorkerAlias(alias);

            // Creazione dello schedule (tipo basato sul criterio)
            String scheduleType = criteria.getScheduleType().toLowerCase().replace(" ", "");
            Schedule schedule = createSchedule(em, scheduleType);

            // Variabile per accumulare la durata totale (in secondi)
            BigDecimal totalDurationSeconds = BigDecimal.ZERO;

            if (scheduleType.equals("daily")) {
                totalDurationSeconds = createDailySchedule(em, schedule, criteria, contract);
            } else if (scheduleType.equals("weekly")) {
                totalDurationSeconds = createWeeklySchedule(em, schedule, criteria, contract);
            } else {
                throw new IllegalStateException("Unexpected schedule type: " + criteria.getScheduleType());
            }

            // Calcolo del totale delle ore lavorate e del compenso totale
            BigDecimal totalHours = totalDurationSeconds.divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP);
            contract.setTotalFee(totalHours.multiply(BigDecimal.valueOf(result.getHourlyRate())));
            contract.setStatus(String.valueOf(ContractStatus.DRAFT));

            // Gestione del service mode
            String serviceMode = criteria.getServiceMode().toLowerCase().replace(" ", "");
            handleServiceMode(em, contract, result, criteria, serviceMode);

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static boolean offerResult(Result result, Criteria criteria, String specialRequests) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {

            // Creazione e impostazione del contratto
            Contract contract = new Contract();
            contract.setProfession(criteria.getProfession());
            contract.setHourlyRate(BigDecimal.valueOf(result.getHourlyRate()));
            Alias alias = em.find(Alias.class, result.getAliasId());
            contract.setWorkerAlias(alias);

            // Creazione dello schedule in base al tipo richiesto
            String scheduleType = criteria.getScheduleType().toLowerCase().replace(" ", "");
            Schedule schedule = createSchedule(em, scheduleType);

            // Variabile per accumulare la durata totale (in secondi)
            BigDecimal totalDurationSeconds = BigDecimal.ZERO;
            if (scheduleType.equals("daily")) {
                totalDurationSeconds = createDailySchedule(em, schedule, criteria, contract);
            } else if (scheduleType.equals("weekly")) {
                totalDurationSeconds = createWeeklySchedule(em, schedule, criteria, contract);
            } else {
                throw new IllegalStateException("Unexpected schedule type: " + criteria.getScheduleType());
            }

            // Calcolo del totale delle ore lavorate e del compenso totale
            BigDecimal totalHours = totalDurationSeconds.divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP);
            contract.setTotalFee(totalHours.multiply(BigDecimal.valueOf(result.getHourlyRate())));
            // Impostiamo lo status del contratto a OFFER
            contract.setStatus(String.valueOf(ContractStatus.OFFER));

            // Gestione dell'OfferComponent (opzionale)
            Offercomponent offercomponent = new Offercomponent();
            offercomponent.setId(contract.getId());
            offercomponent.setContract(contract);
            offercomponent.setSpecialRequests(specialRequests);
            em.persist(offercomponent);
            em.flush();

            String serviceMode = criteria.getServiceMode().toLowerCase().replace(" ", "");
            handleServiceMode(em, contract, result, criteria, serviceMode);

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public static boolean offerDraft(Contract draft, String specialRequests) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        draft.setStatus(String.valueOf(ContractStatus.OFFER));
        if (Collision.detect(draft.getClientAlias().getUser(), draft.getScheduleId())
                || Collision.detect(draft.getWorkerAlias().getUser(), draft.getScheduleId())) {
            transaction.rollback();
            return false;
        } else {
            if (draft.getId() == null) {
                em.persist(draft);
            } else {
                em.merge(draft);
            }
            em.flush();

            Offercomponent offercomponent = new Offercomponent();
            offercomponent.setId(draft.getId());
            offercomponent.setContract(draft);
            offercomponent.setSpecialRequests(specialRequests);
            em.persist(offercomponent);
            em.flush();

            em.getTransaction().commit();
            return true;
        }
    }

    public static boolean addSchedule(Registereduser user, Daily newSchedule) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            TypedQuery<Daily> query = em.createQuery(
                    "select d from Planner p join Daily d on p.schedule.id = d.id WHERE p.user.id = :userId",
                    Daily.class
            );
            query.setParameter("userId", user.getId());
            List<Daily> dailyList = query.getResultList();

            for(Daily existing : dailyList){
                if(existing.getDay().equals(newSchedule.getDay())){
                    if(
                            (newSchedule.getStartHour().isBefore(existing.getStartHour())
                                    && newSchedule.getEndHour().isBefore(existing.getStartHour()))
                            || newSchedule.getStartHour().isAfter(existing.getEndHour())
                    ) break; else return false;
                } else break;
            }
            em.persist(newSchedule);
            em.flush();
            Planner planner = new Planner();
            planner.setUser(user);
            planner.setSchedule(newSchedule.getSchedule());
            em.persist(planner);
            em.flush();
            return true;
        } finally {
            em.close();
        }
    }

    public static List<Schedule> getPlanner(Registereduser user) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                            "select p.schedule from Planner p where p.user = :user",
                            Schedule.class
                    ).setParameter("user", user)
                    .getResultList();
        } finally {
            em.close();
        }
    }


    public static boolean doReview(String review, double rating , Contract expired) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        if(expired.getStatus().equals(String.valueOf(ContractStatus.EXPIRED))){
            Review reviewObj = new Review();
            reviewObj.setId(expired.getId());
            reviewObj.setContract(expired);
            reviewObj.setDescription(review);
            reviewObj.setRating((byte) rating);
            em.persist(reviewObj);
            em.flush();
            transaction.commit();
            return true;
        } else {
            transaction.rollback();
            return false;
        }
    }

    // ========================================
    // Metodi privati per incapsulare sequenze di azioni ridondanti
    // ========================================

    /**
     * Crea e persiste uno schedule in base al tipo (daily o weekly).
     */
    private static Schedule createSchedule(EntityManager em, String scheduleType) {
        Schedule schedule = new Schedule();
        if (scheduleType.equals("weekly")) {
            schedule.setType(String.valueOf(ScheduleType.WEEKLY));
        } else {
            schedule.setType(String.valueOf(ScheduleType.DAILY));
        }
        em.persist(schedule);
        em.flush(); // Necessario per generare l'ID dello schedule
        return schedule;
    }

    /**
     * Crea un record Daily per uno schedule di tipo DAILY e ne ritorna la durata in secondi.
     */
    private static BigDecimal createDailySchedule(EntityManager em, Schedule schedule, Criteria criteria, Contract contract) {
        Daily daily = new Daily();
        daily.setSchedule(schedule);
        daily.setDay(criteria.getDay());
        daily.setStartHour(criteria.getDailyStartHour());
        daily.setEndHour(criteria.getDailyEndHour());
        em.persist(daily);
        em.flush();
        contract.setScheduleId(schedule.getId());
        Duration duration = Duration.between(daily.getStartHour(), daily.getEndHour());
        return BigDecimal.valueOf(duration.getSeconds());
    }

    /**
     * Crea un record Weekly e, per ogni giorno specificato negli intervalli settimanali,
     * crea i record Daily settimana per settimana fino a endDate.
     * Ritorna la durata totale in secondi.
     */
    private static BigDecimal createWeeklySchedule(EntityManager em, Schedule schedule, Criteria criteria, Contract contract) {
        Weekly weekly = new Weekly();
        weekly.setSchedule(schedule);
        weekly.setStartDate(criteria.getStartDate());
        weekly.setEndDate(criteria.getEndDate());
        em.persist(weekly);
        em.flush();

        BigDecimal totalDurationSeconds = BigDecimal.ZERO;
        Map<String, TimeInterval> weekdays = criteria.getWeeklyIntervals();
        for (Map.Entry<String, TimeInterval> entry : weekdays.entrySet()) {
            String key = entry.getKey(); // ad es. "MONDAY"
            TimeInterval interval = entry.getValue();
            DayOfWeek targetDay = DayOfWeek.valueOf(key.toUpperCase());
            LocalDate currentDay = criteria.getStartDate().with(TemporalAdjusters.nextOrSame(targetDay));
            while (!currentDay.isAfter(criteria.getEndDate())) {
                Daily dailyWeekday = new Daily();
                dailyWeekday.setSchedule(schedule);
                dailyWeekday.setStartHour(interval.getStart());
                dailyWeekday.setEndHour(interval.getEnd());
                dailyWeekday.setDay(currentDay);
                em.persist(dailyWeekday);
                Duration d = Duration.between(interval.getStart(), interval.getEnd());
                totalDurationSeconds = totalDurationSeconds.add(BigDecimal.valueOf(d.getSeconds()));
                currentDay = currentDay.plusWeeks(1);
            }
            // Associa il giorno settimanale alla schedule
            Weekday weekday = new Weekday();
            weekday.setWeekly(weekly);
            WeekdayId weekdayId = new WeekdayId();
            weekdayId.setDayOfWeek(key);
            weekdayId.setWeeklyId(weekly.getId());
            em.persist(weekdayId);
            em.persist(weekday);
        }
        contract.setScheduleId(schedule.getId());
        return totalDurationSeconds;
    }

    /**
     * Gestisce la logica di persistenza relativa al service mode, sia per i contratti REMOTE,
     * ONSITE o HOMEDELIVERY.
     */
    private static void handleServiceMode(EntityManager em, Contract contract, Result result, Criteria criteria, String serviceMode) {
        switch (serviceMode) {
            case "remote":
                contract.setServiceMode(String.valueOf(ServiceMode.REMOTE));
                em.persist(contract);
                break;
            case "onsite": {
                contract.setServiceMode(String.valueOf(ServiceMode.ONSITE));
                em.persist(contract);
                Notremotecomponent notremotecomponent = getNotremotecomponent(contract, result, criteria);
                em.persist(notremotecomponent);
                break;
            }
            case "homedelivery": {
                contract.setServiceMode(String.valueOf(ServiceMode.HOMEDELIVERY));
                em.persist(contract);
                Notremotecomponent notremotecomponent = getNotremotecomponent(contract, result, criteria);
                em.persist(notremotecomponent);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected service mode: " + criteria.getServiceMode());
        }
    }

    private static Notremotecomponent getNotremotecomponent(Contract contract, Result result, Criteria criteria) {
        Notremotecomponent notremotecomponent = new Notremotecomponent();
        notremotecomponent.setId(contract.getId());
        notremotecomponent.setCity(criteria.getCity());
        notremotecomponent.setStreet(criteria.getStreet());
        notremotecomponent.setStreetNumber(result.getStreetNumber());
        notremotecomponent.setDistrict(criteria.getDistrict());
        notremotecomponent.setRegion(criteria.getRegion());
        notremotecomponent.setCountry(criteria.getCountry());
        return notremotecomponent;
    }
}