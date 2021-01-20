package com.example.forum.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.forum.R;
import com.example.forum.bean.Comment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommentAdapter extends BaseQuickAdapter<Comment, BaseViewHolder> implements LoadMoreModule {


    public CommentAdapter(int layoutResId, @Nullable List<Comment> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Comment comment) {
        baseViewHolder.setText(R.id.tv_name,comment.getName());
        baseViewHolder.setText(R.id.tv_time,comment.getCreatetime().substring(5,16));
        baseViewHolder.setText(R.id.tv_reply,comment.getContent());
        if (comment.getReply() == 0 ){
            baseViewHolder.setText(R.id.tv_reply_reply,"回复");
        }else {
            baseViewHolder.setText(R.id.tv_reply_reply,"查看"+comment.getReply()+"条回复>");
        }
    }
}
