package org.cuiyang.assistant.util;

/**
 * 主题工具类
 *
 * @author cuiyang
 */
public class ThemeUtils {

    /** 暗 */
    public static final String DARK = "dark";
    /** 亮 */
    public static final String LIGHT = "light";

    public static String getTheme() {
        return DARK;
    }

    public static String getThemeResource() {
        return getThemeResource(getTheme());
    }

    public static String getThemeResource(String theme) {
        return ResourceUtils.getResource(String.format("view/css/%s.css", theme)).toExternalForm();
    }
}
