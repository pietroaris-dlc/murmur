package is.murmur.Model.DAO.Command;

import is.murmur.Model.DAO.DAOStrategy;

import java.util.Map;

public class CommandFactory {

    public static <T> InsertCommand<T> insert(DAOStrategy<T> strategy, T entity) {
        return new InsertCommand<>(strategy, entity);
    }

    public static <T> SelectCommand<T> select(DAOStrategy<T> strategy, Map<String, Object> filters) {
        return new SelectCommand<>(strategy, filters);
    }

    public static <T> SingleSelectCommand<T> singleSelect(DAOStrategy<T> strategy, Map<String, Object> filters) {
        return new SingleSelectCommand<>(strategy, filters);
    }

    public static <T> UpdateCommand<T> update(DAOStrategy<T> strategy, Object[] keysValues, String fieldToUpdate, Object newValue) {
        return new UpdateCommand<>(strategy, keysValues, fieldToUpdate, newValue);
    }

    public static <T> DeleteCommand<T> delete(DAOStrategy<T> strategy, Object... keysValues) {
        return new DeleteCommand<>(strategy, keysValues);
    }
}
