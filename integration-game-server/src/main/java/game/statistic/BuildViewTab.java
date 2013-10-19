package game.statistic;

import game.persistence.BuildConfigurationDao;
import game.persistence.UserScoreDao;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.openapi.buildType.BuildTypeTab;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Patrick Kranz
 */
public class BuildViewTab extends BuildTypeTab {
    private static final String TAB_NAME = "CI-Game";
    private static final String TAB_CODE = "integration-game";
    private static final String JSP_PAGE = "integration-game.jsp";

    private UserScoreDao userScoreDao;
    private BuildConfigurationDao configuration;

    public BuildViewTab(final ProjectManager projectManager, final WebControllerManager manager,
                        final UserScoreDao userScoreDao, BuildConfigurationDao config) {
        super(TAB_CODE, TAB_NAME, manager, projectManager);
        setIncludeUrl(JSP_PAGE);
        this.userScoreDao = userScoreDao;
        this.configuration = config;
    }

    @Override
    public boolean isAvailable(@NotNull HttpServletRequest httpServletRequest) {
        SBuildType buildType = getBuildType(httpServletRequest);
        return buildType != null && configuration.isEnabled(buildType.getExternalId());
    }

    @Override
    protected void fillModel(@NotNull Map<String, Object> stringObjectMap, @NotNull HttpServletRequest httpServletRequest, @NotNull SBuildType sBuildType, @Nullable SUser sUser) {
        stringObjectMap.put("scores", userScoreDao.getScores());
        stringObjectMap.put("configuration", configuration.getConfigurations());
    }
}
