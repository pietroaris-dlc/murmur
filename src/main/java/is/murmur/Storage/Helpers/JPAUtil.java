package is.murmur.Storage.Helpers;

import jakarta.persistence.Cache;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.Query;
import jakarta.persistence.SynchronizationType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.metamodel.Metamodel;

import java.util.HashMap;
import java.util.Map;

/**
 * La classe {@code JPAUtil} fornisce metodi statici per gestire la creazione e la chiusura
 * dell'EntityManagerFactory utilizzato per interagire con il database tramite JPA.
 * <p>
 * Viene creato un singleton dell'EntityManagerFactory basato sull'unità di persistenza definita
 * (in questo caso "MyPersistenceUnit").
 * </p>
 */
public class JPAUtil {

    private static final EntityManagerFactory emf;

    static {
        // Se la proprietà di sistema "test.mode" è impostata su "true", utilizziamo uno stub.
        if ("true".equals(System.getProperty("test.mode"))) {
            emf = new DummyEntityManagerFactory() {
                @Override
                public EntityManager createEntityManager() {
                    return super.createEntityManager();
                }
            };
        } else {
            emf = Persistence.createEntityManagerFactory("MyPersistenceUnit");
        }
    }

    /**
     * Restituisce l'istanza dell'EntityManagerFactory.
     *
     * @return l'EntityManagerFactory
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    /**
     * Chiude l'EntityManagerFactory se è aperto, liberando le risorse associate.
     */
    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    // Stub utilizzato durante i test
    private abstract static class DummyEntityManagerFactory implements EntityManagerFactory {

        @Override
        public EntityManager createEntityManager() {
            throw new UnsupportedOperationException("createEntityManager() is not supported in test mode.");
        }

        @Override
        public EntityManager createEntityManager(Map map) {
            throw new UnsupportedOperationException("createEntityManager(Map) is not supported in test mode.");
        }

        @Override
        public EntityManager createEntityManager(SynchronizationType synchronizationType) {
            throw new UnsupportedOperationException("createEntityManager(SynchronizationType) is not supported in test mode.");
        }

        @Override
        public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
            throw new UnsupportedOperationException("createEntityManager(SynchronizationType, Map) is not supported in test mode.");
        }

        @Override
        public CriteriaBuilder getCriteriaBuilder() {
            throw new UnsupportedOperationException("getCriteriaBuilder() is not supported in test mode.");
        }

        @Override
        public Metamodel getMetamodel() {
            throw new UnsupportedOperationException("getMetamodel() is not supported in test mode.");
        }

        @Override
        public boolean isOpen() {
            return true;
        }

        @Override
        public void close() {
            // No-op in test mode.
        }

        @Override
        public Map<String, Object> getProperties() {
            return new HashMap<>();
        }

        @Override
        public Cache getCache() {
            throw new UnsupportedOperationException("getCache() is not supported in test mode.");
        }

        @Override
        public PersistenceUnitUtil getPersistenceUnitUtil() {
            throw new UnsupportedOperationException("getPersistenceUnitUtil() is not supported in test mode.");
        }

        @Override
        public void addNamedQuery(String name, Query query) {
            throw new UnsupportedOperationException("addNamedQuery() is not supported in test mode.");
        }

        @Override
        public <T> T unwrap(Class<T> cls) {
            throw new UnsupportedOperationException("unwrap() is not supported in test mode.");
        }

        @Override
        public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
            throw new UnsupportedOperationException("addNamedEntityGraph() is not supported in test mode.");
        }
    }
}