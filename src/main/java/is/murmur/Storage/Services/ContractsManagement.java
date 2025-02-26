package is.murmur.Storage.Services;

import is.murmur.Storage.DAO.*;
import is.murmur.Storage.Helpers.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * La classe {@code ContractsManagement} gestisce le operazioni relative ai contratti,
 * come il recupero, la definizione di offerte, la scrittura di bozze e l'invio di offerte.
 * <p>
 * Include metodi per:
 * <ul>
 *   <li>Recuperare contratti in base a utente e stato.</li>
 *   <li>Definire (approvare o rifiutare) un'offerta.</li>
 *   <li>Eliminare una bozza.</li>
 *   <li>Costruire una bozza (DraftBuffer) in base ai criteri e ad un risultato di ricerca.</li>
 *   <li>Inviare un'offerta basata su una bozza.</li>
 * </ul>
 * </p>
 *
 
 */
public class ContractsManagement {

    /**
     * Recupera una lista di contratti in base all'utente e allo stato specificato.
     *
     * @param user   L'utente per cui recuperare i contratti.
     * @param status Lo stato del contratto (es. "DRAFT", "OFFER", "EXPIRED").
     * @return Una lista di {@link Contract} che corrispondono ai criteri di ricerca.
     */
    public static List<Contract> getContracts(User user, String status) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            // Esegue una query per recuperare i contratti associati all'utente come client o worker
            return em.createQuery(
                            "select c from Contract c" +
                                    " join Alias a on a.contract = c" +
                                    " where c.status = :status" +
                                    " and (a.workerAlias.user = :user or a.clientAlias.user = :user)",
                            Contract.class)
                    .setParameter("status", status)
                    .setParameter("user", user)
                    .getResultList();
        } catch(Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Definisce un'offerta a partire da un contratto in stato "OFFER".
     * <p>
     * Se il parametro {@code approval} è {@code true}, viene creato un nuovo schedule
     * (daily o weekly) associato al contratto, vengono creati i Planner per client e worker,
     * e lo stato del contratto viene aggiornato. Se {@code approval} è {@code false},
     * il contratto viene rimosso.
     * </p>
     *
     * @param offer    Il contratto in stato "OFFER" da definire.
     * @param approval {@code true} per approvare l'offerta, {@code false} per rifiutarla.
     * @return {@code true} se l'operazione ha successo, {@code false} altrimenti.
     */
    public static boolean defineOffer(Contract offer, boolean approval) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            // Verifica che il contratto sia in stato OFFER
            if (!offer.getStatus().equals("OFFER")) {
                throw new Exception("Il contratto non è in stato OFFER");
            }
            if (approval) {
                // Crea un nuovo Schedule per il contratto
                Schedule newSchedule = new Schedule();
                newSchedule.setType(offer.getScheduleType());
                em.persist(newSchedule);
                em.flush();

                // Gestisce il caso DAILY
                if (offer.getScheduleType().equals("DAILY")) {
                    Daily newDaily = new Daily();
                    newDaily.setId(newSchedule.getId());
                    newDaily.setSchedule(newSchedule);
                    newDaily.setDay(offer.getDailycontract().getDay());
                    newDaily.setStartHour(offer.getDailycontract().getStartHour());
                    newDaily.setEndHour(offer.getDailycontract().getEndHour());
                    em.persist(newDaily);
                    em.flush();
                } else if (offer.getScheduleType().equals("WEEKLY")) {
                    // Gestisce il caso WEEKLY
                    Weekly newWeekly = new Weekly();
                    newWeekly.setId(newSchedule.getId());
                    newWeekly.setSchedule(newSchedule);
                    newWeekly.setStartDate(offer.getWeeklycontract().getStartDate());
                    newWeekly.setEndDate(offer.getWeeklycontract().getEndDate());
                    em.persist(newWeekly);
                    em.flush();

                    // Recupera i contratti weekday associati al weekly contract
                    List<Weekdaycontract> weekdays = em.createQuery(
                                    "select wdc from Weekdaycontract wdc join Weeklycontract wc where wdc.id.weeklyId = wc.id",
                                    Weekdaycontract.class)
                            .getResultList();
                    // Per ogni giorno della settimana, crea un nuovo Weekday
                    for (LocalDate date = offer.getWeeklycontract().getStartDate();
                         date.isBefore(offer.getWeeklycontract().getEndDate());
                         date = date.plusDays(1)) {
                        for (Weekdaycontract wdc : weekdays) {
                            if (wdc.getId().getDayOfWeek().equals(String.valueOf(date.getDayOfWeek()))) {
                                Weekday newWeekday = new Weekday();
                                newWeekday.setWeekly(newWeekly);
                                newWeekday.setStartHour(wdc.getStartHour());
                                newWeekday.setEndHour(wdc.getEndHour());
                                em.persist(newWeekday);
                                em.flush();
                            }
                        }
                    }
                }

                // Crea e persiste i Planner per client e worker
                PlannerId newClientPlannerId = new PlannerId();
                newClientPlannerId.setScheduleId(newSchedule.getId());
                newClientPlannerId.setUserId(offer.getAlias().getClientAlias().getUser().getId());
                em.persist(newClientPlannerId);
                em.flush();

                Planner newClientPlanner = new Planner();
                newClientPlanner.setId(newClientPlannerId);
                newClientPlanner.setSchedule(newSchedule);
                newClientPlanner.setUser(offer.getAlias().getClientAlias().getUser());
                em.persist(newClientPlanner);
                em.flush();

                PlannerId newWorkerPlannerId = new PlannerId();
                newWorkerPlannerId.setScheduleId(newSchedule.getId());
                newWorkerPlannerId.setUserId(offer.getAlias().getWorkerAlias().getUser().getUser().getId());
                em.persist(newWorkerPlannerId);
                em.flush();

                Planner newWorkerPlanner = new Planner();
                newWorkerPlanner.setId(newWorkerPlannerId);
                // Nota: qui viene utilizzato getUser().getUser() per recuperare il worker "interno"
                newWorkerPlanner.setSchedule(newSchedule);
                newWorkerPlanner.setUser(offer.getAlias().getWorkerAlias().getUser().getUser());
                em.persist(newWorkerPlanner);
                em.flush();

                // Aggiorna lo stato del contratto
                offer.setStatus("OFFER");
                em.merge(offer);

                transaction.commit();
                return true;
            } else {
                // Se non viene approvato, rimuove il contratto
                em.remove(offer);
                transaction.commit();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Elimina una bozza (draft) di contratto.
     *
     * @param draft Il contratto in stato "DRAFT" da eliminare.
     * @return {@code true} se la bozza viene eliminata correttamente, {@code false} altrimenti.
     */
    public static boolean deleteDraft(Contract draft) {
        try (EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager(); em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            // Verifica che il contratto sia effettivamente in stato DRAFT
            if (!"DRAFT".equals(draft.getStatus())) return false;
            em.remove(draft);
            transaction.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Crea una bozza (DraftBuffer) a partire dai criteri di ricerca e da un risultato selezionato.
     * <p>
     * Viene costruito un nuovo contratto in stato "DRAFT" e popolato con i dati relativi a Daily o Weekly
     * contract, calcolando anche la tariffa totale basata sulla durata e la tariffa oraria.
     * </p>
     *
     * @param criteria I criteri di ricerca utilizzati.
     * @param result   Il risultato della ricerca selezionato.
     * @return Un {@link DraftBuffer} contenente il contratto di bozza e le relative informazioni.
     */
    public static DraftBuffer writeDraft(Criteria criteria, Result result) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            // Crea un nuovo contratto in stato DRAFT e imposta le proprietà iniziali
            Contract newDraft = new Contract();
            newDraft.setStatus("DRAFT");
            newDraft.setProfession(criteria.getProfession());
            newDraft.setHourlyRate(result.getCareer().getHourlyRate());
            newDraft.setScheduleType(criteria.getScheduleType());

            BigDecimal totalFee = BigDecimal.ZERO;

            // Utilizza il Builder per costruire il DraftBuffer
            DraftBuffer.Builder builder = new DraftBuffer.Builder(result.getWorker(), newDraft);

            if (newDraft.getScheduleType().equals("DAILY")) {
                // Crea il Dailycontract e lo associa al draft
                Dailycontract newDailyContract = new Dailycontract();
                newDailyContract.setDay(criteria.getDay());
                newDailyContract.setStartHour(criteria.getDailyStartHour());
                newDailyContract.setEndHour(criteria.getDailyEndHour());
                newDailyContract.setContract(newDraft);

                newDraft.setDailycontract(newDailyContract);
                builder = builder.dailyContract(newDailyContract);

                // Calcola la durata in secondi e converte in ore
                long duration = Duration.between(newDailyContract.getStartHour(), newDailyContract.getEndHour()).toSeconds();
                BigDecimal hours = BigDecimal.valueOf(duration)
                        .divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP);

                newDraft.setTotalFee(newDraft.getHourlyRate().multiply(hours));
            } else {
                // Crea il Weeklycontract e i relativi Weekdaycontract
                Weeklycontract newWeekly = new Weeklycontract();
                newWeekly.setStartDate(criteria.getStartDate());
                newWeekly.setEndDate(criteria.getEndDate());
                newWeekly.setContract(newDraft);

                Map<String, TimeInterval> weekdaysCriteria = criteria.getWeeklyIntervals();
                List<Weekdaycontract> weekdaysBuffer = new ArrayList<>();

                // Per ogni intervallo settimanale, crea un Weekdaycontract e accumula il totale delle ore
                for (Map.Entry<String, TimeInterval> entry : weekdaysCriteria.entrySet()) {
                    WeekdaycontractId newWeekdayContractId = new WeekdaycontractId();
                    newWeekdayContractId.setDayOfWeek(entry.getKey());
                    newWeekdayContractId.setWeeklyId(newWeekly.getId());

                    Weekdaycontract weekdayContract = new Weekdaycontract();
                    weekdayContract.setStartHour(entry.getValue().getStart());
                    weekdayContract.setEndHour(entry.getValue().getEnd());
                    weekdayContract.setId(newWeekdayContractId);
                    weekdayContract.setWeekly(newWeekly);

                    weekdaysBuffer.add(weekdayContract);

                    long duration = Duration.between(weekdayContract.getStartHour(), weekdayContract.getEndHour()).toSeconds();
                    BigDecimal hours = BigDecimal.valueOf(duration)
                            .divide(BigDecimal.valueOf(3600), 2, RoundingMode.HALF_UP);
                    totalFee = totalFee.add(newDraft.getHourlyRate().multiply(hours));
                }
                builder = builder.weeklyContract(newWeekly, weekdaysBuffer);
                newDraft.setTotalFee(newDraft.getHourlyRate().multiply(totalFee));
            }
            newDraft.setServiceMode(criteria.getServiceMode());
            if (newDraft.getServiceMode().equals("REMOTE")) {
                return builder.build();
            } else {
                // Se il servizio non è REMOTE, crea e associa un Notremotecontract
                Notremotecontract notRemoteContract = new Notremotecontract();
                notRemoteContract.setContract(newDraft);
                notRemoteContract.setCity(criteria.getCity());
                notRemoteContract.setStreet(criteria.getStreet());
                notRemoteContract.setStreetNumber(result.getStreetNumber());
                notRemoteContract.setDistrict(criteria.getDistrict());
                notRemoteContract.setRegion(criteria.getRegion());
                notRemoteContract.setCountry(criteria.getCountry());

                builder = builder.notRemoteContract(notRemoteContract);
            }
            return builder.build();
        }
    }

    /**
     * Invia un'offerta a partire da un draft e delle richieste speciali.
     * <p>
     * Effettua controlli di collisione sui contratti (sia per schedule DAILY che WEEKLY).
     * Se viene rilevata una collisione, il draft viene rimosso e viene restituito {@code null}.
     * Altrimenti, lo stato del draft viene aggiornato a "OFFER", viene creato un oggetto {@link Offer}
     * e l'offerta viene inviata.
     * </p>
     *
     * @param draft           Il draft del contratto da inviare come offerta.
     * @param specialRequests Richieste speciali relative all'offerta.
     * @return Il contratto offerta se l'operazione va a buon fine, {@code null} in caso di collisione.
     */
    public static Contract sendOffer(Contract draft, String specialRequests) {

        if(draft.getTotalFee().toString() == null){
            throw new IllegalArgumentException("Il Campo Total Fee è vuoto");
        }

        if(draft.getHourlyRate().toString() == null){
            throw new IllegalArgumentException("Il Campo Total Fee è vuoto");
        }



        if (specialRequests.length() < 250) {
            throw new IllegalArgumentException("specialRequests too long");
        }


        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try (em) {
            // Verifica le collisioni per schedule DAILY
            if (draft.getScheduleType().equals("DAILY")) {
                Dailycontract dailycontract = draft.getDailycontract();
                if (
                        Collision.detect(draft.getAlias().getClientAlias().getUser(), dailycontract.getDay(), new TimeInterval(dailycontract.getStartHour(), dailycontract.getEndHour()))
                                || Collision.detect(draft.getAlias().getWorkerAlias().getUser().getUser(), dailycontract.getDay(), new TimeInterval(dailycontract.getStartHour(), dailycontract.getEndHour()))
                ) {
                    // Se c'è una collisione, rimuove il draft e restituisce null
                    em.remove(draft);
                    transaction.commit();
                    return null;
                }
            } else {
                // Verifica le collisioni per schedule WEEKLY
                Weeklycontract weeklycontract = draft.getWeeklycontract();
                List<Weekdaycontract> weekdaysContract = em.createQuery(
                                "select wdc from Weekdaycontract wdc join Weeklycontract wc where wdc.weekly.contract = :draft",
                                Weekdaycontract.class)
                        .setParameter("draft", draft)
                        .getResultList();
                for (Weekdaycontract wdc : weekdaysContract) {
                    // Per ogni giorno nel range del weekly contract, controlla la collisione
                    for (LocalDate date = wdc.getWeekly().getStartDate(); date.isBefore(wdc.getWeekly().getEndDate()); date = date.plusDays(1)) {
                        if (date.getDayOfWeek().equals(wdc.getId().getDayOfWeek())) {
                            if (
                                    Collision.detect(draft.getAlias().getClientAlias().getUser(), date, new TimeInterval(wdc.getStartHour(), wdc.getEndHour()))
                                            || Collision.detect(draft.getAlias().getWorkerAlias().getUser().getUser(), date, new TimeInterval(wdc.getStartHour(), wdc.getEndHour()))
                            ) {
                                // Se c'è una collisione, rimuove il draft e restituisce null
                                em.remove(draft);
                                transaction.commit();
                                return null;
                            }
                        }
                    }
                }
            }
            // Aggiorna lo stato del draft a "OFFER"
            draft.setStatus("OFFER");
            em.merge(draft);
            em.flush();

            // Crea un nuovo oggetto Offer associato al draft e alle special requests
            Offer newOffer = new Offer();
            newOffer.setId(draft.getId());
            newOffer.setContract(draft);
            newOffer.setSpecialRequests(specialRequests);
            em.persist(newOffer);

            transaction.commit();
            return newOffer.getContract();
        }
    }

    public static Review getReview(Contract expired) {
        return expired.getReview();
    }
}