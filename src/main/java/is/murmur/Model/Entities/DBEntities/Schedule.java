package is.murmur.Model.Entities.DBEntities;

import is.murmur.Model.Entities.Enum.ScheduleType;

public class Schedule {

    private Long id;
    private ScheduleType type;

    public Schedule(Long id, ScheduleType type) {
        this.id = id;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScheduleType getScheduleType() {
        return type;
    }

    public void setScheduleType(ScheduleType type) {
        this.type = type;
    }
}
