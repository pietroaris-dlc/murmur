package is.murmur.Model.Entities;

public class Alias {

    private Long id;
    private Long user_id;

    public Alias(Long id, Long user_id) {
        this.id = id;
        this.user_id = user_id;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }
}
