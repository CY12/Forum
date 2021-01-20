package com.example.forum.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forum.base.BaseToolbarActivity;
import com.example.forum.Config;
import com.example.forum.R;
import com.example.forum.bean.BaseResponse;
import com.example.forum.bean.Post;
import com.example.forum.bean.User;
import com.example.forum.http.HttpUtils;
import com.example.forum.utils.GsonUtil;
import com.example.forum.utils.SharedPreferenceUtil;
import com.google.gson.GsonBuilder;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends BaseToolbarActivity {

    private EditText etTitle;
    private View line;
    private EditText etContent;
    private ImageView ivBack1;
    private TextView tvPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "BaseToolbarActivity onCreate");
        title.setText("发布帖子");
        tvGo.setText("发布");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_post;
    }

    @Override
    public void initView() {
        etTitle = (EditText) findViewById(R.id.et_title);
        line = (View) findViewById(R.id.line);
        etContent = (EditText) findViewById(R.id.et_content);
        ivBack1 = (ImageView) findViewById(R.id.iv_back);
        tvPost = (TextView) findViewById(R.id.tv_post);


    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        ivBack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Test","post back");
                finish();

            }
        });
        tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etTitle.getText().toString();
                String content = etContent.getText().toString();
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                    tvPost.setEnabled(false);
                    sendPost(title, content);
                }
            }
        });
    }

    private void sendPost(String title, String content) {
        String userJson = SharedPreferenceUtil.getString(PostActivity.this, SharedPreferenceUtil.USERINFO, null);
        if (TextUtils.isEmpty(userJson)) {
            Toast.makeText(PostActivity.this, "获取人员信息失败", Toast.LENGTH_SHORT).show();
            return;
        }
        User user = GsonUtil.toObject(userJson, User.class);
        if (user == null) return;

        Post post = new Post();
        post.setAvatar(user.getAvatar());
        post.setName(user.getName());
        post.setTitle(title);
        post.setUid(Config.user.getId());
        post.setContent(content);
        GsonBuilder gb = new GsonBuilder();
        gb.disableHtmlEscaping();
        String json = gb.create().toJson(post);
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        HttpUtils.getRequest().addPost(requestBody).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == 200){
                    Toast.makeText(PostActivity.this,"发帖成功",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(PostActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
            }

        });

        tvPost.setEnabled(true);
    }
}