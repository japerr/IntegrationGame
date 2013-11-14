package game.admin.persistence;

import game.admin.domain.BuildConfiguration;
import game.platform.persistence.SqlBuilder;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
    private static final String UPDATE_BUILDID_SQL =
            "update BuildConfiguration set BuildTypeId = ? where BuildTypeId = ?";

    private final ProjectManager projectManager;
    private final DataSource dataSource;

    public BuildConfigurationDao(final ProjectManager projectManager, final DataSource dataSource) {
        this.projectManager = projectManager;
        this.dataSource = dataSource;
    }

    public List<BuildConfiguration> getConfigurations() {
        List<BuildConfiguration> configs = new ArrayList<BuildConfiguration>();
        Map<String, String> idNameMapping = getRegisteredBuildTypes();

        SqlBuilder sqlBuilder = null;
        try {
            sqlBuilder = new SqlBuilder(dataSource);
            ResultSet result = sqlBuilder.prepareStatement(LOAD_CONFIGS_SQL).resultSet();
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
            closeBuilder(sqlBuilder);
        }

        return configs;
    }

    private static void closeBuilder(SqlBuilder sqlBuilder) {
        if (sqlBuilder != null) {
            sqlBuilder.close();
        }
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
        SqlBuilder sqlBuilder = null;
        try {
            sqlBuilder = new SqlBuilder(dataSource);
            ResultSet result = sqlBuilder.prepareStatement(LOAD_CONFIG_SQL)
                    .withParameter(externalBuildTypeId).resultSet();
            if (result.first()) {
                return result.getBoolean(1);
            }
        } catch (SQLException exception) {
            Loggers.SERVER.error("Error while accessing database", exception);
        } finally {
            closeBuilder(sqlBuilder);
        }
        return false;
    }

    public void setEnabled(String externalBuildTypeId, boolean isEnabled) {
        if (externalBuildTypeId == null || externalBuildTypeId.isEmpty()) {
            return;
        }
        SqlBuilder sqlBuilder = null;
        try {
            sqlBuilder = new SqlBuilder(dataSource);
            ResultSet result = sqlBuilder
                    .withTransaction()
                    .prepareStatement(CONFIG_EXISTS_SQL)
                    .withParameter(externalBuildTypeId)
                    .resultSet();
            if (result.first() && result.getInt(1) == 1) {
                sqlBuilder.continueWith()
                        .prepareStatement(SET_ISACTIVE_SQL)
                        .withParameter(isEnabled)
                        .withParameter(externalBuildTypeId)
                        .update();
            } else {
                sqlBuilder.continueWith()
                        .prepareStatement(INSERT_CONFIG_SQL)
                        .withParameter(externalBuildTypeId)
                        .withParameter(isEnabled)
                        .update();
            }
            sqlBuilder.commit();
        } catch (SQLException exception) {
            Loggers.SQL.error("Error while accessing database", exception);
        } finally {
            closeBuilder(sqlBuilder);
        }
    }

    public void updateBuildTypeId(String oldId, String newId) {
        if (oldId == null || oldId.isEmpty() || newId == null || newId.isEmpty()) {
            return;
        }

        SqlBuilder sqlBuilder = null;
        try {
            sqlBuilder = new SqlBuilder(dataSource);
            sqlBuilder.prepareStatement(UPDATE_BUILDID_SQL).withParameter(newId).withParameter(oldId).update();
        } catch (SQLException exception) {
            Loggers.SQL.error("Error updating build type id: ", exception);
        } finally {
            closeBuilder(sqlBuilder);
        }
    }
}
