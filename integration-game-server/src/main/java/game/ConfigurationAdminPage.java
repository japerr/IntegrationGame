package game;

import game.persistence.BuildConfigurationDao;
import jetbrains.buildServer.web.openapi.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Patrick Kranz
 */
public class ConfigurationAdminPage extends SimpleCustomTab {
    private BuildConfigurationDao buildConfigurationDao;

    public ConfigurationAdminPage(PagePlaces pagePlaces, PluginDescriptor pluginDescriptor,
                                  BuildConfigurationDao buildConfigurationDao) {
        super(pagePlaces, PlaceId.ADMIN_SERVER_CONFIGURATION_TAB, "configuration",
                pluginDescriptor.getPluginResourcesPath("/admin/configuration.jsp"), "CI-Game");
        this.buildConfigurationDao = buildConfigurationDao;
        setPosition(PositionConstraint.last());
        register();
    }

    @Override
    public void fillModel(Map<String, Object> model, HttpServletRequest request) {
        model.put("configs", buildConfigurationDao.getConfigurations());
    }

    @Override
    public boolean isAvailable(HttpServletRequest request) {
        return true;
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}