package is.murmur.Model.Entities.DBEntities;

public class Review {
    private Long contractId;
    private Integer rating;
    private String description;

    public Review(Long contractId, Integer rating, String description) {
        this.contractId = contractId;
        this.rating = rating;
        this.description = description;
    }

    public Long getContractId() {
        return contractId;
    }
    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Integer getRating() {
        return rating;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
