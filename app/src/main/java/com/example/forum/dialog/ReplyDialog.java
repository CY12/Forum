package com.example.forum.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.example.forum.Config;
import com.example.forum.R;
import com.example.forum.adapter.ReplyAdapter;
import com.example.forum.bean.BaseResponse;
import com.example.forum.bean.Comment;
import com.example.forum.bean.Message;
import com.example.forum.bean.Reply;
import com.example.forum.http.HttpUtils;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class ReplyDialog extends Dialog {
    private RecyclerView rvReply;
    private RelativeLayout rvDialog;
    private ImageView ivAvatar;
    private TextView tvName;
    private TextView tvTime;
    private TextView tvReply;
    private EditText etComment;
    private TextView tvSend;
    private TextView tvReplyInput;
    private ImageView ivDismiss;
    private RelativeLayout emptyView;
    private boolean isReplyReply = false;
    private int mPosition;
    private String title;







    private List<Reply> replyList = new ArrayList<>();
    private Context context;
    ReplyAdapter replyAdapter;
    Comment comment;



    public ReplyDialog(@NonNull Context context, Comment comment) {
        super(context);
        this.context = context;
        this.comment = comment;
    }

    public ReplyDialog(@NonNull Context context, int themeResId,Comment comment,String title) {
        super(context, themeResId);
        this.context = context;
        this.comment = comment;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_reply);
        rvReply = (RecyclerView) findViewById(R.id.rv_reply);
        rvDialog = (RelativeLayout) findViewById(R.id.rv_dialog);
        View view = getLayoutInflater().inflate(R.layout.layout_reply_head, (ViewGroup) rvDialog.getParent(), false);
        ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvReply = (TextView) view.findViewById(R.id.tv_reply);
        etComment = (EditText) findViewById(R.id.et_comment);
        tvSend = (TextView) findViewById(R.id.tv_send);
        tvReplyInput = (TextView) findViewById(R.id.tv_reply_name);
        ivDismiss = (ImageView) findViewById(R.id.iv_dismiss);
        emptyView = (RelativeLayout) findViewById(R.id.emptyView);


        replyAdapter = new ReplyAdapter(context,R.layout.item_reply,replyList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        replyAdapter.addHeaderView(view);
        rvReply.setLayoutManager(linearLayoutManager);
        rvReply.setAdapter(replyAdapter);
        if (comment == null) return;
        initView();

        getReply(comment.getId(),false);
    }

    private void initView() {
        tvName.setText(comment.getName());
        tvTime.setText(comment.getCreatetime().substring(5,16));
        tvReply.setText(comment.getContent());
        dismissInput();
        replyAdapter.addChildClickViewIds(R.id.tv_reply_reply);
        replyAdapter.getLoadMoreModule().setAutoLoadMore(false);
        replyAdapter.getLoadMoreModule().setEnableLoadMoreIfNotFullPage(false);
        replyAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getReply(comment.getId(),false);
            }
        });

        replyAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.tv_reply_reply){
                    isReplyReply = true;
                    ivDismiss.setVisibility(View.VISIBLE);
                    tvReplyInput.setVisibility(View.VISIBLE);
                    tvReplyInput.setText("回复 "+replyList.get(position).getName()+" :"+replyList.get(position).getContent());
                    mPosition = position;

                    InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        etComment.requestFocus();
                        imm.showSoftInput(etComment, 0);
                    }
                }
            }
        });

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = etComment.getText().toString();
                if (TextUtils.isEmpty(text)) return;
                addReply(text);
                etComment.setText("");
            }
        });
        ivDismiss.setOnClickListener(view -> {
            dismissInput();
        });
    }

    private void dismissInput(){
        isReplyReply = false;
        ivDismiss.setVisibility(View.GONE);
        tvReplyInput.setVisibility(View.GONE);
    }
    private void addReply(String text){
        Reply reply = new Reply();
        reply.setUid(Config.user.getId());
        reply.setName(Config.user.getName());
        reply.setAvatar(Config.user.getAvatar());
        reply.setCommentName(comment.getName());
        reply.setCommentId(comment.getId());
        reply.setCommentUid(comment.getUid());
        reply.setContent(text);
        if (isReplyReply){
            reply.setReplyId(replyList.get(mPosition).getId());
            reply.setReplyName(replyList.get(mPosition).getName());
            reply.setReplyUid(replyList.get(mPosition).getUid());
        }else {

        }
        HttpUtils.getRequest().addReply(reply).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == 200 ){
                    Toast.makeText(context,"发布成功",Toast.LENGTH_SHORT).show();
                    updateComment();
                    comment(comment.getPostId(),1);
                    if (isReplyReply){
                        sendMessage(replyList.get(mPosition).getReplyUid(),comment.getId(),text,replyList.get(mPosition).getId(),replyList.get(mPosition).getContent());
                    }else {
                        sendMessage(comment.getUid(),comment.getId(),text,0,comment.getContent());
                    }

                    getReply(comment.getId(),true);
                    dismissInput();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });



    }
    private void updateComment(){
        HttpUtils.getRequest().reply(comment.getId(),1).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });
    }

    private void getReply(int id,boolean isSend) {
        HttpUtils.getRequest().getReplyList(id).enqueue(new Callback<BaseResponse<List<Reply>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Reply>>> call, Response<BaseResponse<List<Reply>>> response) {
                if (response.code() == 200 && response.body() != null && response.body().getData().size() != 0){
                    emptyView.setVisibility(View.GONE);
                    if (replyList.size() < response.body().getData().size()){
                        if (isSend){ etComment.clearFocus();//取消焦点
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE); //得到InputMethodManager的实例
                            if (imm.isActive()) {//如果开启
                                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);//关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
                            }

                            replyList.clear();
                            replyList.addAll(response.body().getData());
                            replyAdapter.notifyDataSetChanged();
                            Log.d("Test","pos"+(replyList.size()-1));
                            rvReply.scrollToPosition(replyAdapter.getItemCount()-1);

                        }else {
                            replyList.clear();
                            replyList.addAll(response.body().getData());
                            replyAdapter.notifyDataSetChanged();
                        }

                    }else {
                        replyAdapter.getLoadMoreModule().loadMoreComplete();
                        Toast.makeText(context,"暂无更多数据",Toast.LENGTH_SHORT).show();
                    }

                }else if (response.code() == 200 && response.body() != null && response.body().getData().size() == 0){
                    if (replyList.size() == 0){
                        emptyView.setVisibility(View.VISIBLE);
                    }else {
                        replyAdapter.getLoadMoreModule().loadMoreComplete();
                        Toast.makeText(context,"暂无更多数据",Toast.LENGTH_SHORT).show();
                    }


                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Reply>>> call, Throwable t) {

            }
        });

    }

    /**
     *
     * @param receiverId 接受消息id
     * @param commentId 评论id
     * @param context 回复内容
     * @param replyId 回复id
     * @param replyContent 回复的回复内容
     */
    private void sendMessage(int receiverId,int commentId,String context,int replyId,String replyContent){
        Message message = new Message();
        message.setUid(Config.user.getId());
        message.setName(Config.user.getName());
        message.setAvatar(Config.user.getAvatar());
        message.setCommentId(commentId);// 评论
        message.setMessage(context);
        message.setReplyId(replyId);
        message.setPostId(comment.getPostId());
        message.setReceiverUid(receiverId);
        message.setPostTitle(title);
        message.setReplyContent(replyContent);
        GsonBuilder gb = new GsonBuilder();
        gb.disableHtmlEscaping();
        String json = gb.create().toJson(message);
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        HttpUtils.getRequest().addMessage(requestBody).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.e("Test","消息ok");
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e("Test","消息失败"+t.toString());
            }
        });
    }
    private void comment(int id,int count){
        HttpUtils.getRequest().comment(id,count).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });
    }
}
