package org.cuiyang.assistant.util;

import java.io.InputStream;
import java.net.URL;

/**
 * ResourceUtils
 *
 * @author cy48576
 */
public class ResourceUtils {
    public ResourceUtils() {
    }

    public static InputStream getResourceAsStream(String name) {
        return ResourceUtils.class.getClassLoader().getResourceAsStream(name);
    }

    public static URL getResource(String name) {
        return ResourceUtils.class.getClassLoader().getResource(name);
    }
}
