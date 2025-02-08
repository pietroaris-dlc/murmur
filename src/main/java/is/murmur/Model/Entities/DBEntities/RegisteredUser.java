package is.murmur.Model.Entities.DBEntities;

import is.murmur.Model.Entities.Enum.UserType;

import java.time.LocalDateTime;

public class RegisteredUser {

    private long id;
    private int password;
    private String firstName;
    private String lastName;
    private LocalDateTime birthDate;
    private String birthCity;
    private String birthCountry;
    private String birthDistrict;
    private String taxCode;
    private double totalExpenditure;
    private UserType type;
    private boolean admin;
    private boolean locked;

    public RegisteredUser(Long id, int password, String firstName, String lastName, LocalDateTime birthDate,
                          String birthCity, String birthDistrict, String birthCountry, String taxCode, double totalExpenditure,
                          UserType type, boolean admin,boolean locked) {
        this.id = id;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.birthCity = birthCity;
        this.birthDistrict = birthDistrict;
        this.birthCountry = birthCountry;
        this.taxCode = taxCode;
        this.totalExpenditure = totalExpenditure;
        this.type = type;
        this.admin = admin;
        this.locked = locked;
    }

    public long getId() { return id;}
    public void setId(long id) { this.id = id;}

    public int getPassword() { return password; }
    public void setPassword(int password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDateTime getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDateTime birthDate) { this.birthDate = birthDate; }

    public String getBirthCity() { return birthCity; }
    public void setBirthCity(String birthCity) { this.birthCity = birthCity; }

    public String getBirthDistrict() { return birthDistrict;}
    public void setBirthDistrict(String birthDistrict) { this.birthDistrict = birthDistrict;}

    public String getBirthCountry() { return birthCountry; }
    public void setBirthCountry(String birthCountry) { this.birthCountry = birthCountry; }

    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public double getTotalExpenditure() { return totalExpenditure; }
    public void setTotalExpenditure(double totalExpenditure) { this.totalExpenditure = totalExpenditure; }

    public UserType getUserType() { return type; }
    public void setUserType(UserType type) { this.type = type; }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
}

