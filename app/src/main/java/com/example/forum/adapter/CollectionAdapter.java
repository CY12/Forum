package com.example.forum.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.forum.R;
import com.example.forum.bean.Collections;
import com.example.forum.bean.Post;
import com.example.forum.ui.CollectionActivity;
import com.example.forum.utils.ImageUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CollectionAdapter extends BaseQuickAdapter <Post, BaseViewHolder> {
    private Context context;
    private int type;
    public CollectionAdapter(int type,Context context,int layoutResId, @Nullable List<Post> data) {
        super(layoutResId, data);
        this.context = context;
        this.type = type;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Post collections) {
        if (type == CollectionActivity.TYPE_FORUM){
            baseViewHolder.setVisible(R.id.iv_delete,true);
        }else {
            baseViewHolder.setVisible(R.id.iv_delete,false);
        }
        ImageView avatar = baseViewHolder.getView(R.id.iv_avatar);
        ImageUtil.displayRadius(context,avatar,collections.getAvatar(),5);
        baseViewHolder.setText(R.id.tv_user,collections.getName());
        baseViewHolder.setText(R.id.tv_updatetime,collections.getUpdatetime().substring(5, 16));
        baseViewHolder.setText(R.id.tv_title,collections.getTitle());
        baseViewHolder.setText(R.id.tv_content,collections.getContent());
        if (collections.getImage() > 0){
            baseViewHolder.setVisible(R.id.tv_img, true);
            if (collections.getImage() == 1){
                baseViewHolder.setText(R.id.tv_img,"[图片]");
            }else {
                baseViewHolder.setText(R.id.tv_img,"[图片]..");
            }
        }else {
            baseViewHolder.setVisible(R.id.tv_img, false);
        }
    }
}
