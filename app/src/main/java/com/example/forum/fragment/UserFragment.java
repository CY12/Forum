package com.example.forum.fragment;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.forum.Config;
import com.example.forum.R;
import com.example.forum.bean.BaseResponse;
import com.example.forum.bean.User;
import com.example.forum.http.HttpUtils;
import com.example.forum.service.MessageService;
import com.example.forum.ui.UserModifyActivity;
import com.example.forum.utils.ImageUtil;
import com.example.forum.utils.SharedPreferenceUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {
    private ImageView ivSetting;
    private boolean flag = false;
    private RelativeLayout rlTitle;
    private ImageView ivAvatar;
    private TextView tvName;
    private TextView tvDescription;








    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view) {
        rlTitle = (RelativeLayout) view.findViewById(R.id.rl_title);
        ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvDescription = (TextView) view.findViewById(R.id.tv_description);
        ivSetting = (ImageView) view.findViewById(R.id.iv_setting);
        ivSetting.setOnClickListener(view1 -> {
            if (flag){
                Config.IP = "121.196.167.157:9090";

            }else {
                Config.IP = "172.16.18.79:9091";
            }
            SharedPreferenceUtil.putString(getActivity(),SharedPreferenceUtil.CONFIG_IP,Config.IP);
            Toast.makeText(getActivity(),Config.IP,Toast.LENGTH_SHORT).show();
            Log.e("Test","Config.IP"+Config.IP);
            flag = !flag;
        });
        rlTitle.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), UserModifyActivity.class);
            startActivity(intent);
        });
    }

    private void initData() {
        getUser();
    }
    private void getUser(){
        String imei = Config.getUser(getActivity()).getImei();
        if (TextUtils.isEmpty(imei)) {
            Log.e("Test","imei == null");
            return;
        }


        HttpUtils.getRequest().getUser(imei).enqueue(new Callback<BaseResponse<List<User>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<User>>> call, Response<BaseResponse<List<User>>> response) {
                Log.e("Test","getUser");
                if (response.body().getCode() == 200 && response.body().getData() != null){
                    ImageUtil.displayRadius(getActivity(),ivAvatar,response.body().getData().get(0).getAvatar(),10);
                    tvName.setText(response.body().getData().get(0).getName());
                    tvDescription.setText(response.body().getData().get(0).getDescription());
                }else {
                    Toast.makeText(getActivity(),"用户信息获取失败 "+response.body().getMsg(),Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<User>>> call, Throwable t) {
                Toast.makeText(getActivity(),"用户信息获取失败 "+t.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    
}
