package game.score.persistence;

import game.platform.persistence.SqlBuilder;
import game.score.domain.UserScore;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.users.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        SqlBuilder sqlBuilder = null;
        try {
            sqlBuilder = new SqlBuilder(dataSource);
            ResultSet result = sqlBuilder.withTransaction().prepareStatement(GET_SCORE_SQL)
                    .withParameter(user.getId()).resultSet();
            if (result.first()) {
                int currentScore = result.getInt(1);
                sqlBuilder.continueWith().prepareStatement(UPDATE_SCORE_SQL)
                        .withParameter(currentScore + score)
                        .withParameter(user.getId()).update();
            } else {
                sqlBuilder.continueWith().prepareStatement(INSERT_SCORE_SQL)
                        .withParameter(user.getId())
                        .withParameter(score).withParameter(user.getUsername())
                        .update();
            }
            sqlBuilder.commit();
        } catch (SQLException exception) {
            Loggers.SQL.error("Error while saving score: ", exception);
        } finally {
            closeBuilder(sqlBuilder);
        }
    }

    public void ensureUserName(User user) {
        if (user == null) return;
        SqlBuilder sqlBuilder = null;
        try {
            sqlBuilder = new SqlBuilder(dataSource);
            sqlBuilder.prepareStatement(UPDATE_USERNAME_SQL)
                    .withParameter(user.getUsername()).withParameter(user.getId()).update();
        } catch (SQLException exception) {
            Loggers.SQL.error("Error while updating user: ", exception);
        } finally {
            closeBuilder(sqlBuilder);
        }
    }

    public List<UserScore> getScores() {
        List<UserScore> scores = new ArrayList<UserScore>();
        SqlBuilder sqlBuilder = null;
        try {
            sqlBuilder = new SqlBuilder(dataSource);
            ResultSet resultSet = sqlBuilder
                    .prepareStatement(LOAD_SCORE_SQL).resultSet();
            while (resultSet.next()) {
                String userName = resultSet.getString(2);
                int score = resultSet.getInt(3);
                scores.add(new UserScore(userName, score));
            }
        } catch (SQLException exception) {
            Loggers.SQL.error("Error while loading user score: ", exception);
        } finally {
            closeBuilder(sqlBuilder);
        }
        return scores;
    }

    public void delete(User user) {
        if (user == null) return;
        SqlBuilder sqlBuilder = null;
        try {
            sqlBuilder = new SqlBuilder(dataSource);
            sqlBuilder.prepareStatement(DELETE_SCORE_SQL)
                    .withParameter(user.getId()).update();
        } catch (SQLException exception) {
            Loggers.SQL.error("Error while loading user score: ", exception);
        } finally {
            closeBuilder(sqlBuilder);
        }
    }

    private void closeBuilder(SqlBuilder builder) {
        if (builder != null) {
            builder.close();
        }
    }
}
