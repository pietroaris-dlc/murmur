package is.murmur.Model.Entities;

public class Upgrade {

    private Long applicationId;
    private String professionName;
    private double hourlyRate;
    private String city;
    private String street;
    private int streetNumber;
    private String region;
    private String country;

    public Upgrade(Long applicationId, String professionName, double hourlyRate, String city, String street, int streetNumber, String region, String country) {
        this.applicationId = applicationId;
        this.professionName = professionName;
        this.hourlyRate = hourlyRate;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
        this.region = region;
        this.country = country;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getProfessionName() {
        return professionName;
    }

    public double getHourly_rate() {
        return hourlyRate;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public int getStreet_number() {
        return streetNumber;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }
}
