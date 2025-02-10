package is.murmur.Model.Entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "alias", schema = "murmur")
public class Alias {
    @EmbeddedId
    private AliasId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userId", nullable = false)
    private Registereduser user;

    public AliasId getId() {
        return id;
    }

    public void setId(AliasId id) {
        this.id = id;
    }

    public Registereduser getUser() {
        return user;
    }

    public void setUser(Registereduser user) {
        this.user = user;
    }

}