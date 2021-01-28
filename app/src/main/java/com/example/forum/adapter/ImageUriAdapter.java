package com.example.forum.adapter;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.forum.R;
import com.example.forum.utils.ImageUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ImageUriAdapter extends BaseQuickAdapter<Uri, BaseViewHolder> {
    private Context context;
    public ImageUriAdapter(Context context, int layoutResId, @Nullable List<Uri> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Uri uri) {
        ImageView imageView = baseViewHolder.getView(R.id.iv_selectImg);
        ImageUtil.displayRadius(context,imageView,uri,10);
        //Glide.with(context).load(uri).into(imageView);
    }
}
