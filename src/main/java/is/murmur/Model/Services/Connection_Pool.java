package is.murmur.Model.Services;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.sql.Connection;
import java.sql.SQLException;

public class Connection_Pool {
    private static DataSource ds;

    public static Connection getConnection() throws SQLException{
        if(ds == null){
            PoolProperties p = new PoolProperties();
            p.setUrl("jdbc:mysql://localhost:3306/murmur");
            p.setDriverClassName("com.mysql.cj.jdbc.Driver");
            p.setUsername("root");
            p.setPassword("110st!Lrnz");
            p.setMaxActive(100);
            p.setInitialSize(10);
            p.setMinIdle(10);
            p.setRemoveAbandonedTimeout(60);
            p.setRemoveAbandoned(true);
            ds = new DataSource();
            ds.setPoolProperties(p);
        }
        return ds.getConnection();
    }

    public static void closeConnection(Connection connection) throws SQLException{
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connessione chiusa.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

