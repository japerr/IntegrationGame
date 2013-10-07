package game.utils;

/**
 * @author Patrick Kranz
 */
public final class UrlUtil {
    private UrlUtil() {}

    public static String getAdminUrlFor(String tabId) {
        return "/admin/admin.html?item=" + tabId;
    }
}
