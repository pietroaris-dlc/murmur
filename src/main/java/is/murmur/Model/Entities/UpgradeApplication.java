package is.murmur.Model.Entities;

public class UpgradeApplication {
    private Application application;
    private UpgradeComponent upgradeComponent;

    public UpgradeApplication(Application application, UpgradeComponent upgradeComponent) {
        this.application = application;
        this.upgradeComponent = upgradeComponent;
    }

    public Application getApplication() {
        return application;
    }
    public void setApplication(Application application) {
        this.application = application;
    }
    public UpgradeComponent getUpgradeComponent() {
        return upgradeComponent;
    }
    public void setUpgradeComponent(UpgradeComponent upgradeComponent) {
        this.upgradeComponent = upgradeComponent;
    }
}
