package is.murmur.Model.Entities.DBEntities;

public class RejectedComponent {

    private Long applicationId;
    private String rejectionNote;

    public RejectedComponent(Long applicationId, String rejectionNote) {
        this.applicationId = applicationId;
        this.rejectionNote = rejectionNote;
    }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public String getRejectionNote() { return rejectionNote; }
    public void setRejectionNote(String rejectionNote) { this.rejectionNote = rejectionNote; }
}
