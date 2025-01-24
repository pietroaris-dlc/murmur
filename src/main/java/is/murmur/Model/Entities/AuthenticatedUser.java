package is.murmur.Model.Entities;

import java.time.LocalDateTime;

public class AuthenticatedUser extends RegisteredUser{

    public AuthenticatedUser(Long id, int password, String firstName, String last_name, LocalDateTime birthDate,
                             String birthCity, String birthCountry, String taxCode, double totalExpenditure,
                             Type type, boolean admin, boolean locked) {
        super(id, password, firstName, last_name, birthDate, birthCity, birthCountry, taxCode, totalExpenditure, type, admin, locked);
    }
}
