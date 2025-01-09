package is.murmur.Model.DAO;

import is.murmur.Model.Entities.ActivityArea;

import java.util.List;

public abstract class ActivityArea_DAO implements Generic_DAO {
    private static final String[] KEYS = {"worker_id", "location_id"};

    public static ActivityArea selectByKeys(Long workerId, Long locationId) {
        Object[] keysValues = {workerId, locationId};
        return Generic_DAO.selectByKeys(ActivityArea.class, KEYS, keysValues);
    }

    public static boolean insert(ActivityArea activityArea) {
        return Generic_DAO.doInsert(activityArea);
    }

    public static boolean delete(ActivityArea activityArea) {
        Object[] keysValues = {activityArea.getWorkerId(), activityArea.getLocationId()};
        return Generic_DAO.doDelete(ActivityArea.class, KEYS, keysValues);
    }

    public static boolean update(ActivityArea activityArea, String fieldName, Object value) {
        Object[] keysValues = {activityArea.getWorkerId(), activityArea.getLocationId()};
        if (fieldName.equals("worker_id") || fieldName.equals("location_id")) return false;
        return Generic_DAO.doUpdate(ActivityArea.class, KEYS, keysValues, fieldName, value);
    }

    public static List<ActivityArea> getAll() {
        return Generic_DAO.selectAll(ActivityArea.class);
    }

    public static List<ActivityArea> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(ActivityArea.class, fields, values);
    }

    public static List<ActivityArea> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(ActivityArea.class, field, fieldValue);
    }
}
