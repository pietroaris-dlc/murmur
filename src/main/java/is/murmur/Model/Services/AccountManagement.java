package is.murmur.Model.Services;

import is.murmur.Model.Entities.*;
import is.murmur.Model.Enums.ApplicationStatus;
import is.murmur.Model.Enums.ApplicationType;
import is.murmur.Model.Enums.UserType;
import is.murmur.Model.Helpers.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * La classe AccountManagement gestisce le operazioni relative agli account:
 * registrazione, login, logout, cancellazione account, invio di application per upgrade
 * o collaborazione, upgrade account, avvio collaborazione e visualizzazione delle informazioni personali.
 */
public class AccountManagement {

    /**
     * Registra un nuovo utente nel sistema.
     *
     * @param signinInputs array di input contenente:
     *                     [0] - email,
     *                     [1] - password,
     *                     [2] - firstName,
     *                     [3] - lastName,
     *                     [4] - birthDate (formato yyyy-MM-dd),
     *                     [5] - birthCity,
     *                     [6] - birthDistrict,
     *                     [7] - birthCountry,
     *                     [8] - taxCode.
     * @return true se l'utente è stato creato con successo, false altrimenti.
     */
    public static boolean signIn(String[] signinInputs) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // Verifica se esiste già un utente con la stessa email
            TypedQuery<Registereduser> query = em.createQuery(
                    "select u from Registereduser u where u.email = :email",
                    Registereduser.class
            );
            query.setParameter("email", signinInputs[0]);
            List<Registereduser> results = query.getResultList();
            if (!results.isEmpty()) {
                // Esiste già un utente con la stessa email, rollback e uscita
                transaction.rollback();
                return false;
            }

            // Creazione e popolamento del nuovo utente
            Registereduser u = new Registereduser();
            u.setEmail(signinInputs[0]);
            u.setPassword(BCrypt.hashpw(signinInputs[1], BCrypt.gensalt()));
            u.setFirstName(signinInputs[2]);
            u.setLastName(signinInputs[3]);
            u.setBirthDate(LocalDate.parse(signinInputs[4]));
            u.setBirthCity(signinInputs[5]);
            u.setBirthDistrict(signinInputs[6]);
            u.setBirthCountry(signinInputs[7]);
            u.setTaxCode(signinInputs[8]);
            u.setType(String.valueOf(UserType.CLIENT));
            u.setAdmin(false);
            u.setLocked(false);

            em.persist(u);
            transaction.commit();

            return u.getId() != null;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Effettua il login di un utente.
     *
     * @param loginInputs array di input contenente:
     *                    [0] - email,
     *                    [1] - password.
     * @param drafts      lista di contratti (drafts) che, se privi di ID, verranno associati all'utente
     *                    tramite un nuovo alias.
     * @return l'oggetto Registereduser se il login ha successo, null altrimenti.
     */
    public static Registereduser logIn(String[] loginInputs, List<Contract> drafts) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // Ricerca dell'utente in base all'email
            Registereduser toLogin = em.createQuery(
                    "select u from Registereduser u where u.email = :email",
                    Registereduser.class
            ).setParameter("email", loginInputs[0]).getSingleResult();
            if (toLogin == null) {
                transaction.rollback();
                return null;
            }else if (!BCrypt.checkpw(loginInputs[1], toLogin.getPassword())) {
                transaction.rollback();
                return null;
            } else {
                // Gestione dei draft: se il draft non ha ancora un ID, viene creato un nuovo Alias
                for (Contract draft : drafts) {
                    if (draft.getId() == null) {
                        Alias alias = new Alias();
                        alias.setUser(toLogin);
                        em.persist(alias);
                        draft.setClientAlias(alias);
                        em.persist(draft);
                    }
                }
                transaction.commit();
                if(!toLogin.getLocked()){
                    return null;
                } else return toLogin;
            }

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

