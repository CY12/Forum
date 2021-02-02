package com.example.forum.ui;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.forum.Config;
import com.example.forum.R;
import com.example.forum.base.BaseToolbarActivity;
import com.example.forum.bean.BaseResponse;
import com.example.forum.bean.User;
import com.example.forum.http.HttpUtils;
import com.example.forum.utils.FileUtil;
import com.example.forum.utils.GsonUtil;
import com.example.forum.utils.ImageUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserModifyActivity extends BaseToolbarActivity {
    public static final int SELECT_IMG = 400;
    private ImageView ivHead;
    private EditText etName;
    private EditText etSign;
    private TextView tvConfirm;
    private Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_modify;
    }

    @Override
    public String setTitle() {
        return "修改用户信息";
    }

    @Override
    public void initView() {
        ivHead = (ImageView) findViewById(R.id.iv_head);
        etName = (EditText) findViewById(R.id.et_name);
        etSign = (EditText) findViewById(R.id.et_sign);
        tvConfirm = (TextView) findViewById(R.id.tv_confirm);
        ImageUtil.displayRadius(this,ivHead, Config.getUser(UserModifyActivity.this).getAvatar(),10);
        etName.setHint(Config.getUser(UserModifyActivity.this).getName());
        etSign.setHint(Config.getUser(UserModifyActivity.this).getDescription());
    }

    @Override
    public void initData() {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void initListener() {
        ivHead.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");//无类型限制
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent,SELECT_IMG);
        });
        tvConfirm.setOnClickListener(view -> {
            if (uri == null){
                Log.e("Test","uri == null");
            }else {
                Log.e("Test","uri != null");
            }
            if (uri == null && TextUtils.isEmpty(etName.getText().toString()) && TextUtils.isEmpty(etSign.getText().toString())){
                Toast.makeText(UserModifyActivity.this,"未作修改",Toast.LENGTH_SHORT).show();
                return;
            }
            if (uri == null){
                updateUser();
            }else {
                updateImageUser();
            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateImageUser(){

        User mUser = new User();
        if (TextUtils.isEmpty(etName.getText().toString()) && TextUtils.isEmpty(etSign.getText().toString())) {
            mUser.setAvatar(Config.getUser(UserModifyActivity.this).getAvatar());
        }else {
            mUser.setName(etName.getText().toString());
            mUser.setDescription(etSign.getText().toString());
        }

        mUser.setId(Config.getUser(UserModifyActivity.this).getId());
        String userJson = GsonUtil.toJson(mUser);
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), userJson);
        Map params = new HashMap<>();
        params.put("userJson",requestBody);
        String path = FileUtil.getPath(UserModifyActivity.this,uri);
        File file = new File(path);
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody1);
        HttpUtils.getRequest().updateImageUser(params,part).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body().getCode() == 200 ){
                    Toast.makeText(UserModifyActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(UserModifyActivity.this,"修改失败 "+response.body().getMsg(),Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(UserModifyActivity.this,"修改失败"+t.toString(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void updateUser(){
        User mUser = new User();
        mUser.setName(etName.getText().toString());
        mUser.setDescription(etSign.getText().toString());
        mUser.setId(Config.getUser(UserModifyActivity.this).getId());
        HttpUtils.getRequest().updateUser(mUser).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body().getCode() == 200){
                    Toast.makeText(UserModifyActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(UserModifyActivity.this,"修改失败 "+response.body().getMsg(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(UserModifyActivity.this,"修改失败"+t.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMG && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            ImageUtil.displayRadius(this,ivHead,uri,10);
        }
    }
}