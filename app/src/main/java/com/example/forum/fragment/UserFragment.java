package com.example.forum.fragment;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.forum.Config;
import com.example.forum.R;
import com.example.forum.service.MessageService;
import com.example.forum.utils.SharedPreferenceUtil;

public class UserFragment extends Fragment {
    private ImageView ivSetting;
    private boolean flag = false;




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
    }

    private void initData() {

    }
    
}
