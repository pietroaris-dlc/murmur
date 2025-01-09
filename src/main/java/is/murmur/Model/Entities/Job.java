package is.murmur.Model.Entities;

public class Job {

    private Long applicationId;
    private String professionName;
    private double hourlyRate;

    public Job(Long applicationId, String professionName, double hourlyRate) {
        this.applicationId = applicationId;
        this.professionName = professionName;
        this.hourlyRate = hourlyRate;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getProfessionName() {
        return professionName;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
}
