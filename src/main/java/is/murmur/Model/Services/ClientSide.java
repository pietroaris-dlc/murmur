package is.murmur.Model.Services;

import is.murmur.Model.Beans.*;
import is.murmur.Model.Helpers.*;
import is.murmur.Model.Services.SearchStrategy.SearchStrategyFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Classe che implementa le operazioni lato client.
 * <p>
 * Questa classe fornisce metodi statici per operazioni lato client come la ricerca,
 * la gestione di bozze (draft) di contratti, l'invio di offerte, la revisione dei contratti,
 * il calcolo delle metriche dei lavoratori e la pianificazione degli orari (daily e weekly).
 * </p>
 *
 
 */
public class ClientSide {

    /**
     * Esegue una ricerca utilizzando la strategia appropriata in base ai criteri.
     *
     * @param criteria I criteri di ricerca.
     * @return Una stringa in formato JSON contenente i risultati della ricerca.
     */
    public static String search(Criteria criteria) {
        // Ottiene la strategia corretta dal factory in base al tipo di schedule e modalità di servizio.
        return SearchStrategyFactory.getStrategy(criteria.getScheduleType(), criteria.getServiceMode()).search(criteria);
    }

    /**
     * Salva una bozza (draft) di contratto.
     * <p>
     * Questo metodo persiste il contratto di bozza e le relative entità associate come
     * Daily/Weekly contracts, alias e, se necessario, contratti non remoti.
     * </p>
     *
     * @param user   L'utente client che salva la bozza.
     * @param buffer Il DraftBuffer contenente il draft e le relative informazioni.
     * @return Il contratto di bozza salvato.
     */
    public static Contract saveDraft(User user, DraftBuffer buffer) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        // Estrae il draft e le entità associate dal buffer
        Contract draft = buffer.getDraft();
        User worker = buffer.getWorker();
        Dailycontract dailyContract = null;
        Weeklycontract weeklyContract = null;
        List<Weekdaycontract> weekdayContract = null;
        Notremotecontract notRemoteContract = null;

        // Persiste il draft
        em.persist(draft);
        em.flush();

        // In base al tipo di schedule, recupera il DailyContract o il WeeklyContract
        if (draft.getScheduleType().equals("DAILY")) {
            dailyContract = buffer.getDailyContract();
        } else {
            weeklyContract = buffer.getWeeklyContract();
            weekdayContract = buffer.getWeekdaysContract();
        }
        // Se il servizio non è REMOTE, recupera il Notremotecontract
        if (!draft.getServiceMode().equals("REMOTE")) {
            notRemoteContract = buffer.getNotremotecontract();
        }

        // Crea e persiste gli alias per client e worker
        Alias aliases = new Alias();
        Clientalias clientalias = new Clientalias();
        clientalias.setUser(user);
        em.persist(clientalias);
        em.flush();

        Workeralias workeralias = new Workeralias();
        workeralias.setUser(worker.getWorker());
        em.persist(workeralias);
        em.flush();

        // Imposta gli alias sul contratto
        aliases.setClientAlias(clientalias);
        aliases.setWorkerAlias(workeralias);
        aliases.setContract(draft);
        aliases.setId(draft.getId());
        em.persist(aliases);
        em.flush();

        // Collega i contratti specifici al draft
        if (dailyContract != null) {
            dailyContract.setContract(draft);
        } else if (weeklyContract != null) {
            weeklyContract.setContract(draft);
            em.persist(weeklyContract);
            em.flush();

            // Persiste ogni Weekdaycontract associato al Weeklycontract
            for (Weekdaycontract wdc : weekdayContract) {
                em.persist(wdc);
                em.flush();
            }
        }
        if (notRemoteContract != null) {
            notRemoteContract.setContract(draft);
            em.persist(notRemoteContract);
            em.flush();
        }

        // Associa l'ID del draft al dailyContract e lo persiste
        dailyContract.setId(draft.getId());
        em.persist(dailyContract);
        em.flush();

