package is.murmur.Model.Helpers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;

public class Criteria {
    // Campi comuni obbligatori
    private final String scheduleType; // DAILY o WEEKLY
    private final String serviceMode;    // REMOTE, ONSITE, HOMEDELIVERY
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

    // Campi per la location (opzionali, per ricerche Onsite/HomeDelivery)
    private final String city;
    private final String street;
    private final String district;
    private final String region;
    private final String country;

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

    // Getters per tutti i campi (omessi per brevità)
    public String getScheduleType() { return scheduleType; }
    public String getServiceMode() { return serviceMode; }
    public String getProfession() { return profession; }
    public double getHourlyRateMin() { return hourlyRateMin; }
    public double getHourlyRateMax() { return hourlyRateMax; }
    public LocalDate getDay() { return day; }
    public LocalTime getDailyStartHour() { return dailyStartHour; }
    public LocalTime getDailyEndHour() { return dailyEndHour; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Map<String, TimeInterval> getWeeklyIntervals() { return weeklyIntervals; }
    public String getCity() { return city; }
    public String getStreet() { return street; }
    public String getDistrict() { return district; }
    public String getRegion() { return region; }
    public String getCountry() { return country; }

    public static class Builder {
        // Campi comuni obbligatori
        private final String scheduleType; // "daily" o "weekly"
        private final String serviceMode;    // "REMOTE", "ONSITE", "HOMEDELIVERY"
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

        // Campi opzionali per la location
        private String city = "";
        private String street = "";
        private String district = "";
        private String region = "";
        private String country = "";

        /**
         * Il costruttore del Builder richiede i campi comuni obbligatori:
         * scheduleType, serviceMode, profession, hourlyRateMin e hourlyRateMax.
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
         * @param startDate Data di inizio.
         * @param endDate Data di fine.
         * @param weeklyIntervals Mappa dei giorni (es. "MONDAY") e relativi intervalli.
         */
        public Builder weekly(LocalDate startDate, LocalDate endDate, Map<String, TimeInterval> weeklyIntervals) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.weeklyIntervals = weeklyIntervals;
            return this;
        }

        /**
         * Imposta i campi relativi alla location (per ricerche Onsite o HomeDelivery).
         */
        public Builder location(String city, String street, String district, String region, String country) {
            this.city = city;
            this.street = street;
            this.district = district;
            this.region = region;
            this.country = country;
            return this;
        }

        public Criteria build() {
            return new Criteria(this);
        }
    }
}