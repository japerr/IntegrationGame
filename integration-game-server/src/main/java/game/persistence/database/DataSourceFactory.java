package game.persistence.database;

import game.Configuration;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

/**
 * @author Patrick Kranz
 */
public class DataSourceFactory {
    private static final String JDBC_PREFIX = "jdbc:h2:";

    private final Configuration configuration;

    public DataSourceFactory(Configuration configuration) throws Exception {
        this.configuration = configuration;
    }

    public DataSource createDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(JDBC_PREFIX + configuration.getDatabasePath());
        dataSource.setUser(configuration.getDbUser());
        dataSource.setPassword(configuration.getDbPassword());
        return dataSource;
    }
}
