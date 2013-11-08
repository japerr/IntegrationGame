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
    /**
     * We want the users to commit often in small deltas, so success score is low.
     */
    private static final int SUCCESS_SCORE = 1;
    /**
     * Build failure is considered a bad thing, therefore a rather high negative score.
     */
    private static final int FAILURE_SCORE = -5;

    private UserScoreDao userScoreDao;
    private BuildConfigurationDao buildConfigurationDao;

    public BuildListener(SBuildServer buildServer, UserScoreDao userScoreDao, BuildConfigurationDao buildConfigurationDao) {
        this.userScoreDao = userScoreDao;
        this.buildConfigurationDao = buildConfigurationDao;
        buildServer.addListener(this);
    }

    @Override
    public void buildFinished(SRunningBuild build) {
        if (buildShouldBeIgnored(build)) {
            return;
        }
        if (buildWasSuccessful(build)) {
            addPointsToAllCommitters(build, SUCCESS_SCORE);
        } else if (buildHasFailed(build)) {
            addPointsToAllCommitters(build, FAILURE_SCORE);
        }
    }

    private boolean buildHasFailed(SRunningBuild build) {
        return build.getBuildStatus() == Status.ERROR || build.getBuildStatus() == Status.FAILURE;
    }

    private boolean buildWasSuccessful(SRunningBuild build) {
        return build.getBuildStatus() == Status.NORMAL || build.getBuildStatus() == Status.WARNING;
    }

    private boolean buildShouldBeIgnored(SRunningBuild build) {
        return build.isPersonal() || !buildConfigurationDao.isEnabled(build.getBuildTypeExternalId());
    }

    private void addPointsToAllCommitters(SRunningBuild build, int points) {
        UserSet<SUser> committers = build.getCommitters(SelectPrevBuildPolicy.SINCE_LAST_BUILD);
        for (SUser committer : committers.getUsers()) {
            this.userScoreDao.addScore(committer, points);
        }
    }
}
