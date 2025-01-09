package is.murmur.Model.DAO;

import is.murmur.Model.Entities.Application;
import is.murmur.Model.Entities.LoggedInUser;

import java.util.List;

public abstract class LoggedInUser_DAO {

    private static final String[] KEYS = {"id"};

    public static LoggedInUser selectByKeys(Long id) {
        Object[] keysValues = {id};
        return Generic_DAO.selectByKeys(LoggedInUser.class, KEYS, keysValues);
    }

    public static boolean insert(LoggedInUser loggedInUser) {
        return Generic_DAO.doInsert(loggedInUser);
    }
    public static boolean delete(LoggedInUser loggedInUser) {
        Object[] keysValues = {loggedInUser.getId()};
        return Generic_DAO.doDelete(LoggedInUser.class, KEYS, keysValues);
    }
    public static boolean update(LoggedInUser loggedInUser, String fieldName, Object value) {
        Object[] keysValues = {loggedInUser.getId()};
        if(fieldName.equals("id")) return false;
        return Generic_DAO.doUpdate(LoggedInUser.class, KEYS, keysValues, fieldName, value);
    }



    public static List<LoggedInUser> getAll() {
        return Generic_DAO.selectAll(LoggedInUser.class);
    }
    public static List<LoggedInUser> getByParameters(String[] fields, Object[] values) {
        return Generic_DAO.selectByParameters(LoggedInUser.class, fields, values);
    }
    public static List<LoggedInUser> getByParameters(String fieldName, Object value) {
        String[] field = {fieldName};
        Object[] fieldValue = {value};
        return Generic_DAO.selectByParameters(LoggedInUser.class, field, fieldValue);
    }



}
