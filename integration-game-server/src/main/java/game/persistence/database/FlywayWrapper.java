package game.persistence.database;

import com.googlecode.flyway.core.Flyway;

/**
 * This class wraps the flyway main class because flyway is loading its
 * resources with a classLoader that is obtained by the current thread
 * instead of calling <code>getClass().getClassLoader()</code></>. The
 * problem is that by this, flyway only get a reference to the web
 * application class loader and not to TeamCitys plugin classloader.
 *
 * Unfortunately the plugin classLoder is the one containing the
 * path to the migration scripts.
 *
 * Therefore, this wrapper sets the plugin classLoader on the
 * current thread.
 *
 * @author Patrick Kranz
 */
public class FlywayWrapper {
    private final Flyway flyway;

    public FlywayWrapper(final Flyway flyway) {
        this.flyway = flyway;
    }

    /**
     * Sets the context classLoader to the plugin classLoader. Then
     * migrate is called and finally the original classLoader is restored.
     */
    public void migrate() {
        ClassLoader pluginClassLoader = getClass().getClassLoader();
        ClassLoader contextClassLoader =
                Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(pluginClassLoader);
        try {
            flyway.migrate();
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }
}
