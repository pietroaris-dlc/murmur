package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Weekday;

import java.util.List;

public abstract class Weekday_DAO implements Generic_DAO {
    private static final String[] KEYS = {"weekly_id", "day_of_week"};

    public static Weekday selectByKeys(Long weeklyId, Weekday.DayOfWeek dayOfWeek) {
        Object[] keysValues = {weeklyId, dayOfWeek};
        return Generic_DAO.selectByKeys(Weekday.class, KEYS, keysValues);
    }

    public static boolean insert(Weekday weekday) {
        return Generic_DAO.doInsert(weekday);
    }

    public static boolean delete(Weekday weekday) {
        Object[] keysValues = {weekday.getWeeklyId(), weekday.getDayOfWeek()};
        return Generic_DAO.doDelete(Weekday.class, KEYS, keysValues);
    }

    public static boolean update(Weekday weekday, String fieldName, Object value) {
        Object[] keysValues = {weekday.getWeeklyId(), weekday.getDayOfWeek()};
        if (fieldName.equals("weekly_id") || fieldName.equals("day_of_week")) return false;
        return Generic_DAO.doUpdate(Weekday.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Weekday> getAll() {
        return Generic_DAO.selectAll(Weekday.class);
    }

    public static List<Weekday> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Weekday.class, fields, values);
    }

    public static List<Weekday> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Weekday.class, field, fieldValue);
    }
}
