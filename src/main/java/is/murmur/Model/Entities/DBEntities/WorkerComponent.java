package is.murmur.Model.Entities.DBEntities;

public class WorkerComponent {

    private Long userId;
    private double priority;
    private double averageRating;
    private double lastMonthWorkdays;
    private double totalProfit;

    public WorkerComponent(Long userId, double priority, double averageRating, double lastMonthWorkdays, double totalProfit) {
        this.userId = userId;
        this.priority = priority;
        this.averageRating = averageRating;
        this.lastMonthWorkdays = lastMonthWorkdays;
        this.totalProfit = totalProfit;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public double getPriority() { return priority; }
    public void setPriority(double priority) { this.priority = priority; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public double getLastMonthWorkdays() { return lastMonthWorkdays; }
    public void setLastMonthWorkdays(double lastMonthWorkdays) { this.lastMonthWorkdays = lastMonthWorkdays; }

    public double getTotalProfit() { return totalProfit; }
    public void setTotalProfit(double totalProfit) { this.totalProfit = totalProfit; }
}
