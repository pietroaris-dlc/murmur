package is.murmur.Model.Entities;

import java.time.LocalDateTime;

public class RegisteredUser extends GenericUser{

    private long id;
    private int password;
    private String firstName;
    private String last_name;
    private LocalDateTime birthDate;
    private String birthCity;
    private String birthCountry;
    private String taxCode;
    private double totalExpenditure;
    private Type type;
    private boolean admin;
    private boolean locked;

    public enum Type {
        CLIENT,
        WORKER
    }

    public RegisteredUser(Long id, int password, String firstName, String last_name, LocalDateTime birthDate,
                          String birthCity, String birthCountry, String taxCode, double totalExpenditure,
                          Type type, boolean admin,boolean locked) {
        this.id = id;
        this.password = password;
        this.firstName = firstName;
        this.last_name = last_name;
        this.birthDate = birthDate;
        this.birthCity = birthCity;
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

    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }

    public LocalDateTime getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDateTime birthDate) { this.birthDate = birthDate; }

    public String getBirthCity() { return birthCity; }
    public void setBirthCity(String birthCity) { this.birthCity = birthCity; }

    public String getBirthCountry() { return birthCountry; }
    public void setBirthCountry(String birthCountry) { this.birthCountry = birthCountry; }

    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public double getTotalExpenditure() { return totalExpenditure; }
    public void setTotalExpenditure(double totalExpenditure) { this.totalExpenditure = totalExpenditure; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
}

