package com.example.forum.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

public class MyRelativeLayout extends RelativeLayout {
    private int mScreenHeight = -1;

    public MyRelativeLayout(Context context) {
        super(context);
        mScreenHeight = getHeight(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScreenHeight = getHeight(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScreenHeight = getHeight(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mScreenHeight = getHeight(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mScreenHeight != -1){
            Log.e("Test","mScreenHeight  == "+mScreenHeight);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mScreenHeight, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    public int getHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager mWm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWm.getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        return screenHeight;
    }
}
