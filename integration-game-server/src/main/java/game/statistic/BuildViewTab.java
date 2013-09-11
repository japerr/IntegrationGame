package game.statistic;

import game.persistence.BuildConfigurationDao;
import game.persistence.UserScoreDao;
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
    private static final String TAB_NAME = "CI-Game";
    private static final String TAB_CODE = "integration-game";
    private static final String JSP_PAGE = "integration-game.jsp";

    private UserScoreDao userScoreDao;
    private BuildConfigurationDao configuration;

    public BuildViewTab(final SBuildServer buildServer, final WebControllerManager manager,
                        final UserScoreDao userScoreDao, BuildConfigurationDao config) {
        super(TAB_NAME, TAB_CODE, manager, JSP_PAGE, buildServer);
        this.userScoreDao = userScoreDao;
        this.configuration = config;
    }

    @Override
    protected void fillModel(@NotNull Map<String, Object> stringObjectMap,
                             @NotNull HttpServletRequest httpServletRequest,
                             @NotNull BuildPromotion buildPromotion) {
        stringObjectMap.put("scores", userScoreDao.getScores());
        stringObjectMap.put("configuration", configuration.getConfigurations());
    }

    @Override
    protected boolean isAvailable(@NotNull HttpServletRequest httpServletRequest,
                                  @NotNull BuildPromotion buildPromotion) {
        String buildId = buildPromotion.getBuildTypeId();
        return configuration.isEnabled(buildId);
    }
}
