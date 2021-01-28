package com.example.forum.ui;



import android.content.Context;
import android.content.Intent;
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
import com.example.forum.adapter.ReplyAdapter;
import com.example.forum.base.BaseToolbarActivity;
import com.example.forum.R;
import com.example.forum.bean.BaseResponse;
import com.example.forum.bean.Comment;
import com.example.forum.bean.Message;
import com.example.forum.bean.Reply;
import com.example.forum.bean.ReplyDetail;
import com.example.forum.http.HttpUtils;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageDetailActivity extends BaseToolbarActivity {

    public final static String COMMENT_ID = "commentId";
    public final static String POST_ID = "postId";
    public final static String TITLE = "title";
    private RecyclerView rvReply;
    private ImageView ivAvatar;
    private TextView tvName;
    private TextView tvTime;
    private TextView tvReply;
    private RelativeLayout emptyView;
    private ImageView ivNone;
    private TextView tvReplyName;
    private ImageView ivDismiss;
    private EditText etComment;
    private TextView tvSend;
    private int commentId;
    private int mPosition;
    private int postId;
    private boolean isReplyReply = false;
    private Comment comment;
    private String titleText;






    private List<Reply> replyList = new ArrayList<>();
    ReplyAdapter replyAdapter;




    public static void startActivity(Context context,int commentId,int postId,String title){
        Intent intent = new Intent(context,MessageDetailActivity.class);
        intent.putExtra(COMMENT_ID,commentId);
        intent.putExtra(POST_ID,postId);
        intent.putExtra(TITLE,title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_detail;
    }

    @Override
    public void initView() {

        rvReply = (RecyclerView) findViewById(R.id.rv_reply);
        emptyView = (RelativeLayout) findViewById(R.id.emptyView);
        ivNone = (ImageView) findViewById(R.id.iv_none);
        tvReplyName = (TextView) findViewById(R.id.tv_reply_name);
        ivDismiss = (ImageView) findViewById(R.id.iv_dismiss);
        etComment = (EditText) findViewById(R.id.et_comment);
        tvSend = (TextView) findViewById(R.id.tv_send);
        title.setText("帖子主题");
        ivBack.setVisibility(View.VISIBLE);

        View view = getLayoutInflater().inflate(R.layout.layout_reply_head, (ViewGroup) rvReply.getParent(), false);
        ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvReply = (TextView) view.findViewById(R.id.tv_reply);
        replyAdapter = new ReplyAdapter(MessageDetailActivity.this,R.layout.item_reply,replyList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        replyAdapter.addHeaderView(view);
        rvReply.setLayoutManager(linearLayoutManager);
        rvReply.setAdapter(replyAdapter);

        replyAdapter.getLoadMoreModule().setAutoLoadMore(false);
        replyAdapter.getLoadMoreModule().setEnableLoadMoreIfNotFullPage(false);

        if (getIntent() != null && getIntent().getExtras() != null){
            commentId = getIntent().getIntExtra(COMMENT_ID,0);
            titleText = getIntent().getStringExtra(TITLE);
            postId = getIntent().getIntExtra(POST_ID,0);
            Log.e("Test","commentId"+commentId+"  titleText"+titleText+"  postId"+postId);
            if (postId == 0 || commentId == 0 || TextUtils.isEmpty(titleText)) return;
        }
        title.setText(titleText);
        tvGo.setText("查看主题");
        tvGo.setVisibility(View.VISIBLE);


    }

    @Override
    public void initListener() {
        replyAdapter.addChildClickViewIds(R.id.tv_reply_reply);
        replyAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getReply(commentId,false);
            }
        });

        replyAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.tv_reply_reply){
                    isReplyReply = true;
                    ivDismiss.setVisibility(View.VISIBLE);
                    tvReplyName.setVisibility(View.VISIBLE);
                    tvReplyName.setText("回复 "+replyList.get(position).getName()+" :"+replyList.get(position).getContent());
                    mPosition = position;

                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
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
        ivDismiss.setOnClickListener(view1 -> {
            dismissInput();
        });
        ivBack.setOnClickListener(view -> finish());

        tvGo.setOnClickListener(view -> {
            DetailActivity.startActivity(MessageDetailActivity.this,postId);
        });
    }

    @Override
    public void initData() {
        getReplyDetail(commentId);
    }

    @Override
    public String setTitle() {
        return null;
    }

    private void getReplyDetail(int commentId){
        HttpUtils.getRequest().getReplyDetail(commentId).enqueue(new Callback<BaseResponse<ReplyDetail>>() {
            @Override
            public void onResponse(Call<BaseResponse<ReplyDetail>> call, Response<BaseResponse<ReplyDetail>> response) {
                if (response.code() == 200 && response.body().getData() != null ){
                    if (response.body().getData().getComment() != null){
                        comment = response.body().getData().getComment();
                        tvName.setText(response.body().getData().getComment().getName());
                        tvTime.setText(response.body().getData().getComment().getCreatetime().substring(5,16));
                        tvReply.setText(response.body().getData().getComment().getContent());
                    }
                }
                if (response.body().getData().getReplyList()!=null && response.body().getData().getReplyList().size()>0){
                    emptyView.setVisibility(View.GONE);
                    replyList.addAll(response.body().getData().getReplyList());
                    replyAdapter.notifyDataSetChanged();
                }else {
                    emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<ReplyDetail>> call, Throwable t) {

            }
        });
    }

    /**
     * 关闭软键盘
     */
    private void dismissInput(){
        isReplyReply = false;
        ivDismiss.setVisibility(View.GONE);
        tvReplyName.setVisibility(View.GONE);
    }

    /**
     * 回复
     * @param text
     */
    private void addReply(String text){
        if (comment == null) return;
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
                    Toast.makeText(MessageDetailActivity.this,"回复成功",Toast.LENGTH_SHORT).show();
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

    /**
     * 帖子回复量 +1
     */
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

    private void getComment(int id){

    }

    /**
     * 获取回复
     * @param id
     * @param isSend
     */
    private void getReply(int id,boolean isSend) {
        HttpUtils.getRequest().getReplyList(id).enqueue(new Callback<BaseResponse<List<Reply>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Reply>>> call, Response<BaseResponse<List<Reply>>> response) {
                if (response.code() == 200 && response.body() != null && response.body().getData().size() != 0){
                    emptyView.setVisibility(View.GONE);
                    if (replyList.size() < response.body().getData().size()){
                        if (isSend){ etComment.clearFocus();//取消焦点
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //得到InputMethodManager的实例
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
                        Toast.makeText(MessageDetailActivity.this,"暂无更多数据",Toast.LENGTH_SHORT).show();
                    }

                }else if (response.code() == 200 && response.body() != null && response.body().getData().size() == 0){
                    if (replyList.size() == 0){
                        emptyView.setVisibility(View.VISIBLE);
                    }else {
                        replyAdapter.getLoadMoreModule().loadMoreComplete();
                        Toast.makeText(MessageDetailActivity.this,"暂无更多数据",Toast.LENGTH_SHORT).show();
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
        message.setPostTitle(titleText);
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