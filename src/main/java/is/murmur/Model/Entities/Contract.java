package is.murmur.Model.Entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Entity
@Table(name = "contract", schema = "murmur")
public class Contract {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "profession", nullable = false)
    private String profession;

    @Column(name = "hourlyRate", nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "clientAlias", nullable = false, referencedColumnName = "id")
    private Alias clientAlias;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "workerAlias", nullable = false, referencedColumnName = "id")
    private Alias workerAlias;

    @Column(name = "scheduleId", nullable = false)
    private Long scheduleId;

    @Column(name = "totalFee", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalFee;

    @Lob
    @Column(name = "serviceMode")
    private String serviceMode;

    @Lob
    @Column(name = "status", nullable = false)
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Alias getClientAlias() {
        return clientAlias;
    }

    public void setClientAlias(Alias clientAlias) {
        this.clientAlias = clientAlias;
    }

    public Alias getWorkerAlias() {
        return workerAlias;
    }

    public void setWorkerAlias(Alias workerAlias) {
        this.workerAlias = workerAlias;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public String getServiceMode() {
        return serviceMode;
    }

    public void setServiceMode(String serviceMode) {
        this.serviceMode = serviceMode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}