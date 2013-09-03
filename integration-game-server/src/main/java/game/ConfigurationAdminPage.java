package game;

import game.persistence.ConfigurationDao;
import jetbrains.buildServer.web.openapi.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

/**
 * @author Patrick Kranz
 */
public class ConfigurationAdminPage extends SimpleCustomTab {
    private ConfigurationDao configurationDao;

    public ConfigurationAdminPage(PagePlaces pagePlaces, PluginDescriptor pluginDescriptor,
                                  ConfigurationDao configurationDao) {
        super(pagePlaces, PlaceId.ADMIN_SERVER_CONFIGURATION_TAB, "configuration",
                pluginDescriptor.getPluginResourcesPath("/admin/configuration.jsp"), "CI-Game");
        this.configurationDao = configurationDao;
        setPosition(PositionConstraint.last());
        register();
    }

    @Override
    public void fillModel(Map<String, Object> model, HttpServletRequest request) {
        model.put("configs", configurationDao.getConfigurations());
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