    /**
     * Effettua il logout dell'utente gestendo i drafts relativi.
     *
     * @param toLogout utente che effettua il logout.
     * @param drafts   lista di contratti (drafts) che, se privi di ID, verranno associati all'utente
     *                 tramite un nuovo alias.
     * @return true se l'operazione di logout è andata a buon fine, false altrimenti.
     */
    public static boolean logout(Registereduser toLogout, List<Contract> drafts) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            for (Contract draft : drafts) {
                if (draft.getId() == null) {
                    Alias alias = new Alias();
                    alias.setUser(toLogout);
                    em.persist(alias);
                    draft.setClientAlias(alias);
                    em.persist(draft);
                }
            }
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Elimina l'account dell'utente se non sono presenti contratti in stati "DRAFT", "OFFER" o "ACTIVE".
     * In caso contrario, non esegue la cancellazione.
     *
     * @param registereduser utente da eliminare.
     * @return true se l'account è stato eliminato (ovvero, se non esistevano contratti in stato attivo),
     *         false altrimenti.
     */
    public static boolean accountDeletion(Registereduser registereduser) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // Verifica dell'esistenza di contratti in stati non cancellabili
            TypedQuery<Contract> contractQuery = em.createQuery(
                    "select c from Contract c " +
                            "where c.status in ('DRAFT', 'OFFER', 'ACTIVE') " +
                            "  and (c.clientAlias.user.id = :userId or c.workerAlias.user.id = :userId)",
                    Contract.class);
            contractQuery.setParameter("userId", registereduser.getId());

            if (!contractQuery.getResultList().isEmpty()) {
                transaction.rollback();
                return false;
            }

            // Rimozione delle application associate all'utente
            TypedQuery<Application> applicationQuery = em.createQuery(
                    "select a from Application a where a.user.id = :userId",
                    Application.class);
            applicationQuery.setParameter("userId", registereduser.getId());

            List<Application> applications = applicationQuery.getResultList();
            for (Application app : applications) {
                em.remove(app);
            }

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Cancella una application.
     *
     * @param application l'application da cancellare.
     * @return true se l'application è stata cancellata con successo, false altrimenti.
     */
    public static boolean cancelApplication(Application application) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // Se l'oggetto non è gestito, viene unito al contesto di persistenza
            if (!em.contains(application)) {
                application = em.merge(application);
            }
            em.remove(application);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Invia una richiesta di upgrade per l'utente.
     *
     * @param upgradeInputs array di input contenente:
     *                      [0] - submissionDate (formato yyyy-MM-dd),
     *                      [1] - submissionHour (formato HH:mm:ss),
     *                      [2] - docsUrl,
     *                      [3] - professionName,
     *                      [4] - hourlyRate (può contenere valori decimali),
     *                      [5] - city,
     *                      [6] - street,
     *                      [7] - streetNumber,
     *                      [8] - district,
     *                      [9] - country.
     * @param registereduser utente che richiede l'upgrade.
     * @return true se la richiesta è stata registrata con successo, false altrimenti.
     */
    public static boolean upgradeApplication(String[] upgradeInputs, Registereduser registereduser) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // Creazione della Application per l'upgrade
            Application app = new Application();
            app.setUser(registereduser);
            app.setSubmissionDate(LocalDate.parse(upgradeInputs[0]));
            app.setSubmissionHour(LocalTime.parse(upgradeInputs[1]));
            app.setDocsUrl(upgradeInputs[2]);
            app.setStatus(String.valueOf(ApplicationStatus.PENDING));
            app.setType(String.valueOf(ApplicationType.UPGRADE));
            em.persist(app);

