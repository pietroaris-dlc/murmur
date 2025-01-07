package is.murmur.Model.Entities;

public class Location {
    private Long id;
    private String city;
    private String street;
    private Short streetNumber;
    private String region;
    private String country;

    public Location(Long id, String city, String street, Short streetNumber, String region, String country) {
        this.id = id;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
        this.reg
