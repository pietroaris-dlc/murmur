package is.murmur.Model.Services;

import is.murmur.Model.Beans.*;
import is.murmur.Model.Helpers.Collision;
import is.murmur.Model.Helpers.JPAUtil;
import is.murmur.Model.Helpers.TimeInterval;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * La classe {@code WorkerSide} gestisce le operazioni relative ai lavoratori,
 * quali la gestione della carriera, delle applicazioni per lavoro, delle aree di attività
 * e la definizione dei contratti.
 * <p>
 * Implementa l'interfaccia {@link Collision} per permettere il controllo di collisione degli orari.
 * </p>
 *
 
 */
public class WorkerSide implements Collision {

    /**
     * Recupera la lista delle carriere associate a un lavoratore.
     *
     * @param worker L'utente lavoratore di cui recuperare le carriere.
     * @return Una lista di {@link Career} associata al lavoratore.
     */
    public static List<Career> getCareers(User worker) {
        try (EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery(
                            "select c from Career c where c.worker.id = :workerId", Career.class)
                    .setParameter("workerId", worker.getId())
                    .getResultList();
        }
    }

    /**
     * Invia una candidatura per un lavoro.
     * <p>
     * Crea e persiste un oggetto {@link Application} di tipo "JOB" e un oggetto {@link Jobapplication}
     * contenente le informazioni specifiche della candidatura.
     * </p>
     *
     * @param user     L'utente che invia la candidatura.
     * @param docsUrl  L'URL dei documenti allegati alla candidatura.
     * @param jobInputs Un array di stringhe contenente i dati specifici del lavoro (es. professione, tariffa, seniority).
     * @return L'oggetto {@link Application} creato.
     */
    public static Application jobApplication(User user, String docsUrl, String[] jobInputs) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            // Crea una nuova candidatura di tipo "JOB"
            Application application = new Application();
            application.setUser(user);
            application.setSubmissionDate(LocalDate.now());
            application.setSubmissionHour(LocalTime.now());
            application.setDocsUrl(docsUrl);
            application.setStatus("PENDING");
            application.setType("JOB");
            em.persist(application);

            // Crea il componente specifico della candidatura per il lavoro
            Jobapplication jobApplication = new Jobapplication();
            jobApplication.setId(application.getId());
            jobApplication.setApplication(application);
            jobApplication.setProfessionName(jobInputs[1]);
            jobApplication.setHourlyRate(new BigDecimal(jobInputs[2]));
            jobApplication.setSeniority(Integer.valueOf(jobInputs[3]));
            em.persist(jobApplication);

