package com.example.forum.adapter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.forum.R;
import com.example.forum.bean.Reply;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReplyAdapter extends BaseQuickAdapter<Reply, BaseViewHolder> implements LoadMoreModule {

    public ReplyAdapter(int layoutResId, @Nullable List<Reply> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Reply reply) {
        baseViewHolder.setText(R.id.tv_name, reply.getName());
        baseViewHolder.setText(R.id.tv_time, reply.getCreatetime().substring(5, 16));
        if (TextUtils.isEmpty(reply.getReplyName())) {
            baseViewHolder.setText(R.id.tv_reply, reply.getContent());
        } else {

            int end = reply.getReplyName().length() + 3;
            SpannableString spannableString = new SpannableString("回复 " + reply.getReplyName() + " :"+reply.getContent());
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#E603A9F4"));
            spannableString.setSpan(colorSpan, 3, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View view) {

                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
//                super.updateDrawState(ds);
                }
            };
            spannableString.setSpan(clickableSpan, 2, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            TextView textView = (TextView) baseViewHolder.getView(R.id.tv_reply);
            textView.setText(spannableString);

        }

    }
}
