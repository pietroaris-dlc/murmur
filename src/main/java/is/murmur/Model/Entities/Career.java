package is.murmur.Model.Entities;

public class Career {

    private Long worker_id;
    private String profession;
    private double hourly_rate;
    private int seniority;

    public Career(Long worker_id, String profession, double hourly_rate, int seniority) {
        this.worker_id = worker_id;
        this.profession = profession;
        this.hourly_rate = hourly_rate;
        this.seniority = seniority;
    }

    public Long getWorker_id() { return worker_id; }
    public void setWorker_id(Long worker_id) { this.worker_id = worker_id; }

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    public double getHourly_rate() { return hourly_rate; }
    public void setHourly_rate(double hourly_rate) { this.hourly_rate = hourly_rate; }

    public int getSeniority() { return seniority; }
    public void setSeniority(int seniority) { this.seniority = seniority; }
}
