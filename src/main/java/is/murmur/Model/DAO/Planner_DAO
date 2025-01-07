package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Planner;
import java.util.List;

public abstract class Planner_DAO implements Generic_DAO {
    private static final String[] KEYS = {"userId", "scheduleId"};

    public static Planner selectByKeys(Long userId, Long scheduleId) {
        Object[] keysValues = {userId, scheduleId};
        return Generic_DAO.selectByKeys(Planner.class, KEYS, keysValues);
    }

    public static boolean insert(Planner planner) {
        return Generic_DAO.doInsert(planner);
    }

    public static boolean delete(Planner planner) {
        Object[] keysValues = {planner.getUserId(), planner.getScheduleId()};
        return Generic_DAO.doDelete(Planner.class, KEYS, keysValues);
    }

    public static boolean update(Planner planner, String fieldName, Object value) {
        Object[] keysValues = {planner.getUserId(), planner.getScheduleId()};
        return Generic_DAO.doUpdate(Planner.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Planner> getAll() {
        return Generic_DAO.selectAll(Planner.class);
    }

    public static List<Planner> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Planner.class, fields, values);
    }

    public static List<Planner> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Planner.class, field, fieldValue);
    }
}
