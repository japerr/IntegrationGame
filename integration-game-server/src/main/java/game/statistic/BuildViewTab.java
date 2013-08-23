package game.statistic;

import game.domain.UserScore;
import game.persistence.ScoreDao;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.BuildPromotion;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserSet;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import jetbrains.buildServer.web.openapi.ViewBuildTab;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Patrick Kranz
 */
public class BuildViewTab extends ViewBuildTab {
    private ScoreDao scoreDao;

    public BuildViewTab(final SBuildServer buildServer, final WebControllerManager manager,
                        final ScoreDao scoreDao) {
        super("CI-Game", "integration-game", manager, "integration-game.jsp", buildServer);
        this.scoreDao = scoreDao;
    }

    @Override
    protected void fillModel(@NotNull Map<String, Object> stringObjectMap,
                             @NotNull HttpServletRequest httpServletRequest,
                             @NotNull BuildPromotion buildPromotion) {
        stringObjectMap.put("scores", scoreDao.getScores());
    }

    @Override
    protected boolean isAvailable(@NotNull HttpServletRequest httpServletRequest, @NotNull BuildPromotion buildPromotion) {
        return true;
    }
}
