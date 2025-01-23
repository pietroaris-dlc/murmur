package is.murmur.Model.Entities;

public class UpgradeComponent {

    private Long applicationId;
    private String professionName;
    private double hourlyRate;
    private String city;
    private String street;
    private int streetNumber;
    private String region;
    private String country;

    public UpgradeComponent(Long applicationId, String professionName, double hourlyRate, String city,
                            String street, int streetNumber, String region, String country) {
        this.applicationId = applicationId;
        this.professionName = professionName;
        this.hourlyRate = hourlyRate;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
        this.region = region;
        this.country = country;
    }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public String getProfessionName() { return professionName; }
    public void setProfessionName(String professionName) { this.professionName = professionName; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public int getStreetNumber() { return streetNumber; }
    public void setStreetNumber(int streetNumber) { this.streetNumber = streetNumber; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}