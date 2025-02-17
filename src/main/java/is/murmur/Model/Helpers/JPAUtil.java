package is.murmur.Model.Helpers;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * La classe {@code JPAUtil} fornisce metodi statici per gestire la creazione e la chiusura
 * dell'EntityManagerFactory utilizzato per interagire con il database tramite JPA.
 * <p>
 * Viene creato un singleton dell'EntityManagerFactory basato sull'unità di persistenza definita
 * (in questo caso "MyPersistenceUnit").
 * </p>
 *
 
 */
public class JPAUtil {

    /**
     * Singleton dell'EntityManagerFactory, creato una sola volta durante il ciclo di vita dell'applicazione.
     */
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyPersistenceUnit");

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
}