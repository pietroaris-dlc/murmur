package is.murmur.Model.Entities;

import java.time.LocalDateTime;

public class Logged_in_user {

    private Long id;
    private int password;
    private String first_name;
    private String last_name;
    private LocalDateTime birth_date;
    private String birth_city;
    private String birth_country;
    private String tax_code;
    private double total_expenditure;
    private Type type;

    public enum Type {
        CLIENT,
        WORKER
    }

    // Costruttore
    public Logged_in_user(Long id, int password, String first_name, String last_name, LocalDateTime birth_date,
                          String birth_city, String birth_country, String tax_code, double total_expenditure, Type type) {
        this.id = id;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
        this.birth_date = birth_date;
        this.birth_city = birth_city;
        this.birth_country = birth_country;
        this.tax_code = tax_code;
        this.total_expenditure = total_expenditure;
        this.type = type;
    }

    // Getter e Setter (aggiornati con snake_case)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getPassword() { return password; }
    public void setPassword(int password) { this.password = password; }

    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }

    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }

    public LocalDateTime getBirth_date() { return birth_date; }
    public void setBirth_date(LocalDateTime birth_date) { this.birth_date = birth_date; }

    public String getBirth_city() { return birth_city; }
    public void setBirth_city(String birth_city) { this.birth_city = birth_city; }

    public String getBirth_country() { return birth_country; }
    public void setBirth_country(String birth_country) { this.birth_country = birth_country; }

    public String getTax_code() { return tax_code; }
    public void setTax_code(String tax_code) { this.tax_code = tax_code; }

    public double getTotal_expenditure() { return total_expenditure; }
    public void setTotal_expenditure(double total_expenditure) { this.total_expenditure = total_expenditure; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
}