            // Creazione del componente di upgrade associato alla Application
            Upgradecomponent upgradecomponent = new Upgradecomponent();
            upgradecomponent.setApplication(app);
            upgradecomponent.setProfessionName(upgradeInputs[3]);
            upgradecomponent.setHourlyRate(new BigDecimal(upgradeInputs[4])); // Gestisce valori decimali
            upgradecomponent.setCity(upgradeInputs[5]);
            upgradecomponent.setStreet(upgradeInputs[6]);
            upgradecomponent.setStreetNumber(Short.valueOf(upgradeInputs[7]));
            upgradecomponent.setDistrict(upgradeInputs[8]);
            upgradecomponent.setCountry(upgradeInputs[9]);
            em.persist(upgradecomponent);

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Invia una richiesta di collaborazione.
     *
     * @param collabInputs array di input contenente:
     *                     [0] - submissionDate (formato yyyy-MM-dd),
     *                     [1] - submissionHour (formato HH:mm:ss),
     *                     [2] - docsUrl.
     * @param registereduser utente che richiede la collaborazione.
     * @return true se la richiesta è stata registrata con successo, false altrimenti.
     */
    public static boolean collabApplication(String[] collabInputs, Registereduser registereduser) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // Creazione della Application per la collaborazione
            Application app = new Application();
            app.setUser(registereduser);
            app.setSubmissionDate(LocalDate.parse(collabInputs[0]));
            app.setSubmissionHour(LocalTime.parse(collabInputs[1]));
            app.setDocsUrl(collabInputs[2]);
            app.setStatus(String.valueOf(ApplicationStatus.PENDING));
            app.setType(String.valueOf(ApplicationType.COLLAB));
            em.persist(app);

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Esegue l'upgrade dell'account dell'utente a WORKER, approvando l'application associata
     * e aggiungendo la prima Activity Area e la prima Career.
     *
     * @param registereduser  utente da aggiornare.
     * @param upgradecomponent componente di upgrade contenente le informazioni aggiuntive.
     * @return true se l'upgrade è completato con successo, false altrimenti.
     */
    public static boolean upgradeAccount(Registereduser registereduser, Upgradecomponent upgradecomponent) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // Aggiornamento del tipo di utente a WORKER
            registereduser.setType(String.valueOf(UserType.WORKER));
            em.merge(registereduser);

            // Aggiornamento dello status dell'application associata all'upgrade
            Application app = upgradecomponent.getApplication();
            app.setStatus(String.valueOf(ApplicationStatus.APPROVED));
            em.merge(app);

