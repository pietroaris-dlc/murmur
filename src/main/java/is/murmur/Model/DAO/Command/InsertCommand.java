package is.murmur.Model.DAO.Command;

import is.murmur.Model.DAO.DAOStrategy;
import is.murmur.Model.DAO.QueryBuilder;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
        Field[] allFields = clazz.getDeclaredFields();

        // Filtra i campi da inserire, escludendo il campo "id"
        List<Field> fieldsToInsert = new ArrayList<>();
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (Field field : allFields) {
            // Se il campo si chiama "id" lo saltiamo (si assume che sia auto-generato)
            if ("id".equalsIgnoreCase(field.getName())) {
                continue;
            }
            fieldsToInsert.add(field);
            if (columns.length() > 0) {
                columns.append(",");
                placeholders.append(",");
            }
            columns.append(field.getName());
            placeholders.append("?");
        }

        // Costruisce la query di INSERT
        QueryBuilder builder = new QueryBuilder();
        String sql = builder.insertInto(strategy.getTableName(), columns.toString())
                .values(placeholders.toString())
                .build();

        // Prepara lo statement specificando il flag per recuperare le chiavi generate
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Imposta i parametri dello statement
            for (int i = 0; i < fieldsToInsert.size(); i++) {
                Field field = fieldsToInsert.get(i);
                field.setAccessible(true);
                ps.setObject(i + 1, field.get(entity));
            }

            // Esegue l'INSERT e controlla che almeno una riga sia stata inserita
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert fallito, nessuna riga inserita.");
            }

            // Recupera il valore generato (chiave serial)
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);

                    // Imposta il valore dell'id generato nell'entità
                    try {
                        Field idField = clazz.getDeclaredField("id");
                        idField.setAccessible(true);
                        idField.set(entity, generatedId);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new SQLException("Errore nel settare l'id generato sull'entità.", e);
                    }
                } else {
                    throw new SQLException("Insert fallito, nessun ID generato.");
                }
            }
            return true;
        } catch (IllegalAccessException e) {
            throw new SQLException(e);
        }
    }
}