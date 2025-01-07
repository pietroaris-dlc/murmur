package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Location;

import java.util.List;

public abstract class Location_DAO implements Generic_DAO {
    private static final String[] KEYS = {"id"};

    public static Location selectByKeys(Long id) {
        Object[] keysValues = {id};
        return Generic_DAO.selectByKeys(Location.class, KEYS, keysValues);
    }

    public static boolean insert(Location location) {
        return Generic_DAO.doInsert(location);
    }

    public static boolean delete(Location location) {
        Object[] keysValues = {location.getId()};
        return Generic_DAO.doDelete(Location.class, KEYS, keysValues);
    }

    public static boolean update(Location location, String fieldName, Object value) {
        Object[] keysValues = {location.getId()};
        if (fieldName.equals("id")) return false;
        return Generic_DAO.doUpdate(Location.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Location> getAll() {
        return Generic_DAO.selectAll(Location.class);
    }

    public static List<Location> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Location.class, fields, values);
    }

    public static List<Location> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Location.class, field, fieldValue);
    }
}
