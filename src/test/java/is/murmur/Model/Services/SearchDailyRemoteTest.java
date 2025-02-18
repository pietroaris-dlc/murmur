package is.murmur.Model.Services;

import is.murmur.Model.Beans.Career;
import is.murmur.Model.Beans.Profession;
import is.murmur.Model.Beans.User;
import is.murmur.Model.Beans.Worker;
import is.murmur.Model.Helpers.*;
import is.murmur.Model.Services.SearchStrategy.DailyRemoteSearchStrategy;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per verificare i controlli sui parametri di ricerca per il servizio REMOTE giornaliero.
 * Questa classe contiene metodi di test che validano la correttezza dei campi di input per la ricerca
 * di lavoratori con servizio REMOTE in modalità DAILY.
 */
class SearchDailyRemoteTest {

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

    @Test
    void testProfessionNull() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "REMOTE", null, 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("profession is required", json.getString("results"));
    }

    @Test
    void testProfessionEmpty() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "REMOTE", "", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("profession is required", json.getString("results"));
    }

    @Test
    void testProfessionContainsDigits() {
        // "Idraulico23" contiene un digit
        Criteria testCriteria = new Criteria.Builder("DAILY", "REMOTE", "Idraulico23", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("profession contains digits", json.getString("results"));
    }

    @Test
    void testProfessionContainsSpecialCharacters() {
        // "Idr@ulico" contiene il carattere '@'
        Criteria testCriteria = new Criteria.Builder("DAILY", "REMOTE", "Idr@ulico", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("profession contains special characters", json.getString("results"));
    }

    @Test
    void testHourlyRateMaxLessThanMin() {
        // Valori tariffari errati: hourlyRateMax (20) < hourlyRateMin (30)
        Criteria testCriteria = new Criteria.Builder("DAILY", "REMOTE", "Idraulico", 30, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("the hourlyRateMax must be greater than or equal to the hourlyRateMin", json.getString("results"));
    }

    @Test
    void testDayNull() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "REMOTE", "Idraulico", 10, 20)
                .daily(null, validStartHour(), validEndHour())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("Day cannot be null", json.getString("results"));
    }

    @Test
    void testStartHourNull() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "REMOTE", "Idraulico", 10, 20)
                .daily(validDay(), null, validEndHour())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("StartHour cannot be null", json.getString("results"));
    }

    @Test
    void testEndHourNull() {
        Criteria testCriteria = new Criteria.Builder("DAILY", "REMOTE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), null)
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("EndHour cannot be null", json.getString("results"));
    }

    @Test
    void testInvalidTimeInterval() {
        // endHour non è successivo a startHour (ad esempio 10:00 e 09:00)
        LocalTime invalidEnd = LocalTime.of(9, 0);
        Criteria testCriteria = new Criteria.Builder("DAILY", "REMOTE", "Idraulico", 10, 20)
                .daily(validDay(), validStartHour(), invalidEnd)
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("the endHour must be after the StartHour", json.getString("results"));
    }

    @Test
    void testNoResultsFound() {
        // Parametri validi ma si presume che non esista alcun lavoratore corrispondente nel DB.
        Criteria testCriteria = new Criteria.Builder("DAILY", "REMOTE", "NonexistentProfession", 10, 20)
                .daily(validDay(), validStartHour(), validEndHour())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("No Results Found", json.getString("results"));
    }
}