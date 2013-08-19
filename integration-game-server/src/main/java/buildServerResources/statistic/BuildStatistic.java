package buildServerResources.statistic;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserSet;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import jetbrains.buildServer.web.openapi.SimpleCustomTab;
import jetbrains.buildServer.web.openapi.ViewBuildTab;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Patrick Kranz
 */
public class BuildStatistic extends BuildServerAdapter {
    private WebControllerManager webControllerManager;
    private ProjectManager projectManager;
    private SBuildServer buildServer;

    public BuildStatistic(final SBuildServer buildServer, WebControllerManager manager, ProjectManager projectManager) {
        buildServer.addListener(this);
        webControllerManager = manager;
        this.projectManager = projectManager;
        this.buildServer = buildServer;
    }

    public void register() {
        SimpleCustomTab extension = new ViewBuildTab("CI-game", "integration-game", webControllerManager,
                "integration-game.jsp", buildServer) {

            @Override
            protected void fillModel(@NotNull Map<String, Object> stringObjectMap, @NotNull HttpServletRequest httpServletRequest, @NotNull BuildPromotion buildPromotion) {
            }

            @Override
            protected boolean isAvailable(@NotNull HttpServletRequest httpServletRequest, @NotNull BuildPromotion buildPromotion) {
             return true;
            }
        };
        extension.register();
    }

    @Override
    public void buildFinished(SRunningBuild build) {
        if (build.getBuildStatus() == Status.NORMAL || build.getBuildStatus() == Status.WARNING) {
            addPointsToAllCommitters(build, 1);
        } else if (build.getBuildStatus() == Status.ERROR || build.getBuildStatus() == Status.FAILURE) {
            addPointsToAllCommitters(build, -5);
        }
    }

    private static void addPointsToAllCommitters(SRunningBuild build, int points) {
        UserSet<SUser> committers = build.getCommitters(SelectPrevBuildPolicy.SINCE_LAST_BUILD);
        for (SUser committer : committers.getUsers()) {
            System.err.println("User " + committer.getDescriptiveName() + " got " + points + " points.");
        }
    }
}
