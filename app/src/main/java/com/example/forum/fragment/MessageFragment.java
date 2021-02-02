package com.example.forum.fragment;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.example.forum.Config;
import com.example.forum.R;
import com.example.forum.adapter.MessageAdapter;
import com.example.forum.bean.BaseResponse;
import com.example.forum.bean.Message;
import com.example.forum.bean.PointNum;
import com.example.forum.bean.User;
import com.example.forum.http.HttpUtils;
import com.example.forum.service.MessageService;
import com.example.forum.ui.MessageDetailActivity;
import com.example.forum.utils.GsonUtil;
import com.example.forum.utils.SharedPreferenceUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageFragment extends Fragment {

    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView rvMessage;
    private List<Message> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private int start;
    private int size = 20;
    boolean isLoadMore = false;
    private RelativeLayout emptyView;

    private MessageService messageService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            messageService = ((MessageService.MessageBinder)iBinder).getMessageService();

            messageService.setMessageCallBack(new MessageService.MessageCallBack() {
                @Override
                public void sendNewMessage(PointNum pointNum) {
                    Log.d("Test","sendNewMessage");
                    if (pointNum.getNums() > 0 && messageAdapter != null){
                        Log.d("Test","MessageFragment 获得新消息"+pointNum.getNums());
                        EventBus.getDefault().post(pointNum);
                        isLoadMore = false;
                        start = 0;
                        getMessage();

                    }
                }
            });
            if (Config.user == null){
                String  userInfo = SharedPreferenceUtil.getString(getContext(),SharedPreferenceUtil.USERINFO,"");
                if (userInfo.length() == 0){
                    Log.d("Test","SharedPreferenceUtil userInfo 0");
                    return;
                }
                User user = GsonUtil.toObject(userInfo,User.class);
                Config.user = user;
            }else {
                Log.d("Test",Config.user.toString());
            }
            messageService.getMessage(Config.user.getId());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("Test","Fail Connection" );
        }
    };








    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initData() {
        Intent intent=new Intent(getContext(),MessageService.class);
        getActivity().startService(intent);
        getActivity().bindService(intent,serviceConnection, Service.BIND_AUTO_CREATE);
        getMessage();


    }

    private void initView(View view) {
        smartRefreshLayout = (SmartRefreshLayout) view.findViewById(R.id.smartRefreshLayout);
        rvMessage = (RecyclerView) view.findViewById(R.id.rv_message);
        emptyView = (RelativeLayout) view.findViewById(R.id.emptyView);
        messageAdapter = new MessageAdapter(getActivity(),R.layout.item_message,messageList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvMessage.setLayoutManager(linearLayoutManager);
        rvMessage.setAdapter(messageAdapter);
        messageAdapter.addChildClickViewIds(R.id.rv_message,R.id.ll_toReply);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);
                isLoadMore = false;
                start = 0;
                getMessage();

            }
        });

        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);
                isLoadMore = true;
                getMessage();
            }
        });

        messageAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.rv_message){
                    MessageDetailActivity.startActivity(getActivity(),messageList.get(position).getCommentId(),messageList.get(position).getPostId(),messageList.get(position).getPostTitle());
                    updateMessageRead(messageList.get(position).getId());
                    messageList.get(position).setIsRead(1);
                    messageAdapter.notifyItemChanged(position);
                }
            }
        });
    }

    private void getMessage(){
        if (Config.user == null){
            String  userInfo = SharedPreferenceUtil.getString(getContext(),SharedPreferenceUtil.USERINFO,"");
            if (userInfo.length() == 0) return;
            User user = GsonUtil.toObject(userInfo,User.class);
            Config.user = user;
        }
        HttpUtils.getRequest().getMessageList(Config.user.getId(),start,size).enqueue(new Callback<BaseResponse<List<Message>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Message>>> call, Response<BaseResponse<List<Message>>> response) {
                if (response.code() == 200 && response.body() != null && response.body().getData().size() > 0){
                    if (!isLoadMore){
                        messageList.clear();
                    }
                    messageList.addAll(response.body().getData());
                    messageAdapter.notifyDataSetChanged();
                    start = messageList.size();
                }
                if (messageList.size() == 0){
                    emptyView.setVisibility(View.VISIBLE);
                }else {
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Message>>> call, Throwable t) {
                Toast.makeText(getContext(),t.toString(),Toast.LENGTH_SHORT).show();
                if (messageList.size() == 0){
                    emptyView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void updateMessageRead(int id){
        HttpUtils.getRequest().updateMessageRead(id).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
