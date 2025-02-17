package is.murmur.Model.Helpers;

import is.murmur.Model.Beans.*;

import java.util.List;

/**
 * La classe {@code DraftBuffer} rappresenta un contenitore per una bozza di contratto,
 * includendo il contratto stesso e le entità associate quali Dailycontract, Weeklycontract,
 * Weekdaycontract e Notremotecontract (se applicabile).
 * <p>
 * Questa classe utilizza il pattern Builder per facilitare la sua costruzione.
 * </p>
 *
 * @see DraftBuffer.Builder
 */
public class DraftBuffer {

    private final Contract draft;
    private final User worker;
    private final Dailycontract dailyContract;
    private final Weeklycontract weeklyContract;
    private final List<Weekdaycontract> weekdaysContract;
    private final Notremotecontract notremotecontract;

    /**
     * Costruttore privato utilizzato dal Builder per creare un'istanza di {@code DraftBuffer}.
     *
     * @param builder Il Builder contenente i dati necessari per la costruzione.
     */
    private DraftBuffer(Builder builder) {
        this.draft = builder.draft;
        this.worker = builder.worker;
        this.dailyContract = builder.dailyContract;
        this.weeklyContract = builder.weeklyContract;
        this.weekdaysContract = builder.weekdaysContract;
        this.notremotecontract = builder.notremotecontract;
    }

    /**
     * Restituisce il contratto in bozza.
     *
     * @return Il {@link Contract} associato alla bozza.
     */
    public Contract getDraft() {
        return draft;
    }

    /**
     * Restituisce il lavoratore associato al draft.
     *
     * @return L'oggetto {@link User} rappresentante il lavoratore.
     */
    public User getWorker() {
        return worker;
    }

    /**
     * Restituisce il Dailycontract associato al draft, se presente.
     *
     * @return Il {@link Dailycontract} oppure {@code null} se non è stato impostato.
     */
    public Dailycontract getDailyContract() {
        return dailyContract;
    }

    /**
     * Restituisce il Weeklycontract associato al draft, se presente.
     *
     * @return Il {@link Weeklycontract} oppure {@code null} se non è stato impostato.
     */
    public Weeklycontract getWeeklyContract() {
        return weeklyContract;
    }

    /**
     * Restituisce la lista dei Weekdaycontract associati al draft, se presente.
     *
     * @return Una lista di {@link Weekdaycontract} oppure {@code null} se non è stata impostata.
     */
    public List<Weekdaycontract> getWeekdaysContract() {
        return weekdaysContract;
    }

    /**
     * Restituisce il Notremotecontract associato al draft, se presente.
     *
     * @return Il {@link Notremotecontract} oppure {@code null} se non è stato impostato.
     */
    public Notremotecontract getNotremotecontract() {
        return notremotecontract;
    }

    /**
     * Builder per la classe {@code DraftBuffer}.
     * <p>
     * Utilizza il pattern Builder per creare istanze di {@code DraftBuffer} in maniera flessibile.
     * Il Builder richiede i campi obbligatori {@code worker} e {@code draft} e permette
     * di impostare in modo facoltativo il Dailycontract, il Weeklycontract (insieme ai relativi Weekdaycontract)
     * e il Notremotecontract.
     * </p>
     */
    public static class Builder {

        private final Contract draft;
        private final User worker;
        private Dailycontract dailyContract = null;
        private Weeklycontract weeklyContract = null;
        private List<Weekdaycontract> weekdaysContract = null;
        private Notremotecontract notremotecontract = null;

        /**
         * Costruttore del Builder.
         *
         * @param worker Il lavoratore associato al draft.
         * @param draft  Il {@link Contract} in bozza.
         */
        public Builder(User worker, Contract draft) {
            this.draft = draft;
            this.worker = worker;
        }

        /**
         * Imposta il Dailycontract per il draft.
         *
         * @param dailyContract Il {@link Dailycontract} da associare.
         * @return Il Builder aggiornato.
         */
        public Builder dailyContract(Dailycontract dailyContract) {
            this.dailyContract = dailyContract;
            return this;
        }

        /**
         * Imposta il Weeklycontract e la lista dei Weekdaycontract per il draft.
         *
         * @param weeklyContract  Il {@link Weeklycontract} da associare.
         * @param weekdaysContract La lista di {@link Weekdaycontract} da associare.
         * @return Il Builder aggiornato.
         */
        public Builder weeklyContract(Weeklycontract weeklyContract, List<Weekdaycontract> weekdaysContract) {
            this.weeklyContract = weeklyContract;
            this.weekdaysContract = weekdaysContract;
            return this;
        }

        /**
         * Imposta il Notremotecontract per il draft.
         *
         * @param notremotecontract Il {@link Notremotecontract} da associare.
         * @return Il Builder aggiornato.
         */
        public Builder notRemoteContract(Notremotecontract notremotecontract) {
            this.notremotecontract = notremotecontract;
            return this;
        }

        /**
         * Costruisce l'istanza di {@link DraftBuffer} con i dati impostati.
         *
         * @return Un nuovo oggetto {@code DraftBuffer}.
         */
        public DraftBuffer build() {
            return new DraftBuffer(this);
        }
    }
}