        transaction.commit();
        return draft;
    }

    /**
     * Genera una bozza (DraftBuffer) a partire dai criteri e dal risultato ottenuto.
     *
     * @param criteria I criteri di ricerca.
     * @param result   Il risultato selezionato.
     * @return Il DraftBuffer contenente le informazioni della bozza.
     */
    public static DraftBuffer draftResult(Criteria criteria, Result result) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            // Scrive la bozza utilizzando il metodo di ContractsManagement
            return ContractsManagement.writeDraft(criteria, result);
        }
    }

    /**
     * Crea e invia un'offerta basata su un DraftBuffer e delle richieste speciali.
     *
     * @param user            L'utente che invia l'offerta.
     * @param criteria        I criteri di ricerca usati per generare il draft.
     * @param result          Il risultato selezionato dalla ricerca.
     * @param specialRequests Richieste speciali per l'offerta.
     * @return Il contratto di offerta creato.
     */
    public static Contract offerResult(User user, Criteria criteria, Result result, String specialRequests) {
        // Genera il draft e quindi invia l'offerta
        return offerDraft(user, draftResult(criteria, result), specialRequests);
    }

    /**
     * Invia un'offerta basata su una bozza (DraftBuffer).
     *
     * @param user            L'utente che invia l'offerta.
     * @param buffer          Il DraftBuffer contenente il draft.
     * @param specialRequests Richieste speciali per l'offerta.
     * @return Il contratto di offerta inviato.
     */
    public static Contract offerDraft(User user, DraftBuffer buffer, String specialRequests) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            // Salva il draft e invia l'offerta tramite il metodo di ContractsManagement
            return ContractsManagement.sendOffer(saveDraft(user, buffer), specialRequests);
        }
    }

    /**
     * Effettua una recensione su un contratto scaduto.
     *
     * @param expired     Il contratto scaduto.
     * @param description La descrizione della recensione.
     * @param rating      Il punteggio della recensione (da 0 a 10).
     * @return L'oggetto {@link Review} creato.
     */
    public static Review doReview(Contract expired, String description, int rating) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            // Crea una nuova recensione e la associa al contratto scaduto
            Review review = new Review();
            review.setDescription(description);
            review.setContract(expired);
            review.setRating((byte) rating);
            review.setId(expired.getId());
            em.persist(review);
            em.flush();
            // Aggiorna le metriche del lavoratore basandosi sul contratto
            murmur(expired.getAlias().getWorkerAlias().getUser().getUser());
            transaction.commit();
            return review;
        } finally {
            em.close();
        }
    }

    /**
     * Calcola e aggiorna le metriche del lavoratore.
     * <p>
     * Questo metodo calcola il tempo di lavoro dell'ultimo mese, la valutazione media
     * e la priorità del lavoratore, aggiornandone anche il profitto totale.
     * </p>
     *
     * @param worker Il lavoratore da aggiornare.
     */
    private static void murmur(User worker) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            double lastMonthWorkTime = 0;
            double averageRating = 0;

            // Calcola la data di un mese fa
            LocalDate lastMonth = LocalDate.now().minusMonths(1);

            // Recupera i contratti scaduti dell'ultimo mese per il lavoratore
            List<Contract> lastMonthContracts = em.createQuery(
                            "select c from Contract c" +
                                    " join Dailycontract dc on dc.contract.id = c.id" +
                                    " join Weeklycontract wc on wc.contract.id = c.id" +
                                    " where c.alias.workerAlias.user.user = :user" +
                                    " and wc.endDate > :lastMonth and dc.day > :lastMonth" +
                                    " and c.status = :expired",
                            Contract.class)
                    .setParameter("user", worker)
                    .setParameter("lastMonth", lastMonth)
                    .setParameter("expired", "EXPIRED")
                    .getResultList();

            // Per ogni contratto, accumula il tempo di lavoro e le valutazioni
            for (Contract contract : lastMonthContracts) {
                if (contract.getScheduleType().equals("DAILY")) {
                    // Calcola la durata del contratto giornaliero in secondi
                    long duration = Duration.between(contract.getDailycontract().getStartHour(), contract.getDailycontract().getEndHour()).toSeconds();
                    lastMonthWorkTime += duration;
                } else {
                    // Per i contratti settimanali, recupera i contratti per ogni giorno
                    List<Weekdaycontract> weekdays = em.createQuery(
                                    "select wdc from Weekdaycontract wdc" +
                                            " join Weeklycontract wc" +
                                            " where wdc.weekly.contract = :contract",
                                    Weekdaycontract.class)
                            .setParameter("contract", contract)
                            .getResultList();
                    for (Weekdaycontract wdc : weekdays) {
                        long duration = Duration.between(wdc.getStartHour(), wdc.getEndHour()).toSeconds();
                        lastMonthWorkTime += duration;
                    }
                }
                // Recupera le recensioni associate al contratto
                List<Review> reviews = em.createQuery(
                                "select r from Review r join Contract c on r.contract.id = c.id",
                                Review.class)
                        .getResultList();
                for (Review review : reviews) {
                    averageRating += review.getRating();
                }
                averageRating /= reviews.size();
            }

            // Aggiorna le informazioni del lavoratore
            Worker workerInfos = worker.getWorker();
            workerInfos.setAverageRating(averageRating);
            workerInfos.setLastMonthWorkTime(lastMonthWorkTime);

            // Calcola la priorità del lavoratore
            workerInfos.setPriority(Math.pow(workerInfos.getAverageRating(), 0.5) / (Math.pow(workerInfos.getLastMonthWorkTime(), 1.2) + 1));
            em.persist(workerInfos);

            // Calcola il profitto totale dei contratti scaduti
            List<Contract> contracts = em.createQuery(
                            "select c from Contract c where c.alias.workerAlias.user.user = :user and c.status = :expired",
                            Contract.class)
                    .setParameter("user", worker)
                    .setParameter("expired", "EXPIRED")
                    .getResultList();

            BigDecimal totalProfit = BigDecimal.ZERO;
            for (Contract contract : contracts) {
                totalProfit = totalProfit.add(contract.getTotalFee());
            }
            workerInfos.setTotalProfit(totalProfit);
            em.merge(workerInfos);
            em.flush();

            transaction.commit();
        }
    }

    /**
     * Aggiunge uno schedule giornaliero per un utente.
     * <p>
     * Verifica che non vi siano collisioni con altri schedule, quindi crea e persiste
     * un nuovo Schedule, Daily, PlannerId e Planner.
     * </p>
     *
     * @param user         L'utente per cui aggiungere lo schedule.
     * @param day          Il giorno in cui impostare lo schedule.
     * @param timeInterval L'intervallo orario per lo schedule.
     * @return {@code true} se lo schedule viene aggiunto con successo, {@code false} altrimenti.
     */
    public static boolean addDailySchedule(User user, LocalDate day, TimeInterval timeInterval) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try (em) {
            transaction.begin();

            // Verifica se esiste una collisione per lo schedule
            if (Collision.detect(user, day, timeInterval)) return false;

            // Crea un nuovo Schedule di tipo DAILY e lo persiste
            Schedule newSchedule = new Schedule();
            newSchedule.setType("DAILY");
            em.persist(newSchedule);
            em.flush();

            // Crea un'entità Daily associata al Schedule
            Daily daily = new Daily();
            daily.setId(newSchedule.getId());
            daily.setSchedule(newSchedule);
            daily.setDay(day);
            daily.setStartHour(timeInterval.getStart());
            daily.setEndHour(timeInterval.getEnd());
            em.persist(daily);
            em.flush();

            // Crea e persiste il PlannerId per associare l'utente allo Schedule
            PlannerId plannerId = new PlannerId();
            plannerId.setScheduleId(newSchedule.getId());
            plannerId.setUserId(user.getId());
            em.persist(plannerId);
            em.flush();

            // Crea il Planner e lo persiste
            Planner planner = new Planner();
            planner.setUser(user);
            planner.setId(plannerId);
            planner.setSchedule(newSchedule);
            em.persist(planner);
            em.flush();

            transaction.commit();
            return true;
        }
    }

    /**
     * Versione sovraccaricata di addDailySchedule che utilizza un EntityManager già esistente.
     *
     * @param em           L'EntityManager da utilizzare.
     * @param user         L'utente per cui aggiungere lo schedule.
     * @param day          Il giorno in cui impostare lo schedule.
     * @param timeInterval L'intervallo orario per lo schedule.
     * @return {@code true} se lo schedule viene aggiunto con successo, {@code false} altrimenti.
     */
    public static boolean addDailySchedule(EntityManager em, User user, LocalDate day, TimeInterval timeInterval) {
        try {
            em.getTransaction().begin();

            // Verifica la collisione con altri schedule
            if (Collision.detect(user, day, timeInterval)) return false;

            // Crea un nuovo Schedule di tipo DAILY
            Schedule newSchedule = new Schedule();
            newSchedule.setType("DAILY");
            em.persist(newSchedule);

            // Crea l'entità Daily e la persiste
            Daily daily = new Daily();
            daily.setId(newSchedule.getId());
            daily.setSchedule(newSchedule);
            daily.setDay(day);
            daily.setStartHour(timeInterval.getStart());
            daily.setEndHour(timeInterval.getEnd());
            em.persist(daily);

            // Crea e persiste il PlannerId
            PlannerId plannerId = new PlannerId();
            plannerId.setScheduleId(newSchedule.getId());
            plannerId.setUserId(user.getId());
            em.persist(plannerId);

            // Crea il Planner e lo persiste
            Planner planner = new Planner();
            planner.setUser(user);
            planner.setId(plannerId);
            planner.setSchedule(newSchedule);
            em.persist(planner);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Aggiunge uno schedule settimanale per un utente.
     * <p>
     * Per ogni giorno compreso tra la data di inizio e fine, verifica se il giorno corrente
     * corrisponde ad uno dei giorni specificati nell'intervallo settimanale e, in tal caso,
     * crea uno schedule giornaliero e successivamente un schedule settimanale con relativi dati.
     * </p>
     *
     * @param user     L'utente per cui aggiungere lo schedule.
     * @param startDate La data di inizio dello schedule settimanale.
     * @param endDate   La data di fine dello schedule settimanale.
     * @param weekdays  Una mappa contenente i giorni della settimana e i relativi intervalli orari.
     * @return {@code true} se lo schedule viene aggiunto con successo, {@code false} altrimenti.
     */
    public static boolean addWeeklySchedule(User user, LocalDate startDate, LocalDate endDate, Map<String, TimeInterval> weekdays) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try (em) {
            transaction.begin();

            // Itera per ogni giorno fra startDate e endDate
            for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
                // Per ogni entry nella mappa dei giorni, controlla se il giorno corrisponde
                for (Map.Entry<String, TimeInterval> entry : weekdays.entrySet()) {
                    if (entry.getKey().equals(date.getDayOfWeek().toString())) {

                        // Aggiunge uno schedule giornaliero per il giorno corrispondente
                        if (!addDailySchedule(em, user, date, entry.getValue())) {
                            transaction.rollback();
                            return false;
                        }

                        // Crea un nuovo Schedule di tipo WEEKLY
                        Schedule newSchedule = new Schedule();
                        newSchedule.setType("WEEKLY");
                        em.persist(newSchedule);

                        // Crea e persiste l'entità Weekly
                        Weekly newWeekly = new Weekly();
                        newWeekly.setId(newSchedule.getId());
                        newWeekly.setSchedule(newSchedule);
                        newWeekly.setStartDate(startDate);
                        newWeekly.setEndDate(endDate);
                        em.persist(newWeekly);

                        // Crea e persiste il PlannerId
                        PlannerId newPlannerId = new PlannerId();
                        newPlannerId.setScheduleId(newSchedule.getId());
                        newPlannerId.setUserId(user.getId());
                        em.persist(newPlannerId);

                        // Crea e persiste il Planner
                        Planner newPlanner = new Planner();
                        newPlanner.setId(newPlannerId);
                        newPlanner.setUser(user);
                        newPlanner.setSchedule(newSchedule);
                        em.persist(newPlanner);

                        // Crea e persiste l'entità Weekday
                        WeekdayId weekdayId = new WeekdayId();
                        weekdayId.setWeeklyId(newWeekly.getId());
                        weekdayId.setDayOfWeek(entry.getKey());
                        em.persist(weekdayId);

                        Weekday weekday = new Weekday();
                        weekday.setId(weekdayId);
                        weekday.setWeekly(newWeekly);
                        weekday.setStartHour(entry.getValue().getStart());
                        weekday.setEndHour(entry.getValue().getEnd());
                        em.persist(weekday);

                        return true;
                    }
                }
            }
            transaction.commit();
            return false;
        }
    }

    /**
     * Versione sovraccaricata di addWeeklySchedule che utilizza un EntityManager già esistente.
     *
     * @param em        L'EntityManager da utilizzare.
     * @param user      L'utente per cui aggiungere lo schedule.
     * @param startDate La data di inizio dello schedule settimanale.
     * @param endDate   La data di fine dello schedule settimanale.
     * @param weekdays  Una mappa contenente i giorni della settimana e i relativi intervalli orari.
     * @return {@code true} se lo schedule viene aggiunto con successo, {@code false} altrimenti.
     */
    public static boolean addWeeklySchedule(EntityManager em, User user, LocalDate startDate, LocalDate endDate, Map<String, TimeInterval> weekdays) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // Itera per ogni giorno fra startDate e endDate
            for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
                // Per ogni entry nella mappa, controlla se il giorno corrisponde
                for (Map.Entry<String, TimeInterval> entry : weekdays.entrySet()) {
                    if (entry.getKey().equals(date.getDayOfWeek().toString())) {

                        // Aggiunge uno schedule giornaliero per il giorno corrente
                        if (!addDailySchedule(em, user, date, entry.getValue())) {
                            transaction.rollback();
                            return false;
                        }

                        // Crea un nuovo Schedule di tipo WEEKLY
                        Schedule newSchedule = new Schedule();
                        newSchedule.setType("WEEKLY");
                        em.persist(newSchedule);

                        // Crea e persiste l'entità Weekly
                        Weekly newWeekly = new Weekly();
                        newWeekly.setId(newSchedule.getId());
                        newWeekly.setSchedule(newSchedule);
                        newWeekly.setStartDate(startDate);
                        newWeekly.setEndDate(endDate);
                        em.persist(newWeekly);

                        // Crea e persiste il PlannerId
                        PlannerId newPlannerId = new PlannerId();
                        newPlannerId.setScheduleId(newSchedule.getId());
                        newPlannerId.setUserId(user.getId());
                        em.persist(newPlannerId);

                        // Crea e persiste il Planner
                        Planner newPlanner = new Planner();
                        newPlanner.setId(newPlannerId);
                        newPlanner.setUser(user);
                        newPlanner.setSchedule(newSchedule);
                        em.persist(newPlanner);

                        // Crea e persiste l'entità Weekday
                        WeekdayId weekdayId = new WeekdayId();
                        weekdayId.setWeeklyId(newWeekly.getId());
                        weekdayId.setDayOfWeek(entry.getKey());
                        em.persist(weekdayId);

                        Weekday weekday = new Weekday();
                        weekday.setId(weekdayId);
                        weekday.setWeekly(newWeekly);
                        weekday.setStartHour(entry.getValue().getStart());
                        weekday.setEndHour(entry.getValue().getEnd());
                        em.persist(weekday);

                        return true;
                    }
                }
            }
            transaction.commit();
            return false;
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }
    public static List<Schedule> getPlanner(User user) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT p.schedule FROM Planner p WHERE p.id.userId = :userId", Schedule.class)
                    .setParameter("userId", user.getId())
                    .getResultList();
        } finally {
            em.close();
        }
    }


}