package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Alias;

import java.util.List;

public abstract class Alias_DAO {

    private static final String[] KEYS = {"id", "user_id"};

    public static Alias selectByKeys(Long id, Long user_id) {
        Object[] keysValues = {id, user_id};
        return Generic_DAO.selectByKeys(Alias.class, KEYS, keysValues);
    }

    public static boolean insert(Alias alias) {
        return Generic_DAO.doInsert(alias);
    }

    public static boolean delete(Alias alias) {
        Object[] keysValues = {alias.getId(), alias.getUserId()};
        return Generic_DAO.doDelete(Alias.class, KEYS, keysValues);
    }

    public static boolean update(Alias alias, String fieldName, Object value) {
        Object[] keysValues = {alias.getId(), alias.getUserId()};
        if (fieldName.equals("id") || fieldName.equals("user_id")) return false;
        return Generic_DAO.doUpdate(Alias.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Alias> getAll() {
        return Generic_DAO.selectAll(Alias.class);
    }

    public static List<Alias> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Alias.class, fields, values);
    }

    public static List<Alias> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Alias.class, field, fieldValue);
    }
}
