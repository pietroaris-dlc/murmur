package is.murmur.Model.DAO;
import is.murmur.Model.Entities.Upgrade_component;
import java.util.List;

public abstract class Upgrade_component_DAO {

    private static final String[] KEYS = {"application_id"};

    public static Upgrade_component selectByKeys(Long application_id) {
        Object[] keysValues = {application_id};
        return Generic_DAO.selectByKeys(Upgrade_component.class, KEYS, keysValues);
    }

    public static boolean insert(Upgrade_component upgrade) {
        return Generic_DAO.doInsert(upgrade);
    }

    public static boolean delete(Upgrade_component upgrade) {
        Object[] keysValues = {upgrade.getApplication_id()};
        return Generic_DAO.doDelete(Upgrade_component.class, KEYS, keysValues);
    }

    public static boolean update(Upgrade_component upgrade, String fieldName, Object value) {
        Object[] keysValues = {upgrade.getApplication_id()};
        if (fieldName.equals("application_id")) return false;
        return Generic_DAO.doUpdate(Upgrade_component.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Upgrade_component> getAll() {
        return Generic_DAO.selectAll(Upgrade_component.class);
    }

    public static List<Upgrade_component> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Upgrade_component.class, fields, values);
    }

    public static List<Upgrade_component> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Upgrade_component.class, field, fieldValue);
    }
}
