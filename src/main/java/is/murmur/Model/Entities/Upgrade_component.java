package is.murmur.Model.Entities;

public class Upgrade_component {

    private Long application_id;
    private String profession_name;
    private double hourly_rate;
    private String city;
    private String street;
    private int street_number;
    private String region;
    private String country;

    public Upgrade_component(Long application_id, String profession_name, double hourly_rate, String city,
                             String street, int street_number, String region, String country) {
        this.application_id = application_id;
        this.profession_name = profession_name;
        this.hourly_rate = hourly_rate;
        this.city = city;
        this.street = street;
        this.street_number = street_number;
        this.region = region;
        this.country = country;
    }

    public Long getApplication_id() { return application_id; }
    public void setApplication_id(Long application_id) { this.application_id = application_id; }

    public String getProfession_name() { return profession_name; }
    public void setProfession_name(String profession_name) { this.profession_name = profession_name; }

    public double getHourly_rate() { return hourly_rate; }
    public void setHourly_rate(double hourly_rate) { this.hourly_rate = hourly_rate; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public int getStreet_number() { return street_number; }
    public void setStreet_number(int street_number) { this.street_number = street_number; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}