package is.murmur.Model.Entities;

public class NotRemoteComponent {
    private long contractId;
    private String city;
    private String streetName;
    private int streetNumber;
    private String region;
    private String country;

    public NotRemoteComponent(long contractId, String city, String streetName, int streetNumber, String region, String country) {
        this.contractId = contractId;
        this.city = city;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.region = region;
        this.country = country;
    }
    public long getContractId() {
        return contractId;
    }
    public void setContractId(long contractId) {
        this.contractId = contractId;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getStreetName() {
        return streetName;
    }
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }
    public int getStreetNumber() {
        return streetNumber;
    }
    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
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
