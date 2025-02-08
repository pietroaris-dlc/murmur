package is.murmur.Model.Entities.DBEntities;

import is.murmur.Model.Entities.Enum.CancellationRequestStatus;

import java.time.LocalDateTime;

public class CancellationRequest {

    private long contractId;
    private LocalDateTime submissionDate;
    private String description;
    private CancellationRequestStatus status;

    public CancellationRequest(long contractId, LocalDateTime submissionDate, String description, CancellationRequestStatus status) {
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

    public CancellationRequestStatus getStatus() { return status;}
    public void setStatus(CancellationRequestStatus status) { this.status = status;}
}

