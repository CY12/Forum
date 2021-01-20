package com.example.forum.adapter;

import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.forum.R;
import com.example.forum.bean.Post;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ForumAdapter extends BaseQuickAdapter<Post, BaseViewHolder> {
    public ForumAdapter(int layoutResId, @Nullable List<Post> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Post post) {
        Log.d("Test",""+post.toString());

        baseViewHolder.setText(R.id.tv_user,post.getName());
        baseViewHolder.setText(R.id.tv_updatetime,post.getUpdatetime().substring(5,16)+"更新");
        baseViewHolder.setText(R.id.tv_title,post.getTitle());
        baseViewHolder.setText(R.id.tv_content,post.getContent());
        baseViewHolder.setText(R.id.tv_star,post.getStarts()+"");
        baseViewHolder.setText(R.id.tv_comment,post.getComments()+"");
        baseViewHolder.setText(R.id.tv_view,post.getViews()+"");




    }
}
