package is.murmur.Model.Helpers;

import java.time.LocalTime;

/**
 * La classe {@code TimeInterval} rappresenta un intervallo di tempo definito da un orario di inizio e uno di fine.
 * <p>
 * Viene utilizzata per specificare intervalli orari nelle ricerche e nelle operazioni che richiedono la gestione di orari,
 * come il controllo delle collisioni.
 * </p>
 *
 */
public class TimeInterval {
    // Orario di inizio dell'intervallo
    private final LocalTime start;
    // Orario di fine dell'intervallo
    private final LocalTime end;

    /**
     * Costruisce un nuovo {@code TimeInterval} con l'orario di inizio e di fine specificati.
     *
     * @param start l'orario di inizio dell'intervallo
     * @param end   l'orario di fine dell'intervallo
     */
    public TimeInterval(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Restituisce l'orario di inizio dell'intervallo.
     *
     * @return l'orario di inizio come {@link LocalTime}
     */
    public LocalTime getStart() {
        return start;
    }

    /**
     * Restituisce l'orario di fine dell'intervallo.
     *
     * @return l'orario di fine come {@link LocalTime}
     */
    public LocalTime getEnd() {
        return end;
    }
}