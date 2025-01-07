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
    private LocalDateTime submission_date;
    private String docsURL;
    private Status approval_status;
    private Type type;

    public Application(Long id, LocalDateTime submission_date, String docsURL, Status approval_status, Type type){
        this.id = id;
        this.submission_date = submission_date;
        this.docsURL = docsURL;
        this.approval_status = approval_status;
        this.type = type;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDateTime getSubmission_date() {
        return submission_date;
    }
    public void setSubmission_date(LocalDateTime submission_date) {
        this.submission_date = submission_date;
    }
    public String getDocsURL() {
        return docsURL;
    }
    public void setDocsURL(String docsURL) {
        this.docsURL = docsURL;
    }
    public Status getApproval_status() {
        return approval_status;
    }
    public void setApproval_status(Status approval_status) {
        this.approval_status = approval_status;
    }
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
}
