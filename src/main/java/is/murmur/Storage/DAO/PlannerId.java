package is.murmur.Storage.DAO;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.util.Objects;

@Embeddable
public class PlannerId implements java.io.Serializable {
    private static final long serialVersionUID = -5252872638765518510L;
    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "scheduleId", nullable = false)
    private Long scheduleId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PlannerId entity = (PlannerId) o;
        return Objects.equals(this.userId, entity.userId) &&
                Objects.equals(this.scheduleId, entity.scheduleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, scheduleId);
    }

}