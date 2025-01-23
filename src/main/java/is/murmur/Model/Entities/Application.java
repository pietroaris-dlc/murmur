package is.murmur.Model.Entities;

import java.time.LocalDateTime;

public class Application {
    public enum Status {
        PENDING,
        CHECKED,
        APPROVED,
        REJECTED
    }
    public enum Type {
        UPGRADE,
        JOB,
        COLLAB
    }

    private Long id;
    private LocalDateTime submissionDate;
    private String docsURL;
    private Status status;
    private Type type;

    public Application(Long id, LocalDateTime submissionDate, String docsURL, Status status, Type type){
        this.id = id;
        this.submissionDate = submissionDate;
        this.docsURL = docsURL;
        this.status = status;
        this.type = type;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }
    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }
    public String getDocsURL() {
        return docsURL;
    }
    public void setDocsURL(String docsURL) {
        this.docsURL = docsURL;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
}
