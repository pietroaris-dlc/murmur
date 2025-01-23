package is.murmur.Model.Entities;

public class ExpiredContract extends Contract{
    private Review review;
    public ExpiredContract(Long id, String profession, double hourlyRate,
                           String city, String street, int streetNumber, String region, String country,
                           Long clientAlias, Long workerAlias,
                           Long scheduleId, double totalFee, Criteria.ServiceMode serviceMode, Status status, Review review) {
        super(id, profession, hourlyRate, city, street, streetNumber, region, country, clientAlias, workerAlias, scheduleId, totalFee, serviceMode, status);
        this.review = review;
    }
    public Review getReview() {
        return review;
    }
    public void setReview(Review review) {
        this.review = review;
    }
}
