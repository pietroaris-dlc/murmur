package is.murmur.Storage.Helpers;

import is.murmur.Storage.DAO.*;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Interfaccia per il rilevamento di collisioni di orari.
 * <p>
 * Fornisce un metodo statico per verificare se esiste una collisione tra un intervallo orario
 * specificato e gli orari giornalieri già presenti per un determinato utente.
 * </p>
 */
public interface Collision {

    /**
     * Verifica se esiste una collisione tra l'intervallo orario fornito e gli orari giornalieri
     * già registrati per l'utente nel giorno specificato.
     * <p>
     * Il metodo esegue una query per ottenere tutti gli oggetti {@link Daily} associati all'utente
     * e al giorno indicato, quindi controlla se l'intervallo fornito si sovrappone a uno qualsiasi
     * degli orari giornalieri. Una collisione viene rilevata se:
     * <ul>
     *   <li>L'orario di inizio del daily è prima dell'inizio dell'intervallo e l'orario di fine del daily è dopo l'inizio dell'intervallo.</li>
     *   <li>L'orario di inizio del daily è dopo l'inizio dell'intervallo ma prima della fine dell'intervallo.</li>
     *   <li>L'orario di inizio del daily è esattamente uguale all'inizio dell'intervallo.</li>
     *   <li>L'orario di fine del daily è esattamente uguale alla fine dell'intervallo.</li>
     * </ul>
     * </p>
     *
     * @param user     L'utente per cui controllare la collisione.
     * @param day      Il giorno per il quale verificare gli orari giornalieri.
     * @param interval L'intervallo orario da controllare.
     * @return {@code true} se viene rilevata una collisione, {@code false} altrimenti.
     */
    static boolean detect(User user, LocalDate day, TimeInterval interval) {
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            List<Daily> dailies = entityManager
                    .createQuery("select d from Planner p join Daily d on p.schedule = d.schedule where p.user = :user and d.day = :day", Daily.class)
                    .setParameter("day", day)
                    .setParameter("user", user)
                    .getResultList();
            for (Daily daily : dailies) {
                LocalTime dailyStart = daily.getStartHour();
                LocalTime dailyEnd = daily.getEndHour();
                if ((dailyStart.isBefore(interval.getStart()) && dailyEnd.isAfter(interval.getStart()))
                        || (dailyStart.isAfter(interval.getStart()) && dailyStart.isBefore(interval.getEnd()))
                        || dailyStart.equals(interval.getStart())
                        || dailyEnd.equals(interval.getEnd())) {
                    return true;
                }
            }
            return false;
        } finally {
            entityManager.close();
        }
    }
}