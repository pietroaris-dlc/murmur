package is.murmur.Model.Entities;
public class Checked_component {

    private Long applicationId;
    private Long adminId;

    public Checked_component(Long applicationId, Long adminId) {
        this.applicationId = applicationId;
        this.adminId = adminId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getAdminId() {
        return adminId;
    }
}
