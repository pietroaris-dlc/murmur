package is.murmur.Model.Entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class Daily {
    private Long id;
    private LocalDate day;
    private LocalTime startHour;
    private LocalTime endHour;

    public Daily(Long id, LocalDate day, LocalTime startHour, LocalTime endHour) {
        this.id = id;
        this.day = day;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public LocalTime getStartHour() {
        return startHour;
    }

    public void setStartHour(LocalTime startHour) {
        this.startHour = startHour;
    }

    public LocalTime getEndHour() {
        return endHour;
    }

    public void setEndHour(LocalTime endHour) {
        this.endHour = endHour;
    }
}
