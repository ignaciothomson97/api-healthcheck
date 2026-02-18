package cl.apihealthcheck.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class DatabaseConnection {

    private static final Properties properties = new Properties();
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    static {
        try (InputStream inputStream = DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (inputStream == null) throw new IOException("database.properties not found");
            properties.load(inputStream);
        } catch (IOException exception) {
            LOGGER.severe("Ha ocurrido un error al intentar leer el archivo database.properties");
        }
    }

    public static Connection connect() {
        try {
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.username");
            String pass = properties.getProperty("db.password");
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            LOGGER.severe("Ha ocurrido un error al establecer una conexión con la base de datos");
            return null;
        }
    }
}
