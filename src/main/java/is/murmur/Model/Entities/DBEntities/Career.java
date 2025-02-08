package is.murmur.Model.Entities.DBEntities;

public class Career {

    private Long workerId;
    private String profession;
    private double hourlyRate;
    private int seniority;

    public Career(Long workerId, String profession, double hourlyRate, int seniority) {
        this.workerId = workerId;
        this.profession = profession;
        this.hourlyRate = hourlyRate;
        this.seniority = seniority;
    }

    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public int getSeniority() { return seniority; }
    public void setSeniority(int seniority) { this.seniority = seniority; }
}
