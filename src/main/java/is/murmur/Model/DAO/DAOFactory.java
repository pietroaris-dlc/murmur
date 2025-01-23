package is.murmur.Model.DAO;

import is.murmur.Model.Entities.*;

public class DAOFactory {

    public static DAOStrategy<RegisteredUser> authenticatedUser() {
        return new DAOStrategy<>("RegisteredUser", RegisteredUser.class, new String[]{"id"});
    }

    public static DAOStrategy<WorkerComponent> workerComponent() {
        return new DAOStrategy<>("WorkerComponent", WorkerComponent.class, new String[]{"userId"});
    }

    public static DAOStrategy<Alias> alias() {
        return new DAOStrategy<>("Alias", Alias.class, new String[]{"id", "userId"});
    }

    public static DAOStrategy<Location> location() {
        return new DAOStrategy<>("Location", Location.class, new String[]{"id"});
    }

    public static DAOStrategy<Profession> profession() {
        return new DAOStrategy<>("Profession", Profession.class, new String[]{"name"});
    }

    public static DAOStrategy<Schedule> schedule() {
        return new DAOStrategy<>("Schedule", Schedule.class, new String[]{"id"});
    }

    public static DAOStrategy<Daily> daily() {
        return new DAOStrategy<>("Daily", Daily.class, new String[]{"id"});
    }

    public static DAOStrategy<Weekly> weekly() {
        return new DAOStrategy<>("Weekly", Weekly.class, new String[]{"id"});
    }

    public static DAOStrategy<Weekday> weekday() {
        return new DAOStrategy<>("Weekday", Weekday.class, new String[]{"weeklyId", "dayOfWeek"});
    }

    public static DAOStrategy<Career> career() {
        return new DAOStrategy<>("Career", Career.class, new String[]{"workerId", "profession"});
    }

    public static DAOStrategy<ActivityArea> activityArea() {
        return new DAOStrategy<>("ActivityArea", ActivityArea.class, new String[]{"workerId", "locationId"});
    }

    public static DAOStrategy<Planner> planner() {
        return new DAOStrategy<>("Planner", Planner.class, new String[]{"userId", "scheduleId"});
    }

    public static DAOStrategy<Contract> contract() {
        return new DAOStrategy<>("Contract", Contract.class, new String[]{"id"});
    }

    public static DAOStrategy<NotRemoteComponent> notRemoteComponent() {
        return new DAOStrategy<>("NotRemoteComponent", NotRemoteComponent.class, new String[]{"contractId"});
    }

    public static DAOStrategy<Application> application() {
        return new DAOStrategy<>("Application", Application.class, new String[]{"id"});
    }

    public static DAOStrategy<UpgradeComponent> upgradeComponent() {
        return new DAOStrategy<>("UpgradeComponent", UpgradeComponent.class, new String[]{"applicationId"});
    }

    public static DAOStrategy<JobComponent> jobComponent() {
        return new DAOStrategy<>("JobComponent", JobComponent.class, new String[]{"applicationId"});
    }

    public static DAOStrategy<CheckedComponent> checkedComponent() {
        return new DAOStrategy<>("CheckedComponent", CheckedComponent.class, new String[]{"applicationId"});
    }

    public static DAOStrategy<RejectedComponent> rejectedComponent() {
        return new DAOStrategy<>("RejectedComponent", RejectedComponent.class, new String[]{"applicationId"});
    }

    public static DAOStrategy<Review> review() {
        return new DAOStrategy<>("Review", Review.class, new String[]{"contractId"});
    }

    public static DAOStrategy<CancellationRequest> cancellationRequest() {
        return new DAOStrategy<>("CancellationRequest", CancellationRequest.class, new String[]{"contractId"});
    }
}