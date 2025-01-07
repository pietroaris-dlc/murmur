package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Schedule;
import java.util.List;

public abstract class Schedule_DAO implements Generic_DAO {
    private static final String[] KEYS = {"id"};

    public static Schedule selectByKeys(Long id) {
        Object[] keysValues = {id};
        return Generic_DAO.selectByKeys(Schedule.class, KEYS, keysValues);
    }

    public static boolean insert(Schedule schedule) {
        return Generic_DAO.doInsert(schedule);
    }

    public static boolean delete(Schedule schedule) {
        Object[] keysValues = {schedule.getId()};
        return Generic_DAO.doDelete(Schedule.class, KEYS, keysValues);
    }

    public static boolean update(Schedule schedule, String fieldName, Object value) {
        Object[] keysValues = {schedule.getId()};
        if (fieldName.equals("id")) return false;
        return Generic_DAO.doUpdate(Schedule.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Schedule> getAll() {
        return Generic_DAO.selectAll(Schedule.class);
    }

    public static List<Schedule> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Schedule.class, fields, values);
    }

    public static List<Schedule> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Schedule.class, field, fieldValue);
    }
}
