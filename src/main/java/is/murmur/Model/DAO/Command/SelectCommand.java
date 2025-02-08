package is.murmur.Model.DAO.Command;

import is.murmur.Model.DAO.DAOStrategy;
import is.murmur.Model.DAO.QueryBuilder;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class SelectCommand<T> implements DBCommand<List<Map<String, Object>>>, DAOMapping{
    private final DAOStrategy<T> strategy;
    private final Object[] keysValues;

    public SelectCommand(DAOStrategy<T> strategy, Object... keysValues) {
        this.strategy = strategy;
        this.keysValues = keysValues;
    }

    @Override
    public List<Map<String, Object>> execute(Connection connection) throws SQLException {
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

        return getMaps(connection, sql, keysValues);
    }
}


