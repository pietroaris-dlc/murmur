package is.murmur.Model.DAO;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdaptiveDAO {

    private final String password;
    private DAOStrategy<?> currentDAOStrategy;

    public AdaptiveDAO(String password) {
        this.password = password;
    }

    public <T> void setDAOStrategy(DAOStrategy<T> strategy) {
        this.currentDAOStrategy = strategy;
    }

    public <T> T selectByKeys(Object[] keysValues) {
        DAOStrategy<T> strategy = getTypedDAOStrategy();
        String tableName = strategy.getTableName();
        String[] keys = strategy.getKeys();
        StringBuilder query = new StringBuilder("SELECT * FROM " + tableName + " WHERE " + keys[0] + "=?");
        for (int i = 1; i < keys.length; i++) {
            query.append(" AND ").append(keys[i]).append("=?");
        }

        try (Connection connection = ConnectionPool.getConnection(password)) {
            PreparedStatement ps = connection.prepareStatement(query.toString());
            setPreparedStatementParameters(ps, keysValues);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToEntity(strategy.getTargetEntity(), rs);
            }
            return null;
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> selectAll() {
        DAOStrategy<T> strategy = getTypedDAOStrategy();
        String tableName = strategy.getTableName();
        String query = "SELECT * FROM " + tableName;

        List<T> results = new ArrayList<>();

        try (Connection connection = ConnectionPool.getConnection(password)) {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToEntity(strategy.getTargetEntity(), rs));
            }
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return results;
    }

    public <T> boolean insert(T entity) {
        DAOStrategy<T> strategy = getTypedDAOStrategy();
        String tableName = strategy.getTableName();
        Class<T> clazz = strategy.getTargetEntity();

        Field[] fields = clazz.getDeclaredFields();
        String query = getQuery(fields, tableName);

        try (Connection connection = ConnectionPool.getConnection(password)) {
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

    public <T> boolean delete(Object[] keysValues) {
        DAOStrategy<T> strategy = getTypedDAOStrategy();
        String tableName = strategy.getTableName();
        String[] keys = strategy.getKeys();
        StringBuilder query = new StringBuilder("DELETE FROM " + tableName + " WHERE " + keys[0] + "=?");

        for (int i = 1; i < keys.length; i++) {
            query.append(" AND ").append(keys[i]).append("=?");
        }

        try (Connection connection = ConnectionPool.getConnection(password)) {
            PreparedStatement ps = connection.prepareStatement(query.toString());
            setPreparedStatementParameters(ps, keysValues);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> boolean update(Object[] keysValues, String fieldToUpdate, Object newValue) {
        DAOStrategy<T> strategy = getTypedDAOStrategy();
        String tableName = strategy.getTableName();
        String[] keys = strategy.getKeys();
        StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET " + fieldToUpdate + " = ? WHERE " + keys[0] + "=?");

        for (int i = 1; i < keys.length; i++) {
            query.append(" AND ").append(keys[i]).append("=?");
        }

        try (Connection connection = ConnectionPool.getConnection(password)) {
            PreparedStatement ps = connection.prepareStatement(query.toString());

            ps.setObject(1, newValue);
            setPreparedStatementParameters(ps, keysValues);

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> DAOStrategy<T> getTypedDAOStrategy() {
        return (DAOStrategy<T>) currentDAOStrategy;
    }

    private static String getQuery(Field[] fields, String tableName) {
        StringBuilder fieldNames = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (Field field : fields) {
            if (fieldNames.length() > 0) {
                fieldNames.append(",");
                placeholders.append(",");
            }
            fieldNames.append(field.getName());
            placeholders.append("?");
        }

        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, fieldNames.toString(), placeholders.toString());
    }

    private static void setPreparedStatementParameters(PreparedStatement ps, Object[] values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            ps.setObject(i + 1, values[i]);
        }
    }

    private static <T> T mapResultSetToEntity(Class<T> clazz, ResultSet rs) throws SQLException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        T entity = clazz.getDeclaredConstructor().newInstance();

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