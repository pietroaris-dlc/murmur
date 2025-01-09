package is.murmur.Model.DAO;

import java.util.List;
import is.murmur.Model.Entities.Logged_in_user;
public abstract class Logged_in_user_DAO {

    private static final String[] KEYS = {"id"};

    public static Logged_in_user selectByKeys(Long id) {
        Object[] keysValues = {id};
        return Generic_DAO.selectByKeys(Logged_in_user.class, KEYS, keysValues);
    }

    public static boolean insert(Logged_in_user loggedInUser) {
        return Generic_DAO.doInsert(loggedInUser);
    }
    public static boolean delete(Logged_in_user loggedInUser) {
        Object[] keysValues = {loggedInUser.getId()};
        return Generic_DAO.doDelete(Logged_in_user.class, KEYS, keysValues);
    }
    public static boolean update(Logged_in_user loggedInUser, String fieldName, Object value) {
        Object[] keysValues = {loggedInUser.getId()};
        if(fieldName.equals("id")) return false;
        return Generic_DAO.doUpdate(Logged_in_user.class, KEYS, keysValues, fieldName, value);
    }



    public static List<Logged_in_user> getAll() {
        return Generic_DAO.selectAll(Logged_in_user.class);
    }
    public static List<Logged_in_user> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(Logged_in_user.class, fields, values);
    }
    public static List<Logged_in_user> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(Logged_in_user.class, field, fieldValue);
    }



}
