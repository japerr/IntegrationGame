package game.platform;

import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.ServerPaths;

import java.io.File;

/**
 * @author Patrick Kranz
 */
public class Configuration {
    public static final String PLUGIN_SHORT_NAME = "ci-game";

    private static final String DB_PASSWORD = "sa";
    private static final String DB_USER = "sa";
    private static final String DATABASE_NAME = "database";

    private final String dataDirectory;

    public Configuration(ServerPaths serverPaths) {
        if (serverPaths == null) {
            throw new IllegalArgumentException("ServerPaths must not be null");
        }
        this.dataDirectory = createDataDirectoryPath(serverPaths);
        ensureDataDirectoryExists(this.dataDirectory);
    }

    private static void ensureDataDirectoryExists(final String directory) {
        File dataDir = new File(directory);
        if (dataDir.exists()) {
            Loggers.SERVER.debug("Data directory for " + PLUGIN_SHORT_NAME + " already exists.");
            return;
        }
        Loggers.SERVER.debug("Data directory for " + PLUGIN_SHORT_NAME + " does not exist.");
        Loggers.SERVER.debug("Creating it.");
        if (!dataDir.mkdir()) {
            Loggers.SERVER.error("Unable to create data directory " + PLUGIN_SHORT_NAME);
        }
    }

    private static String createDataDirectoryPath(final ServerPaths serverPaths) {
        return new File(serverPaths.getPluginDataDirectory(), PLUGIN_SHORT_NAME).getPath();
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public String getDatabasePath() {
        return new File(getDataDirectory(), DATABASE_NAME).getPath();
    }

    public String getDbUser() {
        return DB_USER;
    }

    public String getDbPassword() {
        return DB_PASSWORD;
    }
}
