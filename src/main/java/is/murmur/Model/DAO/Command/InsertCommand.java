package is.murmur.Model.DAO.Command;

import is.murmur.Model.DAO.DAOStrategy;
import is.murmur.Model.DAO.QueryBuilder;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertCommand<T> implements DBCommand<Boolean> {
    private final DAOStrategy<T> strategy;
    private final T entity;

    public InsertCommand(DAOStrategy<T> strategy, T entity) {
        this.strategy = strategy;
        this.entity = entity;
    }

    @Override
    public Boolean execute(Connection connection) throws SQLException {
        Class<T> clazz = strategy.getTargetEntity();
        Field[] fields = clazz.getDeclaredFields();

        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                columns.append(",");
                placeholders.append(",");
            }
            columns.append(fields[i].getName());
            placeholders.append("?");
        }

        QueryBuilder builder = new QueryBuilder();
        String sql = builder.insertInto(strategy.getTableName(), columns.toString())
                .values(placeholders.toString())
                .build();

        PreparedStatement ps = connection.prepareStatement(sql);
        try {
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                ps.setObject(i + 1, fields[i].get(entity));
            }
        } catch (IllegalAccessException e) {
            throw new SQLException(e);
        }
        return ps.executeUpdate() == 1;
    }
}

