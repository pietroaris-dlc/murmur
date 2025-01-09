package is.murmur.Model.Entities;

public class Rejected {

    private Long applicationId;
    private String rejectionNote;

    public Rejected(Long applicationId, String rejectionNote) {
        this.applicationId = applicationId;
        this.rejectionNote = rejectionNote;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getRejectionNote() {
        return rejectionNote;
    }
}
