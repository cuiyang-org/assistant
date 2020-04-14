package org.cuiyang.assistant.util;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 浏览器工具类
 *
 * @author cy48576
 */
public class BrowseUtils {

    public static void open(String uri) {
        try {
            open(new URI(uri));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void open(URI uri) {
        Desktop d = Desktop.getDesktop();
        try {
            d.browse(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
