package is.murmur.Model.Entities.DBEntities;

public class Alias {

    private Long id;
    private Long userId;

    public Alias(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
