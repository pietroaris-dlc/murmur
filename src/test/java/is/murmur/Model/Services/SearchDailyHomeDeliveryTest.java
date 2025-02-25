package is.murmur.Model.Services;

import is.murmur.Model.Beans.*;
import is.murmur.Model.Helpers.*;
import is.murmur.Model.Services.SearchStrategy.DailyOnsiteSearchStrategy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per verificare i controlli sui parametri di ricerca per il servizio ONSITE giornaliero.
 * Questa classe contiene diversi metodi di test che validano la correttezza dei campi di input
 * per la ricerca di lavoratori con servizio ONSITE in modalità DAILY.
 */
class SearchDailyHomeDeliveryTest {

    // Helper per ottenere un giorno valido
    private LocalDate validDay() {
        return LocalDate.of(2025, 2, 10);
    }

    // Helper per ottenere un orario di inizio valido
    private LocalTime validStartHour() {
        return LocalTime.of(10, 0);
    }

    // Helper per ottenere un orario di fine valido (maggiore di quello di inizio)
    private LocalTime validEndHour() {
        return LocalTime.of(11, 0);
    }

    // Helper per i parametri di localizzazione
    private String validCity() {
        return "Fisciano";
    }
    private String validStreet() {
        return "Via Giovanni Paolo II";
    }
    private String validDistrict() {
        return "Salerno";
    }
    private String validRegion() {
        return "Campania";
    }
    private String validCountry() {
        return "Italia";
    }

    // ----------------------- Test sui parametri di ricerca -----------------------

    @Test
    void testProfessionNull() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", null, 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("profession is required", json.getString("results"));
    }

    @Test
    void testProfessionEmpty() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("profession is required", json.getString("results"));
    }

    @Test
    void testProfessionContainsDigits() {
        // "Idraulico23" contiene un digit
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico23", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("profession contains digits", json.getString("results"));
    }

    @Test
    void testProfessionContainsSpecialCharacters() {
        // "Idr@ulico" contiene il carattere '@'
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idr@ulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("profession contains special characters", json.getString("results"));
    }

    @Test
    void testHourlyRateMaxLessThanMin() {
        // hourlyRateMax (20) < hourlyRateMin (30)
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 30, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("the hourlyRateMax must be greater than or equal to the hourlyRateMin", json.getString("results"));
    }

    @Test
    void testDayNull() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(null, validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Day cannot be null", json.getString("results"));
    }

    @Test
    void testStartHourNull() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), null, validEndHour())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("StartHour cannot be null", json.getString("results"));
    }

    @Test
    void testEndHourNull() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), null)
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("EndHour cannot be null", json.getString("results"));
    }

    @Test
    void testInvalidTimeInterval() {
        // endHour non è successivo a startHour (ad esempio 10:00 e 09:00)
        LocalTime invalidEnd = LocalTime.of(9, 0);
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), invalidEnd)
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("the endHour must be after the StartHour", json.getString("results"));
    }

    @Test
    void testCityNull() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(null, validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("City cannot be null", json.getString("results"));
    }

    @Test
    void testCityContainsDigits() {
        // "Fisc14no" contiene un digit
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote("Fisc14no", validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("City contains digits", json.getString("results"));
    }

    @Test
    void testCityContainsSpecialCharacters() {
        // "Fis!ciano" contiene '!'
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote("Fis!ciano", validStreet(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("City contains special characters", json.getString("results"));
    }

    @Test
    void testStreetNull() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), null, validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Street cannot be null", json.getString("results"));
    }

    @Test
    void testStreetEmpty() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), "", validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Street cannot be empty", json.getString("results"));
    }

    @Test
    void testStreetTooLong() {
        StringBuilder longStreet = new StringBuilder();
        for (int i = 0; i < 130; i++) {
            longStreet.append("a");
        }
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), longStreet.toString(), validDistrict(), validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Street contains more than 128 characters", json.getString("results"));
    }

    @Test
    void testDistrictNull() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), null, validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("District cannot be null", json.getString("results"));
    }

    @Test
    void testDistrictEmpty() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), "", validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("District cannot be empty", json.getString("results"));
    }

    @Test
    void testDistrictContainsDigits() {
        // "Sal3rno" contiene un digit
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), "Sal3rno", validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("District contains digits", json.getString("results"));
    }

    @Test
    void testDistrictContainsSpecialCharacters() {
        // "Sal!erno" contiene '!'
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), "Sal!erno", validRegion(), validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("District contains special characters", json.getString("results"));
    }

    @Test
    void testRegionNull() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), validDistrict(), null, validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Region cannot be null", json.getString("results"));
    }

    @Test
    void testRegionEmpty() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), validDistrict(), "", validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Region cannot be empty", json.getString("results"));
    }

    @Test
    void testRegionContainsDigits() {
        // "Camp4nia" contiene un digit
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), validDistrict(), "Camp4nia", validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Region contains digits", json.getString("results"));
    }

    @Test
    void testRegionContainsSpecialCharacters() {
        // "Camp@nia" contiene '@'
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), validDistrict(), "Camp@nia", validCountry())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Region contains special characters", json.getString("results"));
    }

    @Test
    void testCountryNull() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), null)
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Country cannot be null", json.getString("results"));
    }

    @Test
    void testCountryEmpty() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), "")
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Country cannot be empty", json.getString("results"));
    }

    @Test
    void testCountryContainsDigits() {
        // "Itali4" contiene un digit
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), "Itali4")
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Country contains digits", json.getString("results"));
    }

    @Test
    void testCountryContainsSpecialCharacters() {
        // "Ital!a" contiene '!'
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote(validCity(), validStreet(), validDistrict(), validRegion(), "Ital!a")
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Country contains special characters", json.getString("results"));
    }

    @Test
    void testNoResultsFound() {
        // Parametri validi, ma si presume che non esista alcun lavoratore corrispondente nel DB.
        Criteria testCriteria = new Criteria.Builder("DAILY", "ONSITE", "NonexistentProfession", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .notRemote("NonexistentCity", "SomeStreet", "SomeDistrict", "SomeRegion", "SomeCountry")
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("No Results Found", json.getString("results"));
    }
}