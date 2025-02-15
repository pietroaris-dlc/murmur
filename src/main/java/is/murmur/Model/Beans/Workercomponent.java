package is.murmur.Model.Beans;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Entity
@Table(name = "workercomponent")
public class Workercomponent {
    @Id
    @Column(name = "userId", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userId", nullable = false)
    private Registereduser registereduser;

    @Column(name = "priority", nullable = false)
    private Double priority;

    @Column(name = "averageRating", nullable = false)
    private Double averageRating;

    @Column(name = "lastMonthWorkdays", nullable = false)
    private Integer lastMonthWorkdays;

    @Column(name = "totalProfit", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalProfit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Registereduser getRegistereduser() {
        return registereduser;
    }

    public void setRegistereduser(Registereduser registereduser) {
        this.registereduser = registereduser;
    }

    public Double getPriority() {
        return priority;
    }

    public void setPriority(Double priority) {
        this.priority = priority;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getLastMonthWorkdays() {
        return lastMonthWorkdays;
    }

    public void setLastMonthWorkdays(Integer lastMonthWorkdays) {
        this.lastMonthWorkdays = lastMonthWorkdays;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

}