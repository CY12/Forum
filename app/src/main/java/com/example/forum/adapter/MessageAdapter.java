package com.example.forum.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.forum.R;
import com.example.forum.bean.Message;
import com.example.forum.utils.ImageUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MessageAdapter extends BaseQuickAdapter<Message, BaseViewHolder> {
    private Context context;

    public MessageAdapter(Context context,int layoutResId, @Nullable List<Message> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Message message) {
        if (message.getIsRead() == 0) {
            baseViewHolder.getView(R.id.iv_tip).setVisibility(View.VISIBLE);
        } else {
            baseViewHolder.getView(R.id.iv_tip).setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(message.getReplyContent())) {
            baseViewHolder.getView(R.id.tv_reply_reply).setVisibility(View.GONE);
        } else {
            baseViewHolder.getView(R.id.tv_reply_reply).setVisibility(View.VISIBLE);
            String replyContent = message.getReplyContent();
            if (message.getReplyImage() > 0) {
                for (int i = 0; i < message.getReplyImage(); i++) {
                    replyContent = replyContent + "[图片]";
                }
            }
            baseViewHolder.setText(R.id.tv_reply_reply, replyContent);
        }

        baseViewHolder.setText(R.id.tv_name, message.getName());
        baseViewHolder.setText(R.id.tv_time, message.getCreatetime().substring(5, 16));
        String messageContent = message.getMessage();
        if (message.getMessageImage() > 0) {
            for (int i = 0; i < message.getMessageImage(); i++) {
                messageContent = messageContent + "[图片]";
            }
        }
        baseViewHolder.setText(R.id.tv_reply, messageContent);
        baseViewHolder.setText(R.id.tv_post_title, message.getPostTitle());
        ImageView avatar = baseViewHolder.getView(R.id.iv_avatar);
        ImageUtil.displayRadius(context,avatar,message.getAvatar(),5);

    }
}
