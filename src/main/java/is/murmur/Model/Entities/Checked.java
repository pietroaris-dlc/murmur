package is.murmur.Model.Entities;

public class Checked {

    private Long applicationId;
    private Long adminId;

    public Checked(Long applicationId, Long adminId) {
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
