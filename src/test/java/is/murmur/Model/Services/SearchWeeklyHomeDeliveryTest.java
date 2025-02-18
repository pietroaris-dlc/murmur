package is.murmur.Model.Services;

import is.murmur.Model.Helpers.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per verificare i controlli sui parametri di ricerca per la consegna a domicilio settimanale.
 * Questa classe contiene diversi metodi di test che validano la correttezza dei campi di input
 * per la ricerca di lavoratori con servizio HOMEDELIVERY in modalità WEEKLY.
 */
class SearchWeeklyHomeDeliveryTest {

    /**
     * Restituisce una mappa di intervalli settimanali "validi" per i test non focalizzati
     * sui controlli degli intervalli. In questo caso si usano orari uguali per evitare errori.
     *
     * @return mappa contenente un intervallo valido per MONDAY.
     */
    private Map<String, TimeInterval> getValidWeekdays() {
        Map<String, TimeInterval> weekdays = new HashMap<>();
        weekdays.put("MONDAY", new TimeInterval(LocalTime.of(10, 0), LocalTime.of(10, 0)));
        return weekdays;
    }

    /**
     * Restituisce una mappa con un intervallo non valido (l'orario di fine è successivo a quello di inizio)
     * per innescare il controllo di validità.
     *
     * @return mappa contenente un intervallo non valido per MONDAY.
     */
    private Map<String, TimeInterval> getInvalidWeekdays() {
        Map<String, TimeInterval> weekdays = new HashMap<>();
        weekdays.put("MONDAY", new TimeInterval(LocalTime.of(10, 0), LocalTime.of(11, 0)));
        return weekdays;
    }

    /**
     * Metodo helper per ottenere una città valida.
     *
     * @return una stringa contenente una città valida.
     */
    private String validCity() {
        return "Fisciano";
    }

    /**
     * Metodo helper per ottenere una street valida.
     *
     * @return una stringa contenente una street valida.
     */
    private String validStreet() {
        return "Via Giovanni Paolo II";
    }

    /**
     * Metodo helper per ottenere un district valido.
     *
     * @return una stringa contenente un district valido.
     */
    private String validDistrict() {
        return "Salerno";
    }

    /**
     * Metodo helper per ottenere una region valida.
     *
     * @return una stringa contenente una region valida.
     */
    private String validRegion() {
        return "Campania";
    }

    /**
     * Metodo helper per ottenere un country valido.
     *
     * @return una stringa contenente un country valido.
     */
    private String validCountry() {
        return "Italia";
    }

    /**
     * Testa la validazione del campo "profession" quando è null.
     * Ci si aspetta il messaggio d'errore "profession is required".
     */
    @Test
    void testProfessionNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", null, 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("profession is required", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "profession" quando è una stringa vuota.
     * Ci si aspetta il messaggio d'errore "profession is required".
     */
    @Test
    void testProfessionEmpty() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("profession is required", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "profession" quando contiene cifre.
     * La stringa "Idraulico23" contiene un digit e deve essere rifiutata.
     * Ci si aspetta il messaggio d'errore "profession contains digits".
     */
    @Test
    void testProfessionContainsDigits() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        // "Idraulico23" contiene un digit
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico23", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("profession contains digits", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "profession" quando contiene caratteri speciali.
     * La stringa "Idr@ulico" contiene il carattere '@' e deve essere rifiutata.
     * Ci si aspetta il messaggio d'errore "profession contains special characters".
     */
    @Test
    void testProfessionContainsSpecialCharacters() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        // "Idr@ulico" contiene il carattere speciale '@'
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idr@ulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("profession contains special characters", json.getString("results"));
    }

