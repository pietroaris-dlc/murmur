package is.murmur.Model.Helpers;

import is.murmur.Model.Beans.Alias;

import java.math.BigDecimal;

public class Result {
    private final Long workerId;
    private Alias workerAlias;
    private final String profession;
    private final BigDecimal hourlyRate;
    private final Short streetNumber;
    private final Double priority;
    private final Integer seniority;

    public Result(Long workerId, Alias workerAlias, String profession, BigDecimal hourlyRate, Short streetNumber, Double priority, Integer seniority) {
        this.workerId = workerId;
        this.workerAlias = workerAlias;
        this.profession = profession;
        this.hourlyRate = hourlyRate;
        this.streetNumber = streetNumber;
        this.priority = priority;
        this.seniority = seniority;
    }

    public Long getWorkerId() { return workerId; }
    public Alias getWorkerAlias() { return workerAlias; }
    public void setWorkerAlias(Alias workerAlias) { this.workerAlias = workerAlias;}
    public String getProfession() { return profession;}
    public BigDecimal getHourlyRate() { return hourlyRate;}
    public Short getStreetNumber() { return streetNumber; }
    public Double getPriority() { return priority;}
    public Integer getSeniority() { return seniority;}
}

