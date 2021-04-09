package com.example.forum.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.forum.Config;
import com.example.forum.R;
import com.example.forum.adapter.CollectionAdapter;
import com.example.forum.base.BaseToolbarActivity;
import com.example.forum.bean.BaseResponse;
import com.example.forum.bean.Collections;
import com.example.forum.bean.Post;
import com.example.forum.http.HttpUtils;
import com.example.forum.utils.DialogUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionActivity extends BaseToolbarActivity {
    public static final String TYPE = "type";

    public static int TYPE_COLLECTION = 111;
    public static int TYPE_FORUM = 112;

    private List<Post> postList = new ArrayList<>();
    private RecyclerView rvCollection;
    private CollectionAdapter collectionAdapter;
    private int type = 0;
    String title = "";


    public static void startActivity(int type, Context context){
        Intent intent = new Intent(context,CollectionActivity.class);
        intent.putExtra(TYPE,type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collection;
    }

    @Override
    public String setTitle() {
        return title;
    }

    @Override
    public void initView() {
        rvCollection = (RecyclerView) findViewById(R.id.rv_collection);


    }

    @Override
    public void initData() {
        if (getIntent() != null && getIntent().getExtras() != null){
            type = getIntent().getExtras().getInt(TYPE);
            if (type == TYPE_COLLECTION){
                title = "我的收藏";
                getCollections();

            }else {
                title = "我的帖子";
                getForum();
            }
        }
        collectionAdapter = new CollectionAdapter(type,CollectionActivity.this,R.layout.item_collection,postList);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(CollectionActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvCollection.setLayoutManager(linearLayoutManager);
        rvCollection.setAdapter(collectionAdapter);


    }

    @Override
    public void initListener() {
        collectionAdapter.addChildClickViewIds(R.id.iv_delete);
        collectionAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.iv_delete){
                    if (type == TYPE_FORUM){
                        DialogUtil.showDialog(CollectionActivity.this, "是否删除此帖子", new DialogUtil.OnClickCallBackListener() {
                            @Override
                            public void onClickCallBack(Bundle data) {
                                deleteForum(postList.get(position).getId(),position);
                            }
                        });
                    }
                }
            }
        });
        collectionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Log.e("Test","onItemClick");
                DetailActivity.startActivity(CollectionActivity.this,postList.get(position).getId());
            }

        });
    }

    @Override
    public String getTag() {
        return "CollectionActivity";
    }

    private void getForum(){
        HttpUtils.getRequest().getPostByUser(Config.getUser(CollectionActivity.this).getId()).enqueue(new Callback<BaseResponse<List<Post>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Post>>> call, Response<BaseResponse<List<Post>>> response) {
                if (response.body() != null && response.body().getCode() == 200 && response.body().getData() !=null){
                    if (response.body().getData().size() > 0){
                        postList.clear();
                        postList.addAll(response.body().getData());
                        collectionAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(CollectionActivity.this,"您还未发布帖子",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(CollectionActivity.this,response.body().getCode()+" "+response.body().getMsg(),Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Post>>> call, Throwable t) {
                Toast.makeText(CollectionActivity.this,t.toString(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void deleteForum(int id,int position){
        HttpUtils.getRequest().deletePost(id).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body() != null && response.body().getCode() == 200){
                    Toast.makeText(CollectionActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                    postList.remove(position);
                    collectionAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(CollectionActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });
    }

    private void getCollections(){
        int uid = Config.getUser(CollectionActivity.this).getId();
        if (uid == 0) return;
        HttpUtils.getRequest().getCollections(uid).enqueue(new Callback<BaseResponse<List<Collections>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Collections>>> call, Response<BaseResponse<List<Collections>>> response) {
                if (response.body().getCode() == 200 && response.body().getData() != null ){
                    if (response.body().getData().size() > 0){
                        Log.e("Test","SIZE"+response.body().getData().size());
                        postList.clear();
                        for (int i=0;i<response.body().getData().size();i++){
                            if (response.body().getData().get(i).getPost()!= null){
                                postList.add(response.body().getData().get(i).getPost());
                            }

                        }
                        collectionAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(CollectionActivity.this,"您还未收藏",Toast.LENGTH_SHORT).show();

                    }

                }else {
                    Toast.makeText(CollectionActivity.this,response.body().getCode()+" "+response.body().getMsg(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Collections>>> call, Throwable t) {
                Toast.makeText(CollectionActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}