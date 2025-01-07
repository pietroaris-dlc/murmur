package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Application;

import java.util.List;

public abstract class Application_DAO implements Generic_DAO {
    private static final String[] KEYS = {"id"};

    public static Application selectByKeys(Long id) {
        Object[] keysValues = {id};
        return Generic_DAO.selectByKeys(Application.class, KEYS, keysValues);
    }
    public static boolean insert(Application application) {
        return Generic_DAO.doInsert(application);
    }
    public static boolean delete(Application application) {
        Object[] keysValues = {application.getId()};
        return Generic_DAO.doDelete(Application.class, KEYS, keysValues);
    }
    public static boolean update(Application application, String fieldName, Object value) {
        Object[] keysValues = {application.getId()};
        if(fieldName.equals("id")) return false;
        return Generic_DAO.doUpdate(Application.class, KEYS, keysValues, fieldName, value);
    }
    public static List<Application> getAll() {
        return Generic_DAO.selectAll(Application.class);
    }
    public static List<Application> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Application.class, fields, values);
    }
    public static List<Application> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Application.class, field, fieldValue);
    }
}