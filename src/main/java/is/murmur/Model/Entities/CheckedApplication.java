package is.murmur.Model.Entities;

import java.time.LocalDateTime;

public class CheckedApplication extends Application {
    private CheckedComponent checkedComponent;

    public CheckedApplication(Long id, LocalDateTime submissionDate, String docsURL, Type type, CheckedComponent checkedComponent) {
        super(id, submissionDate, docsURL, Status.CHECKED, type);
        this.checkedComponent = checkedComponent;
    }

    public CheckedComponent getCheckedComponent() {
        return checkedComponent;
    }
    public void setCheckedComponent(CheckedComponent checkedComponent) {
        this.checkedComponent = checkedComponent;
    }
}
