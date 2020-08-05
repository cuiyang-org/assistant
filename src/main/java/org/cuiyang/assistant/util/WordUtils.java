package org.cuiyang.assistant.util;

/**
 * 单词工具类
 *
 * @author cuiyang
 */
public class WordUtils {
    /**
     * 小驼峰
     */
    public static String lowerCamelCase(String name) {
        String[] split = name.split("_");
        StringBuilder className = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            if (i == 0) {
                className.append(firstLowerCase(split[i]));
            } else {
                className.append(firstUpperCase(split[i]));
            }
        }
        return className.toString();
    }

    /**
     * 大驼峰
     */
    public static String upperCamelCase(String name) {
        String[] split = name.split("_");
        StringBuilder className = new StringBuilder();
        for (String str : split) {
            className.append(firstUpperCase(str));
        }
        return className.toString();
    }

    /**
     * 首字母大写
     */
    public static String firstUpperCase(String name) {
        char[] cs = name.toCharArray();
        if (cs[0] >= 'a' && cs[0] <= 'z') {
            cs[0] -= 32;
            return String.valueOf(cs);
        }
        return name;
    }

    /**
     * 首字母小写
     */
    public static String firstLowerCase(String name) {
        char[] cs = name.toCharArray();
        if (cs[0] >= 'A' && cs[0] <= 'Z') {
            cs[0] += 32;
            return String.valueOf(cs);
        }
        return name;
    }
}
