package is.murmur.Model.Entities;

public class Worker_component {

    private Long user_id;
    private double priority;
    private double average_rating;
    private double average_workdays_per_month;
    private double average_profit_per_month;
    private double total_profit;

    public Worker_component(Long user_id, double priority, double average_rating, double average_workdays_per_month,
                            double average_profit_per_month, double total_profit) {
        this.user_id = user_id;
        this.priority = priority;
        this.average_rating = average_rating;
        this.average_workdays_per_month = average_workdays_per_month;
        this.average_profit_per_month = average_profit_per_month;
        this.total_profit = total_profit;
    }

    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }

    public double getPriority() { return priority; }
    public void setPriority(double priority) { this.priority = priority; }

    public double getAverage_rating() { return average_rating; }
    public void setAverage_rating(double average_rating) { this.average_rating = average_rating; }

    public double getAverage_workdays_per_month() { return average_workdays_per_month; }
    public void setAverage_workdays_per_month(double average_workdays_per_month) { this.average_workdays_per_month = average_workdays_per_month; }

    public double getAverage_profit_per_month() { return average_profit_per_month; }
    public void setAverage_profit_per_month(double average_profit_per_month) { this.average_profit_per_month = average_profit_per_month; }

    public double getTotal_profit() { return total_profit; }
    public void setTotal_profit(double total_profit) { this.total_profit = total_profit; }
}
