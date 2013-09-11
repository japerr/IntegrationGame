package game.statistic;

import game.persistence.BuildConfigurationDao;
import game.persistence.UserScoreDao;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserSet;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;

/**
 * @author Patrick Kranz
 */
public class BuildListener extends BuildServerAdapter {
    private UserScoreDao userScoreDao;
    private BuildConfigurationDao buildConfigurationDao;

    public BuildListener(SBuildServer buildServer, UserScoreDao userScoreDao, BuildConfigurationDao buildConfigurationDao) {
        this.userScoreDao = userScoreDao;
        this.buildConfigurationDao = buildConfigurationDao;
        buildServer.addListener(this);
    }

    @Override
    public void buildFinished(SRunningBuild build) {
        if (build.isPersonal() || !buildConfigurationDao.isEnabled(build.getBuildTypeId())) {
            return;
        }
        if (build.getBuildStatus() == Status.NORMAL || build.getBuildStatus() == Status.WARNING) {
            addPointsToAllCommitters(build, 1);
        } else if (build.getBuildStatus() == Status.ERROR || build.getBuildStatus() == Status.FAILURE) {
            addPointsToAllCommitters(build, -5);
        }
    }

    private void addPointsToAllCommitters(SRunningBuild build, int points) {
        UserSet<SUser> committers = build.getCommitters(SelectPrevBuildPolicy.SINCE_LAST_BUILD);
        for (SUser committer : committers.getUsers()) {
            this.userScoreDao.addScore(committer, points);
        }
    }
}
