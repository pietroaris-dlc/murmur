package is.murmur.Model.Beans;

import jakarta.persistence.*;

@Entity
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Lob
    @Column(name = "type", nullable = false)
    private String type;

    // Associazione One-to-One con Daily: "schedule" è la proprietà in Daily
    @OneToOne(mappedBy = "schedule")
    private Daily daily;

    // Se non desideri gestire la relazione con gli utenti, rimuovi il mapping qui:
    // @ManyToMany(mappedBy = "schedules")
    // private Set<User> users = new LinkedHashSet<>();

    // Associazione One-to-One con Weekly: "schedule" è la proprietà in Weekly
    @OneToOne(mappedBy = "schedule")
    private Weekly weekly;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Daily getDaily() {
        return daily;
    }

    public void setDaily(Daily daily) {
        this.daily = daily;
    }

    public Weekly getWeekly() {
        return weekly;
    }

    public void setWeekly(Weekly weekly) {
        this.weekly = weekly;
    }
}