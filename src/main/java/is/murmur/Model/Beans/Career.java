package is.murmur.Model.Beans;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Entity
@Table(name = "career")
public class Career {
    @EmbeddedId
    private CareerId id;

    @MapsId("workerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "workerId", nullable = false)
    private Registereduser worker;

    @MapsId("profession")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "profession", nullable = false)
    private Profession profession;

    @Column(name = "hourlyRate", nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "seniority", nullable = false)
    private Integer seniority;

    public CareerId getId() {
        return id;
    }

    public void setId(CareerId id) {
        this.id = id;
    }

    public Registereduser getWorker() {
        return worker;
    }

    public void setWorker(Registereduser worker) {
        this.worker = worker;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Integer getSeniority() {
        return seniority;
    }

    public void setSeniority(Integer seniority) {
        this.seniority = seniority;
    }

}