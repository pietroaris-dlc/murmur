package is.murmur.Model.Entities;

import java.time.LocalDateTime;

public class RejectedApplication extends Application {
    private RejectedComponent rejectedComponent;

    public RejectedApplication(Long id, LocalDateTime submissionDate, String docsURL, Type type, RejectedComponent rejectedComponent) {
        super(id, submissionDate, docsURL, Status.REJECTED, type);
        this.rejectedComponent = rejectedComponent;
    }

    public RejectedComponent getRejectedComponent() {
        return rejectedComponent;
    }

    public void setRejectedComponent(RejectedComponent rejectedComponent) {
        this.rejectedComponent = rejectedComponent;
    }
}
