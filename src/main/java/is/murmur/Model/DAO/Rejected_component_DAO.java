package is.murmur.Model.DAO;
import is.murmur.Model.Entities.Rejected_component;
import java.util.List;

public abstract class Rejected_component_DAO {

    private static final String[] KEYS = {"application_id"};

    public static Rejected_component selectByKeys(Long application_id) {
        Object[] keysValues = {application_id};
        return Generic_DAO.selectByKeys(Rejected_component.class, KEYS, keysValues);
    }

    public static boolean insert(Rejected_component rejected) {
        return Generic_DAO.doInsert(rejected);
    }

    public static boolean delete(Rejected_component rejected) {
        Object[] keysValues = {rejected.getApplication_id()};
        return Generic_DAO.doDelete(Rejected_component.class, KEYS, keysValues);
    }

    public static boolean update(Rejected_component rejected, String fieldName, Object value) {
        Object[] keysValues = {rejected.getApplication_id()};
        if (fieldName.equals("application_id")) return false;
        return Generic_DAO.doUpdate(Rejected_component.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Rejected_component> getAll() {
        return Generic_DAO.selectAll(Rejected_component.class);
    }

    public static List<Rejected_component> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Rejected_component.class, fields, values);
    }

    public static List<Rejected_component> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Rejected_component.class, field, fieldValue);
    }
}
