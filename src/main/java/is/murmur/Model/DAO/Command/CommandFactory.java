package is.murmur.Model.DAO.Command;

import is.murmur.Model.DAO.DAOStrategy;

public class CommandFactory {

    public static <T> InsertCommand<T> insert(DAOStrategy<T> strategy, T entity) {
        return new InsertCommand<>(strategy, entity);
    }

    public static <T> SelectCommand<T> select(DAOStrategy<T> strategy, Object... keysValues) {
        return new SelectCommand<>(strategy, keysValues);
    }

    public static JoinSelectCommand joinSelect(String sql, Object... parameters) {
        return new JoinSelectCommand(sql, parameters);
    }

    public static <T> UpdateCommand<T> update(DAOStrategy<T> strategy, Object[] keysValues, String fieldToUpdate, Object newValue) {
        return new UpdateCommand<>(strategy, keysValues, fieldToUpdate, newValue);
    }

    public static <T> DeleteCommand<T> delete(DAOStrategy<T> strategy, Object... keysValues) {
        return new DeleteCommand<>(strategy, keysValues);
    }
}
