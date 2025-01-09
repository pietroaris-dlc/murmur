package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Checked_component;

import java.util.List;

public abstract class Checked_component_DAO {

    private static final String[] KEYS = {"application_id"};

    public static Checked_component selectByKeys(Long application_id) {
        Object[] keysValues = {application_id};
        return Generic_DAO.selectByKeys(Checked_component.class, KEYS, keysValues);
    }

    public static boolean insert(Checked_component checkedComponent) {
        return Generic_DAO.doInsert(checkedComponent);
    }

    public static boolean delete(Checked_component checkedComponent) {
        Object[] keysValues = {checkedComponent.getApplicationId()};
        return Generic_DAO.doDelete(Checked_component.class, KEYS, keysValues);
    }

    public static boolean update(Checked_component checked, String fieldName, Object value) {
        Object[] keysValues = {checked.getApplicationId()};
        if (fieldName.equals("application_id")) return false;
        return Generic_DAO.doUpdate(Checked_component.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Checked_component> getAll() {
        return Generic_DAO.selectAll(Checked_component.class);
    }

    public static List<Checked_component> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Checked_component.class, fields, values);
    }

    public static List<Checked_component> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Checked_component.class, field, fieldValue);
    }
}
