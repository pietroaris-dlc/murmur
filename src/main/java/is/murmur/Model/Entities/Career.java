package is.murmur.Model.Entities;

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

    public Long getWorkerId() {
        return workerId;
    }

    public String getProfession() {
        return profession;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public int getSeniority() {
        return seniority;
    }
}
