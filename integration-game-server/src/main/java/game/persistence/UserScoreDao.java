package game.persistence;

import game.domain.UserScore;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.users.PropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.SimplePropertyKey;
import jetbrains.buildServer.users.UserSet;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * @author Patrick Kranz
 */
public class UserScoreDao {
    public static final String PROPERTY_KEY = "ci-game-score";

    private final PropertyKey propertyKey;
    private final SBuildServer buildServer;

    public UserScoreDao(SBuildServer buildServer) {
        this.propertyKey = new SimplePropertyKey(PROPERTY_KEY);
        this.buildServer = buildServer;
    }

    public void saveScore(SUser user, int score) {
        user.setUserProperty(propertyKey, Integer.toString(score));
    }

    public int getScore(SUser user) {
        String scoreString = user.getPropertyValue(propertyKey);
        if (scoreString == null) {
            return 0;
        } else {
            return parseInt(scoreString);
        }
    }

    public List<UserScore> getScores() {
        List<UserScore> scores = new ArrayList<UserScore>();
        UserSet<SUser> userSet = buildServer.getUserModel().getAllUsers();
        for(SUser user : userSet.getUsers()) {
            scores.add(new UserScore(user.getUsername(), getScore(user)));
        }
        return scores;
    }

    public void addScore(SUser user, int points) {
        int currentScore = getScore(user);
        int newScore = currentScore + points;
        saveScore(user, newScore);
    }
}
