package game.controller;

import game.persistence.BuildConfigurationDao;
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

/**
 * @author Patrick Kranz
 */
public class ConfigurationController extends BaseController {
    private static final String CONFIGURATION_URL = "/admin/configureCIGame.html";

    private BuildConfigurationDao buildConfigurationDao;

    public ConfigurationController(final SBuildServer buildServer,
                                   final WebControllerManager controllerManager,
                                   final BuildConfigurationDao buildConfigurationDao) {
        super(buildServer);
        this.buildConfigurationDao = buildConfigurationDao;
        controllerManager.registerController(CONFIGURATION_URL, this);
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse) throws Exception {
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

        return new ModelAndView(new RedirectView("/admin/admin.html?item=configuration", true));
    }

    private ModelAndView get() {
        return new ModelAndView("/admin/admin.html?item=configuration",
                "configs", buildConfigurationDao.getConfigurations());
    }
}
