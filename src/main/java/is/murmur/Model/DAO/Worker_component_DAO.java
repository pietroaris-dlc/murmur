package is.murmur.Model.DAO;
import is.murmur.Model.Entities.Worker_component;

import java.util.List;

public abstract class Worker_component_DAO {

    private static final String[] KEYS = {"id"};

    public static Worker_component selectByKeys(Long id) {
        Object[] keysValues = {id};
        return Generic_DAO.selectByKeys(Worker_component.class, KEYS, keysValues);
    }

    public static boolean insert(Worker_component worker) {
        return Generic_DAO.doInsert(worker);
    }

    public static boolean delete(Worker_component worker) {
        Object[] keysValues = {worker.getUser_id()};
        return Generic_DAO.doDelete(Worker_component.class, KEYS, keysValues);
    }

    public static boolean update(Worker_component worker, String fieldName, Object value) {
        Object[] keysValues = {worker.getUser_id()};
        if (fieldName.equals("id")) return false;
        return Generic_DAO.doUpdate(Worker_component.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Worker_component> getAll() {
        return Generic_DAO.selectAll(Worker_component.class);
    }

    public static List<Worker_component> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Worker_component.class, fields, values);
    }

    public static List<Worker_component> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Worker_component.class, field, fieldValue);
    }
}
