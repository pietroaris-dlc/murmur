package is.murmur.Storage.Helpers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;

/**
 * La classe {@code Criteria} rappresenta i criteri di ricerca per la selezione di
 * lavoratori in base a vari parametri come il tipo di schedule (daily o weekly),
 * la modalità di servizio (REMOTE, ONSITE, HOMEDELIVERY), la professione, le tariffe,
 * gli orari e la localizzazione.
 * <p>
 * Questa classe è immutabile e utilizza il pattern Builder per la sua costruzione.
 * </p>
 */
public class Criteria {
    // Campi comuni obbligatori
    private final String scheduleType; // DAILY o WEEKLY
    private final String serviceMode;  // REMOTE, ONSITE, HOMEDELIVERY
    private final String profession;
    private final double hourlyRateMin;
    private final double hourlyRateMax;

    // Campi specifici per la ricerca Daily (opzionali)
    private final LocalDate day;         // Data per Daily
    private final LocalTime dailyStartHour;
    private final LocalTime dailyEndHour;

    // Campi specifici per la ricerca Weekly (opzionali)
    private final LocalDate startDate;   // Data di inizio per Weekly
    private final LocalDate endDate;     // Data di fine per Weekly
    // Mappa dei giorni della settimana (es. "MONDAY") con relativi intervalli di tempo
    private final Map<String, TimeInterval> weeklyIntervals;

    // Campi per la notRemote (opzionali, per ricerche Onsite/HomeDelivery)
    private final String city;
    private final String street;
    private final String district;
    private final String region;
    private final String country;

    /**
     * Costruttore privato utilizzato dal Builder per creare un'istanza immutabile di {@code Criteria}.
     *
     * @param builder L'istanza del Builder contenente i valori da assegnare.
     */
    private Criteria(Builder builder) {
        this.scheduleType = builder.scheduleType;
        this.serviceMode = builder.serviceMode;
        this.profession = builder.profession;
        this.hourlyRateMin = builder.hourlyRateMin;
        this.hourlyRateMax = builder.hourlyRateMax;
        this.day = builder.day;
        this.dailyStartHour = builder.dailyStartHour;
        this.dailyEndHour = builder.dailyEndHour;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.weeklyIntervals = builder.weeklyIntervals;
        this.city = builder.city;
        this.street = builder.street;
        this.district = builder.district;
        this.region = builder.region;
        this.country = builder.country;
    }

    // Getters per tutti i campi

    /**
     * Restituisce il tipo di schedule.
     *
     * @return {@code String} che rappresenta il tipo di schedule (DAILY o WEEKLY).
     */
    public String getScheduleType() {
        return scheduleType;
    }

    /**
     * Restituisce la modalità di servizio.
     *
     * @return {@code String} che rappresenta la modalità di servizio (REMOTE, ONSITE, HOMEDELIVERY).
     */
    public String getServiceMode() {
        return serviceMode;
    }

    /**
     * Restituisce il nome della professione.
     *
     * @return {@code String} con il nome della professione.
     */
    public String getProfession() {
        return profession;
    }

    /**
     * Restituisce il valore minimo della tariffa oraria.
     *
     * @return {@code double} che rappresenta il valore minimo della tariffa oraria.
     */
    public double getHourlyRateMin() {
        return hourlyRateMin;
    }

    /**
     * Restituisce il valore massimo della tariffa oraria.
     *
     * @return {@code double} che rappresenta il valore massimo della tariffa oraria.
     */
    public double getHourlyRateMax() {
        return hourlyRateMax;
    }

    /**
     * Restituisce la data per la ricerca Daily.
     *
     * @return {@code LocalDate} della ricerca Daily.
     */
    public LocalDate getDay() {
        return day;
    }

    /**
     * Restituisce l'orario di inizio per la ricerca Daily.
     *
     * @return {@code LocalTime} che rappresenta l'orario di inizio.
     */
    public LocalTime getDailyStartHour() {
        return dailyStartHour;
    }

    /**
     * Restituisce l'orario di fine per la ricerca Daily.
     *
     * @return {@code LocalTime} che rappresenta l'orario di fine.
     */
    public LocalTime getDailyEndHour() {
        return dailyEndHour;
    }

    /**
     * Restituisce la data di inizio per la ricerca Weekly.
     *
     * @return {@code LocalDate} che rappresenta la data di inizio.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Restituisce la data di fine per la ricerca Weekly.
     *
     * @return {@code LocalDate} che rappresenta la data di fine.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Restituisce la mappa degli intervalli settimanali.
     *
     * @return Una mappa {@code Map<String, TimeInterval>} contenente i giorni della settimana e relativi intervalli.
     */
    public Map<String, TimeInterval> getWeeklyIntervals() {
        return weeklyIntervals;
    }

