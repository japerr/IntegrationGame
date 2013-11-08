package game.admin.persistence;

import game.admin.domain.BuildConfiguration;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * @author Patrick Kranz
 */
@RunWith(MockitoJUnitRunner.class)
public class BuildConfigurationDaoTest {

    @Mock
    private ProjectManager projectManagerMock;

    private List<SBuildType> buildTypes;

    private BuildConfigurationDao dao;

    private DataSource dataSource;

    @Before
    public void setup() throws SQLException {
        dataSource = createDataSource();
        buildTypes = createBuildTypes();
        when(projectManagerMock.getAllBuildTypes()).thenReturn(buildTypes);
        createTable(dataSource);
        dao = new BuildConfigurationDao(projectManagerMock, dataSource);
    }

    private static void createTable(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("drop table if exists BuildConfiguration");
        statement.execute(
                "CREATE TABLE BuildConfiguration(BuildTypeId VARCHAR(255) PRIMARY KEY, IsActive BOOLEAN);"
        );
    }

    private static List<SBuildType> createBuildTypes() {
        List<SBuildType> buildTypes = new ArrayList<SBuildType>();
        SBuildType compileBuild = Mockito.mock(SBuildType.class);
        when(compileBuild.getExternalId()).thenReturn("compileBuild");
        when(compileBuild.getFullName()).thenReturn("Compile");
        SBuildType testBuild = Mockito.mock(SBuildType.class);
        when(testBuild.getExternalId()).thenReturn("testBuild");
        when(testBuild.getFullName()).thenReturn("Test Application");
        buildTypes.add(compileBuild);
        buildTypes.add(testBuild);
        return buildTypes;
    }

