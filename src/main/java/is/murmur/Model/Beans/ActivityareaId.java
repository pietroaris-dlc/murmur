package is.murmur.Model.Beans;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.util.Objects;

@Embeddable
public class ActivityareaId implements java.io.Serializable {
    private static final long serialVersionUID = -6416553231919854548L;
    @Column(name = "workerId", nullable = false)
    private Long workerId;

    @Column(name = "locationId", nullable = false)
    private Long locationId;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ActivityareaId entity = (ActivityareaId) o;
        return Objects.equals(this.workerId, entity.workerId) &&
                Objects.equals(this.locationId, entity.locationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workerId, locationId);
    }

}