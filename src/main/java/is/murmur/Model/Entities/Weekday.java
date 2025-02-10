package is.murmur.Model.Entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "weekday", schema = "murmur")
public class Weekday {
    @EmbeddedId
    private WeekdayId id;

    @MapsId("weeklyId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "weeklyId", nullable = false)
    private Weekly weekly;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "dailyId", nullable = false)
    private Daily daily;

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

    public Daily getDaily() {
        return daily;
    }

    public void setDaily(Daily daily) {
        this.daily = daily;
    }

}