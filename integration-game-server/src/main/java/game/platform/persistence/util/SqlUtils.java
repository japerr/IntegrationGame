package game.platform.persistence.util;

import jetbrains.buildServer.log.Loggers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Patrick Kranz
 */
public final class SqlUtils {
    private SqlUtils() {
        // prevent instance creation
    }

    public static void closeQuietly(Connection connection) {
        try {
            if (connection != null)
                connection.close();
        } catch(SQLException exception) {
            Loggers.SQL.error("Error closing connection.", exception);
        }
    }

    public static void closeQuietly(PreparedStatement statement) {
        try {
            if (statement != null)
                statement.close();
        } catch(SQLException exception) {
            Loggers.SQL.error("Error closing prepared statement.", exception);
        }
    }
}