            transaction.commit();
            return application;
        }
    }

    /**
     * Aggiunge una carriera per un lavoratore.
     * <p>
     * Il metodo cerca una {@link Profession} esistente con il nome specificato; se non viene trovata,
     * ne crea una nuova. Successivamente, crea un nuovo oggetto {@link Career} associato al lavoratore
     * e lo persiste.
     * </p>
     *
     * @param em         L'EntityManager da utilizzare.
     * @param worker     Il lavoratore a cui associare la carriera.
     * @param profession Il nome della professione.
     * @param hourlyRate La tariffa oraria.
     * @param seniority  Il livello di anzianità.
     * @return {@code true} se l'operazione ha successo, {@code false} in caso di eccezione.
     */
    public static boolean addCareer(EntityManager em, User worker, String profession, BigDecimal hourlyRate, Integer seniority) {
        try {
            // Cerca una professione esistente con il nome specificato
            List<Profession> professions = em.createQuery(
                            "select p from Profession p where p.name = :profession",
                            Profession.class)
                    .setParameter("profession", profession)
                    .getResultList();
            Profession professionToUse;
            if (!professions.isEmpty()) {
                professionToUse = professions.get(0);
            } else {
                // Se non esiste, crea e persiste una nuova Profession
                professionToUse = new Profession();
                professionToUse.setName(profession);
                em.persist(professionToUse);
            }

            // Crea e persiste la carriera associata al lavoratore
            Career career = new Career();
            career.setWorker(worker.getWorker());
            career.setProfession(professionToUse);
            career.setHourlyRate(hourlyRate);
            career.setSeniority(seniority);
            em.persist(career);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina una carriera.
     *
     * @param career La carriera da eliminare.
     * @return {@code true} se la carriera viene eliminata con successo, {@code false} in caso contrario.
     */
    public static boolean deleteCareer(Career career) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try (em) {
            transaction.begin();
            // Trova la carriera nel contesto di persistenza
            Career managedCareer = em.find(Career.class, career.getId());
            if (managedCareer == null) {
                transaction.rollback();
                return false;
            }
            em.remove(managedCareer);
            transaction.commit();
            return true;
        }
    }

    /**
     * Recupera le aree di attività associate a un lavoratore.
     *
     * @param user Il lavoratore di cui recuperare le aree di attività.
     * @return Una lista di {@link Activityarea} associate al lavoratore.
     */
    public static List<Activityarea> getActivityArea(User user) {
        try (EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery(
                            "SELECT a FROM Activityarea a WHERE a.worker = :worker", Activityarea.class)
                    .setParameter("worker", user)
                    .getResultList();
        }
    }

    /**
     * Aggiunge un'area di attività per un lavoratore.
     * <p>
     * Il metodo cerca un oggetto {@link Location} esistente con i parametri specificati; se non viene trovato,
     * ne crea uno nuovo. Successivamente, crea un nuovo {@link Activityarea} associato al lavoratore e alla notRemote.
     * </p>
     *
     * @param em           L'EntityManager da utilizzare.
     * @param user         Il lavoratore.
     * @param city         La città della notRemote.
     * @param street       La via della notRemote.
     * @param streetNumber Il numero civico della notRemote.
     * @param district     Il distretto della notRemote.
     * @param region       La regione della notRemote.
     * @param country      Il paese della notRemote.
     * @return {@code true} se l'operazione ha successo, {@code false} in caso di eccezione.
     */
    public static boolean addToActivityArea(
            EntityManager em,
            User user,
            String city,
            String street,
            Short streetNumber,
            String district,
            String region,
            String country
    ) {
        try {
            // Cerca una notRemote esistente con i parametri specificati
            List<Location> locations = em.createQuery(
                            "select l from Location l " +
                                    "where l.city = :city " +
                                    "  and l.street = :street " +
                                    "  and l.streetNumber = :streetNumber " +
                                    "  and l.district = :district " +
                                    "  and l.country = :country",
                            Location.class)
                    .setParameter("city", city)
                    .setParameter("street", street)
                    .setParameter("streetNumber", streetNumber)
                    .setParameter("district", district)
                    .setParameter("country", country)
                    .getResultList();
            Location locationToUse;
            if (!locations.isEmpty()) {
                locationToUse = locations.get(0);
            } else {
                // Se non esiste, crea e persiste una nuova Location
                locationToUse = new Location();
                locationToUse.setCity(city);
                locationToUse.setStreet(street);
                locationToUse.setStreetNumber(streetNumber);
                locationToUse.setDistrict(district);
                locationToUse.setCountry(country);
                em.persist(locationToUse);
            }

            // Crea e persiste l'Activityarea associata al lavoratore e alla notRemote
            Activityarea activityArea = new Activityarea();
            activityArea.setWorker(user.getWorker());
            activityArea.setLocation(locationToUse);
            em.persist(activityArea);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Rimuove un'area di attività per un lavoratore.
     *
     * @param user     Il lavoratore.
     * @param location La notRemote da cui rimuovere l'area di attività.
     * @return {@code true} se l'operazione ha successo, {@code false} altrimenti.
     */
    public static boolean deleteFromActivityArea(User user, Location location) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            // Recupera l'Activityarea corrispondente al lavoratore e alla notRemote
            Activityarea activityarea = em.createQuery(
                            "select a from Activityarea a where a.worker = :worker and a.location = :location",
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

    /**
     * Definisce un contratto offerta.
     * <p>
     * In base al parametro {@code approval}, se l'offerta viene approvata, viene creato uno schedule
     * (DAILY o WEEKLY) per il contratto e vengono creati i relativi Planner per client e worker; in caso di rifiuto,
     * lo stato del contratto viene impostato a "REJECTED".
     * </p>
     *
     * @param offer    Il contratto offerta.
     * @param approval {@code true} per approvare il contratto, {@code false} per rifiutarlo.
     * @return {@code true} se il contratto viene definito con successo, {@code false} altrimenti.
     */
    public static boolean defineContract(Contract offer, boolean approval) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try (em) {
            transaction.begin();

            if (approval) {
                if (offer.getScheduleType().equals("DAILY")) {
                    // Aggiunge lo schedule giornaliero per client e worker
                    if (
                            !ClientSide.addDailySchedule(
                                    em, offer.getAlias().getClientAlias().getUser(),
                                    offer.getDailycontract().getDay(), new TimeInterval(offer.getDailycontract().getStartHour(), offer.getDailycontract().getEndHour())
                            )
                                    || !ClientSide.addDailySchedule(
                                    em, offer.getAlias().getWorkerAlias().getUser().getUser(),
                                    offer.getDailycontract().getDay(), new TimeInterval(offer.getDailycontract().getStartHour(), offer.getDailycontract().getEndHour())
                            )
                    ) {
                        // In caso di collisione, rifiuta il contratto
                        offer.setStatus("REJECTED");
                        em.merge(offer);
                        transaction.commit();
                        return false;
                    } else {
                        // Se non ci sono collisioni, approva il contratto
                        offer.setStatus("APPROVED");
                        em.merge(offer);
                        transaction.commit();
                        return true;
                    }
                } else {
                    // Gestione del contratto settimanale
                    Schedule newSchedule = new Schedule();
                    newSchedule.setType("WEEKLY");
                    em.persist(newSchedule);
                    em.flush();

                    Weekly newWeekly = new Weekly();
                    newWeekly.setId(newSchedule.getId());
                    newWeekly.setSchedule(newSchedule);
                    newWeekly.setStartDate(offer.getWeeklycontract().getStartDate());
                    newWeekly.setEndDate(offer.getWeeklycontract().getEndDate());
                    em.persist(newWeekly);
                    em.flush();

                    // Recupera i Weekdaycontract associati al nuovo Weekly
                    List<Weekdaycontract> weekdayContracts = em.createQuery(
                                    "select wdc from Weekdaycontract wdc where wdc.weekly = :weekly",
                                    Weekdaycontract.class)
                            .setParameter("weekly", newWeekly)
                            .getResultList();

                    // Crea una mappa dei giorni della settimana e relativi intervalli
                    Map<String, TimeInterval> weekdays = new HashMap<>();
                    for (Weekdaycontract wdc : weekdayContracts)
                        weekdays.put(wdc.getId().getDayOfWeek(), new TimeInterval(wdc.getStartHour(), wdc.getEndHour()));

                    // Aggiunge lo schedule settimanale per client e worker
                    if (
                            !ClientSide.addWeeklySchedule(offer.getAlias().getClientAlias().getUser(), newWeekly.getStartDate(), newWeekly.getEndDate(), weekdays)
                                    || !ClientSide.addWeeklySchedule(offer.getAlias().getWorkerAlias().getUser().getUser(), newWeekly.getStartDate(), newWeekly.getEndDate(), weekdays)
                    ) {
                        transaction.rollback();
                        return false;
                    } else {
                        offer.setStatus("APPROVED");
                        em.merge(offer);
                        transaction.commit();
                        return true;
                    }
                }
            } else {
                // Se l'approvazione non viene data, imposta lo stato a REJECTED
                offer.setStatus("REJECTED");
                em.merge(offer);
                transaction.commit();
                return false;
            }
        }
    }
}