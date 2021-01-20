package com.example.forum.adapter;

import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.forum.R;
import com.example.forum.bean.Message;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MessageAdapter extends BaseQuickAdapter<Message, BaseViewHolder> {


    public MessageAdapter(int layoutResId, @Nullable List<Message> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Message message) {
        if (message.getIsRead() == 0){
            baseViewHolder.getView(R.id.iv_tip).setVisibility(View.VISIBLE);
        }else {
            baseViewHolder.getView(R.id.iv_tip).setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(message.getReplyContent())){
            baseViewHolder.getView(R.id.tv_reply_reply).setVisibility(View.GONE);
        }else {
            baseViewHolder.getView(R.id.tv_reply_reply).setVisibility(View.VISIBLE);
            baseViewHolder.setText(R.id.tv_reply_reply,message.getReplyContent());
        }

        baseViewHolder.setText(R.id.tv_name,message.getName());
        baseViewHolder.setText(R.id.tv_time,message.getCreatetime().substring(5,16));
        baseViewHolder.setText(R.id.tv_reply,message.getMessage());
        baseViewHolder.setText(R.id.tv_post_title,message.getPostTitle());


    }
}
