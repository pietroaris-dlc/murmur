package is.murmur.Model.Entities.DBEntities;

public class OfferComponent {

    private long contractsId;
    private String specialRequests;

    public OfferComponent(long contractsId, String specialRequests) {
        this.contractsId = contractsId;
        this.specialRequests = specialRequests;
    }

    public long getContractsId() {
        return contractsId;
    }

    public void setContractsId(long contractsId) {
        this.contractsId = contractsId;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
}
