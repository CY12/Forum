package com.example.forum.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.example.forum.Config;
import com.example.forum.ui.DetailActivity;
import com.example.forum.R;
import com.example.forum.adapter.ForumAdapter;
import com.example.forum.bean.BaseResponse;
import com.example.forum.bean.Post;
import com.example.forum.bean.User;
import com.example.forum.http.HttpUtils;
import com.example.forum.utils.GsonUtil;
import com.example.forum.utils.SharedPreferenceUtil;
import com.google.gson.GsonBuilder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.TELEPHONY_SERVICE;

public class ForumFragment extends Fragment {

    private LinearLayout llSearch;
    private RecyclerView rvForum;
    ProgressBar progressBar;
    ForumAdapter forumAdapter;

    SmartRefreshLayout smartRefreshLayout;
    private int start = 0;
    boolean isLoadMore = false;
    List<Post> postList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forum, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         initView(view);
    }
    private void initView(View view){
        llSearch = (LinearLayout) view.findViewById(R.id.ll_search);
        rvForum = (RecyclerView) view.findViewById(R.id.rv_forum);
        smartRefreshLayout = view.findViewById(R.id.smartRefreshLayout);
        progressBar = view.findViewById(R.id.progress);
        forumAdapter = new ForumAdapter(R.layout.item_post, postList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvForum.setLayoutManager(linearLayoutManager);
        rvForum.setAdapter(forumAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getUser();
        }
        forumAdapter.addChildClickViewIds(R.id.tv_content,R.id.tv_title,R.id.tv_star,R.id.tv_comment);
        forumAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                Log.d("Test","forumAdapter position"+position+"  view"+view);
                if (view.getId() == R.id.tv_content) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(DetailActivity.POSITION,position);
                    startActivity(intent);
                    view(postList.get(position).getId());
                }
            }
        });

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);
                isLoadMore = false;
                start = 0;
                getPostList();

            }
        });

        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);
                isLoadMore = true;
                getPostList();


            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getUser() {
        String userInfo = SharedPreferenceUtil.getString(getActivity(), SharedPreferenceUtil.USERINFO, null);
        if ( userInfo == null) {
            registerUser();
        }else {
            progressBar.setVisibility(View.VISIBLE);
            User user = GsonUtil.toObject(userInfo,User.class);
            if ( user == null) return;
            getUserInfo(user.getImei());

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void registerUser(){
        TelephonyManager TelephonyMgr = (TelephonyManager) getActivity().getSystemService(TELEPHONY_SERVICE);
        String szImei = TelephonyMgr.getDeviceId();
        User user = new User();
        user.setImei(szImei);
        String name = getRandomWord() + getRandomWord() + getRandomWord();
        user.setName(name);
        user.setDescription("该用户没有介绍");
        user.setAvatar("1");
        GsonBuilder gb = new GsonBuilder();
        gb.disableHtmlEscaping();
        String json = gb.create().toJson(user);
        Log.d("Test","方法外面线程名称==="+Thread.currentThread().getName());
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        progressBar.setVisibility(View.VISIBLE);
        HttpUtils.getRequest().register(requestBody).enqueue(new Callback<BaseResponse>() {

            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.code() == 200) {
                    Log.d("Test","方法外面线程名称==="+Thread.currentThread().getName());

                    String ujson = GsonUtil.toJson(user);
                    SharedPreferenceUtil.putString(getActivity(),SharedPreferenceUtil.USERINFO,ujson);
                    getPostList();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getUserInfo(String imei){
        Log.d("Test","getUserInfo");
        if (TextUtils.isEmpty(imei)) return;
        HttpUtils.getRequest().getUser(imei).enqueue(new Callback<BaseResponse<List<User>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<User>>> call, Response<BaseResponse<List<User>>> response) {
                if (response.code() == 200 && response.body().getData().size() > 0){
                    User user = response.body().getData().get(0);
                    Config.user = user;
                    String userJson = GsonUtil.toJson(user);
                    SharedPreferenceUtil.putString(getActivity(),SharedPreferenceUtil.USERINFO,userJson);
                    getPostList();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<User>>> call, Throwable t) {

            }
        });
    }

    private void view(int id){
        HttpUtils.getRequest().view(id).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });
    }

    private void getPostList() {
        Log.d("Test","getPostList");
        HttpUtils.getRequest().getPostList(start, 20).enqueue(new Callback<BaseResponse<List<Post>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Post>>> call, Response<BaseResponse<List<Post>>> response) {
                if (response.code() == 200 && response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {
                    if (!isLoadMore){
                        postList.clear();
                        postList.addAll(response.body().getData());
                        String json = GsonUtil.toJson(postList);
                        SharedPreferenceUtil.putString(getActivity(),SharedPreferenceUtil.POSTS,json);
                    }else {
                        postList.addAll(response.body().getData());
                        String json = GsonUtil.toJson(postList);
                        SharedPreferenceUtil.putString(getActivity(),SharedPreferenceUtil.POSTS,json);
                    }
                    forumAdapter.notifyDataSetChanged();
                    start += 20;
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Post>>> call, Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        progressBar.setVisibility(View.GONE);
    }


    public void refresh(){
        smartRefreshLayout.autoRefresh();
    }
    private String getRandomWord() {
        String str = "";
        int heightPos;
        int lowPos;
        Random rd = new Random();
        heightPos = 176 + Math.abs(rd.nextInt(39));
        lowPos = 161 + Math.abs(rd.nextInt(93));
        byte[] bt = new byte[2];
        bt[0] = Integer.valueOf(heightPos).byteValue();
        bt[1] = Integer.valueOf(lowPos).byteValue();
        try {
            str = new String(bt, "GBK");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }
}
