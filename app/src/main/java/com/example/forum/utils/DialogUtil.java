package com.example.forum.utils;




import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.forum.R;


/**
 * 对话框工具类
 */
public class DialogUtil {
    /**
     * 弹框时设置背景 阴影度 50%
     */
    private static final float WINDOW_ALPHA_DARK = 0.5f;


    public interface OnClickCallBackListener {
        void onClickCallBack(Bundle data);
    }

    public static void setAlpha(Activity context, float alpha) {
        LayoutParams params = context.getWindow().getAttributes();
        params.alpha = alpha;
        context.getWindow().setAttributes(params);
    }


    /**
     * 显示dialog
     *
     * @param view
     */
    private static void showDialog(Dialog dialog, View view, int gravity, boolean cancelable) {
        Window window = dialog.getWindow();
        window.setGravity(gravity); // 此处可以设置dialog显示的位置
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.setContentView(view);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        dialog.show();
    }


    /**
     * 通用消息提醒弹窗  两个按钮 有标题
     *
     * @param title 提示内容
     */
    public static void showDialog(final Context context, String title, final OnClickCallBackListener onClickCallBackListener) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog, null);
        TextView tv_left = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tv_right = (TextView) view.findViewById(R.id.tv_confirm);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText(title);
        tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCallBackListener.onClickCallBack(null);
                dialog.dismiss();
            }
        });

        showDialog(dialog, view, Gravity.CENTER, true);
    }

}