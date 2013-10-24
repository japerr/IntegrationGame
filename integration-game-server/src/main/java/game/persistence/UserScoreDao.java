package game.persistence;

import game.domain.UserScore;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserSet;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static game.utils.SqlUtils.closeQuietly;

/**
 * @author Patrick Kranz
 */
public class UserScoreDao {
    private static final String UPDATE_SCORE_SQL =
            "update Score set Score = ? where UserId = ?";
    private static final String GET_SCORE_SQL =
            "select Score From Score where UserId = ?";
    private static final String INSERT_SCORE_SQL =
            "insert into Score(UserId, Score) values(?, ?)";
    private static final String LOAD_SCORE_SQL =
            "select UserId, Score from Score";


    private final SBuildServer buildServer;
    private final DataSource dataSource;

    public UserScoreDao(SBuildServer buildServer, DataSource dateSource) {
        this.buildServer = buildServer;
        this.dataSource = dateSource;
    }

    public void addScore(SUser user, int score) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
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
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            Loggers.SQL.error("Error while saving score: ", exception);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }

/*
    public int getScore(SUser user) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(GET_SCORE_SQL);
            statement.setLong(1, user.getId());
            ResultSet result = statement.executeQuery();
            if (result.first()) {
                return result.getInt(1);
            }
        } catch (SQLException exception) {
            Loggers.SQL.error("Error while executing sql query: ", exception);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
        }
        return 0;
    }
*/

    public List<UserScore> getScores() {
        List<UserScore> scores = new ArrayList<UserScore>();
        Map<Long, String> userMap = getUserIdUserNameMap();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(LOAD_SCORE_SQL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long userId = resultSet.getLong(1);
                int score = resultSet.getInt(2);
                if (userMap.containsKey(userId)) {
                    scores.add(new UserScore(userMap.get(userId), score));
                }
            }
        } catch (SQLException exception) {
            Loggers.SQL.error("Error while loading user score: ", exception);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
        }
        return scores;
    }

    private Map<Long, String> getUserIdUserNameMap() {
        Map<Long, String> userMap = new HashMap<Long, String>();
        UserSet<SUser> userSet = buildServer.getUserModel().getAllUsers();
        for(SUser user : userSet.getUsers()) {
            userMap.put(user.getId(), user.getUsername());
        }
        return userMap;
    }
}
