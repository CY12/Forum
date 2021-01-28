package com.example.forum.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class ImageUtil {
    /**
     * 圆形头像
     */
    public static void displayRound(Context context, ImageView imageView, int url) {
        if (imageView == null) {
            throw new IllegalArgumentException("argument error");
        }
        Glide.with(context)
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .centerCrop()
                                .circleCrop())

                .load(url)
                .into(imageView);
    }

    public static void displayRadius(Context context,ImageView imageView,int url,int radius){
        RoundedCorners roundedCorners= new RoundedCorners(radius);
//通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        RequestOptions options=RequestOptions.bitmapTransform(roundedCorners);

        Glide.with(context).load(url).apply(options).into(imageView);
    }
    public static void displayRadius(Context context,ImageView imageView,String url,int radius){


        RoundedCorners roundedCorners= new RoundedCorners(radius);
        RequestOptions options=RequestOptions.bitmapTransform(roundedCorners);
//        options.inJustDecodeBounds = true;//这个参数设置为true才有效，
        Glide.with(context).load(url).apply(options).into(imageView);
    }

    public static void displayRadius(Context context, ImageView imageView, Uri uri, int radius){
        RoundedCorners roundedCorners= new RoundedCorners(radius);
        RequestOptions options=RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(context).load(uri).apply(options).into(imageView);
    }

    //通过字符串获取img
    public static int getImgByString(Context context,String name){
        return context.getResources().getIdentifier(name,"mipmap","com.example.gameflower");

    }
}
