package is.murmur.Storage.DAO;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "worker")
public class Worker {
    @Id
    @Column(name = "userId", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(name = "priority", nullable = false)
    private Double priority;

    @Column(name = "averageRating", nullable = false)
    private Double averageRating;

    @Column(name = "lastMonthWorkTime", nullable = false)
    private Double lastMonthWorkTime;

    @Column(name = "totalProfit", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalProfit;

    @OneToMany(mappedBy = "worker")
    private Set<Career> careers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Workeralias> workeraliases = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Double getLastMonthWorkTime() {
        return lastMonthWorkTime;
    }

    public void setLastMonthWorkTime(Double lastMonthWorkTime) {
        this.lastMonthWorkTime = lastMonthWorkTime;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

    public Set<Career> getCareers() {
        return careers;
    }

    public void setCareers(Set<Career> careers) {
        this.careers = careers;
    }

    public Set<Workeralias> getWorkeraliases() {
        return workeraliases;
    }

    public void setWorkeraliases(Set<Workeralias> workeraliases) {
        this.workeraliases = workeraliases;
    }

}