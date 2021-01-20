package com.example.forum.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.util.Map;
import java.util.Set;


public class SharedPreferenceUtil {
    private static String sharedPreferencesName = "Post_SharedPreference";
    public static final String IMEI = "imei";
    public static final String POSTS = "posts";
    public static final String USERINFO = "userinfo";



    /**
     * 设置编辑器Editor
     * @param context
     * @return
     */
    private static Editor getEditor(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        return editor;
    }

    /**
     * 设置SharedPreferences
     * @param context
     * @return
     */
    private static SharedPreferences getSharedPreferences(Context context){
        SharedPreferences sharedPreferences;
        if(sharedPreferencesName.isEmpty()){
            //获取默认的sharedPreferences
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }else{
            sharedPreferences = context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        }

        return sharedPreferences;

    }

    /**
     * 设置键值对
     * @param context
     * @param key
     * @param value 默认值
     */
    public static void putInt(Context context, String key, Integer value){
        Editor editor = getEditor(context);
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 设置键值对
     * @param context
     * @param key
     * @param value 默认值
     */
    public static void putFloat(Context context, String key, Float value){
        Editor editor = getEditor(context);
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * 设置键值对
     * @param context
     * @param key
     * @param value 默认值
     */
    public static void putLong(Context context, String key, Long value){
        Editor editor = getEditor(context);
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 设置键值对
     * @param context
     * @param key
     * @param value 默认值
     */
    public static void putBoolean(Context context, String key, Boolean value){
        Editor editor = getEditor(context);
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 设置键值对
     * @param context
     * @param key
     * @param value 默认值
     */
    public static void putString(Context context, String key, String value){
        Editor editor = getEditor(context);
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 设置键值对集合
     * @param context
     * @param key
     * @param values
     */
    public static void putStringSet(Context context, String key, Set<String> values){
        Editor editor = getEditor(context);
        editor.putStringSet(key, values);
        editor.commit();
    }




    /**
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static Integer getInt(Context context, String key, Integer defaultValue){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(key, defaultValue);
    }

    /**
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static Float getFloat(Context context, String key, Float defaultValue){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getFloat(key, defaultValue);
    }

    /**
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static Long getLong(Context context, String key, Long defaultValue){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getLong(key, defaultValue);
    }

    /**
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static Boolean getBoolean(Context context, String key, Boolean defaultValue){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(Context context, String key, String defaultValue){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(key, defaultValue);
    }

    /**
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static Set<String> getStringSet(Context context, String key, Set<String> defaultValue){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getStringSet(key, defaultValue);
    }

    /**
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        Map<String, ?> all =  sharedPreferences.getAll();
        return all;
    }

    /**
     * @param context
     * @param key
     */
    public static void remove(Context context, String key) {
        Editor editor = getEditor(context);
        editor.remove(key);
        editor.commit();
    }

    /**
     * @param context
     */
    public static void clear(Context context){
        Editor editor = getEditor(context);
        editor.clear();
        editor.commit();
    }

    /**
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.contains(key);
    }
}
