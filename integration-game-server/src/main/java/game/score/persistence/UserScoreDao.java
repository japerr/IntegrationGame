package game.score.persistence;

import game.score.domain.UserScore;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.users.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static game.platform.persistence.util.SqlUtils.closeQuietly;

/**
 * @author Patrick Kranz
 */
public class UserScoreDao {
    private static final String UPDATE_SCORE_SQL =
            "update Score set Score = ? where UserId = ?";
    private static final String GET_SCORE_SQL =
            "select Score From Score where UserId = ?";
    private static final String INSERT_SCORE_SQL =
            "insert into Score(UserId, Score, UserName) values(?, ?, ?)";
    private static final String LOAD_SCORE_SQL =
            "select UserId, UserName, Score from Score";
    private static final String UPDATE_USERNAME_SQL =
            "update Score set UserName = ? where UserId = ?";
    private static final String DELETE_SCORE_SQL =
            "delete from Score where UserId = ?";

    private final DataSource dataSource;

    public UserScoreDao(DataSource dateSource) {
        this.dataSource = dateSource;
    }

    public void addScore(User user, int score) {
        if (user == null) return;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(GET_SCORE_SQL);
            statement.setLong(1, user.getId());
            ResultSet result = statement.executeQuery();
            if (result.first()) {
                int currentScore = result.getInt(1);
                closeQuietly(statement);
                statement = connection.prepareStatement(UPDATE_SCORE_SQL);
                statement.setInt(1, currentScore + score);
                statement.setLong(2, user.getId());
                statement.executeUpdate();
            } else {
                closeQuietly(statement);
                statement = connection.prepareStatement(INSERT_SCORE_SQL);
                statement.setLong(1, user.getId());
                statement.setInt(2, score);
                statement.setString(3, user.getUsername());
                statement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException exception) {
            Loggers.SQL.error("Error while saving score: ", exception);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    public void ensureUserName(User user) {
        if (user == null) return;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(UPDATE_USERNAME_SQL);
            statement.setString(1, user.getUsername());
            statement.setLong(2, user.getId());
            statement.executeUpdate();
        } catch (SQLException exception) {
            Loggers.SQL.error("Error while updating user: ", exception);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

    public List<UserScore> getScores() {
        List<UserScore> scores = new ArrayList<UserScore>();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(LOAD_SCORE_SQL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String userName = resultSet.getString(2);
                int score = resultSet.getInt(3);
                scores.add(new UserScore(userName, score));
            }
        } catch (SQLException exception) {
            Loggers.SQL.error("Error while loading user score: ", exception);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
        }
        return scores;
    }

    public void delete(User user) {
        if (user == null) return;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(DELETE_SCORE_SQL);
            statement.setLong(1, user.getId());
            statement.executeUpdate();
        } catch (SQLException exception) {
            Loggers.SQL.error("Error while loading user score: ", exception);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }
}
