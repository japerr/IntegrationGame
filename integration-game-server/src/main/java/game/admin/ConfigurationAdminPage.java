package game.admin;

import game.persistence.BuildConfigurationDao;
import jetbrains.buildServer.web.openapi.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Patrick Kranz
 */
public class ConfigurationAdminPage extends SimpleCustomTab {
    public static final String CONFIGURATION_KEY = "configs";
    private static final String JSP_PAGE = "/admin/configuration.jsp";
    private static final String TAB_ID = "configuration";
    private static final String PAGE_TITLE = "CI-Game";

    private BuildConfigurationDao buildConfigurationDao;

    public ConfigurationAdminPage(PagePlaces pagePlaces, PluginDescriptor pluginDescriptor,
                                  BuildConfigurationDao buildConfigurationDao) {
        super(pagePlaces, PlaceId.ADMIN_SERVER_CONFIGURATION_TAB, TAB_ID,
                pluginDescriptor.getPluginResourcesPath(JSP_PAGE), PAGE_TITLE);
        this.buildConfigurationDao = buildConfigurationDao;
        setPosition(PositionConstraint.last());
        register();
    }

    @Override
    public void fillModel(Map<String, Object> model, HttpServletRequest request) {
        model.put(CONFIGURATION_KEY, buildConfigurationDao.getConfigurations());
    }
}