package is.murmur.Model.Entities;

import java.time.LocalDateTime;

public class Client extends AuthenticatedUser{
    public Client(Long id, int password, String firstName, String last_name, LocalDateTime birthDate,
                  String birthCity, String birthCountry, String taxCode, double totalExpenditure, boolean admin, boolean lock) {
        super(id, password, firstName, last_name, birthDate, birthCity, birthCountry, taxCode, totalExpenditure, Type.CLIENT, admin,lock);
    }
}
