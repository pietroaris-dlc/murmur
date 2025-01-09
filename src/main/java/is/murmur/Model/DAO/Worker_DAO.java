package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Worker;

import java.util.List;

public abstract class Worker_DAO {

    private static final String[] KEYS = {"id"};

    public static Worker selectByKeys(Long id) {
        Object[] keysValues = {id};
        return Generic_DAO.selectByKeys(Worker.class, KEYS, keysValues);
    }

    public static boolean insert(Worker worker) {
        return Generic_DAO.doInsert(worker);
    }

    public static boolean delete(Worker worker) {
        Object[] keysValues = {worker.getId()};
        return Generic_DAO.doDelete(Worker.class, KEYS, keysValues);
    }

    public static boolean update(Worker worker, String fieldName, Object value) {
        Object[] keysValues = {worker.getId()};
        if (fieldName.equals("id")) return false;
        return Generic_DAO.doUpdate(Worker.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Worker> getAll() {
        return Generic_DAO.selectAll(Worker.class);
    }

    public static List<Worker> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Worker.class, fields, values);
    }

    public static List<Worker> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Worker.class, field, fieldValue);
    }
}
