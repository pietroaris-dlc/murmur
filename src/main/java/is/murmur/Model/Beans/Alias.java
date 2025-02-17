package is.murmur.Model.Beans;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "aliases")
public class Alias {
    @Id
    @Column(name = "contractId", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "contractId", nullable = false)
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "clientAlias", nullable = false)
    private Clientalias clientAlias;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "workerAlias", nullable = false)
    private Workeralias workerAlias;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Clientalias getClientAlias() {
        return clientAlias;
    }

    public void setClientAlias(Clientalias clientAlias) {
        this.clientAlias = clientAlias;
    }

    public Workeralias getWorkerAlias() {
        return workerAlias;
    }

    public void setWorkerAlias(Workeralias workerAlias) {
        this.workerAlias = workerAlias;
    }

}