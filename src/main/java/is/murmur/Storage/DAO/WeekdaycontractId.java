package is.murmur.Storage.DAO;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.util.Objects;

@Embeddable
public class WeekdaycontractId implements java.io.Serializable {
    private static final long serialVersionUID = -5223307172443358057L;
    @Column(name = "weeklyId", nullable = false)
    private Long weeklyId;

    @Column(name = "dayOfWeek", nullable = false, length = 10)
    private String dayOfWeek;

    public Long getWeeklyId() {
        return weeklyId;
    }

    public void setWeeklyId(Long weeklyId) {
        this.weeklyId = weeklyId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        WeekdaycontractId entity = (WeekdaycontractId) o;
        return Objects.equals(this.dayOfWeek, entity.dayOfWeek) &&
                Objects.equals(this.weeklyId, entity.weeklyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, weeklyId);
    }

}