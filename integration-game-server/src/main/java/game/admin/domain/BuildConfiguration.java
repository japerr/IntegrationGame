package game.admin.domain;

/**
 * @author Patrick Kranz
 */
public class BuildConfiguration {
    private String buildName;
    private String buildId;
    private boolean enabled;

    public BuildConfiguration(String buildName, String buildId, boolean isEnabled) {
        this.buildName = buildName;
        this.enabled = isEnabled;
        this.buildId = buildId;
    }

    public String getBuildName() {
        return this.buildName;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getBuildId() {
        return buildId;
    }
}
