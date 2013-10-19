package game.persistence;

import game.domain.BuildConfiguration;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static game.utils.SqlUtils.closeQuietly;

/**
 * @author Patrick Kranz
 */
public class BuildConfigurationDao {
    private static final String LOAD_CONFIGS_SQL = "select BuildTypeId, "+
            "IsActive from BuildConfiguration";
    private static final String LOAD_CONFIG_SQL =
            "select IsActive from BuildConfiguration where BuildTypeId = ?";
    private static final String CONFIG_EXISTS_SQL =
            "select count(BuildTypeId) from BuildConfiguration where BuildTypeId = ?";
    private static final String SET_ISACTIVE_SQL =
            "update BuildConfiguration set IsActive = ? where BuildTypeId = ?";
    private static final String INSERT_CONFIG_SQL =
            "insert into BuildConfiguration(BuildTypeId, IsActive) values(?, ?)";

    private final ProjectManager projectManager;
    private final DataSource dataSource;

    public BuildConfigurationDao(final ProjectManager projectManager, final DataSource dataSource) {
        this.projectManager = projectManager;
        this.dataSource = dataSource;
    }

    public List<BuildConfiguration> getConfigurations() {
        List<BuildConfiguration> configs = new ArrayList<BuildConfiguration>();
        Map<String, String> idNameMapping = getRegisteredBuildTypes();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(LOAD_CONFIGS_SQL);
            ResultSet result = statement.executeQuery();
            Map<String, Boolean> idActiveMapping = new HashMap<String, Boolean>();
            while(result.next()) {
                idActiveMapping.put(result.getString(1), result.getBoolean(2));
            }
            for (String externalId : idNameMapping.keySet()) {
                boolean isActive = idActiveMapping.containsKey(externalId) ? idActiveMapping.get(externalId) : false;
                configs.add(new BuildConfiguration(idNameMapping.get(externalId), externalId, isActive));
            }
        } catch (SQLException exception) {
            Loggers.SERVER.error("Error while accessing database", exception);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
        }

        return configs;
    }

    private Map<String, String> getRegisteredBuildTypes() {
        List<SBuildType> buildTypes = projectManager.getAllBuildTypes();
        Map<String, String> idNameMapping = new HashMap<String, String>();
        for (SBuildType buildType : buildTypes) {
            idNameMapping.put(buildType.getExternalId(), buildType.getFullName());
        }
        return idNameMapping;
    }

    public Set<String> getBuildIds() {
        Set<String> buildIds = new HashSet<String>();
        for (SBuildType build : projectManager.getAllBuildTypes()) {
            buildIds.add(build.getExternalId());
        }
        return buildIds;
    }

    public boolean isEnabled(String externalBuildTypeId) {
        if (externalBuildTypeId == null || externalBuildTypeId.isEmpty()) {
            return false;
        }
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(LOAD_CONFIG_SQL);
            statement.setString(1, externalBuildTypeId);
            ResultSet result = statement.executeQuery();
            if (result.first()) {
                return result.getBoolean(1);
            }
        } catch (SQLException exception) {
            Loggers.SERVER.error("Error while accessing database", exception);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
        }
        return false;
    }

    public void setEnabled(String externalBuildTypeId, boolean isEnabled) {
        if (externalBuildTypeId == null || externalBuildTypeId.isEmpty()) {
            return;
        }
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(CONFIG_EXISTS_SQL);
            statement.setString(1, externalBuildTypeId);
            ResultSet result = statement.executeQuery();
            if (result.first() && result.getInt(1) == 1) {
                closeQuietly(statement);
                statement = connection.prepareStatement(SET_ISACTIVE_SQL);
                statement.setBoolean(1, isEnabled);
                statement.setString(2, externalBuildTypeId);
                statement.executeUpdate();
            } else {
                closeQuietly(statement);
                statement = connection.prepareStatement(INSERT_CONFIG_SQL);
                statement.setString(1, externalBuildTypeId);
                statement.setBoolean(2, isEnabled);
                statement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException exception) {
            Loggers.SQL.error("Error while accessing database", exception);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }
}
