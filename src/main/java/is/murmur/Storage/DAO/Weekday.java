package is.murmur.Storage.DAO;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalTime;

@Entity
@Table(name = "weekday")
public class Weekday {
    @EmbeddedId
    private WeekdayId id;

    @MapsId("weeklyId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "weeklyId", nullable = false)
    private Weekly weekly;

    @Column(name = "startHour", nullable = false)
    private LocalTime startHour;

    @Column(name = "endHour", nullable = false)
    private LocalTime endHour;

    public WeekdayId getId() {
        return id;
    }

    public void setId(WeekdayId id) {
        this.id = id;
    }

    public Weekly getWeekly() {
        return weekly;
    }

    public void setWeekly(Weekly weekly) {
        this.weekly = weekly;
    }

    public LocalTime getStartHour() {
        return startHour;
    }

    public void setStartHour(LocalTime startHour) {
        this.startHour = startHour;
    }

    public LocalTime getEndHour() {
        return endHour;
    }

    public void setEndHour(LocalTime endHour) {
        this.endHour = endHour;
    }

}