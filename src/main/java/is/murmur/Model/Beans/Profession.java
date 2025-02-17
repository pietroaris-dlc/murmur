package is.murmur.Model.Beans;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "profession")
public class Profession {
    @Id
    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @OneToMany(mappedBy = "profession")
    private Set<Career> careers = new LinkedHashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Career> getCareers() {
        return careers;
    }

    public void setCareers(Set<Career> careers) {
        this.careers = careers;
    }

}