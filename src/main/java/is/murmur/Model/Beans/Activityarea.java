package is.murmur.Model.Beans;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "activityarea")
public class Activityarea {
    @EmbeddedId
    private ActivityareaId id;

    @MapsId("workerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "workerId", nullable = false)
    private Registereduser worker;

    @MapsId("locationId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "locationId", nullable = false)
    private Location location;

    public ActivityareaId getId() {
        return id;
    }

    public void setId(ActivityareaId id) {
        this.id = id;
    }

    public Registereduser getWorker() {
        return worker;
    }

    public void setWorker(Registereduser worker) {
        this.worker = worker;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}