package is.murmur.Model.Services;


import is.murmur.Model.Entities.*;
import is.murmur.Model.Enums.ApplicationStatus;
import is.murmur.Model.Enums.ApplicationType;
import is.murmur.Model.Enums.UserType;
import is.murmur.Model.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class AdminSide {

    public static List<Application> getApplications(Registereduser admin) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            TypedQuery<Application> query = em.createQuery(
                    "SELECT a FROM Application a WHERE a.user = :adminId", Application.class);
            query.setParameter("adminId", admin.getId());
            if(query.getSingleResult().getStatus() == String.valueOf(ApplicationStatus.PENDING) )
                return query.getResultList();
            if( query.getSingleResult().getStatus() == String.valueOf(ApplicationStatus.CHECKED ) ){

                return query.getResultList();

            }
            return query.getResultList();

        } catch (Exception e) {
            return null;

        }
    }
    public static boolean documentationCheck(Registereduser admin, Application application) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Checkedcomponent chkdcmp = new Checkedcomponent();
            if (application.getStatus() == String.valueOf(ApplicationStatus.PENDING)) {

                chkdcmp.setAdmin(admin);
                chkdcmp.setApplication(application);

                application.setStatus(String.valueOf(ApplicationStatus.CHECKED));
                em.persist(chkdcmp);
                em.persist(application);

                transaction.commit();

                return true;
            }
            return false;

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            em.close();
        }


    }

    public static boolean defineApplication(Application application, boolean state) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            Application a = new Application();

            a.setStatus(application.getStatus());

            // Update the approval status of the application
            if(state == true) {
                a.setStatus(String.valueOf(ApplicationStatus.APPROVED));
            }else if (state == false) {
                a.setStatus(String.valueOf(ApplicationStatus.REJECTED ));
            }

            em.merge(a);

            // Commit transaction to save changes
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
                return false;
            }
        } finally {
            em.close();
        }

        return false;
    }



    public static boolean lockUser( Registereduser admin) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            // Fetch the specific user using a JPQL query
            TypedQuery<Registereduser> query = em.createQuery(
                    "SELECT u FROM Registereduser u WHERE u.id = :adminId", Registereduser.class
            );
            query.setParameter("adminId", admin.getId());
            List<Registereduser> admins = query.getResultList();
            if(admins.isEmpty()) {
                return false;
            }

            Registereduser u = new Registereduser();
            u.setLocked(true);
            em.merge(u);
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

    public static boolean unlockUser( Registereduser admin ) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            // Fetch the specific user using a JPQL query
            TypedQuery<Registereduser> query = em.createQuery(
                    "SELECT u FROM Registereduser u WHERE u.id = :adminId", Registereduser.class
            );
            query.setParameter("adminId", admin.getId());
            List<Registereduser> admins = query.getResultList();
            if(admins.isEmpty()) {
                return false;
            }

            Registereduser u = new Registereduser();
            u.setLocked(false);
            em.merge(u);
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

}
