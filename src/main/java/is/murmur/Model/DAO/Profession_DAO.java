package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Profession;

import java.util.List;

public abstract class Profession_DAO {

    private static final String[] KEYS = {"name"};

    public static Profession selectByKeys(String name) {
        Object[] keysValues = {name};
        return Generic_DAO.selectByKeys(Profession.class, KEYS, keysValues);
    }

    public static boolean insert(Profession profession) {
        return Generic_DAO.doInsert(profession);
    }

    public static boolean delete(Profession profession) {
        Object[] keysValues = {profession.getName()};
        return Generic_DAO.doDelete(Profession.class, KEYS, keysValues);
    }

    public static boolean update(Profession profession, String fieldName, Object value) {
        Object[] keysValues = {profession.getName()};
        if (fieldName.equals("name")) return false;
        return Generic_DAO.doUpdate(Profession.class, KEYS, keysValues, fieldName, value);
    }

    public static List<Profession> getAll() {
        return Generic_DAO.selectAll(Profession.class);
    }

    public static List<Profession> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Profession.class, fields, values);
    }

    public static List<Profession> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Profession.class, field, fieldValue);
    }
}
