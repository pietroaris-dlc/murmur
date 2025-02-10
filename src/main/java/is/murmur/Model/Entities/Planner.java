package is.murmur.Model.Entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "planner", schema = "murmur")
public class Planner {
    @EmbeddedId
    private PlannerId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userId", nullable = false)
    private Registereduser user;

    @MapsId("scheduleId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "scheduleId", nullable = false)
    private Schedule schedule;

    public PlannerId getId() {
        return id;
    }

    public void setId(PlannerId id) {
        this.id = id;
    }

    public Registereduser getUser() {
        return user;
    }

    public void setUser(Registereduser user) {
        this.user = user;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

}