package is.murmur.Model.Entities;

public class ActivityArea {
    private Long workerId;
    private Long locationId;

    public ActivityArea(Long workerId, Long locationId) {
        this.workerId = workerId;
        this.locationId = locationId;
    }

    public Long getWorkerId() {
        return workerId;
    }
    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public Long getLocationId() {
        return locationId;
    }
    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
}
