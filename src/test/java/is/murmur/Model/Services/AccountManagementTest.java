package is.murmur.Model.Services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountManagementTest {

    @Test
    void testNullInput() {
        // Creiamo un oggetto AccountManagement
        AccountManagement am = new AccountManagement();

        // Eseguiamo il metodo con parametri nulli e verifichiamo che venga lanciata IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            am.signIn(null);
        });
    }

    @Test
    void testInvalidEmail() {
        // Creiamo un AccountManagement e un input con email non valida
        AccountManagement am = new AccountManagement();
        String[] testUserData = {
                "invalid-email", "password123", "Mario", "Rossi",
                "1990-01-01", "Empoli", "Firenze", "Italia", "50053"
        };

        // Verifica che venga lanciata IllegalArgumentException per formato email non valido
        assertThrows(IllegalArgumentException.class, () -> {
            am.signIn(testUserData);
        });
    }

    @Test
    void testShortPassword() {
        // Creiamo un input con password troppo corta
        AccountManagement am = new AccountManagement();
        String[] testUserData = {
                "m.rossi23@gmail.com", "pass", "Mario", "Rossi",
                "1990-01-01", "Empoli", "Firenze", "Italia", "50053"
        };

        // Verifica che venga lanciata IllegalArgumentException per password troppo corta
        assertThrows(IllegalArgumentException.class, () -> {
            am.signIn(testUserData);
        });
    }

    @Test
    void testNameWithNumbers() {
        // Creiamo un input con nome contenente numeri
        AccountManagement am = new AccountManagement();
        String[] testUserData = {
                "m.rossi23@gmail.com", "password123", "Mari0", "Rossi",
                "1990-01-01", "Empoli", "Firenze", "Italia", "50053"
        };

        // Verifica che venga lanciata IllegalArgumentException per nome contenente numeri
        assertThrows(IllegalArgumentException.class, () -> {
            am.signIn(testUserData);
        });
    }

    @Test
    void testLastNameWithNumbers() {
        // Creiamo un input con cognome contenente numeri
        AccountManagement am = new AccountManagement();
        String[] testUserData = {
                "m.rossi23@gmail.com", "password123", "Mario", "R0ssi",
                "1990-01-01", "Empoli", "Firenze", "Italia", "50053"
        };

        // Verifica che venga lanciata IllegalArgumentException per cognome contenente numeri
        assertThrows(IllegalArgumentException.class, () -> {
            am.signIn(testUserData);
        });
    }

    @Test
    void testInvalidBirthDateFormat() {
        // Creiamo un input con formato data di nascita errato
        AccountManagement am = new AccountManagement();
        String[] testUserData = {
                "m.rossi23@gmail.com", "password123", "Mario", "Rossi",
                "01-01-1990", "Empoli", "Firenze", "Italia", "50053"
        };

        // Verifica che venga lanciata IllegalArgumentException per formato data di nascita errato
        assertThrows(IllegalArgumentException.class, () -> {
            am.signIn(testUserData);
        });
    }

    @Test
    void testAgeLessThan18() {
        // Creiamo un input con un'età inferiore a 18 anni
        AccountManagement am = new AccountManagement();
        String[] testUserData = {
                "m.rossi23@gmail.com", "password123", "Mario", "Rossi",
                "2010-01-01", "Empoli", "Firenze", "Italia", "50053"
        };

        // Verifica che venga lanciata IllegalArgumentException per età inferiore a 18 anni
        assertThrows(IllegalArgumentException.class, () -> {
            am.signIn(testUserData);
        });
    }

    @Test
    void testBirthCityWithNumbers() {
        // Creiamo un input con una città di nascita che contiene numeri
        AccountManagement am = new AccountManagement();
        String[] testUserData = {
                "m.rossi23@gmail.com", "password123", "Mario", "Rossi",
                "1990-01-01", "Emp0li", "Firenze", "Italia", "50053"
        };

        // Verifica che venga lanciata IllegalArgumentException per città di nascita con numeri
        assertThrows(IllegalArgumentException.class, () -> {
            am.signIn(testUserData);
        });
    }

    @Test
    void testBirthDistrictWithNumbers() {
        // Creiamo un input con un distretto di nascita che contiene numeri
        AccountManagement am = new AccountManagement();
        String[] testUserData = {
                "m.rossi23@gmail.com", "password123", "Mario", "Rossi",
                "1990-01-01", "Empoli", "Fir3nze", "Italia", "50053"
        };

        // Verifica che venga lanciata IllegalArgumentException per distretto di nascita con numeri
        assertThrows(IllegalArgumentException.class, () -> {
            am.signIn(testUserData);
        });
    }

    @Test
    void testBirthCountryWithNumbers() {
        // Creiamo un input con un paese di nascita che contiene numeri
        AccountManagement am = new AccountManagement();
        String[] testUserData = {
                "m.rossi23@gmail.com", "password123", "Mario", "Rossi",
                "1990-01-01", "Empoli", "Firenze", "It4lia", "50053"
        };

        // Verifica che venga lanciata IllegalArgumentException per paese di nascita con numeri
        assertThrows(IllegalArgumentException.class, () -> {
            am.signIn(testUserData);
        });
    }

    @Test
    void testInvalidTaxCode() {
        // Creiamo un input con un codice fiscale non valido
        AccountManagement am = new AccountManagement();
        String[] testUserData = {
                "m.rossi23@gmail.com", "password123", "Mario", "Rossi",
                "1990-01-01", "Empoli", "Firenze", "Italia", "5005A"
        };

        // Verifica che venga lanciata IllegalArgumentException per codice fiscale non valido
        assertThrows(IllegalArgumentException.class, () -> {
            am.signIn(testUserData);
        });
    }

    @Test
    void testValidUser() {
        // Creiamo un input valido per l'utente
        AccountManagement am = new AccountManagement();
        String[] testUserData = {
                "m.rossi23@gmail.com", "ValidPassword1", "Mario", "Rossi",
                "1990-01-01", "Empoli", "Firenze", "Italia", "50053"
        };

        // Verifica che il metodo non lanci eccezioni (utente valido)
        assertDoesNotThrow(() -> {
            am.signIn(testUserData);
        });
    }
    void testEmailAlreadyExists() {
        // Creiamo un oggetto AccountManagement
        AccountManagement am = new AccountManagement();

        // Dati per il primo utente, che viene creato
        String[] firstUserData = {
                "m.rossi23@gmail.com", "ValidPassword1", "Mario", "Rossi",
                "1990-01-01", "Empoli", "Firenze", "Italia", "50053"
        };

        // Registriamo il primo utente
        assertDoesNotThrow(() -> {
            am.signIn(firstUserData); // Si suppone che il metodo signIn registri l'utente
        });

        // Dati per il secondo utente, con la stessa email
        String[] secondUserData = {
                "m.rossi23@gmail.com", "AnotherPassword2", "Giuseppe", "Bianchi",
                "1985-05-15", "Roma", "Lazio", "Italia", "00100"
        };

        // Verifica che venga lanciata una IllegalArgumentException per email già esistente
        assertThrows(IllegalArgumentException.class, () -> {
            am.signIn(secondUserData); // Si suppone che il metodo signIn lanci un'eccezione se l'email è già registrata
        });
    }
}