package is.murmur.Model.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "schedule", schema = "murmur")
public class Schedule {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Lob
    @Column(name = "type", nullable = false)
    private String type;

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

}