package com.example.forum.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.forum.R;
import com.example.forum.utils.ImageUtil;
import com.hitomi.tilibrary.utils.ImageUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ImageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private Context context;
    private static List<String> originalList = new ArrayList<>();
//    public static final int TYPE_POST = 1;
    private int size = 0;

//    public static ImageAdapter startPostMoreAdapter(Context context,int layoutResId, @Nullable List<String> data){
//        if (data.size() > 3){
//            originalList.addAll(data);
//            List<String> urlList = new ArrayList<>();
//            for (int i = 0;i<4;i++){
//                urlList.add(data.get(i));
//            }
//            return new ImageAdapter(context,layoutResId,urlList);
//        }
//        return new ImageAdapter(context,layoutResId,data);
//    }

    public ImageAdapter(Context context,int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
        this.context = context;
    }
    public ImageAdapter(Context context,int layoutResId, @Nullable List<String> data,int size) {
        super(layoutResId, data);
        this.context = context;
        this.size = size;

    }


    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, String s) {
        ImageView imageView = baseViewHolder.getView(R.id.iv_img);

        if (size != 0){
            MultiTransformation multi =new  MultiTransformation<Bitmap>(
                    new CenterCrop(),
                    new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL));
            Glide.with(context).load(s)
                    .apply(RequestOptions.bitmapTransform(multi))
                    .into(imageView);
            if (baseViewHolder.getLayoutPosition() == 2){
                Log.e("Test","position == 3");
                imageView.setImageAlpha(80);
                TextView tvNum =  baseViewHolder.getView(R.id.tv_img_more);
                tvNum.setVisibility(View.VISIBLE);
                tvNum.setText("+"+size);
            }else {
                baseViewHolder.getView(R.id.tv_img_more).setVisibility(View.GONE);
            }
        }else {
            ImageUtil.displayRadius(context,imageView,s,40);
        }

    }
}
