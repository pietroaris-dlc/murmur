package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Job;

import java.util.List;

public abstract class Job_DAO {

    private static final String[] KEYS = {"application_id"};

    public static Job selectByKeys(Long application_id) {
        Object[] keysValues = {application_id};
        return Generic_DAO.selectByKeys(Job.class, KEYS, keysValues);
    }

    public static boolean insert(Job job) {
        return Generic_DAO.doInsert(job);
    }

    public static boolean delete(Job job) {
        Object[] keysValues = {job.getApplicationId()};
        return Generic_DAO.doDelete(Job.class, KEYS, keysValues);
    }

    public static boolean update(Job job, String fieldName, Object value) {
        Object[] keysValues = {job.getApplicationId()};
        if (fieldName.equals("application_id")) return false;
        return Generic_DAO.doUpdate(Job.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Job> getAll() {
        return Generic_DAO.selectAll(Job.class);
    }

    public static List<Job> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Job.class, fields, values);
    }

    public static List<Job> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Job.class, field, fieldValue);
    }
}
