package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Rejected;

import java.util.List;

public abstract class Rejected_DAO {

    private static final String[] KEYS = {"application_id"};

    public static Rejected selectByKeys(Long application_id) {
        Object[] keysValues = {application_id};
        return Generic_DAO.selectByKeys(Rejected.class, KEYS, keysValues);
    }

    public static boolean insert(Rejected rejected) {
        return Generic_DAO.doInsert(rejected);
    }

    public static boolean delete(Rejected rejected) {
        Object[] keysValues = {rejected.getApplicationId()};
        return Generic_DAO.doDelete(Rejected.class, KEYS, keysValues);
    }

    public static boolean update(Rejected rejected, String fieldName, Object value) {
        Object[] keysValues = {rejected.getApplicationId()};
        if (fieldName.equals("application_id")) return false;
        return Generic_DAO.doUpdate(Rejected.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Rejected> getAll() {
        return Generic_DAO.selectAll(Rejected.class);
    }

    public static List<Rejected> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Rejected.class, fields, values);
    }

    public static List<Rejected> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Rejected.class, field, fieldValue);
    }
}
