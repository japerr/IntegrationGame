package game.admin;

import game.admin.persistence.BuildConfigurationDao;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.SBuildType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Patrick Kranz
 */
public class BuildConfigurationServerListener extends BuildServerAdapter {
    private final BuildConfigurationDao dao;

    public BuildConfigurationServerListener(final BuildConfigurationDao dao) {
        this.dao = dao;
    }

    @Override
    public void buildTypeExternalIdChanged(@NotNull SBuildType buildType,
                                           @NotNull String oldTypeId,
                                           @NotNull String newTypeId) {
        this.dao.updateBuildTypeId(oldTypeId, newTypeId);
    }
}
