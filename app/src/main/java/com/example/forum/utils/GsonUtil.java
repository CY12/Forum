package com.example.forum.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class GsonUtil {




    /**
     * obj -> json
     *
     * @param object 序列化对象
     * @return json
     */
    public static String toJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    /**
     * json -> object
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toObject(String json,Class<T> clazz){
        Gson gson = new Gson();
        return gson.fromJson(json,clazz);
    }

    /**
     * @param json list的序列化字符串
     * @param <T>  T类型
     * @return List<T>
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(json, TypeToken.getParameterized(List.class, clazz).getType());
    }

// 或者

    /**
     * @param json list的序列化字符串
     * @param <T>  T类型
     * @return List<T>
     */
    public static <T> List<T> parseList(String json, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        try {
            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            for (final JsonElement elem : array) {
                list.add(new Gson().fromJson(elem, clazz));
            }
        } catch (Exception e) {
            return null;
        }
        return list;
    }

    /**
     * @param json map的序列化结果
     * @param <K>  k类型
     * @param <V>  v类型
     * @return Map<K                                                                                                                               ,                                                                                                                               V>
     */
    public static  <K, V> Map<K, V> toMap(String json, Class<K> kClazz, Class<V> vClazz) {
        Gson gson = new Gson();
        return gson.fromJson(json, TypeToken.getParameterized(Map.class, kClazz, vClazz).getType());
    }

// 或者

    /**
     * @param json map的序列化字符串
     * @param <V>  value类型
     * @return Map<String,V>
     */
    public static <V> Map<String, V> toStringKeyMap(String json, Class<V> vClazz) {
        Gson gson = new Gson();
        Map<String, V> map = new HashMap<>();
        try {
            JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = obj.entrySet();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                String entryKey = entry.getKey();
                JsonObject object = (JsonObject) entry.getValue();
                V value = gson.fromJson(object, vClazz);
                map.put(entryKey, value);
            }
        } catch (Exception e) {
            return null;
        }
        return map;
    }



}
