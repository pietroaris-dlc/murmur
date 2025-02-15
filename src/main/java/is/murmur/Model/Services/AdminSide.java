package is.murmur.Model.Services;


import is.murmur.Model.Beans.*;
import is.murmur.Model.Helpers.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class AdminSide {

    public static List<Application> getApplications(Registereduser admin, String type) {
        try (EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery(
                            "select a from Application a " +
                                    "where a.type = :type " +
                                    "  and ( a.status = 'PENDING' " +
                                    "        or (a.status = 'CHECKED' " +
                                    "            and exists (" +
                                    "                select cc from Checkedcomponent cc " +
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

    public static boolean documentationCheck(Registereduser admin, Application application) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            application.setStatus("CHECKED");
            em.merge(application);
            em.flush();

            Checkedcomponent checkedcomponent;
            checkedcomponent = new Checkedcomponent();
            checkedcomponent.setApplication(application);
            checkedcomponent.setId(application.getId());
            checkedcomponent.setAdmin(admin);
            em.persist(checkedcomponent);

            transaction.commit();
            return true;
        }
    }

    public static boolean approveApplication(Application application) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            application.setStatus("APPROVED");
            em.merge(application);
            em.flush();
            switch (application.getType().toLowerCase()) {
                case "upgrade":
                    Upgradecomponent upgradeComponent = em.createQuery(
                            "select up from Upgradecomponent up where up.application = :application",
                            Upgradecomponent.class)
                            .setParameter("application", application)
                            .getSingleResult();
                    if(WorkerSide.addCareer(
                            application.getUser(),
                            em.find(Profession.class, upgradeComponent.getProfessionName()),
                            upgradeComponent.getHourlyRate(),
                            upgradeComponent.getSeniority()
                    ) == null) {
                        transaction.rollback();
                        return rejectApplication(application, "Errore durante l'inserimento della carriera");
                    }
                    if(WorkerSide.addToActivityArea(
                            application.getUser(),
                            upgradeComponent.getCity(),
                            upgradeComponent.getStreet(),
                            upgradeComponent.getStreetNumber(),
                            upgradeComponent.getDistrict(),
                            upgradeComponent.getRegion(),
                            upgradeComponent.getCountry()
                    )==null) {
                        transaction.rollback();
                        return rejectApplication(application, "Errore durante l'inserimento della location");
                    }
                    break;
                case "job":
                    Jobcomponent jobComponent = em.createQuery(
                            "select j from Jobcomponent j where j.application = :application",
                            Jobcomponent.class)
                            .setParameter("application", application)
                            .getSingleResult();
                    if(WorkerSide.addCareer(
                        application.getUser(),
                        em.find(Profession.class, jobComponent.getProfessionName()),
                        jobComponent.getHourlyRate(),
                        jobComponent.getSeniority()
                    ) == null){
                        transaction.rollback();
                        return rejectApplication(application, "Errore durante l'inserimento della carriera");
                    }
                    break;
                case "collab":
                    application.setStatus("REJECTED");
                    em.merge(application);
                    em.flush();
                    Registereduser user = application.getUser();
                    user.setAdmin(true);
                    em.merge(user);
                    em.flush();
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

    public static boolean rejectApplication(Application application, String rejectionNote) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            application.setStatus("REJECTED");
            em.merge(application);
            em.flush();
            Rejectedcomponent rejectedcomponent = new Rejectedcomponent();
            rejectedcomponent.setApplication(application);
            rejectedcomponent.setId(application.getId());
            rejectedcomponent.setRejectionNote(rejectionNote);
            em.persist(rejectedcomponent);
            transaction.commit();
            return false;
        }
    }

    public static boolean lockUnlockUser(Long toLockId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try (em) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            Registereduser toLock = em.find(Registereduser.class, toLockId);
            if (toLock != null && !toLock.getLocked()) {
                toLock.setLocked(true);
            } else {
                assert toLock != null;
                toLock.setLocked(false);
            }
            em.merge(toLock);
            transaction.commit();
            return true;
        }
    }

    public static Registereduser getUser(String email){
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        return em.createQuery(
                "select u from Registereduser u where u.email = :email"
                ,Registereduser.class
        )
                .setParameter("email", email)
                .getSingleResult();
    }
}
