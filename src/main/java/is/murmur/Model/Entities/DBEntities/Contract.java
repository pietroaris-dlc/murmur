package is.murmur.Model.Entities.DBEntities;

import is.murmur.Model.Entities.Enum.ContractStatus;
import is.murmur.Model.Entities.Enum.ServiceMode;

import java.util.List;

public class Contract {

    private Long id;
    private String profession;
    private double hourlyRate;
    private String city;
    private String street;
    private int streetNumber;
    private String region;
    private String country;
    private Long clientAlias;
    private Long workerAlias;
    private Long scheduleId;
    private double totalFee;
    private ServiceMode serviceMode;
    private ContractStatus status;

    public Contract(Long id, String profession, double hourlyRate, String city, String street, int streetNumber, String region, String country, Long clientAlias, Long workerAlias, Long scheduleId, double totalFee, ServiceMode serviceMode, ContractStatus status) {
        this.id = id;
        this.profession = profession;
        this.hourlyRate = hourlyRate;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
        this.region = region;
        this.country = country;
        this.clientAlias = clientAlias;
        this.workerAlias = workerAlias;
        this.scheduleId = scheduleId;
        this.totalFee = totalFee;
        this.serviceMode = serviceMode;
        this.status = status;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getProfession() {
        return profession;
    }
    public void setProfession(String profession) {
        this.profession = profession;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
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

    public Long getClientAlias() {
        return clientAlias;
    }
    public void setClientAlias(Long clientAlias) {
        this.clientAlias = clientAlias;
    }

    public Long getWorkerAlias() {
        return workerAlias;
    }
    public void setWorkerAlias(Long workerAlias) {
        this.workerAlias = workerAlias;
    }

    public Long getScheduleId() {
        return scheduleId;
    }
    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public double getTotalFee() {
        return totalFee;
    }
    public void setTotalFee(double totalFee) {
        this.totalFee = totalFee;
    }

    public ServiceMode getServiceMode() {
        return serviceMode;
    }
    public void setServiceMode(ServiceMode serviceMode) {
        this.serviceMode = serviceMode;
    }

    public ContractStatus getContractStatus() {
        return status;
    }
    public void setContractStatus(ContractStatus status) {
        this.status = status;
    }
}
