package is.murmur.Model.DAO.Command;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class JoinSelectCommand implements DBCommand<List<Map<String, Object>>>, DAOMapping {
    private final String sql;
    private final Object[] parameters;

    public JoinSelectCommand(String sql, Object... parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }

    @Override
    public List<Map<String, Object>> execute(Connection connection) throws SQLException {
        return getMaps(connection, sql, parameters);
    }
}

