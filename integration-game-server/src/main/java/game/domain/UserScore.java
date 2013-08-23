package game.domain;

/**
 * @author Patrick Kranz
 */
public class UserScore {
    private String userName;
    private int score;

    public UserScore(String userName, int score) {
        this.userName = userName;
        this.score = score;
    }

    public String getUserName() {
        return this.userName;
    }

    public int getScore() {
        return this.score;
    }
}
