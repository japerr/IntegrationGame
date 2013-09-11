package game.persistence;

import game.domain.BuildConfiguration;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Patrick Kranz
 */
public class BuildConfigurationDao {
    private static final String STORAGE_KEY = "ci-game";
    private static final String ENABLED_KEY = "game-enabled";

    private SBuildServer buildServer;

    public BuildConfigurationDao(SBuildServer buildServer) {
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

    public Set<String> getBuildIds() {
        Set<String> buildIds = new HashSet<String>();
        for (SBuildType build : buildServer.getProjectManager().getAllBuildTypes()) {
            buildIds.add(build.getBuildTypeId());
        }
        return buildIds;
    }

    public boolean isEnabled(String buildId) {
        SBuildType type = buildServer.getProjectManager().findBuildTypeById(buildId);
        String enabledString = type.getCustomDataStorage(STORAGE_KEY).getValue(ENABLED_KEY);
        return parseBoolean(enabledString);
    }

    public void setEnabled(String buildId, boolean isEnabled) {
        SBuildType build = buildServer.getProjectManager().findBuildTypeById(buildId);
        if (build != null) {
            build.getCustomDataStorage(STORAGE_KEY).putValue(ENABLED_KEY,
                    Boolean.toString(isEnabled));
        }
    }

    private boolean parseBoolean(String booleanValue) {
        return booleanValue != null && Boolean.parseBoolean(booleanValue);
    }
}
