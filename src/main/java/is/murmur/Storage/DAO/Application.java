package is.murmur.Storage.DAO;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "application")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(name = "submissionDate", nullable = false)
    private LocalDate submissionDate;

    @Column(name = "submissionHour", nullable = false)
    private LocalTime submissionHour;

    @Column(name = "docsUrl", nullable = false, length = 2048)
    private String docsUrl;

    @Lob
    @Column(name = "status", nullable = false)
    private String status;

    @Lob
    @Column(name = "type", nullable = false)
    private String type;

    @ManyToMany
    @JoinTable(name = "checkedapplication",
            joinColumns = @JoinColumn(name = "applicationId"),
            inverseJoinColumns = @JoinColumn(name = "adminId"))
    private Set<User> users = new LinkedHashSet<>();

    @OneToOne(mappedBy = "application")
    private Jobapplication jobapplication;

    @OneToOne(mappedBy = "application")
    private Rejectedapplication rejectedapplication;

    @OneToOne(mappedBy = "application")
    private Upgradeapplication upgradeapplication;

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

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalTime getSubmissionHour() {
        return submissionHour;
    }

    public void setSubmissionHour(LocalTime submissionHour) {
        this.submissionHour = submissionHour;
    }

    public String getDocsUrl() {
        return docsUrl;
    }

    public void setDocsUrl(String docsUrl) {
        this.docsUrl = docsUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Jobapplication getJobapplication() {
        return jobapplication;
    }

    public void setJobapplication(Jobapplication jobapplication) {
        this.jobapplication = jobapplication;
    }

    public Rejectedapplication getRejectedapplication() {
        return rejectedapplication;
    }

    public void setRejectedapplication(Rejectedapplication rejectedapplication) {
        this.rejectedapplication = rejectedapplication;
    }

    public Upgradeapplication getUpgradeapplication() {
        return upgradeapplication;
    }

    public void setUpgradeapplication(Upgradeapplication upgradeapplication) {
        this.upgradeapplication = upgradeapplication;
    }

}