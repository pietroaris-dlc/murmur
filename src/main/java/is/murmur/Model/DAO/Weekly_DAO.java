package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Weekly;

import java.util.List;

public abstract class Weekly_DAO implements Generic_DAO {
    private static final String[] KEYS = {"id"};

    public static Weekly selectByKeys(Long id) {
        Object[] keysValues = {id};
        return Generic_DAO.selectByKeys(Weekly.class, KEYS, keysValues);
    }

    public static boolean insert(Weekly weekly) {
        return Generic_DAO.doInsert(weekly);
    }

    public static boolean delete(Weekly weekly) {
        Object[] keysValues = {weekly.getId()};
        return Generic_DAO.doDelete(Weekly.class, KEYS, keysValues);
    }

    public static boolean update(Weekly weekly, String fieldName, Object value) {
        Object[] keysValues = {weekly.getId()};
        if (fieldName.equals("id")) return false;
        return Generic_DAO.doUpdate(Weekly.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Weekly> getAll() {
        return Generic_DAO.selectAll(Weekly.class);
    }

    public static List<Weekly> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Weekly.class, fields, values);
    }

    public static List<Weekly> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Weekly.class, field, fieldValue);
    }
}
