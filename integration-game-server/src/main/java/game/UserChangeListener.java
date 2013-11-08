package game;

import game.persistence.UserScoreDao;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.users.User;
import jetbrains.buildServer.users.UserModelListener;
import org.jetbrains.annotations.NotNull;

/**
 * This listener makes sure that changes to the users username
 * are propagated to the H2 database. This way we can store all
 * relevant information to display in the same database and we
 * do not need to query for all user scores first and then
 * iterate over all users from TeamCitys database to find out
 * which user really exists.
 *
 * Also the user removal will cleanup unused data in the database.
 *
 * @author Patrick Kranz
 */
public class UserChangeListener implements UserModelListener {

    private final UserScoreDao userScoreDao;
    private SBuildServer buildServer;

    public UserChangeListener(UserScoreDao userScoreDao, SBuildServer buildServer) {
        this.userScoreDao = userScoreDao;
        this.buildServer = buildServer;
    }

    public void registerListener() {
        buildServer.getUserModel().addListener(this);
    }

    public void userAccountCreated(@NotNull User user) {
    }

    public void userAccountChanged(User user) {
        userScoreDao.ensureUserName(user);
    }

    public void userAccountRemoved(User user) {
        userScoreDao.delete(user);
    }
}
