package game.statistic;

import game.persistence.ConfigurationDao;
import game.persistence.ScoreDao;
import jetbrains.buildServer.serverSide.BuildPromotion;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.ViewBuildTab;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Patrick Kranz
 */
public class BuildViewTab extends ViewBuildTab {
    private ScoreDao scoreDao;
    private ConfigurationDao configuration;

    public BuildViewTab(final SBuildServer buildServer, final WebControllerManager manager,
                        final ScoreDao scoreDao, ConfigurationDao config) {
        super("CI-Game", "integration-game", manager, "integration-game.jsp", buildServer);
        this.scoreDao = scoreDao;
        this.configuration = config;
    }

    @Override
    protected void fillModel(@NotNull Map<String, Object> stringObjectMap,
                             @NotNull HttpServletRequest httpServletRequest,
                             @NotNull BuildPromotion buildPromotion) {
        stringObjectMap.put("scores", scoreDao.getScores());
        stringObjectMap.put("configuration", configuration.getConfigurations());
    }

    @Override
    protected boolean isAvailable(@NotNull HttpServletRequest httpServletRequest, @NotNull BuildPromotion buildPromotion) {
        String buildId = buildPromotion.getBuildTypeId();
        return configuration.isEnabled(buildId);
    }
}
