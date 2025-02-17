package is.murmur.Model.Beans;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalTime;

@Entity
@Table(name = "weekdaycontract")
public class Weekdaycontract {
    @EmbeddedId
    private WeekdaycontractId id;

    @MapsId("weeklyId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "weeklyId", nullable = false)
    private Weeklycontract weekly;

    @Column(name = "startHour", nullable = false)
    private LocalTime startHour;

    @Column(name = "endHour", nullable = false)
    private LocalTime endHour;

    public WeekdaycontractId getId() {
        return id;
    }

    public void setId(WeekdaycontractId id) {
        this.id = id;
    }

    public Weeklycontract getWeekly() {
        return weekly;
    }

    public void setWeekly(Weeklycontract weekly) {
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