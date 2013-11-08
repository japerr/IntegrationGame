package game.admin.view;

import game.admin.persistence.BuildConfigurationDao;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Set;

import static game.admin.view.ConfigurationAdminPage.CONFIGURATION_KEY;
import static game.platform.util.UrlUtil.getAdminUrlFor;

/**
 * @author Patrick Kranz
 */
public class ConfigurationController extends BaseController {
    public static final String ADMIN_URL = getAdminUrlFor(ConfigurationAdminPage.TAB_ID);
    private static final String CONTROLLER_URL = "/admin/configureCIGame.html";

    private BuildConfigurationDao buildConfigurationDao;

    public ConfigurationController(final SBuildServer buildServer,
                                   final WebControllerManager controllerManager,
                                   final BuildConfigurationDao buildConfigurationDao) {
        super(buildServer);
        this.buildConfigurationDao = buildConfigurationDao;
        controllerManager.registerController(CONTROLLER_URL, this);
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest httpServletRequest,
                                    @NotNull HttpServletResponse httpServletResponse) throws Exception {
        if (isPost(httpServletRequest)) {
            return post(httpServletRequest);
        } else {
            return get();
        }
    }

    private ModelAndView post(HttpServletRequest request) {
        Set<String> allBuildIds = buildConfigurationDao.getBuildIds();
        Enumeration<String> buildIds = request.getParameterNames();
        while (buildIds.hasMoreElements()) {
            String id = buildIds.nextElement();
            if (allBuildIds.contains(id)) {
                buildConfigurationDao.setEnabled(id, true);
                allBuildIds.remove(id);
            }
        }
        for (String remainingId : allBuildIds) {
            buildConfigurationDao.setEnabled(remainingId, false);
        }

        return new ModelAndView(new RedirectView(ADMIN_URL, true));
    }

    private ModelAndView get() {
        return new ModelAndView(ADMIN_URL,
                CONFIGURATION_KEY, buildConfigurationDao.getConfigurations());
    }
}
