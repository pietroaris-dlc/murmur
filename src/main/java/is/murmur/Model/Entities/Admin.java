package is.murmur.Model.Entities;

import java.time.LocalDateTime;

public class Admin extends AuthenticatedUser{

    public Admin(Long id, int password, String firstName, String last_name, LocalDateTime birthDate, String birthCity, String birthCountry, String taxCode, double totalExpenditure, Type type) {
        super(id, password, firstName, last_name, birthDate, birthCity, birthCountry, taxCode, totalExpenditure, type, true, false);
    }
}
