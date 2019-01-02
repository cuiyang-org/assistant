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

    public static void open(String url) {
        Desktop d = Desktop.getDesktop();
        try {
            d.browse(new URI(url));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}
