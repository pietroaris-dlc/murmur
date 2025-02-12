package is.murmur.Model.Services;

import is.murmur.Model.Entities.*;
import is.murmur.Model.Enums.ApplicationStatus;
import is.murmur.Model.Enums.ApplicationType;
import is.murmur.Model.Enums.*;
import is.murmur.Model.Enums.UserType;
import is.murmur.Model.Helpers.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ContractsManagement {
    public static List<Contract> getDrafts(Registereduser registeredUser) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {

            TypedQuery<Alias> aliasQuery = em.createQuery(
                    "SELECT a FROM Alias a WHERE a.user.id = :userId", Alias.class);
            aliasQuery.setParameter("userId", registeredUser.getId());
            List<Alias> aliases = aliasQuery.getResultList();

            if (aliases.isEmpty()) {
                return null;
            }

            TypedQuery<Contract> contractQuery = em.createQuery(
                    "SELECT c FROM Contract c WHERE c.status = :status AND c.clientAlias IN :aliases",
                    Contract.class);
            contractQuery.setParameter("status", ContractStatus.DRAFT);
            contractQuery.setParameter("aliases", aliases);

            return contractQuery.getResultList();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static List<Contract> getOffers (Registereduser registeredUser){
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {

            TypedQuery<Alias> aliasQuery = em.createQuery(
                    "SELECT a FROM Alias a WHERE a.user.id = :userId", Alias.class);
            aliasQuery.setParameter("userId", registeredUser.getId());
            List<Alias> aliases = aliasQuery.getResultList();

            if (aliases.isEmpty()) {
                return null;
            }

            TypedQuery<Contract> contractQuery = em.createQuery(
                    "SELECT c FROM Contract c WHERE c.status = :status AND c.clientAlias IN :aliases",
                    Contract.class);
            contractQuery.setParameter("status", ContractStatus.OFFER);
            contractQuery.setParameter("aliases", aliases);

            return contractQuery.getResultList();
        }
        catch (Exception e) {
            e.printStackTrace(); //da inserire miglior gestione dell'errore
            return null;
        } finally {
            em.close();
        }
    }

    public static List<Contract> getActiveContracts(Registereduser registeredUser){
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {

            TypedQuery<Alias> aliasQuery = em.createQuery(
                    "SELECT a FROM Alias a WHERE a.user.id = :userId", Alias.class);
            aliasQuery.setParameter("userId", registeredUser.getId());
            List<Alias> aliases = aliasQuery.getResultList();

            if (aliases.isEmpty()) {
                return null;
            }

            TypedQuery<Contract> contractQuery = em.createQuery(
                    "SELECT c FROM Contract c WHERE c.status = :status AND c.clientAlias IN :aliases",
                    Contract.class);
            contractQuery.setParameter("status", ContractStatus.ACTIVE);
            contractQuery.setParameter("aliases", aliases);

            return contractQuery.getResultList();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static List<Contract> getExpiredContracts(Registereduser registeredUser){
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {

            TypedQuery<Alias> aliasQuery = em.createQuery(
                    "SELECT a FROM Alias a WHERE a.user.id = :userId", Alias.class);
            aliasQuery.setParameter("userId", registeredUser.getId());
            List<Alias> aliases = aliasQuery.getResultList();

            if (aliases.isEmpty()) {
                return null;
            }

            TypedQuery<Contract> contractQuery = em.createQuery(
                    "SELECT c FROM Contract c WHERE c.status = :status AND c.clientAlias IN :aliases",
                    Contract.class);
            contractQuery.setParameter("status", ContractStatus.EXPIRED);
            contractQuery.setParameter("aliases", aliases);

            return contractQuery.getResultList();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static Review getReview(Contract contract){
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            if (!"EXPIRED".equals(contract.getStatus()))
                return null;

            TypedQuery<Review> reviewQuery = em.createQuery(
                    "SELECT r FROM Review r WHERE r.contract = :contract",
                    Review.class);
            reviewQuery.setParameter("contract", contract);
            return reviewQuery.getSingleResult();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public static boolean defineOffer(Contract contract, boolean approval){
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            if (!"OFFER".equals(contract.getStatus()))
                return false;

            if (approval)
                contract.setStatus("ACTIVE");
            else contract.setStatus("REJECTED");

            em.merge(contract);
            transaction.commit();
            return true;
        }
        catch (Exception e) {
            if (transaction.isActive())
                transaction.rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public static Cancellationrequest sendCancellationRequest(Contract contract, String description) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            if (!ContractStatus.ACTIVE.equals(contract.getStatus())) {
                return null;
            }
            TypedQuery<Cancellationrequest> query = em.createQuery(
                    "SELECT c FROM Cancellationrequest c WHERE c.contract = :contract",
                    Cancellationrequest.class);
            query.setParameter("contract", contract);
            List<Cancellationrequest> existingRequests = query.getResultList();
            if (!existingRequests.isEmpty()) {
                return null;
            }

            Cancellationrequest cancellationRequest = new Cancellationrequest();
            cancellationRequest.setContract(contract);
            cancellationRequest.setId(contract.getId());
            cancellationRequest.setSubmissionDate(Instant.now());
            cancellationRequest.setDescription(description);
            cancellationRequest.setStatus(CancellationRequestStatus.PENDING.name());

            em.persist(cancellationRequest);
            transaction.commit();
            return cancellationRequest;
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

    public static Cancellationrequest getCancellationRequest(Contract contract) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            TypedQuery<Cancellationrequest> query = em.createQuery(
                    "SELECT cr FROM Cancellationrequest cr WHERE cr.contract = :contract",
                    Cancellationrequest.class
            );
            query.setParameter("contract", contract);
            return query.getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }


    public static boolean defineCancellationRequests(Cancellationrequest request, boolean approve) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            Cancellationrequest managedRequest = em.find(Cancellationrequest.class, request.getId());
            if (managedRequest == null) {
                return false;
            }

            if (approve) {
                managedRequest.setStatus("APPROVED");
                Contract contract = managedRequest.getContract();
                if (contract != null) {
                    contract.setStatus("REJECTED");
                    em.merge(contract);
                }
            } else {
                managedRequest.setStatus("REJECTED");
            }

            em.merge(managedRequest);
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

    public static boolean deleteDraft(Contract contract) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            if (!"DRAFT".equals(contract.getStatus())) {
                return false;
            }
            Contract managedContract = em.find(Contract.class, contract.getId());
            if (managedContract != null) {
                em.remove(managedContract);
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

    public static Contract writeDraft(String[] args) {
        if (args == null || args.length < 10) {
            return null;
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            Long contractId = Long.parseLong(args[0]);
            String profession = args[1];
            BigDecimal hourlyRate = new BigDecimal(args[2]);
            Long clientAliasId = Long.parseLong(args[3]);
            Long clientAliasUserId = Long.parseLong(args[4]);
            Long workerAliasId = Long.parseLong(args[5]);
            Long workerAliasUserId = Long.parseLong(args[6]);
            Long scheduleId = Long.parseLong(args[7]);
            BigDecimal totalFee = new BigDecimal(args[8]);
            String serviceMode = args[9];

            TypedQuery<Alias> clientAliasQuery = em.createQuery(
                    "SELECT a FROM Alias a WHERE a.id = :aliasId AND a.user.id = :userId", Alias.class);
            clientAliasQuery.setParameter("aliasId", clientAliasId);
            clientAliasQuery.setParameter("userId", clientAliasUserId);
            Alias clientAlias = clientAliasQuery.getSingleResult();

            TypedQuery<Alias> workerAliasQuery = em.createQuery(
                    "SELECT a FROM Alias a WHERE a.id = :aliasId AND a.user.id = :userId", Alias.class);
            workerAliasQuery.setParameter("aliasId", workerAliasId);
            workerAliasQuery.setParameter("userId", workerAliasUserId);
            Alias workerAlias = workerAliasQuery.getSingleResult();

            Contract contract = new Contract();
            contract.setId(contractId);
            contract.setProfession(profession);
            contract.setHourlyRate(hourlyRate);
            contract.setClientAlias(clientAlias);
            contract.setWorkerAlias(workerAlias);
            contract.setScheduleId(scheduleId);
            contract.setTotalFee(totalFee);
            contract.setServiceMode(serviceMode);
            contract.setStatus("DRAFT");

            em.persist(contract);
            transaction.commit();
            return contract;
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


    public static boolean sendOffer(Contract contract) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            if (!ContractStatus.DRAFT.equals(contract.getStatus())) {
                return false;
            }
            contract.setStatus("OFFER");
            em.merge(contract);
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

}
