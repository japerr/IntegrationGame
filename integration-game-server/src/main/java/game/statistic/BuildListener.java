package game.statistic;

import game.persistence.ScoreDao;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserSet;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;

/**
 * @author Patrick Kranz
 */
public class BuildListener extends BuildServerAdapter {
    private ScoreDao scoreDao;

    public BuildListener(SBuildServer buildServer, ScoreDao scoreDao) {
        this.scoreDao = scoreDao;
        buildServer.addListener(this);
    }

    @Override
    public void buildFinished(SRunningBuild build) {
        if (build.getBuildStatus() == Status.NORMAL || build.getBuildStatus() == Status.WARNING) {
            addPointsToAllCommitters(build, 1);
        } else if (build.getBuildStatus() == Status.ERROR || build.getBuildStatus() == Status.FAILURE) {
            addPointsToAllCommitters(build, -5);
        }
    }

    private void addPointsToAllCommitters(SRunningBuild build, int points) {
        CustomDataStorage storage = build.getBuildType().getCustomDataStorage("ci-game");
        UserSet<SUser> committers = build.getCommitters(SelectPrevBuildPolicy.SINCE_LAST_BUILD);
        for (SUser committer : committers.getUsers()) {
            this.scoreDao.addScore(committer, points);
        }
    }
}
