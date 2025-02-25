package is.murmur.Services;

import is.murmur.Model.Beans.*;
import is.murmur.Model.Helpers.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

/**
 * Classe che gestisce le operazioni lato amministratore.
 * <p>
 * Questa classe fornisce metodi statici per gestire le applicazioni (upgrade, job, collaborazioni),
 * il controllo della documentazione, l'approvazione o il rifiuto delle applicazioni, il blocco/sblocco degli utenti
 * e il recupero delle informazioni sugli utenti.
 * </p>
 *
 
 */
public class AdminSide {

    /**
     * Recupera le applicazioni in attesa per un determinato tipo.
     * <p>
     * Il metodo restituisce una lista di applicazioni che sono di un certo tipo e il cui stato è "PENDING"
     * oppure "CHECKED" (se esiste un record corrispondente in Checkedapplication associato all'amministratore).
     * </p>
     *
     * @param admin L'amministratore che effettua la richiesta.
     * @param type  Il tipo di applicazione da filtrare.
     * @return Una lista di {@link Application} che corrispondono ai criteri specificati.
     */
    public static List<Application> getApplications(User admin, String type) {
        try (EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery(
                            "select a from Application a " +
                                    "where a.type = :type " +
                                    "  and ( a.status = 'PENDING' " +
                                    "        or (a.status = 'CHECKED' " +
                                    "            and exists (" +
                                    "                select cc from Checkedapplication cc " +
                                    "                where cc.application = a " +
                                    "                  and cc.admin = :admin" +
                                    "            )" +
                                    "        )" +
                                    "      )",
                            Application.class)
                    .setParameter("type", type)
                    .setParameter("admin", admin)
                    .getResultList();
        }
    }

    /**
     * Effettua il controllo della documentazione di un'applicazione.
     * <p>
     * Il metodo imposta lo stato dell'applicazione a "CHECKED" e crea un record in Checkedapplication,
     * associando l'applicazione all'amministratore che ha effettuato il controllo.
     * </p>
     *
     * @param admin       L'amministratore che controlla la documentazione.
     * @param application L'applicazione da controllare.
     * @return {@code true} se l'operazione avviene con successo, {@code false} altrimenti.
     */
    public static boolean documentationCheck(User admin, Application application) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            // Aggiorna lo stato dell'applicazione a "CHECKED"
            application.setStatus("CHECKED");
            em.merge(application);
            em.flush();

            // Crea un record in Checkedapplication associando l'applicazione all'amministratore
            Checkedapplication checkedapplication = new Checkedapplication();
            checkedapplication.setApplication(application);
            checkedapplication.setId(application.getId());
            checkedapplication.setAdmin(admin);
            em.persist(checkedapplication);

            transaction.commit();
            return true;
        }
    }

    /**
     * Approva un'applicazione.
     * <p>
     * Il metodo imposta lo stato dell'applicazione a "APPROVED" e, in base al tipo di applicazione,
     * esegue operazioni specifiche (upgrade, job, collab).
     * In caso di errori nelle operazioni specifiche, l'applicazione viene eventualmente rifiutata.
     * </p>
     *
     * @param application L'applicazione da approvare.
     * @return {@code true} se l'applicazione viene approvata con successo, {@code false} altrimenti.
     */
    public static boolean approveApplication(Application application) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            // Imposta lo stato dell'applicazione a "APPROVED"
            application.setStatus("APPROVED");
            em.merge(application);
            em.flush();

            // Esegue operazioni specifiche in base al tipo di applicazione
            switch (application.getType().toLowerCase()) {
                case "upgrade":
                    // Per l'upgrade, chiama il metodo di AccountManagement per aggiornare l'account
                    if (!AccountManagement.upgradeAccount(application.getUser(), application.getUpgradeapplication())) {
                        transaction.rollback();
                        return false;
                    }
                    break;
                case "job":
                    // Per la candidatura a lavoro, recupera il componente Jobapplication
                    Jobapplication jobComponent = em.createQuery(
                                    "select j from Jobapplication j where j.application = :application",
                                    Jobapplication.class)
                            .setParameter("application", application)
                            .getSingleResult();
                    // Aggiunge la carriera al lavoratore; in caso di errore, rifiuta l'applicazione
                    if (WorkerSide.addCareer(
                            em,
                            application.getUser(),
                            jobComponent.getProfessionName(),
                            jobComponent.getHourlyRate(),
                            jobComponent.getSeniority()
                    )) {
                        transaction.rollback();
                        return rejectApplication(application, "Errore durante l'inserimento della carriera");
                    }
                    break;
                case "collab":
                    // Per le collaborazioni, avvia la collaborazione tramite AccountManagement
                    if (AccountManagement.startCollab(application.getUser(), application)) {
                        application.setStatus("APPROVED");
                        em.merge(application);
                        em.flush();
                        transaction.commit();
                        return true;
                    }
                    break;
                default:
                    transaction.rollback();
                    return false;
            }

            transaction.commit();
            return true;
        } finally {
            em.close();
        }
    }

    /**
     * Rifiuta un'applicazione fornendo una nota di rifiuto.
     * <p>
     * Il metodo imposta lo stato dell'applicazione a "REJECTED" e crea un record in Rejectedapplication
     * con la nota di rifiuto specificata.
     * </p>
     *
     * @param application   L'applicazione da rifiutare.
     * @param rejectionNote La nota che spiega il motivo del rifiuto.
     * @return {@code false} per indicare che l'applicazione non è stata approvata.
     */
    public static boolean rejectApplication(Application application, String rejectionNote) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            // Imposta lo stato dell'applicazione a "REJECTED"
            application.setStatus("REJECTED");
            em.merge(application);
            em.flush();

            // Crea un record in Rejectedapplication con la nota di rifiuto
            Rejectedapplication rejectedapplication = new Rejectedapplication();
            rejectedapplication.setApplication(application);
            rejectedapplication.setId(application.getId());
            rejectedapplication.setRejectionNote(rejectionNote);
            em.persist(rejectedapplication);

            transaction.commit();
            return false;
        }
    }

    /**
     * Blocca o sblocca un utente in base al suo attuale stato.
     * <p>
     * Se l'utente è attualmente sbloccato, viene bloccato, e viceversa.
     * </p>
     *
     * @param toLockId L'ID dell'utente da bloccare o sbloccare.
     * @return {@code true} se l'operazione viene eseguita con successo, {@code false} altrimenti.
     */
    public static boolean lockUnlockUser(Long toLockId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            // Trova l'utente tramite il suo ID
            User toLock = em.find(User.class, toLockId);
            if (toLock != null && !toLock.getLocked()) {
                // Blocca l'utente se non è già bloccato
                toLock.setLocked(true);
            } else {
                // Sblocca l'utente se è già bloccato
                assert toLock != null;
                toLock.setLocked(false);
            }
            em.merge(toLock);
            transaction.commit();
            return true;
        }
    }

    /**
     * Recupera un utente in base alla sua email.
     * <p>
     * Il metodo esegue una query per trovare l'utente con l'email specificata.
     * </p>
     *
     * @param email L'email dell'utente da recuperare.
     * @return L'oggetto {@link User} corrispondente all'email, oppure lancia un'eccezione se non viene trovato.
     */
    public static User findUser(String email) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        return em.createQuery(
                        "select u from User u where u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
    }
}