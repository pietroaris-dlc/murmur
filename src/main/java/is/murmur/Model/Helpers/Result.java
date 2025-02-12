package is.murmur.Model.Helpers;

public class Result {
    private final Long workerId;
    private final String profession;
    private final Double hourlyRate;
    private final Short streetNumber;
    private final Double priority;
    private final Integer seniority;
    private Long aliasId;

    public Result(Long workerId, String profession, Double hourlyRate, Short streetNumber,Double priority, Integer seniority) {
        this.workerId = workerId;
        this.profession = profession;
        this.hourlyRate = hourlyRate;
        this.streetNumber = streetNumber;
        this.priority = priority;
        this.seniority = seniority;
    }

    public Long getWorkerId() { return workerId; }

    public String getProfession() {
        return profession;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public Short getStreetNumber() { return streetNumber; }

    public Double getPriority() {
        return priority;
    }

    public Integer getSeniority() {
        return seniority;
    }

    public Long getAliasId() {
        return aliasId;
    }

    public void setAliasId(Long aliasId) {
        this.aliasId = aliasId;
    }
}

