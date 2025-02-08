package is.murmur.Model.Entities.DBEntities;

import is.murmur.Model.Entities.Enum.ApplicationStatus;
import is.murmur.Model.Entities.Enum.ApplicationType;

import java.time.LocalDateTime;

public class Application {

    private Long id;
    private LocalDateTime submissionDate;
    private String docsURL;
    private ApplicationStatus status;
    private ApplicationType type;

    public Application(Long id, LocalDateTime submissionDate, String docsURL, ApplicationStatus status, ApplicationType type){
        this.id = id;
        this.submissionDate = submissionDate;
        this.docsURL = docsURL;
        this.status = status;
        this.type = type;
    }

    public Long getId() { return id;}
    public void setId(Long id) { this.id = id;}

    public LocalDateTime getSubmissionDate() { return submissionDate;}
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate;}

    public String getDocsURL() { return docsURL;}
    public void setDocsURL(String docsURL) { this.docsURL = docsURL;}

    public ApplicationStatus getStatus() { return status;}
    public void setStatus(ApplicationStatus status) { this.status = status;}

    public ApplicationType getType() {
        return type;
    }
    public void setType(ApplicationType type) {
        this.type = type;
    }
}
