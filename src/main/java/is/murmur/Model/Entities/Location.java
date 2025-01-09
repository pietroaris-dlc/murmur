package is.murmur.Model.Entities;

public class Location {
    private Long id;
    private String city;
    private String street;
    private Short street_number;
    private String region;
    private String country;

    public Location(Long id, String city, String street, Short street_number, String region, String country) {
        this.id = id;
        this.city = city;
        this.street = street;
        this.street_number = street_number;
        this.region = region;
        this.country = country;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }

    public Short getStreet_number() {
        return street_number;
    }
    public void setStreet_number(Short street_number) {
        this.street_number = street_number;
    }

    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
}
