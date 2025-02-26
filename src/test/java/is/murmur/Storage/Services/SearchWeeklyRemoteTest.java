package is.murmur.Storage.Services;

import is.murmur.Storage.Helpers.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe di test per verificare i controlli sui parametri di ricerca per il servizio remoto settimanale.
 * Questa classe contiene diversi metodi di test che validano la correttezza dei campi di input
 * per la ricerca di lavoratori con servizio REMOTE in modalità WEEKLY.
 */
class SearchWeeklyRemoteTest {

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
     * Testa la validazione del campo "profession" quando è null.
     * Ci si aspetta il messaggio d'errore "profession is required".
     */
    @Test
    void testProfessionNull() {
        LocalDate startDate = LocalDate.of(2025, 2, 10);
        LocalDate endDate   = LocalDate.of(2025, 3, 15);
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "REMOTE", null, 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                 // Imposta il flag per il servizio REMOTE
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
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "REMOTE", "", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
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
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "REMOTE", "Idraulico23", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
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
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "REMOTE", "Idr@ulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
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
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "REMOTE", "Idraulico", 30, 20)
                .weekly(startDate, endDate, getValidWeekdays())
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
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "REMOTE", "Idraulico", 10, 20)
                // Passa null per la data di inizio
                .weekly(null, endDate, getValidWeekdays())
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
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "REMOTE", "Idraulico", 10, 20)
                // Passa null per la data di fine
                .weekly(startDate, null, getValidWeekdays())
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
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "REMOTE", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
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
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "REMOTE", "Idraulico", 10, 20)
                // Passa null per gli intervalli settimanali
                .weekly(startDate, endDate, null)
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
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "REMOTE", "Idraulico", 10, 20)
                .weekly(startDate, endDate, emptyWeekdays)
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
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "REMOTE", "Idraulico", 10, 20)
                .weekly(startDate, endDate, getInvalidWeekdays())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("the endHour must be after the StartHour", json.getString("results"));
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
        Criteria testCriteria = new Criteria.Builder("WEEKLY", "REMOTE", "NonexistentProfession", 10, 20)
                .weekly(startDate, endDate, getValidWeekdays())
                .build();
        String result = ClientSide.search(testCriteria);
        JSONObject json = new JSONObject(result);
        assertEquals("No Results Found", json.getString("results"));
    }
}