package com.example.forum.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.forum.R;

public class RedPointView extends View {
    private int height;
    private int width;
    private int defaultSize = 15;
    private Paint redPaint;

    public RedPointView(Context context) {
        super(context);
    }

    public RedPointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RedPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getMySize(defaultSize,heightMeasureSpec);
        width = getMySize(defaultSize,widthMeasureSpec);
        if (height > width){
            height = width;
        }else if (height < width){
            width = height;
        }
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPaint();
        int r = width/2;
        canvas.drawCircle(r,r,r,redPaint);
    }

    private void initPaint() {
        redPaint = new Paint();
        redPaint.setColor(Color.RED);
        redPaint.setAntiAlias(true);
        redPaint.setStyle(Paint.Style.FILL);

    }
    public void setText(){

    }

    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {
                //如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY: {
                //如果是固定的大小，那就不要去改变它
                //如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size;
                break;
            }
        }
        return mySize;
    }

    private float dp2px(Context context, float dp) {

        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }
}
