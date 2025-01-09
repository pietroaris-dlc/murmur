package is.murmur.Model.Entities;

public class Job_component {

    private Long application_id;
    private String profession_name;
    private double hourly_rate;

    public Job_component(Long application_id, String profession_name, double hourly_rate) {
        this.application_id = application_id;
        this.profession_name = profession_name;
        this.hourly_rate = hourly_rate;
    }

    public Long getApplication_id() { return application_id; }
    public void setApplication_id(Long application_id) { this.application_id = application_id; }

    public String getProfession_name() { return profession_name; }
    public void setProfession_name(String profession_name) { this.profession_name = profession_name; }

    public double getHourly_rate() { return hourly_rate; }
    public void setHourly_rate(double hourly_rate) { this.hourly_rate = hourly_rate; }
}
