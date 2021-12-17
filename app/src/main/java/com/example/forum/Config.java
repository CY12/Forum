package com.example.forum;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.forum.bean.User;
import com.example.forum.utils.GsonUtil;
import com.example.forum.utils.SharedPreferenceUtil;

public class Config {

    public static int statusBarHeight = -1;

    public static int uid = 0;

    public static String uName = "";

    public static String avatar = "";

    public static User user;



    public static String IP = "152.136.137.189:8082";

    public static User getUser(Context context){
        if (user == null){
            String uJson = SharedPreferenceUtil.getString(context,SharedPreferenceUtil.USERINFO,"");
            if (TextUtils.isEmpty(uJson)){
                Toast.makeText(context,"用户信心获取失败",Toast.LENGTH_SHORT).show();
                user = new User();
            }else {
                user = GsonUtil.toObject(uJson,User.class);
            }
        }
        return user;
    }
}
