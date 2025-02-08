package is.murmur.Model.Entities.DBEntities;

public class Weekday {
    public enum DayOfWeek {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }

    private Long weeklyId;
    private Long dailyId;
    private DayOfWeek dayOfWeek;

    public Weekday(Long weeklyId, Long dailyId, DayOfWeek dayOfWeek) {
        this.weeklyId = weeklyId;
        this.dailyId = dailyId;
        this.dayOfWeek = dayOfWeek;
    }

    public Long getWeeklyId() {
        return weeklyId;
    }
    public void setWeeklyId(Long weeklyId) {
        this.weeklyId = weeklyId;
    }

    public Long getDailyId() {
        return dailyId;
    }
    public void setDailyId(Long dailyId) {
        this.dailyId = dailyId;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
