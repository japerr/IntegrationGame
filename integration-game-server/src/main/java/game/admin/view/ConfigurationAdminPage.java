package game.admin.view;

import game.admin.persistence.BuildConfigurationDao;
import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.web.openapi.Groupable;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static jetbrains.buildServer.web.openapi.PositionConstraint.last;

/**
 * @author Patrick Kranz
 */
public class ConfigurationAdminPage extends AdminPage {
    public static final String CONFIGURATION_KEY = "configs";
    public static final String TAB_ID = "CI-Game-Configuration";

    private static final String JSP_PAGE = "/admin/configuration.jsp";
    private static final String PAGE_TITLE = "CI-Game";

    private BuildConfigurationDao buildConfigurationDao;

    public ConfigurationAdminPage(PagePlaces pagePlaces, PluginDescriptor pluginDescriptor,
                                  BuildConfigurationDao buildConfigurationDao) {
        super(pagePlaces, TAB_ID, pluginDescriptor.getPluginResourcesPath(JSP_PAGE), PAGE_TITLE);
        this.buildConfigurationDao = buildConfigurationDao;
        setPosition(last());
        register();
    }

    @Override
    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
        model.put(CONFIGURATION_KEY, buildConfigurationDao.getConfigurations());
    }

    @NotNull
    public String getGroup() {
        return Groupable.PROJECT_RELATED_GROUP;
    }
}