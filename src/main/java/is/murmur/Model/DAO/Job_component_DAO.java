package is.murmur.Model.DAO;
import is.murmur.Model.Entities.Job_component;
import java.util.List;

public abstract class Job_component_DAO {

    private static final String[] KEYS = {"application_id"};

    public static Job_component selectByKeys(Long application_id) {
        Object[] keysValues = {application_id};
        return Generic_DAO.selectByKeys(Job_component.class, KEYS, keysValues);
    }

    public static boolean insert(Job_component job) {
        return Generic_DAO.doInsert(job);
    }

    public static boolean delete(Job_component job) {
        Object[] keysValues = {job.getApplication_id()};
        return Generic_DAO.doDelete(Job_component.class, KEYS, keysValues);
    }

    public static boolean update(Job_component job, String fieldName, Object value) {
        Object[] keysValues = {job.getApplication_id()};
        if (fieldName.equals("application_id")) return false;
        return Generic_DAO.doUpdate(Job_component.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Job_component> getAll() {
        return Generic_DAO.selectAll(Job_component.class);
    }

    public static List<Job_component> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Job_component.class, fields, values);
    }

    public static List<Job_component> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Job_component.class, field, fieldValue);
    }
}
