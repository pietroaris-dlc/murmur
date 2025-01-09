package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Career;

import java.util.List;

public abstract class Career_DAO {

    private static final String[] KEYS = {"worker_id", "profession"};

    public static Career selectByKeys(Long worker_id, String profession) {
        Object[] keysValues = {worker_id, profession};
        return Generic_DAO.selectByKeys(Career.class, KEYS, keysValues);
    }

    public static boolean insert(Career career) {
        return Generic_DAO.doInsert(career);
    }

    public static boolean delete(Career career) {
        Object[] keysValues = {career.getWorkerId(), career.getProfession()};
        return Generic_DAO.doDelete(Career.class, KEYS, keysValues);
    }

    public static boolean update(Career career, String fieldName, Object value) {
        Object[] keysValues = {career.getWorkerId(), career.getProfession()};
        if (fieldName.equals("worker_id") || fieldName.equals("profession")) return false;
        return Generic_DAO.doUpdate(Career.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Career> getAll() {
        return Generic_DAO.selectAll(Career.class);
    }

    public static List<Career> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Career.class, fields, values);
    }

    public static List<Career> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Career.class, field, fieldValue);
    }
}
