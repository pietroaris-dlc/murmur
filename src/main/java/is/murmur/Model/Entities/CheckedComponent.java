package is.murmur.Model.Entities;
public class CheckedComponent {

    private Long applicationId;
    private Long adminId;

    public CheckedComponent(Long applicationId, Long adminId) {
        this.applicationId = applicationId;
        this.adminId = adminId;
    }

    public Long getApplicationId() {
        return applicationId;
    }
    public Long setApplicationId(Long applicationId) { return this.applicationId = applicationId; }
    public Long getAdminId() {
        return adminId;
    }
    public Long setAdminId(Long adminId) { return this.adminId = adminId; }
}
