package is.murmur.Model.Beans;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import org.hibernate.Hibernate;

import java.util.Objects;

@Embeddable
public class WeekdayId implements java.io.Serializable {
    private static final long serialVersionUID = -7264221565484413015L;
    @Column(name = "weeklyId", nullable = false)
    private Long weeklyId;

    @Lob
    @Column(name = "dayOfWeek", nullable = false)
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
        WeekdayId entity = (WeekdayId) o;
        return Objects.equals(this.dayOfWeek, entity.dayOfWeek) &&
                Objects.equals(this.weeklyId, entity.weeklyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, weeklyId);
    }

}