package bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String HOST = "b4qeia9j7ow7vk83hbph-mysql.services.clever-cloud.com";
    private static final String PORT = "3306";
    private static final String DATABASE = "b4qeia9j7ow7vk83hbph";
    private static final String USER = "umzmgntf4ufmh2sg";
    private static final String PASSWORD = "o2CmmaEo5jiNaWTgsJaq";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontro el driver JDBC de MySQL en la carpeta lib.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
