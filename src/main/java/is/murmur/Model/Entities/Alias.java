package is.murmur.Model.Entities;

public class Alias {

    /*
   CREATE TABLE IF NOT EXISTS alias (
    id SERIAL,
    user_id BIGINT UNSIGNED NOT NULL,
    FOREIGN KEY (user_id)
        REFERENCES logged_in_user (id)
        ON DELETE CASCADE,
    PRIMARY KEY (id, user_id)
);



     */

    private Long id;
    private Long userId;

    public Alias(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }
}
