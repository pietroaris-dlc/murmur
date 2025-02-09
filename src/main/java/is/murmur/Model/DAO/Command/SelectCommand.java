package is.murmur.Model.DAO.Command;

import is.murmur.Model.DAO.DAOStrategy;
import is.murmur.Model.DAO.QueryBuilder;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SelectCommand<T> implements DBCommand<T>, DAOMapping{
    private final DAOStrategy<T> strategy;
    private final Object[] keysValues;

    public SelectCommand(DAOStrategy<T> strategy, Object... keysValues) {
        this.strategy = strategy;
        this.keysValues = keysValues;
    }

    @Override
    public T execute(Connection connection) throws SQLException {
        QueryBuilder builder = new QueryBuilder();
        String table = strategy.getTableName();
        String[] keys = strategy.getKeys();
        StringBuilder condition = new StringBuilder();

        for (int i = 0; i < keys.length; i++) {
            if (i > 0) {
                condition.append(" AND ");
            }
            condition.append(keys[i]).append("=?");
        }

        String sql = builder.select("*")
                .from(table)
                .where(condition.toString())
                .build();

        List<Map<String, Object>> results = getMaps(connection, sql, keysValues);

        if (results.isEmpty()) {
            return null;
        }

        return mapToEntity(results.get(0));
    }

    private T mapToEntity(Map<String, Object> data) {
        try {
            T instance = strategy.getTargetEntity().getDeclaredConstructor().newInstance();
            Field[] fields = strategy.getTargetEntity().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true); // Sblocca il campo privato
                if (data.containsKey(field.getName())) {
                    field.set(instance, data.get(field.getName()));
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Errore nel mapping dell'entità", e);
        }
    }
}