    private static DataSource createDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test");
        dataSource.setUser("sa");
        return dataSource;
    }

    @Test
    public void shouldReturnAllExistingBuildIdsWhenBuildIdsGiven() throws Exception {
        Set<String> buildIds = dao.getBuildIds();
        assertThat(buildIds.size(), equalTo(2));
    }

    @Test
    public void shouldReturnEmptySetWhenNoBuildIdsGiven() {
        when(projectManagerMock.getAllBuildTypes()).thenReturn(Collections.EMPTY_LIST);
        Set<String> buildIds = dao.getBuildIds();
        assertThat(buildIds, is(not(nullValue())));
        assertThat(buildIds.size(), is(equalTo(0)));
    }

    @Test
    public void shouldReturnTrueWhenActiveBuildTypeIdGiven() {
        insertRow("test", true);
        assertThat(dao.isEnabled("test"), is(true));
    }

    @Test
    public void shouldReturnFalseWhenInactiveBuildTypeIdGiven() {
        insertRow("test", false);
        assertThat(dao.isEnabled("test"), is(false));
    }

    @Test
    public void shouldReturnFalseWhenUnknownBuildTypeIdGiven() {
        insertRow("test", true);
        assertThat(dao.isEnabled("unknown"), is(false));
    }

    @Test
    public void shouldReturnFalseWhenNullGiven() {
        assertThat(dao.isEnabled(null), is(false));
    }

    @Test
    public void shouldReturnFalseWhenEmptyStringGiven() {
        assertThat(dao.isEnabled(""), is(false));
    }

    @Test
    public void shouldNotAccessDatabaseWhenNullBuildIdGiven() throws SQLException {
        DataSource dataSourceMock = Mockito.mock(DataSource.class);
        dao = new BuildConfigurationDao(projectManagerMock, dataSourceMock);
        dao.setEnabled(null, false);
        verify(dataSourceMock, never()).getConnection();
    }

    @Test
    public void shouldNotAccessDatabaseWhenEmptyBuildIdGiven() throws SQLException {
        DataSource dataSourceMock = Mockito.mock(DataSource.class);
        dao = new BuildConfigurationDao(projectManagerMock, dataSourceMock);
        dao.setEnabled("", false);
        verify(dataSourceMock, never()).getConnection();
    }

    @Test
    public void shouldInsertBuildConfigurationWhenNewBuildIdGiven() throws SQLException {
        dao.setEnabled("test", true);
        Statement statement = dataSource.getConnection().createStatement();
        ResultSet result = statement.executeQuery("select BuildTypeId, IsActive from BuildConfiguration");
        assertThat(result.first(), is(true));
        assertThat(result.getString(1), is(equalTo("test")));
        assertThat(result.getBoolean(2), is(equalTo(true)));
        assertThat(result.next(), is(false));
    }

    @Test
    public void shouldUpdateBuildConfigurationWhenExistingBuildIdGiven() throws SQLException {
        insertRow("test", false);
        dao.setEnabled("test", true);
        Statement statement = dataSource.getConnection().createStatement();
        ResultSet result = statement.executeQuery("select BuildTypeId, IsActive from BuildConfiguration");
        assertThat(result.first(), is(true));
        assertThat(result.getString(1), is(equalTo("test")));
        assertThat(result.getBoolean(2), is(equalTo(true)));
        assertThat(result.next(), is(false));
    }

    @Test
    public void shouldReturnEmptyListWhenNoConfigurationGiven() {
        when(projectManagerMock.getAllBuildTypes()).thenReturn(Collections.EMPTY_LIST);
        List<BuildConfiguration> configurations = dao.getConfigurations();
        assertThat(configurations, is(not(nullValue())));
        assertThat(configurations.size(), is(equalTo(0)));
    }

    @Test
    public void shouldReturnFalseForIsActiveWhenBuildTypeIsNonConfiguredBuildTypeIdGiven() {
        List<BuildConfiguration> configurations = dao.getConfigurations();
        assertThat(configurations, is(not(nullValue())));
        assertThat(configurations.size(), is(equalTo(2)));
        assertThat(configurations.get(0).isEnabled(), is(false));
        assertThat(configurations.get(1).isEnabled(), is(false));
    }

    @Test
    public void shouldReturnEmptyListWhenBuildTypeInConfigurationButNotInTeamCityGiven() {
        insertRow("testBuild", true);
        insertRow("compileBuild", false);
        List<BuildConfiguration> configurations = dao.getConfigurations();
        assertThat(configurations, is(not(nullValue())));
        assertThat(configurations.size(), is(equalTo(2)));
        assertThat(configurations.get(0).isEnabled(), is(true));
        assertThat(configurations.get(0).getBuildId(), is("testBuild"));
        assertThat(configurations.get(0).getBuildName(), is("Test Application"));
        assertThat(configurations.get(1).isEnabled(), is(false));
        assertThat(configurations.get(1).getBuildId(), is("compileBuild"));
        assertThat(configurations.get(1).getBuildName(), is("Compile"));
    }

    @Test
    public void shouldNotAccessDatabaseWhenNullStringGiven() throws SQLException {
        DataSource dataSourceMock = Mockito.mock(DataSource.class);
        dao = new BuildConfigurationDao(projectManagerMock, dataSourceMock);
        dao.updateBuildTypeId(null, "newId");
        dao.updateBuildTypeId("oldId", null);
        verify(dataSourceMock, never()).getConnection();
    }

    @Test
    public void shouldNotAccessDatabaseWhenEmptyStringGiven()  throws SQLException {
        DataSource dataSourceMock = Mockito.mock(DataSource.class);
        dao = new BuildConfigurationDao(projectManagerMock, dataSourceMock);
        dao.updateBuildTypeId("", "newId");
        dao.updateBuildTypeId("oldId", "");
        verify(dataSourceMock, never()).getConnection();
    }

    @Test
    public void shouldUpdateBuildTypeIdWhenNewAndOldBuildTypeIdGiven() throws SQLException {
        insertRow("testId", true);
        insertRow("anotherTestId", false);
        dao.updateBuildTypeId("testId", "newTestId");
        Statement statement = dataSource.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(
                "select count(*) from BuildConfiguration where BuildTypeId = 'newTestId'"
        );

        assertThat(resultSet.first(), is(true));
        assertThat(resultSet.getInt(1), is(equalTo(1)));

        resultSet = statement.executeQuery(
                "select count(*) from BuildConfiguration where BuildTypeId = 'testId'"
        );

        assertThat(resultSet.first(), is(true));
        assertThat(resultSet.getInt(1), is(equalTo(0)));

        resultSet = statement.executeQuery(
                "select count(*) from BuildConfiguration where BuildTypeId = 'anotherTestId'"
        );

        assertThat(resultSet.first(), is(true));
        assertThat(resultSet.getInt(1), is(equalTo(1)));
    }

    private void insertRow(String buildId, boolean isActive) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "insert into BuildConfiguration values(?, ?)"
            );
            statement.setString(1, buildId);
            statement.setBoolean(2, isActive);
            statement.executeUpdate();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }
}
