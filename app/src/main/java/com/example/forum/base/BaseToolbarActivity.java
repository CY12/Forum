package com.example.forum.base;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.flexibleview.FlexibleViewTools;
import com.example.forum.Config;
import com.example.forum.R;

public abstract class BaseToolbarActivity extends AppCompatActivity {
    private String TAG = "LifeCycleActivity";

    private int param = 1;
    private FrameLayout frameLayout;
    public TextView title;
    public TextView tvGo;
    public ImageView ivBack;
    public ImageView ivGo;
    public ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("BaseToolbarActivity", "onCreate called.");
        Log.i(getTag(),"onCreate called.");
        setContentView(R.layout.activity_base_toolbar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        frameLayout = findViewById(R.id.content);
        title = findViewById(R.id.tv_title);
        tvGo = findViewById(R.id.tv_go);
        ivBack = findViewById(R.id.iv_back);
        ivGo = findViewById(R.id.iv_go);
        progressBar = findViewById(R.id.progress);



        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = title.getLayoutParams();
            int topMargin = getHeight();
            params.height = params.height + topMargin;
            title.setPadding(title.getPaddingLeft(), title.getPaddingTop() + topMargin, title.getPaddingRight(), title.getPaddingBottom());
            title.setLayoutParams(params);
            FlexibleViewTools.withRelative(ivBack).setTop(getHeight());
            FlexibleViewTools.withRelative(ivGo).setTop(getHeight());
            FlexibleViewTools.withRelative(tvGo).setTop(getHeight());

        }
//        if (ColorUtils.calculateLuminance(color) >= 0.5) {
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        } else {
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        }



        if (frameLayout != null) {
            frameLayout.removeAllViews();
            LayoutInflater.from(this).inflate(getLayoutId(), frameLayout, true);
        }
        initView();
        initData();
        initListener();
        if (!TextUtils.isEmpty(setTitle())){
            title.setText(setTitle());
        }

    }

    public void startActivity(Context context,Class<?> cls ){
        Intent intent = new Intent(context,cls);
        startActivity(intent);
    }

    protected abstract int getLayoutId();

    public abstract String setTitle();

    public abstract void initView();

    public abstract void initData();

    public abstract void initListener();

    public abstract String getTag();

    private int getHeight() {
        int statusBarHeight1 = Config.statusBarHeight;
        if (statusBarHeight1 > 0) return statusBarHeight1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        Log.d("Test", "status" + statusBarHeight1);
        return statusBarHeight1;
    }

    //Activity创建或者从后台重新回到前台时被调用
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(getTag(), "onStart called.");
    }

    //Activity从后台重新回到前台时被调用
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(getTag(), "onRestart called.");
    }

    //Activity创建或者从被覆盖、后台重新回到前台时被调用
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(getTag(), "onResume called.");
    }

    //Activity窗口获得或失去焦点时被调用,在onResume之后或onPause之后
    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i(TAG, "onWindowFocusChanged called.");
    }*/

    //Activity被覆盖到下面或者锁屏时被调用
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(getTag(), "onPause called.");
        //有可能在执行完onPause或onStop后,系统资源紧张将Activity杀死,所以有必要在此保存持久数据
    }

    //退出当前Activity或者跳转到新Activity时被调用
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(getTag(), "onStop called.");
    }

    //退出当前Activity时被调用,调用之后Activity就结束了
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(getTag(), "onDestory called.");
    }

    /**
     * Activity被系统杀死时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死.
     * 另外,当跳转到其他Activity或者按Home键回到主屏时该方法也会被调用,系统是为了保存当前View组件的状态.
     * 在onPause之前被调用.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("param", param);
        Log.i(getTag(), "onSaveInstanceState called. put param: " + param);
        super.onSaveInstanceState(outState);
    }

    /**
     * Activity被系统杀死后再重建时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死,用户又启动该Activity.
     * 这两种情况下onRestoreInstanceState都会被调用,在onStart之后.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        param = savedInstanceState.getInt("param");
        Log.i(getTag(), "onRestoreInstanceState called. get param: " + param);
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 熄屏
         02-23 09:45:05.136 3316-3316/com.example.forum I/LifeCycleActivity: onPause called.
         02-23 09:45:05.208 3316-3316/com.example.forum I/LifeCycleActivity: onSaveInstanceState called. put param: 1
         02-23 09:45:05.216 3316-3316/com.example.forum I/LifeCycleActivity: onStop called.
         02-23 09:45:38.547 3316-3316/com.example.forum I/LifeCycleActivity: onRestart called.
         02-23 09:45:38.640 3316-3316/com.example.forum I/LifeCycleActivity: onStart called.
         02-23 09:45:38.644 3316-3316/com.example.forum I/LifeCycleActivity: onResume called.

        跳转
         02-23 09:46:01.407 3316-3316/com.example.forum I/LifeCycleActivity: onPause called.
         02-23 09:46:01.422 3316-3316/com.example.forum I/LifeCycleActivity2: onCreate called.
         02-23 09:46:01.448 3316-3316/com.example.forum I/LifeCycleActivity2: onStart called.
         02-23 09:46:01.449 3316-3316/com.example.forum I/LifeCycleActivity2: onResume called.
         02-23 09:46:01.886 3316-3316/com.example.forum I/LifeCycleActivity: onSaveInstanceState called. put param: 1
         02-23 09:46:01.888 3316-3316/com.example.forum I/LifeCycleActivity: onStop called.
         02-23 09:46:49.898 3316-3316/com.example.forum I/LifeCycleActivity2: onPause called.
         02-23 09:46:49.924 3316-3316/com.example.forum I/LifeCycleActivity: onRestart called.
         02-23 09:46:49.928 3316-3316/com.example.forum I/LifeCycleActivity: onStart called.
         02-23 09:46:49.928 3316-3316/com.example.forum I/LifeCycleActivity: onResume called.
         02-23 09:46:50.269 3316-3316/com.example.forum I/LifeCycleActivity2: onStop called.
         02-23 09:46:50.270 3316-3316/com.example.forum I/LifeCycleActivity2: onDestory called.


     */

}