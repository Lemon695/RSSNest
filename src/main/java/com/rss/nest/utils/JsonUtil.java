package com.rss.nest.utils;

import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午4:09:16
 * @description: JSON数据处理
 */
public class JsonUtil {

    public static <T> T fromJson(String jsonData, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(jsonData, clazz);
    }

    public static <T> T fromJson(Object object, Class<T> clazz) {
        String jsonData = "";
        if (object != null) {
            jsonData = object.toString();
        }
        Gson gson = new Gson();
        return gson.fromJson(jsonData, clazz);
    }

    public static <T> List<T> fromJsonArray(String jsonData, Class<T> clazz) {
        List list = new ArrayList();
        if (StringUtils.isNotBlank(jsonData)) {
            Gson gson = new Gson();
            JsonElement el = JsonParser.parseString(jsonData).getAsJsonArray();
            JsonArray jsonArray = el.getAsJsonArray();
            for (JsonElement je : jsonArray) {
                list.add(gson.fromJson(je, clazz));
            }
        }

        return list;
    }

    public static String toJson(Object object, boolean pretty) {
        GsonBuilder gb = new GsonBuilder();
        if (pretty) {
            gb.setPrettyPrinting();
        }

        gb.disableHtmlEscaping();
        Gson gson = gb.create();
        return gson.toJson(object);
    }
}
