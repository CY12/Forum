package com.example.forum.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.example.forum.adapter.ImageAdapter;
import com.example.forum.base.BaseToolbarActivity;
import com.example.forum.Config;
import com.example.forum.R;
import com.example.forum.adapter.CommentAdapter;
import com.example.forum.bean.BaseResponse;
import com.example.forum.bean.Comment;
import com.example.forum.bean.Message;
import com.example.forum.bean.Post;
import com.example.forum.bean.Reply;
import com.example.forum.bean.User;
import com.example.forum.dialog.ReplyDialog;
import com.example.forum.http.HttpUtils;
import com.example.forum.utils.GsonUtil;
import com.example.forum.utils.ImageUtil;
import com.example.forum.utils.SharedPreferenceUtil;
import com.google.gson.GsonBuilder;
import com.hitomi.tilibrary.style.index.NumberIndexIndicator;
import com.hitomi.tilibrary.style.progress.ProgressBarIndicator;
import com.hitomi.tilibrary.transfer.TransferConfig;
import com.hitomi.tilibrary.transfer.Transferee;
import com.vansz.glideimageloader.GlideImageLoader;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends BaseToolbarActivity {

    public static final String POST = "post";
    public static final String POST_ID = "post_id";

    private ImageView ivHead;
    private TextView tvUser;
    private TextView tvUpdatetime;
    private TextView tvContent;
    private ImageView ivStar;
    private TextView tvStar;
    private ImageView ivReply;
    private TextView tvComment;
    private ImageView ivView;
    private TextView tvView;
    private TextView replyText;
    private RecyclerView rvComment;
    private TextView tvReplyName;
    private ImageView ivDismiss;
    private ImageView ivSelectImg;
    private boolean isCollection = false;

    private NestedScrollView nestedScrollView;





    private RecyclerView rvImg;
    private ImageAdapter imageAdapter;
    private EditText etComment;
    private TextView tvSend;
    private RelativeLayout rvDetail;

    private ImageView ivCollection;
    private List<Integer> collectionList = new ArrayList<>();


    private int postId;
    private boolean isReply = false;
    private int mPosition;
    private Post post;
    private int start = 0;
    private int type = 0;


    CommentAdapter commentAdapter;
    List<Comment> commentList = new ArrayList<>();

    List<String> urlList = new ArrayList<>();


    public static void startActivity(Context context,String postJson,int postId){
        Intent intent = new Intent(context,DetailActivity.class);
        intent.putExtra(POST,postJson);
        intent.putExtra(POST_ID,postId);
        context.startActivity(intent);
    }
    public static void startActivity(Context context,int postId){
        Intent intent = new Intent(context,DetailActivity.class);
        intent.putExtra(POST_ID,postId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    public void initView() {
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        rvDetail = (RelativeLayout) findViewById(R.id.rv_detail);
        rvImg = findViewById(R.id.rv_img);
        ivHead = (ImageView) findViewById(R.id.iv_avatar);
        tvUser = (TextView) findViewById(R.id.tv_user);
        tvUpdatetime = (TextView) findViewById(R.id.tv_updatetime);
        tvContent = (TextView) findViewById(R.id.tv_content);
        ivStar = (ImageView) findViewById(R.id.iv_star);
        tvStar = (TextView) findViewById(R.id.tv_star);
        ivReply = (ImageView) findViewById(R.id.iv_reply);
        tvComment = (TextView) findViewById(R.id.tv_comment);
        ivView = (ImageView) findViewById(R.id.iv_view);
        tvView = (TextView) findViewById(R.id.tv_view);
        replyText = (TextView) findViewById(R.id.reply_text);
        ivCollection = (ImageView) findViewById(R.id.iv_collection);
        rvComment = (RecyclerView) findViewById(R.id.rv_comment);

        etComment = (EditText) findViewById(R.id.et_comment);
        tvSend = (TextView) findViewById(R.id.tv_send);
        tvReplyName = findViewById(R.id.tv_reply_name);
        ivDismiss = findViewById(R.id.iv_dismiss);
        ivSelectImg = (ImageView) findViewById(R.id.iv_selectImg);


        commentAdapter = new CommentAdapter(DetailActivity.this,R.layout.item_comment, commentList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvComment.setLayoutManager(linearLayoutManager);
        rvComment.setAdapter(commentAdapter);

        commentAdapter.getLoadMoreModule().setEnableLoadMoreIfNotFullPage(false);
        commentAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getComment(false);
            }
        });
        commentAdapter.addChildClickViewIds(R.id.tv_reply_reply);

        imageAdapter = new ImageAdapter(DetailActivity.this,R.layout.item_img,urlList);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        rvImg.setLayoutManager(linearLayoutManager1);
        rvImg.setAdapter(imageAdapter);
        View view1 = getLayoutInflater().inflate(R.layout.layout_empty_view, (ViewGroup) rvComment.getParent(), false);
        commentAdapter.setEmptyView(view1);
        imageAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                showPreview(urlList,rvImg,R.id.iv_img,position);
            }
        });

    }

    @Override
    public void initListener() {

        ivCollection.setOnClickListener(view -> {
            if (!isCollection){
                addCollection();
                Glide.with(this).load(R.mipmap.collection_blue).into(ivCollection);
                collectionList.add(postId);
                String json = GsonUtil.toJson(collectionList);
                SharedPreferenceUtil.putString(DetailActivity.this,SharedPreferenceUtil.COLLECTION_LIST,json);
            }else {
               for (int i=0;i<collectionList.size();i++){
                   if (postId == collectionList.get(i)){
                       collectionList.remove(i);
                       break;
                   }
               }
                String json = GsonUtil.toJson(collectionList);
                SharedPreferenceUtil.putString(DetailActivity.this,SharedPreferenceUtil.COLLECTION_LIST,json);
                cancelCollection();
                Glide.with(this).load(R.mipmap.collection).into(ivCollection);

            }
            isCollection = !isCollection;


        });
        ivSelectImg.setOnClickListener(view -> {
            if (tvReplyName.getVisibility() == View.VISIBLE){
                Toast.makeText(DetailActivity.this,"回复暂不支持图片",Toast.LENGTH_SHORT).show();
                return;
            }
            String content = etComment.getText().toString();
            Comment comment = new Comment();
            comment.setAvatar(post.getAvatar());
            comment.setContent(content);
            comment.setName(Config.user.getName());
            comment.setPostId(post.getId());
            comment.setUid(Config.user.getId());

            String commentJson = GsonUtil.toJson(comment);

            InputActivity.startActivity(DetailActivity.this,InputActivity.TYPE_COMMENT,commentJson,post.getTitle(),post.getUid());
        });

        commentAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (post == null) {
                    return;
                }
                if (view.getId() == R.id.tv_reply_reply){
                    commentList.get(position).getId();
                    if (commentList.get(position).getReply() ==
                            0){
                        mPosition = position;
                        ivDismiss.setVisibility(View.VISIBLE);
                        tvReplyName.setVisibility(View.VISIBLE);
                        tvReplyName.setText("回复: "+commentList.get(position).getName()+" "+commentList.get(position).getContent());
                        etComment.setFocusable(true);
                        etComment.setFocusableInTouchMode(true);
                        etComment.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            etComment.requestFocus();
                            imm.showSoftInput(etComment, 0);
                        }
                        isReply = true;
                    }else {
                        ReplyDialog replyDialog = new ReplyDialog(DetailActivity.this,R.style.ReplyDialog,commentList.get(position),post.getTitle());
                        Window window = replyDialog.getWindow();
                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        //设置弹出位置
                        window.setGravity(Gravity.BOTTOM);
                        window.setWindowAnimations(R.style.main_menu_animStyle);
                        replyDialog.show();
                        Display display = getWindowManager().getDefaultDisplay();
                        WindowManager.LayoutParams lp = replyDialog.getWindow().getAttributes();
                        lp.width = (int)(display.getWidth()); //设置宽度

                        replyDialog.getWindow().setAttributes(lp);
                    }



                }
            }
        });

        ivDismiss.setOnClickListener(view1 -> dismissInput());
    }

    @Override
    public String getTag() {
        return "DetailActivity";
    }


    @Override
    public void initData() {
        initIntent();
        initPostView();
        if (postId == 0) return;
        String collectionJson = SharedPreferenceUtil.getString(DetailActivity.this,SharedPreferenceUtil.COLLECTION_LIST,"");
        if (!TextUtils.isEmpty(collectionJson)){
            collectionList = GsonUtil.toList(collectionJson,Integer.class);
            for (int i=0;i<collectionList.size();i++){
                Log.e("Test","postId"+collectionList.get(i));
                if (collectionList.get(i) == postId){
                    isCollection = true;
                    Glide.with(this).load(R.mipmap.collection_blue).into(ivCollection);

                }
            }
        }

        getComment(false);
        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString().trim();
                ;
                if (TextUtils.isEmpty(text)) {
                    tvSend.setTextColor(Color.parseColor("#8006B1FF"));
                } else {
                    tvSend.setTextColor(Color.parseColor("#06B1FF"));
                }
            }
        });

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = etComment.getText().toString();
                if (TextUtils.isEmpty(text)) return;
                if (isReply){
                    addReply(text);
                }else {
                    sendComment(text);
                }
                etComment.setText("");
                isReply = false;
            }
        });
    }

    @Override
    public String setTitle() {
        return null;
    }

    private void initPostView() {
        if (post != null){
            tvUser.setText(post.getName());
            tvUpdatetime.setText(post.getCreatetime().substring(5, 10) + "发布");
            title.setText(post.getTitle());
            ImageUtil.displayRadius(DetailActivity.this,ivHead,post.getAvatar(),5);
            tvContent.setText(post.getContent());
            tvStar.setText(post.getStarts() + "");
            tvComment.setText(post.getComments() + "");
            tvView.setText((post.getViews()+1) + "");
            if (post.getUrlList().size()>0){
                urlList.addAll(post.getUrlList());
                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent == null) return;
        String postJson = intent.getStringExtra(POST);
        post = GsonUtil.toObject(postJson,Post.class);
        postId = intent.getIntExtra(POST_ID,0);
        if (post == null) {
            if (postId != 0){
                getPost(postId);
            }
        }


    }
    private void cancelCollection() {
        int uid = Config.getUser(DetailActivity.this).getId();

        HttpUtils.getRequest().cancelCollection(uid,postId).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Toast.makeText(DetailActivity.this,"取消收藏成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(DetailActivity.this,"取消收藏失败"+t.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addCollection(){
        int uid = Config.getUser(DetailActivity.this).getId();
        HttpUtils.getRequest().addCollection(uid,post.getId()).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == 200 && response.body().getCode() == 200){
                    Toast.makeText(DetailActivity.this,"收藏成功",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(DetailActivity.this,"收藏失败"+t.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPost(int id){
        HttpUtils.getRequest().getPost(id).enqueue(new Callback<BaseResponse<Post>>() {
            @Override
            public void onResponse(Call<BaseResponse<Post>> call, Response<BaseResponse<Post>> response) {
                Log.e("Test",response.toString());
                if (response.body() != null && response.body().getData() != null){
                    post = response.body().getData();
                    initPostView();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Post>> call, Throwable t) {
                Toast.makeText(DetailActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 关闭软键盘上的回复详细内容
     */
    private void dismissInput(){
        ivDismiss.setVisibility(View.GONE);
        tvReplyName.setVisibility(View.GONE);
    }

    /**
     * 发布一条评论
     * @param text
     */
    private void sendComment(String text) {
        if (text.length() == 0) return;
        progressBar.setVisibility(View.VISIBLE);

        if (Config.user == null){
            String  userInfo = SharedPreferenceUtil.getString(DetailActivity.this,SharedPreferenceUtil.USERINFO,"");
            if (userInfo.length() == 0) return;
            User user = GsonUtil.toObject(userInfo,User.class);
            Config.user = user;

        }
        if (Config.user == null)return;

        Comment comment = new Comment();
        comment.setAvatar(Config.getUser(DetailActivity.this).getAvatar());
        comment.setContent(text);
        comment.setName(Config.user.getName());
        comment.setPostId(post.getId());
        comment.setUid(Config.user.getId());
//        GsonBuilder gb = new GsonBuilder();
//        gb.disableHtmlEscaping();
//        String json = gb.create().toJson(comment);
//        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        HttpUtils.getRequest().addComment(comment).enqueue(new Callback<BaseResponse<Comment>>() {
            @Override
            public void onResponse(Call<BaseResponse<Comment>> call, Response<BaseResponse<Comment>> response) {
                if (response.code() == 200 && response.body() != null) {
                    int c = Integer.parseInt(tvComment.getText().toString());
                    tvComment.setText(c+1+"");
                    comment(post.getId(),1);
                    sendMessage(post.getUid(),response.body().getData().getId(),text,0,"");
                    getComment(true);


                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Comment>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 0回复时回复
     * @param text
     */
    private void addReply(String text){
        Reply reply = new Reply();
        reply.setUid(Config.user.getId());
        reply.setName(Config.user.getName());
        reply.setAvatar(Config.user.getAvatar());
        reply.setCommentName(commentList.get(mPosition).getName());
        reply.setCommentId(commentList.get(mPosition).getId());
        reply.setCommentUid(commentList.get(mPosition).getUid());
        reply.setContent(text);

        HttpUtils.getRequest().addReply(reply).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == 200 ){
                    Toast.makeText(DetailActivity.this,"回复成功",Toast.LENGTH_SHORT).show();
                    updateComment(commentList.get(mPosition).getId());
                    comment(post.getId(),1);
                    Log.e("Test","sendMessage "+commentList.get(mPosition).toString());
                    sendMessage(commentList.get(mPosition).getUid(),commentList.get(mPosition).getId(),text,0,commentList.get(mPosition).getContent());

                    etComment.clearFocus();//取消焦点

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //得到InputMethodManager的实例
                    if (imm.isActive()) {//如果开启
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);//关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
                    }
                    dismissInput();
                    commentList.get(mPosition).setReply(1);
                    commentAdapter.notifyItemChanged(mPosition);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });



    }

    private void updateComment(int id){
        HttpUtils.getRequest().reply(id,1).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });
    }

    /**
     *  向回复消息人发一条消息
     * @param
     * @param context
     */
    private void sendMessage(int receiverId,int commentId,String context,int replyId,String replyContent){
        Message message = new Message();
        message.setUid(Config.user.getId());
        message.setName(Config.user.getName());
        message.setAvatar(Config.user.getAvatar());
        message.setCommentId(commentId);// 评论
        message.setMessage(context);
        message.setPostId(post.getId());
        message.setReceiverUid(receiverId);
        message.setPostTitle(post.getTitle());
        if (!TextUtils.isEmpty(replyContent)){
            message.setReplyId(replyId);
            message.setReplyContent(replyContent);
        }

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


    private void getComment(boolean isSend) {
        progressBar.setVisibility(View.VISIBLE);

        HttpUtils.getRequest().getCommentList(postId, start, 20).enqueue(new Callback<BaseResponse<List<Comment>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Comment>>> call, Response<BaseResponse<List<Comment>>> response) {
                if (response.code() == 200 && response.body().getData() != null && response.body().getData().size() > 0) {
                    Log.d("Test", "getComment ");

                    commentAdapter.getLoadMoreModule().loadMoreComplete();
                    if (isSend){
                        etComment.clearFocus();//取消焦点

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //得到InputMethodManager的实例
                        if (imm.isActive()) {//如果开启
                            imm.hideSoftInputFromWindow(etComment.getWindowToken(),0);
//                            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);//关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
                        }

                        commentList.addAll(response.body().getData());
                        commentAdapter.notifyDataSetChanged();
                        nestedScrollView.fullScroll(View.FOCUS_DOWN);
                        Log.e("Test","pos"+(commentList.size()-1));
                        rvComment.scrollToPosition(commentList.size()-1);
                    }else {
                        commentList.addAll(response.body().getData());
                        commentAdapter.notifyDataSetChanged();
                    }

                    start = commentList.size();

                } else if (response.code() == 200 && response.body().getData() != null) {
                    if (commentList.size() == 0){
                        Log.e("Test","emptyView");
                    }else {
                        commentAdapter.getLoadMoreModule().loadMoreComplete();
                        Toast.makeText(DetailActivity.this,"暂无更多数据",Toast.LENGTH_SHORT).show();
                    }


                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Comment>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DetailActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == InputActivity.INPUT_IMG && resultCode == InputActivity.INPUT_IMG){
            if (data.getExtras() != null){
                etComment.setText("");
                etComment.clearFocus();
                getComment(true);

            }
        }
    }
    private void showPreview(List<String> urlList,RecyclerView recyclerView,int id,int position){
        Log.e("Test","preview"+position);
        Transferee transferee = Transferee.getDefault(DetailActivity.this);
        TransferConfig recyclerTransConfig = TransferConfig.build()

                .setSourceUrlList(urlList)
                .setProgressIndicator(new ProgressBarIndicator())
                .setIndexIndicator(new NumberIndexIndicator())
                .setImageLoader(GlideImageLoader.with(getApplicationContext()))
                .enableHideThumb(false)
                .bindRecyclerView(recyclerView, id);
        recyclerTransConfig.setNowThumbnailIndex(position);
        transferee.apply(recyclerTransConfig).show();
    }

}