    /**
     * Testa la validazione delle tariffe orarie quando hourlyRateMax è minore di hourlyRateMin.
     * Ci si aspetta il messaggio d'errore "the hourlyRateMax must be greater than or equal to the hourlyRateMin".
     */
    @Test
    void testHourlyRateMaxLessThanMin() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        // Valori tariffari errati: hourlyRateMax (20) < hourlyRateMin (30)
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 30, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("the hourlyRateMax must be greater than or equal to the hourlyRateMin", json.getString("results"));
    }

    /**
     * Testa la validazione della data di inizio quando è null.
     * Ci si aspetta il messaggio d'errore "StartDate cannot be null".
     */
    @Test
    void testStartDateNull() {
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                // Passa null per la data di inizio
                .weekly(null, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("StartDate cannot be null", json.getString("results"));
    }

    /**
     * Testa la validazione della data di fine quando è null.
     * Ci si aspetta il messaggio d'errore "EndDate cannot be null".
     */
    @Test
    void testEndDateNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                // Passa null per la data di fine
                .weekly(startDate, null, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("EndDate cannot be null", json.getString("results"));
    }

    /**
     * Testa la validazione delle date quando la data di fine precede la data di inizio.
     * Ci si aspetta il messaggio d'errore "EndDate cannot be before startDate".
     */
    @Test
    void testEndDateBeforeStartDate() {
        LocalDate startDate = LocalDate.of(2025, 3, 15);
        LocalDate endDate   = LocalDate.of(2025, 2, 10);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("EndDate cannot be before startDate", json.getString("results"));
    }

    /**
     * Testa la validazione degli intervalli settimanali quando questi sono null.
     * Ci si aspetta il messaggio d'errore "WeeklyIntervals cannot be null".
     */
    @Test
    void testWeeklyIntervalsNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                // Passa null per gli intervalli settimanali
                .weekly(startDate, endDate, null)
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("WeeklyIntervals cannot be null", json.getString("results"));
    }

    /**
     * Testa la validazione degli intervalli settimanali quando la mappa è vuota.
     * Ci si aspetta il messaggio d'errore "WeeklyIntervals cannot be empty".
     */
    @Test
    void testWeeklyIntervalsEmpty() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        Map<String, TimeInterval> emptyWeekdays = new HashMap<>();
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, emptyWeekdays)
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("WeeklyIntervals cannot be empty", json.getString("results"));
    }

    /**
     * Testa la validazione degli intervalli settimanali quando l'intervallo non è valido
     * (l'orario di fine è successivo a quello di inizio).
     * Ci si aspetta il messaggio d'errore "the endHour must be after the StartHour".
     */
    @Test
    void testWeeklyIntervalInvalid() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        // Usa un intervallo non valido: end è dopo start
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getInvalidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("the endHour must be after the StartHour", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "city" quando è null.
     * Ci si aspetta il messaggio d'errore "City cannot be null".
     */
    @Test
    void testCityNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        // Passa null per la city
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(null, validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("City cannot be null", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "city" quando contiene cifre.
     * La stringa "Fisc14no" contiene un digit e deve essere rifiutata.
     * Ci si aspetta il messaggio d'errore "City contains digits".
     */
    @Test
    void testCityContainsDigits() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        // "Fisc14no" contiene un digit
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote("Fisc14no", validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("City contains digits", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "city" quando contiene caratteri speciali.
     * La stringa "Fis!ciano" contiene il carattere '!' e deve essere rifiutata.
     * Ci si aspetta il messaggio d'errore "City contains special characters".
     */
    @Test
    void testCityContainsSpecialCharacters() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        // "Fis!ciano" contiene '!'
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote("Fis!ciano", validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("City contains special characters", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "street" quando è null.
     * Ci si aspetta il messaggio d'errore "Street cannot be null".
     */
    @Test
    void testStreetNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), null, validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Street cannot be null", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "street" quando è una stringa vuota.
     * Ci si aspetta il messaggio d'errore "Street cannot be empty".
     */
    @Test
    void testStreetEmpty() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), "", validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Street cannot be empty", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "street" quando la stringa supera i 128 caratteri.
     * Ci si aspetta il messaggio d'errore "Street contains more than 128 characters".
     */
    @Test
    void testStreetTooLong() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        StringBuilder longStreet = new StringBuilder();
        for (int i = 0; i < 130; i++) {
            longStreet.append("a");
        }
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), longStreet.toString(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Street contains more than 128 characters", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "district" quando è null.
     * Ci si aspetta il messaggio d'errore "District cannot be null".
     */
    @Test
    void testDistrictNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), null, validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("District cannot be null", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "district" quando è una stringa vuota.
     * Ci si aspetta il messaggio d'errore "District cannot be empty".
     */
    @Test
    void testDistrictEmpty() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), "", validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("District cannot be empty", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "district" quando contiene cifre.
     * La stringa "Sal3rno" contiene un digit e deve essere rifiutata.
     * Ci si aspetta il messaggio d'errore "District contains digits".
     */
    @Test
    void testDistrictContainsDigits() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        // "Sal3rno" contiene un digit
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), "Sal3rno", validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("District contains digits", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "district" quando contiene caratteri speciali.
     * La stringa "Sal!erno" contiene il carattere '!' e deve essere rifiutata.
     * Ci si aspetta il messaggio d'errore "District contains special characters".
     */
    @Test
    void testDistrictContainsSpecialCharacters() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        // "Sal!erno" contiene '!'
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), "Sal!erno", validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("District contains special characters", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "region" quando è null.
     * Ci si aspetta il messaggio d'errore "Region cannot be null".
     */
    @Test
    void testRegionNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), null, validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Region cannot be null", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "region" quando è una stringa vuota.
     * Ci si aspetta il messaggio d'errore "Region cannot be empty".
     */
    @Test
    void testRegionEmpty() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), "", validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Region cannot be empty", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "region" quando contiene cifre.
     * La stringa "Camp4nia" contiene un digit e deve essere rifiutata.
     * Ci si aspetta il messaggio d'errore "Region contains digits".
     */
    @Test
    void testRegionContainsDigits() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        // "Camp4nia" contiene un digit
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), "Camp4nia", validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Region contains digits", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "region" quando contiene caratteri speciali.
     * La stringa "Camp@nia" contiene il carattere '@' e deve essere rifiutata.
     * Ci si aspetta il messaggio d'errore "Region contains special characters".
     */
    @Test
    void testRegionContainsSpecialCharacters() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        // "Camp@nia" contiene '@'
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), "Camp@nia", validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Region contains special characters", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "country" quando è null.
     * Ci si aspetta il messaggio d'errore "Country cannot be null".
     */
    @Test
    void testCountryNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), null)
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Country cannot be null", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "country" quando è una stringa vuota.
     * Ci si aspetta il messaggio d'errore "Country cannot be empty".
     */
    @Test
    void testCountryEmpty() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), "")
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Country cannot be empty", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "country" quando contiene cifre.
     * La stringa "Itali4" contiene un digit e deve essere rifiutata.
     * Ci si aspetta il messaggio d'errore "Country contains digits".
     */
    @Test
    void testCountryContainsDigits() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        // "Itali4" contiene un digit
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), "Itali4")
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Country contains digits", json.getString("results"));
    }

    /**
     * Testa la validazione del campo "country" quando contiene caratteri speciali.
     * La stringa "Ital!a" contiene il carattere '!' e deve essere rifiutata.
     * Ci si aspetta il messaggio d'errore "Country contains special characters".
     */
    @Test
    void testCountryContainsSpecialCharacters() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        // "Ital!a" contiene '!'
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), "Ital!a")
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Country contains special characters", json.getString("results"));
    }

    /**
     * Testa il caso in cui la ricerca non restituisce alcun risultato.
     * Vengono usati criteri validi ma che si presume non abbiano corrispondenza nel database.
     * Ci si aspetta il messaggio "No Results Found".
     */
    @Test
    void testNoResultsFound() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        // Dati validi ma si assume che non esista alcun lavoratore corrispondente nel DB.
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "HOMEDELIVERY", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .notRemote("NonexistentCity", "SomeStreet", "SomeDistrict", "SomeRegion", "SomeCountry")
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("No Results Found", json.getString("results"));
    }
}