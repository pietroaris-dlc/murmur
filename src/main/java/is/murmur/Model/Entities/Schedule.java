package is.murmur.Model.Entities;

public class Schedule {
    public enum Type {
        DAILY,
        WEEKLY
    }

    private Long id;
    private Type type;

    public Schedule(Long id, Type type) {
        this.id = id;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
