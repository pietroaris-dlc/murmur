package is.murmur.Model.Entities;

public class Worker {
/*
    CREATE TABLE IF NOT EXISTS worker_component (
    user_id BIGINT UNSIGNED NOT NULL,
    priority DOUBLE NOT NULL,
    average_rating DOUBLE NOT NULL,
    average_workdays_per_month DOUBLE NOT NULL,
    average_profit_per_month DECIMAL(10 , 2 ) NOT NULL,
    total_profit DECIMAL(10 , 2 ) NOT NULL,
    FOREIGN KEY (user_id)
    REFERENCES logged_in_user (id)
    ON DELETE CASCADE,
    PRIMARY KEY (user_id)
);
*/

    private Long userId;
    private double priority;
    private double averageRating;
    private double averageWorkdaysPerMonth;
    private double averageProfitPerMonth;
    private double totalProfit;

    public Worker(Long userId, double priority, double averageRating, double averageWorkdaysPerMonth, double averageProfitPerMonth, double totalProfit) {
        this.userId = userId;
        this.priority = priority;
        this.averageRating = averageRating;
        this.averageWorkdaysPerMonth = averageWorkdaysPerMonth;
        this.averageProfitPerMonth = averageProfitPerMonth;
        this.totalProfit = totalProfit;
    }

    public Long getId() {
        return userId;
    }

    public double getPriority() {
        return priority;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public double getAverageWorkdaysPerMonth() {
        return averageWorkdaysPerMonth;
    }

    public double getAverageProfitPerMonth() {
        return averageProfitPerMonth;
    }

    public double getTotalProfit() {
        return totalProfit;
    }
}
