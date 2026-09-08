import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:" + Config.PORT_DB + "/" + Config.NAME_DB 
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String USER_NAME = Config.USER_DB;
    private static final String PASSWORD = Config.PASSWORD_DB;

    public Connection getConnect() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
            System.out.println(">> Database connect successfully to " + Config.NAME_DB + "!");
        } catch (ClassNotFoundException e) {
            System.err.println(">> MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println(">> Connect failure to database: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
}
