package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Upgrade;

import java.util.List;

public abstract class Upgrade_DAO {

    private static final String[] KEYS = {"application_id"};

    public static Upgrade selectByKeys(Long application_id) {
        Object[] keysValues = {application_id};
        return Generic_DAO.selectByKeys(Upgrade.class, KEYS, keysValues);
    }

    public static boolean insert(Upgrade upgrade) {
        return Generic_DAO.doInsert(upgrade);
    }

    public static boolean delete(Upgrade upgrade) {
        Object[] keysValues = {upgrade.getApplicationId()};
        return Generic_DAO.doDelete(Upgrade.class, KEYS, keysValues);
    }

    public static boolean update(Upgrade upgrade, String fieldName, Object value) {
        Object[] keysValues = {upgrade.getApplicationId()};
        if (fieldName.equals("application_id")) return false;
        return Generic_DAO.doUpdate(Upgrade.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Upgrade> getAll() {
        return Generic_DAO.selectAll(Upgrade.class);
    }

    public static List<Upgrade> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Upgrade.class, fields, values);
    }

    public static List<Upgrade> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Upgrade.class, field, fieldValue);
    }
}
