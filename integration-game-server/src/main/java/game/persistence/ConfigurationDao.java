package game.persistence;

import game.domain.BuildConfiguration;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick Kranz
 */
public class ConfigurationDao {
    private static final String STORAGE_KEY = "ci-game";
    private static final String ENABLED_KEY = "game-enabled";

    private SBuildServer buildServer;

    public ConfigurationDao(SBuildServer buildServer) {
        this.buildServer = buildServer;
    }

    public List<BuildConfiguration> getConfigurations() {
        List<BuildConfiguration> configs = new ArrayList<BuildConfiguration>();
        for (SBuildType build : buildServer.getProjectManager().getAllBuildTypes()) {
            String booleanString = build.getCustomDataStorage(STORAGE_KEY).getValue(ENABLED_KEY);
            configs.add(new BuildConfiguration(build.getFullName(), build.getBuildTypeId(), parseBoolean(booleanString)));
        }
        return configs;
    }

    public void saveConfiguration(BuildConfiguration configuration) {
        SBuildType build = buildServer.getProjectManager().findBuildTypeById(configuration.getBuildId());
        if (build == null) {
            throw new IllegalArgumentException("Build with id " + configuration.getBuildId() + " can not be found");
        }
        build.getCustomDataStorage(STORAGE_KEY).putValue(ENABLED_KEY,
                Boolean.toString(configuration.isEnabled()));
    }

    private boolean parseBoolean(String booleanValue) {
        return booleanValue != null && Boolean.parseBoolean(booleanValue);
    }
}
