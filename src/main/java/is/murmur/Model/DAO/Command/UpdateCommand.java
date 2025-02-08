package is.murmur.Model.DAO.Command;

import is.murmur.Model.DAO.DAOStrategy;
import is.murmur.Model.DAO.QueryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateCommand<T> implements DBCommand<Boolean> {
    private final DAOStrategy<T> strategy;
    private final Object[] keysValues;
    private final String fieldToUpdate;
    private final Object newValue;

    public UpdateCommand(DAOStrategy<T> strategy, Object[] keysValues, String fieldToUpdate, Object newValue) {
        this.strategy = strategy;
        this.keysValues = keysValues;
        this.fieldToUpdate = fieldToUpdate;
        this.newValue = newValue;
    }

    @Override
    public Boolean execute(Connection connection) throws SQLException {
        String table = strategy.getTableName();
        String[] keys = strategy.getKeys();

        // Costruisco la clausola WHERE per identificare il record da aggiornare
        StringBuilder whereClause = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            if (i > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append(keys[i]).append("=?");
        }

        // Utilizzo il QueryBuilder per costruire la query UPDATE
        String sql = new QueryBuilder()
                .update(table)
                .set(fieldToUpdate + "=?")
                .where(whereClause.toString())
                .build();

        PreparedStatement ps = connection.prepareStatement(sql);
        // Imposto il nuovo valore da aggiornare
        ps.setObject(1, newValue);
        // Imposto i valori delle chiavi (i placeholder della clausola WHERE)
        for (int i = 0; i < keysValues.length; i++) {
            ps.setObject(i + 2, keysValues[i]);
        }

        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;
    }
}