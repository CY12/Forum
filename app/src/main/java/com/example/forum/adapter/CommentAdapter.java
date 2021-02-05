package com.example.forum.adapter;

import android.content.Context;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.forum.R;
import com.example.forum.bean.Comment;
import com.example.forum.utils.ImageUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommentAdapter extends BaseQuickAdapter<Comment, BaseViewHolder> implements LoadMoreModule {

    private Context context;
    public CommentAdapter(Context context,int layoutResId, @Nullable List<Comment> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Comment comment) {
        ImageView avatar = baseViewHolder.getView(R.id.iv_avatar);
        ImageUtil.displayRadius(context,avatar,comment.getAvatar(),5);
        baseViewHolder.setText(R.id.tv_name,comment.getName());
        baseViewHolder.setText(R.id.tv_time,comment.getCreatetime().substring(5,16));
        baseViewHolder.setText(R.id.tv_reply,comment.getContent());
        if (comment.getReply() == 0 ){
            baseViewHolder.setText(R.id.tv_reply_reply,"回复");
        }else {
            baseViewHolder.setText(R.id.tv_reply_reply,"查看"+comment.getReply()+"条回复>");
        }
        if (comment.getUrlList().size()>0){
            baseViewHolder.setVisible(R.id.rv_img,true);
            RecyclerView rvImg = baseViewHolder.getView(R.id.rv_img);
            ImageAdapter imageAdapter = new ImageAdapter(context,R.layout.item_img,comment.getUrlList());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            rvImg.setLayoutManager(linearLayoutManager);
            rvImg.setAdapter(imageAdapter);
        }else {
            baseViewHolder.setVisible(R.id.rv_img,false);
        }

    }
}
