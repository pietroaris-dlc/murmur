package is.murmur.Model.DAO.Command;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBCommand<T> {
    T execute(Connection connection) throws SQLException;
}
