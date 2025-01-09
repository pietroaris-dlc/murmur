package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Checked;

import java.util.List;

public abstract class Checked_DAO {

    private static final String[] KEYS = {"application_id"};

    public static Checked selectByKeys(Long application_id) {
        Object[] keysValues = {application_id};
        return Generic_DAO.selectByKeys(Checked.class, KEYS, keysValues);
    }

    public static boolean insert(Checked checkedComponent) {
        return Generic_DAO.doInsert(checkedComponent);
    }

    public static boolean delete(Checked checkedComponent) {
        Object[] keysValues = {checkedComponent.getApplicationId()};
        return Generic_DAO.doDelete(Checked.class, KEYS, keysValues);
    }

    public static boolean update(Checked checked, String fieldName, Object value) {
        Object[] keysValues = {checked.getApplicationId()};
        if (fieldName.equals("application_id")) return false;
        return Generic_DAO.doUpdate(Checked.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Checked> getAll() {
        return Generic_DAO.selectAll(Checked.class);
    }

    public static List<Checked> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Checked.class, fields, values);
    }

    public static List<Checked> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Checked.class, field, fieldValue);
    }
}
