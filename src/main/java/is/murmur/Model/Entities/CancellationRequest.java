package is.murmur.Model.Entities;

import java.time.LocalDateTime;

public class CancellationRequest {

    private long contractId;
    private LocalDateTime submissionDate;
    private String description;
    private Status status;

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }

    public CancellationRequest(long contractId, LocalDateTime submissionDate, String description, Status status) {
        this.contractId = contractId;
        this.submissionDate = submissionDate;
        this.description = description;
        this.status = status;
    }

    public long getContractId() { return contractId;}
    public void setContractId(long contractId) { this.contractId = contractId;}

    public LocalDateTime getSubmissionDate() { return submissionDate;}
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate;}

    public String getDescription() { return description;}
    public void setDescription(String description) { this.description = description;}

    public Status getStatus() { return status;}
    public void setStatus(Status status) { this.status = status;}
}

