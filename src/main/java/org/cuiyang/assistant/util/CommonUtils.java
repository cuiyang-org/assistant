package org.cuiyang.assistant.util;

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
