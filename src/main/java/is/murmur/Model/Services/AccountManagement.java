package is.murmur.Model.Services;

import is.murmur.Model.Beans.*;
import is.murmur.Model.Helpers.DraftBuffer;
import is.murmur.Model.Helpers.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Classe di gestione degli account.
 * <p>
 * Questa classe fornisce metodi statici per operazioni di gestione degli account,
 * quali la registrazione (sign in), l'accesso (log in), il logout, la cancellazione dell'account,
 * la gestione delle applicazioni (upgrade, collaborazione, cancellazione) e l'aggiornamento dell'account.
 * </p>
 *
 */
public class AccountManagement {

    /**
     * Registra un nuovo utente (sign in).
     * <p>
     * Il metodo controlla se l'email fornita esiste già e, se non esiste, crea un nuovo utente
     * impostando i dati forniti e hashando la password tramite BCrypt.
     * </p>
     *
     * @param signinInputs Array di stringhe contenente i seguenti dati:
     *                     [0] - email,
     *                     [1] - password,
     *                     [2] - nome,
     *                     [3] - cognome,
     *                     [4] - data di nascita (formato ISO),
     *                     [5] - città di nascita,
     *                     [6] - distretto di nascita,
     *                     [7] - paese di nascita,
     *                     [8] - codice fiscale.
     * @return {@code true} se la registrazione è avvenuta con successo, {@code false} altrimenti.
     */
    public static boolean signIn(String[] signinInputs) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        // Utilizzo di try-with-resources per garantire la chiusura dell'EntityManager
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            // Verifica se l'utente con la stessa email esiste già
            List<User> users = em.createQuery(
                            "select u from User u where u.email = :email",
                            User.class
                    )
                    .setParameter("email", signinInputs[0])
                    .getResultList();
            if (!users.isEmpty()) {
                return false;
            }

            // Crea un nuovo oggetto User e imposta i dati ricevuti
            User u = new User();
            u.setEmail(signinInputs[0]);
            u.setPassword(BCrypt.hashpw(signinInputs[1], BCrypt.gensalt()));
            u.setFirstName(signinInputs[2]);
            u.setLastName(signinInputs[3]);
            u.setBirthDate(LocalDate.parse(signinInputs[4]));
            u.setBirthCity(signinInputs[5]);
            u.setBirthDistrict(signinInputs[6]);
            u.setBirthCountry(signinInputs[7]);
            u.setTaxCode(signinInputs[8]);
            u.setType("CLIENT");
            u.setAdmin(false);
            u.setLocked(false);

            // Persiste il nuovo utente nel database
            em.persist(u);
            transaction.commit();

