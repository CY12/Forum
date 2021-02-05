package com.example.forum.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.forum.Config;
import com.example.forum.adapter.ImageAdapter;
import com.example.forum.ui.DetailActivity;
import com.example.forum.R;
import com.example.forum.adapter.ForumAdapter;
import com.example.forum.bean.BaseResponse;
import com.example.forum.bean.Post;
import com.example.forum.bean.User;
import com.example.forum.http.HttpUtils;
import com.example.forum.utils.GsonUtil;
import com.example.forum.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
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
    public List<Post> postList = new ArrayList<>();

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
        forumAdapter = new ForumAdapter(getActivity(),R.layout.item_post, postList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvForum.setLayoutManager(linearLayoutManager);
        rvForum.setAdapter(forumAdapter);

            getUser();

        rvForum.setItemViewCacheSize(20);
//        View view1 = getLayoutInflater().inflate(R.layout.layout_empty_view, (ViewGroup) rvForum.getParent(), false);
//
//        forumAdapter.setEmptyView(view1);
//        forumAdapter.addChildClickViewIds(R.id.rl_post);
//        forumAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
//            @Override
//            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
//                Log.d("Test","forumAdapter position"+position+"  view"+view);
//                if (view.getId() == R.id.rl_post) {
//
//                    Gson gson = new Gson();
//                    String postJson = gson.toJson(postList.get(position));
//                    DetailActivity.startActivity(getActivity(),postJson,postList.get(position).getId());
//                    postList.get(position).setViews(postList.get(position).getViews()+1);
//                    forumAdapter.notifyItemChanged(position);
//                    view(postList.get(position).getId());
//                }
//            }
//        });
        forumAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Gson gson = new Gson();
                String postJson = gson.toJson(postList.get(position));
                DetailActivity.startActivity(getActivity(),postJson,postList.get(position).getId());
                postList.get(position).setViews(postList.get(position).getViews()+1);
                forumAdapter.notifyItemChanged(position);
                view(postList.get(position).getId());
            }
        });


        forumAdapter.setDiffCallback(new DiffUtil.ItemCallback<Post>() {
            @Override
            public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
                boolean isTheSame = oldItem.getId() == newItem.getId();
                return isTheSame;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
                boolean isTheSame = oldItem.getId() == newItem.getId();
                return isTheSame;
            }
        });

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(400);
                isLoadMore = false;
                start = 0;
                getPostList();

            }
        });

        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(400);
                isLoadMore = true;
                getPostList();


            }
        });
    }

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


    private void registerUser(){
//        TelephonyManager TelephonyMgr = (TelephonyManager) getActivity().getSystemService(TELEPHONY_SERVICE);
//        String szImei = TelephonyMgr.getDeviceId();
        String szImei = getIMEI(getContext());
        User user = new User();
        user.setImei(szImei);
        String name = getRandomWord() + getRandomWord() + getRandomWord();
        user.setName(name);
        user.setDescription("该用户没有介绍");
        user.setAvatar("http://121.196.167.157:9090/image/head_default.png");
        GsonBuilder gb = new GsonBuilder();

        gb.disableHtmlEscaping();
        String json = gb.create().toJson(user);

        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        progressBar.setVisibility(View.VISIBLE);
        HttpUtils.getRequest().register(requestBody).enqueue(new Callback<BaseResponse<User>>() {

            @Override
            public void onResponse(Call<BaseResponse<User>> call, Response<BaseResponse<User>> response) {
                if (response.body().getCode() == 200 && response.body().getData() != null) {
                    Log.e("Test","方法外面线程名称==="+Thread.currentThread().getName());
                    Config.user = response.body().getData();
                    String ujson = GsonUtil.toJson(response.body().getData());
                    SharedPreferenceUtil.putString(getActivity(),SharedPreferenceUtil.USERINFO,ujson);
                    getPostList();
                    String u = SharedPreferenceUtil.getString(getActivity(),SharedPreferenceUtil.USERINFO,"");
                    if (TextUtils.isEmpty(u)){
                        Log.e("Test","error u == null");
                    }else {
                        User mU = GsonUtil.toObject(u,User.class);
                        Log.e("Test",mU.toString());
                    }

                }else {
                    getUserInfo(szImei);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<User>> call, Throwable t) {
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
                    //forumAdapter.setList(postList);
                    //forumAdapter.setDiffNewData(postList);
                    start += 20;
                }else if (response.code() == 200 && response.body() != null && response.body().getData() != null && response.body().getData().size() == 0){
                    if (!isLoadMore){
                        postList.clear();
                        forumAdapter.notifyDataSetChanged();
                    }

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
        if (smartRefreshLayout != null){
            smartRefreshLayout.autoRefresh();
        }

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
    /**
     * 获取手机IMEI号((International Mobile Equipment Identity,国际移动身份识别码)
     */
    public static String getIMEI(Context context) {
        String deviceId = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.System.getString(
                    context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }else {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
             deviceId = telephonyManager.getDeviceId();
        }

        return  deviceId;
    }

}
