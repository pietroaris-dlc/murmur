package is.murmur.Model.DAO;

import is.murmur.Model.Services.Connection_Pool;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface Generic_DAO {

    static <T> T selectByKeys(Class<T> clazz, String[] keys, Object[] keysValues) {
        String tableName = getTableName(clazz);
        StringBuilder query = new StringBuilder("SELECT * FROM " + tableName + " WHERE " + keys[0] + "=?");
        for (int i = 1; i < keys.length; i++) {
            query.append(" AND ").append(keys[i]).append("=?");
        }

        try (Connection connection = Connection_Pool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query.toString());
            setPreparedStatementParameters(ps, keysValues);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToEntity(clazz, rs);
            }

            return null;
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static <T> List<T> selectAll(Class<T> clazz) {
        String tableName = getTableName(clazz);
        String query = "SELECT * FROM " + tableName;

        List<T> results = new ArrayList<>();

        try (Connection connection = Connection_Pool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToEntity(clazz, rs));
            }

        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return results;
    }

    static <T> List<T> selectByParameters(Class<T> clazz, String[] parameters, Object[] values) {
        String tableName = getTableName(clazz);
        StringBuilder query = new StringBuilder("SELECT * FROM " + tableName + " WHERE " + parameters[0] + "=?");
        for (int i = 1; i < parameters.length; i++) {
            query.append(" AND ").append(parameters[i]).append("=?");
        }

        List<T> results = new ArrayList<>();

        try (Connection connection = Connection_Pool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query.toString());
            setPreparedStatementParameters(ps, values);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetToEntity(clazz, rs));
            }

        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return results;
    }

    static <T> boolean doInsert(T entity) {
        Class<?> clazz = entity.getClass();
        String tableName = getTableName(clazz);

        Field[] fields = clazz.getDeclaredFields();
        StringBuilder fieldNames = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (Field field : fields) {
            field.setAccessible(true);
            if (fieldNames.length() > 0) {
                fieldNames.append(",");
                placeholders.append(",");
            }
            fieldNames.append(field.getName());
            placeholders.append("?");
        }

        String query = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, fieldNames.toString(), placeholders.toString());

        try (Connection connection = Connection_Pool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query);

            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                ps.setObject(i + 1, field.get(entity));
            }

            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error");
            }

        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    static <T> boolean doDelete(Class<T> clazz, String[] keys, Object[] keysValues) {
        String tableName = getTableName(clazz);
        StringBuilder query = new StringBuilder("DELETE FROM " + tableName + " WHERE " + keys[0] + "=?");

        for (int i = 1; i < keys.length; i++) {
            query.append(" AND ").append(keys[i]).append("=?");
        }

        try (Connection connection = Connection_Pool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query.toString());
            setPreparedStatementParameters(ps, keysValues);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    static <T> boolean doUpdate(Class<T> clazz, String[] keys, Object[] keysValues, String fieldToUpdate, Object newValue) {
        String tableName = getTableName(clazz);
        StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET " + fieldToUpdate + " = ? WHERE " + keys[0] + "=?");
        for (int i = 1; i < keys.length; i++) {
            query.append(" AND ").append(keys[i]).append("=?");
        }

        try (Connection connection = Connection_Pool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query.toString());

            ps.setObject(1, newValue);
            setPreparedStatementParameters(ps, keysValues);

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static String getTableName(Class<?> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }

    private static void setPreparedStatementParameters(PreparedStatement ps, Object[] values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof String) {
                ps.setString(i + 1, (String) values[i]);
            } else if (values[i] instanceof Integer) {
                ps.setInt(i + 1, (Integer) values[i]);
            } else if (values[i] instanceof Double) {
                ps.setDouble(i + 1, (Double) values[i]);
            } else if (values[i] instanceof Long) {
                ps.setLong(i + 1, (Long) values[i]);
            } else if (values[i] instanceof Timestamp) {
                ps.setTimestamp(i + 1, (Timestamp) values[i]);
            } else {
                throw new IllegalArgumentException("Tipo non supportato");
            }
        }
    }

    private static <T> T mapResultSetToEntity(Class<T> clazz, ResultSet rs) throws SQLException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        T entity = constructor.newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = rs.getObject(field.getName());

            if (field.getType().equals(LocalDate.class) && value instanceof Date) {
                value = ((Date) value).toLocalDate();
            }

            field.set(entity, value);
        }

        return entity;
    }
}
