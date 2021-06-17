package org.cuiyang.assistant.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson.serializer.SerializerFeature.*;

public class JSONUtils {

    public static LinkedHashMap<String, Object> sort(JSONObject o) {
        ArrayList<Map.Entry<String, Object>> entries = new ArrayList<>(o.entrySet());
        entries.sort((o1, o2) -> {
            int result = o1.getKey().length() - o2.getKey().length();
            if (result == 0) {
                return  o1.getKey().compareTo(o2.getKey());
            } else {
                return result;
            }
        });

        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        entries.forEach(item -> {
            if (item.getValue() instanceof JSONObject) {
                result.put(item.getKey(), sort((JSONObject) item.getValue()));
            } else if (item.getValue() instanceof JSONArray) {
                result.put(item.getKey(), sort((JSONArray) item.getValue()));
            } else {
                result.put(item.getKey(), item.getValue());
            }
        });
        return result;
    }

    public static List<Object> sort(JSONArray arr) {
        List<Object> result = new ArrayList<>();
        for (Object o : arr) {
            if (o instanceof JSONObject) {
                result.add(sort((JSONObject) o));
            } else if (o instanceof JSONArray) {
                result.add(sort((JSONArray) o));
            } else {
                result.add(o);
            }
        }
        return result;
    }

    public static Object sort(Object o) {
        if (o instanceof JSONObject) {
            return sort((JSONObject) o);
        } else if (o instanceof JSONArray) {
            return sort((JSONArray) o);
        } else {
            return o;
        }
    }

    public static String format(Object o, boolean sort) {
        Object obj = o;
        if (sort) {
            obj = sort(o);
        }
        String jsonStr = JSON.toJSONString(obj, WriteMapNullValue, PrettyFormat);
        return jsonStr.replaceAll("\t", "  ");
    }
}
