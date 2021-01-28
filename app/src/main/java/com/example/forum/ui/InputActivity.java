package com.example.forum.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.example.forum.Config;
import com.example.forum.R;
import com.example.forum.adapter.ImageUriAdapter;
import com.example.forum.base.BaseToolbarActivity;
import com.example.forum.bean.BaseResponse;
import com.example.forum.bean.Comment;
import com.example.forum.bean.Message;
import com.example.forum.bean.Reply;
import com.example.forum.http.HttpUtils;
import com.example.forum.utils.FileUtil;
import com.example.forum.utils.GsonUtil;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
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

public class InputActivity extends BaseToolbarActivity {


    private final static int SELECT_IMG = 154;
    public final static int INPUT_IMG = 255;
    public static String TYPE = "type";
    public static String DATA_JSON = "dataJson";
    public static String URI_JSON = "uriJson";
    private static String INPUT_CONTENT = "inputContent";
    private static String TITLE = "title";

    public static int TYPE_REPLY = 4;
    public static int TYPE_COMMENT = 3;

    private LinearLayout llContent;
    private EditText etContent;
    private RecyclerView rvImg;
    private ImageView ivBack;
    private TextView tvPost;
    private String content;

    private List<Uri> u_list = new ArrayList<>();
    private List<File> fileList = new ArrayList<>();
    private ImageUriAdapter imageUriAdapter;
    private ImageView ivAddImg;
    private Reply reply;
    private Comment comment;
    private int type = 0;
    private String titleText;


    public static void startActivity(Context context, int type,String replyJson,String title) {
        Intent intent = new Intent(context, InputActivity.class);
        intent.putExtra(TYPE, type);
        intent.putExtra(TITLE,title);
        intent.putExtra(DATA_JSON, replyJson);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_input;
    }

    @Override
    public void initView() {
        llContent = (LinearLayout) findViewById(R.id.ll_content);
        etContent = (EditText) findViewById(R.id.et_content);
        rvImg = (RecyclerView) findViewById(R.id.rv_img);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvPost = (TextView) findViewById(R.id.tv_post);

        View view = getLayoutInflater().inflate(R.layout.layout_foot_select_img, (ViewGroup) rvImg.getParent(), false);
        ivAddImg = (ImageView) view.findViewById(R.id.iv_addImg);
        imageUriAdapter = new ImageUriAdapter(InputActivity.this, R.layout.item_input_img, u_list);
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
                if (view.getId() == R.id.iv_delete) {
                    u_list.remove(position);
                    imageUriAdapter.notifyDataSetChanged();
                }
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == TYPE_REPLY){
                    addImageReply(etContent.getText().toString());
                }else if (type == TYPE_COMMENT){
                    addImageComment(etContent.getText().toString());
                }

            }
        });
    }

    @Override
    public void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
             titleText = getIntent().getStringExtra(TITLE);
             type = getIntent().getIntExtra(TYPE,0);
            if (type == 0) return;
            if (type == TYPE_REPLY){
                String replyJson = getIntent().getExtras().getString(DATA_JSON);
                if (!TextUtils.isEmpty(replyJson)){
                    reply = GsonUtil.toObject(replyJson,Reply.class);
                    if (!TextUtils.isEmpty(reply.getContent())){
                        etContent.setText(reply.getContent());
                    }
                }
            }else if(type == TYPE_COMMENT){
                String commentJson = getIntent().getExtras().getString(DATA_JSON);
                if (!TextUtils.isEmpty(commentJson)){
                    comment = GsonUtil.toObject(commentJson,Comment.class);
                    if (!TextUtils.isEmpty(comment.getContent())){
                        etContent.setText(comment.getContent());
                    }
                }
            }
        }
    }

    @Override
    public String setTitle() {
        return "回复";
    }

    private void selectImg() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");//无类型限制
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//多选参数
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, SELECT_IMG);
    }

    private void addImageComment(String content) {
        if (comment == null) return;
        comment.setContent(content);

        if (fileList.size() > 0) {
            comment.setImage(fileList.size());
            String json = GsonUtil.toJson(comment);
            Map params = new HashMap<>();
            params.put("type", toRequestBody(TYPE_COMMENT+""));
            params.put("reply",toRequestBody(json));
            HttpUtils.getRequest().addImageReply(params,filesToMultipartBodyParts(fileList)).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.code() == 200) {
                        Toast.makeText(InputActivity.this, "回复成功", Toast.LENGTH_SHORT).show();
//                        sendMessage(comment.getPostId(),response.body().getData().,content,);
//                        Intent intent = new Intent();
//                        intent.putExtra(INPUT_CONTENT, content);
//                        String uriJson = GsonUtil.toJson(u_list);
//                        intent.putExtra(URI_JSON, uriJson);
//                        setResult(INPUT_IMG, intent);
//                        finish();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    Toast.makeText(InputActivity.this, "回复失败", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            HttpUtils.getRequest().addComment(comment).enqueue(new Callback<BaseResponse<Comment>>() {
                @Override
                public void onResponse(Call<BaseResponse<Comment>> call, Response<BaseResponse<Comment>> response) {

                }

                @Override
                public void onFailure(Call<BaseResponse<Comment>> call, Throwable t) {

                }
            });

        }
    }
    private void addImageReply(String content) {
        reply.setContent(content);
        String json = GsonUtil.toJson(reply);
        if (fileList.size() > 0){
            Map params = new HashMap<>();
            params.put("type", toRequestBody(TYPE_REPLY+""));
            params.put("reply",toRequestBody(content));
            HttpUtils.getRequest().addImageReply(params,filesToMultipartBodyParts(fileList)).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.code() == 200){
                        Intent intent = new Intent();
                        intent.putExtra(INPUT_CONTENT,content);
                        String uriJson = GsonUtil.toJson(u_list);
                        intent.putExtra(URI_JSON,uriJson);
                        setResult(INPUT_IMG,intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    Toast.makeText(InputActivity.this,"回复失败",Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            HttpUtils.getRequest().addReply(reply).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.code() == 200){
                        Intent intent = new Intent();
                        intent.putExtra(INPUT_CONTENT,content);
                        setResult(INPUT_IMG,intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    Toast.makeText(InputActivity.this,"回复失败",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public static List<MultipartBody.Part> filesToMultipartBodyParts(List<File> files) {
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (File file : files) {
            // TODO: 16-4-2  这里为了简单起见，没有判断file的类型
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            parts.add(part);
        }
        return parts;
    }
    private RequestBody toRequestBody(String value) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), value);
        return requestBody;
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Test", "requestCode==" + requestCode + "   resultCode" + resultCode);
        if (requestCode == SELECT_IMG && resultCode == Activity.RESULT_OK) {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri mUri = item.getUri();
                    String path = FileUtil.getPath(InputActivity.this,mUri);
                    File file = new File(path);
                    fileList.add(file);
                    u_list.add(mUri);
                }
                Log.e("Test", "size" + u_list.size());
            } else {
                Uri uri = data.getData();
                String path = FileUtil.getPath(InputActivity.this,uri);
                File file = new File(path);
                fileList.add(file);
                u_list.add(uri);
            }
            imageUriAdapter.notifyDataSetChanged();
        }
    }
    private void sendMessage(int postId,int receiverId,int commentId,String context,int replyId,String replyContent){
        Message message = new Message();
        message.setUid(Config.user.getId());
        message.setName(Config.user.getName());
        message.setAvatar(Config.user.getAvatar());
        message.setCommentId(commentId);// 评论
        message.setMessage(context);
        message.setPostId(postId);
        message.setReceiverUid(receiverId);
        message.setPostTitle(titleText);
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

}