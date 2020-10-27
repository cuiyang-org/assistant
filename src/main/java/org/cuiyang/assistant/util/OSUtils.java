package org.cuiyang.assistant.util;

/**
 * 操作系统工具类
 *
 * @author cuiyang
 */
public class OSUtils {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isMac(){
        return OS.contains("mac");
    }

    public static boolean isWindows(){
        return OS.contains("windows");
    }

}
