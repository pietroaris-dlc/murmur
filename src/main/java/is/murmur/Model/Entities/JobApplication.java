package is.murmur.Model.Entities;

public class JobApplication {
    private Application application;
    private JobComponent jobComponent;

    public JobApplication(Application application, JobComponent jobComponent) {
        this.application = application;
        this.jobComponent = jobComponent;
    }

    public Application getApplication() {
        return application;
    }
    public void setApplication(Application application) {
        this.application = application;
    }
    public JobComponent getJobComponent() {
        return jobComponent;
    }
    public void setJobComponent(JobComponent jobComponent) {
        this.jobComponent = jobComponent;
    }
}
