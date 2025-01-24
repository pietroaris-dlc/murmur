package is.murmur.Model.Entities;

import java.time.LocalDateTime;

public class CollabApplication extends Application {
    public CollabApplication(Long id, LocalDateTime submissionDate, String docsURL, Status status) {
        super(id, submissionDate, docsURL, status, Type.COLLAB);
    }
}
