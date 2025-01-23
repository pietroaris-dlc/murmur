package is.murmur.Model.Entities;

public class Criteria {
    private String profession;
    private double hourlyRateMin;
    private double hourlyRateMax;
    private String city;
    private String street;
    private int streetNumber;
    private String region;
    private String country;
    private Schedule schedule;
    private ServiceMode serviceMode;

    public enum ServiceMode {
        ONSITE,
        HOMEDELIVERY,
        REMOTE
    }

    public Criteria(String profession, double hourlyRateMin, double HourlyRateMax, String city, String street, int streetNumber, String region, String country, Schedule schedule, ServiceMode serviceMode){
        this.profession = profession;
        this.hourlyRateMin = hourlyRateMin;
        this.hourlyRateMax = HourlyRateMax;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
        this.region = region;
        this.country = country;
        this.schedule = schedule;
        this.serviceMode = serviceMode;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public double getHourlyRateMin() {
        return hourlyRateMin;
    }

    public void setHourlyRateMin(double hourlyRateMin) {
        this.hourlyRateMin = hourlyRateMin;
    }

    public double getHourlyRateMax() {
        return hourlyRateMax;
    }

    public void setHourlyRateMax(double hourlyRateMax) {
        this.hourlyRateMax = hourlyRateMax;
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

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public ServiceMode getServiceMode() {
        return serviceMode;
    }

    public void setServiceMode(ServiceMode serviceMode) {
        this.serviceMode = serviceMode;
    }
}
