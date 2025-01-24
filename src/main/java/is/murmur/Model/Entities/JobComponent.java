package is.murmur.Model.Entities;

public class JobComponent {

    private Long applicationId;
    private String professionName;
    private double hourlyRate;

    public JobComponent(Long applicationId, String professionName, double hourlyRate) {
        this.applicationId = applicationId;
        this.professionName = professionName;
        this.hourlyRate = hourlyRate;
    }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public String getProfessionName() { return professionName; }
    public void setProfessionName(String professionName) { this.professionName = professionName; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
}
