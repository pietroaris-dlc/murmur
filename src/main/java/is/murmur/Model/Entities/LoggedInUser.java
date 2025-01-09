package is.murmur.Model.Entities;

import java.time.LocalDateTime;

public class LoggedInUser {
   /*
    id SERIAL,
    email VARCHAR(256) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    birth_date DATE NOT NULL,
    birth_city VARCHAR(100) NOT NULL,
    birth_country VARCHAR(100) NOT NULL,
    tax_code VARCHAR(256) NOT NULL UNIQUE,
    total_expenditure DECIMAL(10 , 2 ) NOT NULL,
    type ENUM('CLIENT', 'WORKER') NOT NULL,
    is_admin BOOLEAN NOT NULL,
    PRIMARY KEY (id)
    */

   public enum  Type {
      CLIENT,
      WORKER,

   }


    private Long id;
    private int password;
    private String first_name;
    private String last_name;
    private LocalDateTime birth_date;
    private String birth_city;
    private String birth_country;
    private String tax_code;
//    total_expenditure DECIMAL(10 , 2 ) NOT NULL // poi lo faccio
    double total_expediture;
    private Type type;

    public LoggedInUser(Long id, int password, String first_name, String last_name, LocalDateTime birth_date, String birth_city, String birth_country, String tax_code, Double total_expediture, Type type) {

        this.id = id;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
        this.birth_date = birth_date;
        this.birth_city = birth_city;
        this.birth_country = birth_country;
        this.tax_code = tax_code;
        this.total_expediture = total_expediture;
        this.type = type;

    }

    public Long getId() {return id;}
    public int getPassword() {return password;}
    public String getFirst_name() {return first_name;}
    public String getLast_name() {return last_name;}
    public LocalDateTime getBirth_date() {return birth_date;}
    public String getBirth_city() {return birth_city;}
    public String getBirth_country() {return birth_country;}
    public String getTax_code() {return tax_code;}
    public Type getType() {return type;}










}
