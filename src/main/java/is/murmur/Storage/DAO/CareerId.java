package is.murmur.Storage.DAO;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.util.Objects;

@Embeddable
public class CareerId implements java.io.Serializable {
    private static final long serialVersionUID = 3677149878746185565L;
    @Column(name = "workerId", nullable = false)
    private Long workerId;

    @Column(name = "profession", nullable = false, length = 128)
    private String profession;

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CareerId entity = (CareerId) o;
        return Objects.equals(this.profession, entity.profession) &&
                Objects.equals(this.workerId, entity.workerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profession, workerId);
    }

}