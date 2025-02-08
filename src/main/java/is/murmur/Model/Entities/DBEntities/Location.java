package is.murmur.Model.Entities.DBEntities;

public class Location {
    private Long id;
    private String city;
    private String street;
    private int streetNumber;
    private String district;
    private String region;
    private String country;

    public Location(Long id, String city, String street, int streetNumber, String district, String region, String country) {
        this.id = id;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
        this.district = district;
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

    public int getStreetNumber() {
        return streetNumber;
    }
    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getDistrict() { return district;}
    public void setDistrict(String district) { this.district = district;}

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