            // Aggiunta dell'area di attività e della carriera
            if (!addFirstActivityArea(em, registereduser, upgradecomponent)) {
                transaction.rollback();
                return false;
            }
            if (!addFirstCareer(em, registereduser, upgradecomponent)) {
                transaction.rollback();
                return false;
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

    /**
     * Avvia la collaborazione rendendo l'utente amministratore e approvando l'application di collaborazione.
     *
     * @param registereduser  utente che diventerà amministratore.
     * @param collabApplication application di collaborazione da approvare.
     * @return true se l'operazione ha successo, false altrimenti.
     */
    public static boolean startCollab(Registereduser registereduser, Application collabApplication) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // L'utente diventa amministratore
            registereduser.setAdmin(true);
            em.merge(registereduser);

            // Approva l'application di collaborazione
            collabApplication.setStatus(String.valueOf(ApplicationStatus.APPROVED));
            em.merge(collabApplication);

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Restituisce un array di stringhe contenente le informazioni personali dell'utente.
     *
     * @param registereduser utente di cui si vogliono visualizzare le informazioni.
     * @return array di stringhe con i seguenti dati:
     *         email, firstName, lastName, birthDate, birthCity,
     *         birthDistrict, birthCountry, taxCode, totalExpenditure, type, admin.
     */
    public static String[] personalInfosView(Registereduser registereduser) {
        return new String[]{
                registereduser.getEmail(),
                registereduser.getFirstName(),
                registereduser.getLastName(),
                String.valueOf(registereduser.getBirthDate()),
                registereduser.getBirthCity(),
                registereduser.getBirthDistrict(),
                registereduser.getBirthCountry(),
                registereduser.getTaxCode(),
                String.valueOf(registereduser.getTotalExpenditure()),
                registereduser.getType(),
                String.valueOf(registereduser.getAdmin())
        };
    }

    /**
     * Metodo helper che aggiunge la prima Activity Area per l'utente che effettua l'upgrade.
     * Se esiste già una Location con le stesse informazioni, la utilizza; altrimenti ne crea una nuova.
     * <p>
     * Si assume che la transazione sia già attiva.
     * </p>
     *
     * @param em               EntityManager in uso (con transazione attiva).
     * @param registereduser   utente per il quale aggiungere l'Activity Area.
     * @param upgradecomponent componente di upgrade contenente le informazioni per la Location.
     * @return true se l'Activity Area è stata aggiunta con successo, false altrimenti.
     */
    private static boolean addFirstActivityArea(EntityManager em,
                                                Registereduser registereduser,
                                                Upgradecomponent upgradecomponent) {
        try {
            // Ricerca di una Location esistente con gli stessi dati
            TypedQuery<Location> locationQuery = em.createQuery(
                    "select l from Location l " +
                            "where l.city = :city " +
                            "  and l.street = :street " +
                            "  and l.streetNumber = :streetNumber " +
                            "  and l.district = :district " +
                            "  and l.country = :country",
                    Location.class
            );
            locationQuery.setParameter("city", upgradecomponent.getCity());
            locationQuery.setParameter("street", upgradecomponent.getStreet());
            locationQuery.setParameter("streetNumber", upgradecomponent.getStreetNumber());
            locationQuery.setParameter("district", upgradecomponent.getDistrict());
            locationQuery.setParameter("country", upgradecomponent.getCountry());

            List<Location> locations = locationQuery.getResultList();
            Location locationToUse;
            if (!locations.isEmpty()) {
                // Utilizzo della Location esistente
                locationToUse = locations.get(0);
            } else {
                // Creazione di una nuova Location
                locationToUse = new Location();
                locationToUse.setCity(upgradecomponent.getCity());
                locationToUse.setStreet(upgradecomponent.getStreet());
                locationToUse.setStreetNumber(upgradecomponent.getStreetNumber());
                locationToUse.setDistrict(upgradecomponent.getDistrict());
                locationToUse.setCountry(upgradecomponent.getCountry());
                em.persist(locationToUse);
            }

            // Creazione e persistenza della nuova Activity Area
            Activityarea activityArea = new Activityarea();
            activityArea.setWorker(registereduser);
            activityArea.setLocation(locationToUse);
            em.persist(activityArea);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Metodo helper che aggiunge la prima Career per l'utente che effettua l'upgrade.
     * Se esiste già una Profession con lo stesso nome, la utilizza; altrimenti ne crea una nuova.
     * <p>
     * Si assume che la transazione sia già attiva.
     * </p>
     *
     * @param em               EntityManager in uso (con transazione attiva).
     * @param registereduser   utente per il quale aggiungere la Career.
     * @param upgradecomponent componente di upgrade contenente le informazioni per la Profession.
     * @return true se la Career è stata aggiunta con successo, false altrimenti.
     */
    private static boolean addFirstCareer(EntityManager em,
                                          Registereduser registereduser,
                                          Upgradecomponent upgradecomponent) {
        try {
            // Ricerca di una Profession esistente con lo stesso nome
            TypedQuery<Profession> professionQuery = em.createQuery(
                    "select p from Profession p where p.name = :professionName",
                    Profession.class
            );
            professionQuery.setParameter("professionName", upgradecomponent.getProfessionName());
            List<Profession> professions = professionQuery.getResultList();
            Profession professionToUse;
            if (!professions.isEmpty()) {
                // Utilizzo della Profession esistente
                professionToUse = professions.get(0);
            } else {
                // Creazione di una nuova Profession
                professionToUse = new Profession();
                professionToUse.setName(upgradecomponent.getProfessionName());
                em.persist(professionToUse);
            }

            // Creazione e persistenza della nuova Career
            Career career = new Career();
            career.setWorker(registereduser);
            career.setProfession(professionToUse);
            career.setHourlyRate(upgradecomponent.getHourlyRate());
            career.setSeniority(upgradecomponent.getSeniority());
            em.persist(career);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}