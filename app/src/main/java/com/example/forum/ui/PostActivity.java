package com.example.forum.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.example.forum.adapter.ImageUriAdapter;
import com.example.forum.base.BaseToolbarActivity;
import com.example.forum.Config;
import com.example.forum.R;
import com.example.forum.bean.BaseResponse;
import com.example.forum.bean.Post;
import com.example.forum.bean.User;
import com.example.forum.http.HttpUtils;
import com.example.forum.utils.FileUtil;
import com.example.forum.utils.GsonUtil;
import com.example.forum.utils.SharedPreferenceUtil;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends BaseToolbarActivity {
    public static final int SEND_POST = 1000;
    private final static int SELECT_IMG = 144;

    private EditText etTitle;
    private View line;
    private EditText etContent;
    private ImageView ivBack1;
    private TextView tvPost;
    private List<Uri> u_list = new ArrayList<>();
    private List<File> fileList = new ArrayList<>();
    private ImageUriAdapter imageUriAdapter;
    private RecyclerView rvImg;
    private ImageView ivAddImg;






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
        rvImg = (RecyclerView) findViewById(R.id.rv_img);
        View view = getLayoutInflater().inflate(R.layout.layout_foot_select_img, (ViewGroup) rvImg.getParent(), false);
        ivAddImg = (ImageView) view.findViewById(R.id.iv_addImg);
        imageUriAdapter = new ImageUriAdapter(PostActivity.this,R.layout.item_input_img,u_list);
        imageUriAdapter.addFooterView(view);
        imageUriAdapter.addChildClickViewIds(R.id.iv_delete);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);//        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvImg.setLayoutManager(linearLayoutManager);
        rvImg.setAdapter(imageUriAdapter);

    }

    @Override
    public void initListener() {
        ivAddImg.setOnClickListener(view -> {
            selectImg();
        });
        imageUriAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.iv_delete){
                    u_list.remove(position);
                    fileList.remove(position);
                    imageUriAdapter.notifyDataSetChanged();
                }
            }
        });
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
                Log.e("Test","sendPost");
                String title = etTitle.getText().toString();
                String content = etContent.getText().toString();
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content) && title.length() <=16) {
                    tvPost.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    if (fileList.size()>0){
                        sendImgPost(title,content);
                    }else {
                        sendPost(title, content);
                    }

                }else if(TextUtils.isEmpty(title)||TextUtils.isEmpty(content)){
                    Toast.makeText(PostActivity.this,"信息不完整",Toast.LENGTH_SHORT).show();
                }else if (title.length() > 16){
                    Toast.makeText(PostActivity.this,"标题长度大于16",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public String setTitle() {
        return null;
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
        post.setUid(Config.getUser(PostActivity.this).getId());
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
                    setResult(SEND_POST);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(PostActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
            }

        });
        progressBar.setVisibility(View.GONE);
        tvPost.setEnabled(true);
    }

    private void sendImgPost(String title, String content){

        String userJson = SharedPreferenceUtil.getString(PostActivity.this, SharedPreferenceUtil.USERINFO, null);
        if (TextUtils.isEmpty(userJson)) {
            Toast.makeText(PostActivity.this, "获取人员信息失败", Toast.LENGTH_SHORT).show();
            return;
        }
        User user = GsonUtil.toObject(userJson, User.class);
        if (user == null){
            Log.e("Test","user 空");
            return;
        }
        Post post = new Post();
        post.setAvatar(user.getAvatar());
        post.setName(user.getName());
        post.setTitle(title);
        post.setUid(Config.user.getId());
        post.setContent(content);
        post.setImage(fileList.size());
        String postJson = GsonUtil.toJson(post);
        Map params = new HashMap<>();
        params.put("postJson",toRequestBody(postJson));
        HttpUtils.getRequest().addImagePost(params,filesToMultipartBodyParts(fileList)).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body().getCode() == 200){
                    setResult(SEND_POST);
                    finish();
                    Toast.makeText(PostActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(PostActivity.this,response.body().getMsg(),Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(PostActivity.this,t.toString(),Toast.LENGTH_SHORT).show();

            }
        });
        progressBar.setVisibility(View.GONE);
        tvPost.setEnabled(true);
    }
    private void selectImg(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");//无类型限制
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//多选参数
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,SELECT_IMG);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Test","requestCode=="+requestCode+"   resultCode"+resultCode);
        if (requestCode == SELECT_IMG && resultCode == Activity.RESULT_OK) {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri mUri = item.getUri();
                    String path = FileUtil.getPath(PostActivity.this,mUri);
                    File file = new File(path);
                    fileList.add(file);
                    u_list.add(mUri);
                }
                Log.e("Test","size"+u_list.size());
                imageUriAdapter.notifyDataSetChanged();
            }else {
                Uri uri = data.getData();
                String path = FileUtil.getPath(PostActivity.this,uri);
                File file = new File(path);
                fileList.add(file);
                u_list.add(uri);
                imageUriAdapter.notifyDataSetChanged();
            }
        }
    }
    public static List<MultipartBody.Part> filesToMultipartBodyParts(List<File> files) {
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (File file : files) {
            // TODO: 16-4-2  这里为了简单起见，没有判断file的类型
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("files", file.getName(), requestBody);
            parts.add(part);
        }
        return parts;
    }
    private RequestBody toRequestBody(String value) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), value);
        return requestBody;
    }
}