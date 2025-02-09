package is.murmur.Model.DAO.Command;

import is.murmur.Model.DAO.DAOStrategy;
import is.murmur.Model.DAO.QueryBuilder;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleSelectCommand<T> implements DBCommand<T>, DAOMapping {
    private final DAOStrategy<T> strategy;
    private final Map<String, Object> filters;

    public SingleSelectCommand(DAOStrategy<T> strategy, Map<String, Object> filters) {
        this.strategy = strategy;
        this.filters = filters;
    }

    @Override
    public T execute(Connection connection) throws SQLException {
        QueryBuilder builder = new QueryBuilder();
        String table = strategy.getTableName();
        StringBuilder condition = new StringBuilder();
        List<Object> parameters = new ArrayList<>();

        // Costruisce dinamicamente la clausola WHERE basata sui filtri della mappa
        if (filters != null && !filters.isEmpty()) {
            for (Map.Entry<String, Object> entry : filters.entrySet()) {
                if (condition.length() > 0) {
                    condition.append(" AND ");
                }
                condition.append(entry.getKey()).append("=?");
                parameters.add(entry.getValue());
            }
        }

        // Costruisce la query; se non sono presenti filtri, non viene aggiunta la clausola WHERE
        String sql;
        if (condition.length() > 0) {
            sql = builder.select("*")
                    .from(table)
                    .where(condition.toString())
                    .build();
        } else {
            sql = builder.select("*")
                    .from(table)
                    .build();
        }

        // Esegue la query e ottiene una lista di mappe (una per ogni record)
        List<Map<String, Object>> results = getMaps(connection, sql, parameters.toArray());

        // Restituisce la prima istanza trovata oppure null se la lista è vuota
        if (results.isEmpty()) {
            return null;
        }
        return mapToEntity(results.get(0));
    }

    /**
     * Mappa una mappa di valori (campo -> valore) in un'istanza dell'entità target.
     *
     * @param data la mappa contenente i valori del record
     * @return l'istanza dell'entità popolata
     */
    private T mapToEntity(Map<String, Object> data) {
        try {
            T instance = strategy.getTargetEntity().getDeclaredConstructor().newInstance();
            Field[] fields = strategy.getTargetEntity().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true); // Rende accessibili anche i campi privati
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