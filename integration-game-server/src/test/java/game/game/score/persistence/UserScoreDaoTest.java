package game.game.score.persistence;

import game.score.domain.UserScore;
import game.score.persistence.UserScoreDao;
import jetbrains.buildServer.users.User;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Patrick Kranz
 */
@RunWith(MockitoJUnitRunner.class)
public class UserScoreDaoTest {

    @Mock
    private User userMock;

    private UserScoreDao dao;

    private DataSource dataSource;

    @Before
    public void setup() throws SQLException {
        dataSource = createDataSource();
        createTable(dataSource);
        dao = new UserScoreDao(dataSource);
        when(userMock.getId()).thenReturn(5l);
        when(userMock.getUsername()).thenReturn("testUser");
    }

    private static void createTable(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("drop table if exists Score");
        statement.execute(
                "CREATE TABLE Score(UserId BIGINT PRIMARY KEY, Score INTEGER, UserName VARCHAR(255));"
        );
    }

    private static DataSource createDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test");
        dataSource.setUser("sa");
        return dataSource;
    }

    @Test
    public void shouldInsertNewUserWithScoreWhenNewUserGiven() throws SQLException {
        dao.addScore(userMock, 5);
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(
                "select UserId, Score, UserName from Score");

        assertThat(result.first(), is(true));
        assertEquals(5, result.getLong(1));
        assertEquals(5, result.getInt(2));
        assertEquals("testUser", result.getString(3));
    }

    @Test
    public void shouldReturnScoreForUsersWhenUserScoreGiven() throws SQLException {
        insertRow(5l, 4, "testUser");
        List<UserScore> scores = dao.getScores();
        assertEquals(1, scores.size());
        assertEquals("testUser", scores.get(0).getUserName());
        assertEquals(4, scores.get(0).getScore());
    }

    @Test
    public void shouldUpdateExistingUserScoreWhenScoreForExistingUserGiven() {
        dao.addScore(userMock, 5);
        dao.addScore(userMock, 6);
        List<UserScore> scores = dao.getScores();
        assertEquals(1, scores.size());
        assertEquals(11, scores.get(0).getScore());

    }

    @Test
    public void shouldNotAccessDatabaseToUpdateScoreWhenNoUserGiven() throws SQLException {
        DataSource dataSourceMock = Mockito.mock(DataSource.class);
        dao = new UserScoreDao(dataSourceMock);
        dao.addScore(null, 5);
        verify(dataSourceMock, never()).getConnection();
    }

    @Test
    public void shouldNotAccessDatabaseToEnsureUsernameWhenNoUserGiven() throws SQLException {
        DataSource dataSourceMock = Mockito.mock(DataSource.class);
        dao = new UserScoreDao(dataSourceMock);
        dao.ensureUserName(null);
        verify(dataSourceMock, never()).getConnection();
    }

    @Test
    public void shouldUpdateUsernameWhenUserWithNewUsernameGiven() throws SQLException {
        insertRow(5l, 3, "testUser");
        when(userMock.getUsername()).thenReturn("newName");
        dao.ensureUserName(userMock);
        List<UserScore> scores = dao.getScores();
        assertEquals(1, scores.size());
        assertEquals("newName", scores.get(0).getUserName());
    }

    @Test
    public void shouldRemoveUserFromDatabaseWhenUserToRemoveGiven() throws SQLException {
        insertRow(5l, 3, "testUser");
        dao.delete(userMock);
        assertEquals(0, dao.getScores().size());
    }

    @Test
    public void shouldNotAccessDatabaseWhenNoUserToDeleteGiven() throws SQLException {
        DataSource dataSourceMock = Mockito.mock(DataSource.class);
        dao = new UserScoreDao(dataSourceMock);
        dao.delete(null);
        verify(dataSourceMock, never()).getConnection();
    }

    private void insertRow(long userId, int score, String userName) throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.executeUpdate(
                "insert into Score(UserId, UserName, Score) values("+ userId + ",'"
                        + userName + "'," + score + ");");
        connection.close();
    }
}