            // Verifica se l'utente è stato creato (l'id non è nullo)
            return u.getId() != null;
        }
    }

    /**
     * Effettua il login di un utente.
     * <p>
     * Il metodo recupera l'utente tramite l'email, verifica la password usando BCrypt e,
     * in caso di successo, salva eventuali bozze (drafts) sul lato client.
     * </p>
     *
     * @param loginInputs Array di stringhe contenente i dati di login:
     *                    [0] - email,
     *                    [1] - password.
     * @param drafts      Lista di {@link DraftBuffer} da salvare per l'utente.
     * @return L'oggetto {@link User} se il login avviene con successo, {@code null} altrimenti.
     */
    public static User logIn(String[] loginInputs, List<DraftBuffer> drafts) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            // Recupera l'utente in base all'email
            User toLogin = em.createQuery(
                    "select u from User u where u.email = :email",
                    User.class
            ).setParameter("email", loginInputs[0]).getSingleResult();

            // Se l'utente non esiste oppure la password non corrisponde, ritorna null
            if (toLogin == null || !BCrypt.checkpw(loginInputs[1], toLogin.getPassword())) {
                return null;
            } else {
                // Salva le bozze (drafts) lato client
                for (DraftBuffer draftBuffer : drafts) {
                    ClientSide.saveDraft(toLogin, draftBuffer);
                }
                transaction.commit();
                // Se l'utente non è sbloccato, ritorna null; altrimenti, ritorna l'utente
                if (!toLogin.getLocked()) {
                    return null;
                } else {
                    return toLogin;
                }
            }
        }
    }

    /**
     * Esegue il logout di un utente.
     * <p>
     * Il metodo salva le bozze (drafts) lato client e avvia la transazione di logout.
     * </p>
     *
     * @param toLogout L'utente da disconnettere.
     * @param drafts   Lista di {@link DraftBuffer} da salvare.
     * @return {@code true} se il logout avviene con successo.
     */
    public static boolean logout(User toLogout, List<DraftBuffer> drafts) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            // Salva ogni bozza per l'utente
            for (DraftBuffer draft : drafts) {
                ClientSide.saveDraft(toLogout, draft);
            }
            // Nota: non viene eseguito commit della transazione; si presume che il salvataggio
            // delle bozze sia sufficiente per completare il logout.
            return true;
        }
    }

    /**
     * Richiede la cancellazione dell'account di un utente.
     * <p>
     * Il metodo verifica che non esistano contratti attivi (DRAFT, OFFER, ACTIVE) relativi all'utente.
     * Se non esistono, rimuove anche le applicazioni associate e permette la cancellazione.
     * </p>
     *
     * @param user L'utente per il quale si richiede la cancellazione dell'account.
     * @return {@code true} se la cancellazione è possibile, {@code false} altrimenti.
     */
    public static boolean accountDeletion(User user) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            // Recupera i contratti attivi, in offerta o in draft associati all'utente
            List<Contract> contracts = em.createQuery(
                            "select c from Contract c " +
                                    "where c.status in ('DRAFT', 'OFFER', 'ACTIVE') " +
                                    "  and (c.alias.clientAlias.user = :user or c.alias.workerAlias.user.user = :user)",
                            Contract.class)
                    .setParameter("user", user)
                    .getResultList();

            // Se esistono contratti attivi, l'account non può essere cancellato
            if (!contracts.isEmpty()) {
                return false;
            }

            // Recupera e rimuove tutte le applicazioni associate all'utente
            List<Application> applications = em.createQuery(
                            "select a from Application a where a.user.id = :user",
                            Application.class)
                    .setParameter("user", user)
                    .getResultList();
            for (Application app : applications) {
                em.remove(app);
            }

            transaction.commit();
            return true;
        }
    }

    /**
     * Cancella un'applicazione.
     * <p>
     * Il metodo rimuove un'applicazione dal database.
     * </p>
     *
     * @param application L'applicazione da cancellare.
     * @return {@code true} se l'applicazione viene cancellata con successo, {@code false} altrimenti.
     */
    public static boolean cancelApplication(Application application) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            // Se l'applicazione non è gestita dall'EntityManager, la fonde (merge)
            if (!em.contains(application)) {
                application = em.merge(application);
            }
            // Rimuove l'applicazione
            em.remove(application);

            transaction.commit();
            return true;
        }
    }

    /**
     * Invia una richiesta di upgrade.
     * <p>
     * Il metodo crea una nuova applicazione di tipo "UPGRADE" e un oggetto {@link Upgradeapplication}
     * con i dati forniti, e li persiste nel database.
     * </p>
     *
     * @param upgradeInputs Array di stringhe contenente i seguenti dati:
     *                      [0] - URL dei documenti,
     *                      [1] - nome della professione,
     *                      [2] - tariffa oraria,
     *                      [3] - anzianità (seniority),
     *                      [4] - città,
     *                      [5] - via,
     *                      [6] - numero civico,
     *                      [7] - distretto,
     *                      [8] - paese.
     * @param user          L'utente che richiede l'upgrade.
     * @return {@code true} se la richiesta di upgrade è inviata con successo, {@code false} altrimenti.
     */
    public static boolean upgradeApplication(String[] upgradeInputs, User user) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            // Crea una nuova applicazione per l'upgrade
            Application application = new Application();
            application.setUser(user);
            application.setSubmissionDate(LocalDate.now());
            application.setSubmissionHour(LocalTime.now());
            application.setDocsUrl(upgradeInputs[0]);
            application.setStatus("STATUS");
            application.setType("UPGRADE");
            em.persist(application);

            // Crea un oggetto Upgradeapplication con i dettagli dell'upgrade
            Upgradeapplication upgradeApplication = new Upgradeapplication();
            upgradeApplication.setId(application.getId());
            upgradeApplication.setApplication(application);
            upgradeApplication.setProfessionName(upgradeInputs[1]);
            upgradeApplication.setHourlyRate(new BigDecimal(upgradeInputs[2]));
            upgradeApplication.setSeniority(Integer.valueOf(upgradeInputs[3]));
            upgradeApplication.setCity(upgradeInputs[4]);
            upgradeApplication.setStreet(upgradeInputs[5]);
            upgradeApplication.setStreetNumber(Short.valueOf(upgradeInputs[6]));
            upgradeApplication.setDistrict(upgradeInputs[7]);
            upgradeApplication.setCountry(upgradeInputs[8]);
            em.persist(upgradeApplication);

            transaction.commit();
            return true;
        }
    }

    /**
     * Invia una richiesta di collaborazione (collab).
     * <p>
     * Il metodo crea una nuova applicazione di tipo "COLLAB" con stato "PENDING" e la persiste.
     * </p>
     *
     * @param user    L'utente che richiede la collaborazione.
     * @param docsUrl L'URL dei documenti relativi alla richiesta.
     * @return {@code true} se la richiesta viene inviata con successo, {@code false} altrimenti.
     */
    public static boolean collabApplication(User user, String docsUrl) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            // Crea una nuova applicazione per la collaborazione
            Application application = new Application();
            application.setUser(user);
            application.setSubmissionDate(LocalDate.now());
            application.setSubmissionHour(LocalTime.now());
            application.setDocsUrl(docsUrl);
            application.setStatus("PENDING");
            application.setType("COLLAB");
            em.persist(application);

            transaction.commit();
            return true;
        }
    }

    /**
     * Aggiorna l'account di un utente in seguito a una richiesta di upgrade.
     * <p>
     * Il metodo aggiorna il tipo dell'utente a "WORKER", approva l'applicazione di upgrade e
     * esegue l'upgrade tramite il metodo privato {@code doUpgrade}.
     * </p>
     *
     * @param user               L'utente da aggiornare.
     * @param upgradeApplication L'oggetto {@link Upgradeapplication} contenente i dettagli dell'upgrade.
     * @return {@code true} se l'upgrade viene eseguito con successo, {@code false} altrimenti.
     */
    public static boolean upgradeAccount(User user, Upgradeapplication upgradeApplication) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            // Imposta il tipo dell'utente a WORKER
            user.setType("WORKER");
            em.merge(user);

            // Approva l'applicazione di upgrade
            Application app = upgradeApplication.getApplication();
            app.setStatus("APPROVED");
            em.merge(app);

            // Esegue l'upgrade dell'account; in caso di errore, rollback e ritorna false
            if (!doUpgrade(em, user, upgradeApplication)) {
                transaction.rollback();
                return false;
            }

            transaction.commit();
            return true;
        }
    }

    /**
     * Avvia la collaborazione per un utente.
     * <p>
     * Il metodo imposta l'utente come amministratore e approva l'applicazione di collaborazione.
     * </p>
     *
     * @param user              L'utente per il quale avviare la collaborazione.
     * @param collabApplication L'applicazione di collaborazione.
     * @return {@code true} se l'operazione viene completata con successo, {@code false} altrimenti.
     */
    public static boolean startCollab(User user, Application collabApplication) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            // Imposta l'utente come amministratore
            user.setAdmin(true);
            em.merge(user);

            // Approva l'applicazione di collaborazione
            collabApplication.setStatus("APPROVED");
            em.merge(collabApplication);

            transaction.commit();
            return true;
        }
    }

    /**
     * Visualizza le informazioni personali di un utente.
     * <p>
     * Il metodo restituisce un array di stringhe contenente le informazioni personali dell'utente.
     * </p>
     *
     * @param user L'utente di cui visualizzare le informazioni.
     * @return Un array di stringhe contenente:
     *         [0] - email,
     *         [1] - nome,
     *         [2] - cognome,
     *         [3] - data di nascita,
     *         [4] - città di nascita,
     *         [5] - distretto di nascita,
     *         [6] - paese di nascita,
     *         [7] - codice fiscale,
     *         [8] - spesa totale,
     *         [9] - tipo utente,
     *         [10] - se è amministratore.
     */
    public static String[] personalInfosView(User user) {
        return new String[]{
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                String.valueOf(user.getBirthDate()),
                user.getBirthCity(),
                user.getBirthDistrict(),
                user.getBirthCountry(),
                user.getTaxCode(),
                String.valueOf(user.getTotalExpenditure()),
                user.getType(),
                String.valueOf(user.getAdmin())
        };
    }

    /**
     * Esegue l'upgrade dell'account.
     * <p>
     * Metodo privato che esegue le operazioni necessarie per l'upgrade dell'account,
     * aggiungendo l'utente all'area di attività e alla carriera tramite i metodi di {@link WorkerSide}.
     * </p>
     *
     * @param em                 L'EntityManager da utilizzare per le operazioni di persistenza.
     * @param user               L'utente da aggiornare.
     * @param upgradeApplication L'oggetto {@link Upgradeapplication} contenente i dettagli dell'upgrade.
     * @return {@code true} se tutte le operazioni sono completate con successo, {@code false} altrimenti.
     */
    private static boolean doUpgrade(EntityManager em, User user, Upgradeapplication upgradeApplication) {
        // Aggiunge l'utente all'area di attività; se fallisce, ritorna false
        if (!WorkerSide.addToActivityArea(
                em,
                user,
                upgradeApplication.getCity(),
                upgradeApplication.getStreet(),
                upgradeApplication.getStreetNumber(),
                upgradeApplication.getDistrict(),
                upgradeApplication.getRegion(),
                upgradeApplication.getCountry()
        )) {
            return false;
        }
        // Aggiunge l'utente alla carriera; ritorna true se l'operazione ha successo
        return WorkerSide.addCareer(
                em,
                user,
                upgradeApplication.getProfessionName(),
                upgradeApplication.getHourlyRate(),
                upgradeApplication.getSeniority()
        );
    }
}