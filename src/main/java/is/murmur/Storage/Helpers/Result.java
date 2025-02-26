package is.murmur.Storage.Helpers;

import is.murmur.Storage.DAO.Career;
import is.murmur.Storage.DAO.User;

/**
 * La classe {@code Result} rappresenta un risultato della ricerca di lavoratori.
 * <p>
 * Ogni oggetto {@code Result} contiene informazioni sull'utente lavoratore,
 * la sua carriera e, opzionalmente, il numero civico associato alla sua attività.
 * </p>
 *
 
 */
public class Result {
    private final User worker;
    private final Career career;
    private final Short streetNumber;

    /**
     * Costruttore per creare un {@code Result} con informazioni complete.
     *
     * @param worker       L'utente lavoratore.
     * @param career       La carriera associata al lavoratore.
     * @param streetNumber Il numero civico (opzionale) relativo alla localizzazione del lavoratore.
     */
    public Result(User worker, Career career, Short streetNumber) {
        this.worker = worker;
        this.career = career;
        this.streetNumber = streetNumber;
    }

    /**
     * Costruttore per creare un {@code Result} senza il numero civico.
     *
     * @param worker L'utente lavoratore.
     * @param career La carriera associata al lavoratore.
     */
    public Result(User worker, Career career) {
        this.worker = worker;
        this.career = career;
        this.streetNumber = null;
    }

    /**
     * Restituisce l'utente lavoratore associato al risultato.
     *
     * @return l'oggetto {@link User} rappresentante il lavoratore.
     */
    public User getWorker() {
        return worker;
    }

    /**
     * Restituisce la carriera associata al risultato.
     *
     * @return l'oggetto {@link Career} relativo al lavoratore.
     */
    public Career getCareer() {
        return career;
    }

    /**
     * Restituisce il numero civico associato al risultato, se presente.
     *
     * @return il numero civico come {@link Short}, oppure {@code null} se non impostato.
     */
    public Short getStreetNumber() {
        return streetNumber;
    }
}