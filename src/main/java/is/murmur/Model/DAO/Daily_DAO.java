package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Daily;
import java.util.List;

public abstract class Daily_DAO implements Generic_DAO {
    private static final String[] KEYS = {"id"};

    public static Daily selectByKeys(Long id) {
        Object[] keysValues = {id};
        return Generic_DAO.selectByKeys(Daily.class, KEYS, keysValues);
    }

    public static boolean insert(Daily daily) {
        return Generic_DAO.doInsert(daily);
    }

    public static boolean delete(Daily daily) {
        Object[] keysValues = {daily.getId()};
        return Generic_DAO.doDelete(Daily.class, KEYS, keysValues);
    }

    public static boolean update(Daily daily, String fieldName, Object value) {
        Object[] keysValues = {daily.getId()};
        if (fieldName.equals("id")) return false;
        return Generic_DAO.doUpdate(Daily.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Daily> getAll() {
        return Generic_DAO.selectAll(Daily.class);
    }

    public static List<Daily> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Daily.class, fields, values);
    }

    public static List<Daily> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Daily.class, field, fieldValue);
    }
}
