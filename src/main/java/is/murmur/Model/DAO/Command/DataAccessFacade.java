package is.murmur.Model.DAO.Command;

import is.murmur.Model.DAO.ConnectionPool;
import is.murmur.Model.DAO.DAOFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DataAccessFacade {
    private final String dbPassword;

    public DataAccessFacade(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public <T> T executeCommand(DBCommand<T> command) {
        try (Connection connection = ConnectionPool.getConnection(dbPassword)) {
            return command.execute(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'esecuzione del comando", e);
        }
    }
}