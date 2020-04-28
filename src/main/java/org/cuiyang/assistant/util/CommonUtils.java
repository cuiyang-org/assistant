package org.cuiyang.assistant.util;

import javafx.scene.Node;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 通用工具类
 *
 * @author cuiyang
 */
public class CommonUtils {

    /**
     * 控制显示或隐藏
     * @param node 节点
     * @param value true/false
     */
    public static void visible(Node node, boolean value) {
        node.setVisible(value);
        node.setManaged(value);
    }

    /**
     * 控制父节点显示或隐藏
     * @param node 节点
     * @param value true/false
     */
    public static void parentVisible(Node node, boolean value) {
        visible(node.getParent(), value);
    }

    public static List<Integer> indexOf(String str, String key) {
        if (StringUtils.isEmpty(key)) {
            return Collections.emptyList();
        }
        List<Integer> list = new ArrayList<>();
        int index = -1;
        while ((index = str.indexOf(key, index + 1)) > -1) {
            list.add(index);
        }
        return list;
    }
}
