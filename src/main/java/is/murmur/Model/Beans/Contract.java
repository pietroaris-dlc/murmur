package is.murmur.Model.Beans;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "contract")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "profession", nullable = false)
    private String profession;

    @Column(name = "hourlyRate", nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "totalFee", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalFee;

    @Lob
    @Column(name = "scheduleType", nullable = false)
    private String scheduleType;

    @Lob
    @Column(name = "serviceMode")
    private String serviceMode;

    @Lob
    @Column(name = "status", nullable = false)
    private String status;

    @OneToOne(mappedBy = "contract")
    private Alias alias;

    // Modifica qui: usa "contract" anziché "id"
    @OneToOne(mappedBy = "contract")
    private Dailycontract dailycontract;

    @OneToOne(mappedBy = "contract")
    private Notremotecontract notremotecontract;

    @OneToOne(mappedBy = "contract")
    private Offer offer;

    @OneToOne(mappedBy = "contract")
    private Review review;

    @OneToOne(mappedBy = "contract")
    private Weeklycontract weeklycontract;

    // Getters e Setters

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

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
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

    public Alias getAlias() {
        return alias;
    }

    public void setAlias(Alias alias) {
        this.alias = alias;
    }

    public Dailycontract getDailycontract() {
        return dailycontract;
    }

    public void setDailycontract(Dailycontract dailycontract) {
        this.dailycontract = dailycontract;
    }

    public Notremotecontract getNotremotecontract() {
        return notremotecontract;
    }

    public void setNotremotecontract(Notremotecontract notremotecontract) {
        this.notremotecontract = notremotecontract;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public Weeklycontract getWeeklycontract() {
        return weeklycontract;
    }

    public void setWeeklycontract(Weeklycontract weeklycontract) {
        this.weeklycontract = weeklycontract;
    }
}