    /**
     * Restituisce la città per la ricerca basata sulla notRemote.
     *
     * @return {@code String} che rappresenta la città.
     */
    public String getCity() {
        return city;
    }

    /**
     * Restituisce la via per la ricerca basata sulla notRemote.
     *
     * @return {@code String} che rappresenta la via.
     */
    public String getStreet() {
        return street;
    }

    /**
     * Restituisce il distretto per la ricerca basata sulla notRemote.
     *
     * @return {@code String} che rappresenta il distretto.
     */
    public String getDistrict() {
        return district;
    }

    /**
     * Restituisce la regione per la ricerca basata sulla notRemote.
     *
     * @return {@code String} che rappresenta la regione.
     */
    public String getRegion() {
        return region;
    }

    /**
     * Restituisce il paese per la ricerca basata sulla notRemote.
     *
     * @return {@code String} che rappresenta il paese.
     */
    public String getCountry() {
        return country;
    }

    /**
     * La classe Builder per costruire istanze di {@link Criteria}.
     * <p>
     * Il Builder richiede alcuni campi obbligatori e permette di impostare in modo facoltativo
     * campi specifici per la ricerca Daily, Weekly e per la localizzazione.
     * </p>
     */
    public static class Builder {
        // Campi comuni obbligatori
        private final String scheduleType; // "DAILY" o "WEEKLY"
        private final String serviceMode;  // "REMOTE", "ONSITE", "HOMEDELIVERY"
        private final String profession;
        private final double hourlyRateMin;
        private final double hourlyRateMax;

        // Campi opzionali per Daily
        private LocalDate day = null;
        private LocalTime dailyStartHour = null;
        private LocalTime dailyEndHour = null;

        // Campi opzionali per Weekly
        private LocalDate startDate = null;
        private LocalDate endDate = null;
        private Map<String, TimeInterval> weeklyIntervals = Collections.emptyMap();

        // Campi opzionali per la notRemote
        private String city = "";
        private String street = "";
        private String district = "";
        private String region = "";
        private String country = "";

        /**
         * Il costruttore del Builder richiede i campi comuni obbligatori.
         *
         * @param scheduleType  Il tipo di schedule ("DAILY" o "WEEKLY").
         * @param serviceMode   La modalità di servizio ("REMOTE", "ONSITE", "HOMEDELIVERY").
         * @param profession    Il nome della professione.
         * @param hourlyRateMin La tariffa oraria minima.
         * @param hourlyRateMax La tariffa oraria massima.
         */
        public Builder(String scheduleType, String serviceMode, String profession, double hourlyRateMin, double hourlyRateMax) {
            this.scheduleType = scheduleType;
            this.serviceMode = serviceMode;
            this.profession = profession;
            this.hourlyRateMin = hourlyRateMin;
            this.hourlyRateMax = hourlyRateMax;
        }

        /**
         * Imposta i campi specifici per una ricerca Daily.
         *
         * @param day             La data per la ricerca.
         * @param dailyStartHour  L'orario di inizio.
         * @param dailyEndHour    L'orario di fine.
         * @return Il Builder aggiornato.
         */
        public Builder daily(LocalDate day, LocalTime dailyStartHour, LocalTime dailyEndHour) {
            this.day = day;
            this.dailyStartHour = dailyStartHour;
            this.dailyEndHour = dailyEndHour;
            return this;
        }

        /**
         * Imposta i campi specifici per una ricerca Weekly.
         *
         * @param startDate       La data di inizio della ricerca.
         * @param endDate         La data di fine della ricerca.
         * @param weeklyIntervals Una mappa contenente i giorni della settimana (es. "MONDAY") e gli intervalli orari.
         * @return Il Builder aggiornato.
         */
        public Builder weekly(LocalDate startDate, LocalDate endDate, Map<String, TimeInterval> weeklyIntervals) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.weeklyIntervals = weeklyIntervals;
            return this;
        }

        /**
         * Imposta i campi relativi alla notRemote per ricerche Onsite o HomeDelivery.
         *
         * @param city     La città.
         * @param street   La via.
         * @param district Il distretto.
         * @param region   La regione.
         * @param country  Il paese.
         * @return Il Builder aggiornato.
         */
        public Builder notRemote(String city, String street, String district, String region, String country) {
            this.city = city;
            this.street = street;
            this.district = district;
            this.region = region;
            this.country = country;
            return this;
        }

        /**
         * Costruisce un'istanza immutabile di {@link Criteria} utilizzando i valori impostati.
         *
         * @return Un nuovo oggetto {@code Criteria}.
         */
        public Criteria build() {
            return new Criteria(this);
        }
    }
}