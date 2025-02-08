package is.murmur.Model.DAO.Command;

import is.murmur.Model.DAO.DAOStrategy;
import is.murmur.Model.DAO.QueryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteCommand<T> implements DBCommand<Boolean> {
    private final DAOStrategy<T> strategy;
    private final Object[] keysValues;

    public DeleteCommand(DAOStrategy<T> strategy, Object... keysValues) {
        this.strategy = strategy;
        this.keysValues = keysValues;
    }

    @Override
    public Boolean execute(Connection connection) throws SQLException {
        String table = strategy.getTableName();
        String[] keys = strategy.getKeys();

        // Costruisco la clausola WHERE per la DELETE
        StringBuilder whereClause = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            if (i > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append(keys[i]).append("=?");
        }

        // Utilizzo il QueryBuilder per costruire la query DELETE completa
        String sql = new QueryBuilder()
                .deleteFrom(table)
                .where(whereClause.toString())
                .build();

        PreparedStatement ps = connection.prepareStatement(sql);
        for (int i = 0; i < keysValues.length; i++) {
            ps.setObject(i + 1, keysValues[i]);
        }
        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;
    }
}