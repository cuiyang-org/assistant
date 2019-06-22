package org.cuiyang.assistant.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;

import static org.cuiyang.assistant.constant.SystemConstant.CONFIG_FILE;

/**
 * 配置文件工具类
 *
 * @author cuiyang
 */
@Slf4j
public class ConfigUtils {

    private static Properties properties = new Properties();

    static {
        load();
    }

    /**
     * 读取配置
     */
    public synchronized static String get(String key) {
        return properties.getProperty(key);
    }

    /**
     * 读取配置
     */
    public synchronized static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * 设置配置
     */
    public synchronized static void set(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     *  加载配置文件
     */
    public synchronized static void load() {
        properties.clear();
        File file = new File(CONFIG_FILE);
        try (FileReader reader = new FileReader(file)) {
            properties.load(reader);
        } catch (Exception ignore) {
        }
    }

    /**
     * 保存配置文件
     */
    public synchronized static boolean save() {
        File file = new File(CONFIG_FILE);
        try {
            FileUtils.forceMkdirParent(file);
        } catch (IOException e) {
            log.error("创建目录({})失败", CONFIG_FILE, e);
            return false;
        }
        try (FileWriter writer = new FileWriter(file)) {
            properties.store(writer, null);
            return true;
        } catch (Exception e) {
            log.error("保存配置文件失败", e);
            return false;
        }
    }
}
