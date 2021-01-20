package com.example.forum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.forum.base.BaseToolbarActivity;
import com.example.forum.bean.BaseResponse;
import com.example.forum.bean.PointNum;
import com.example.forum.fragment.ForumFragment;
import com.example.forum.fragment.MessageFragment;
import com.example.forum.fragment.UserFragment;
import com.example.forum.http.HttpUtils;
import com.example.forum.ui.PostActivity;
import com.example.forum.view.DragFloatActionButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseToolbarActivity {

    public static final int SEND_POST = 1000;

    DragFloatActionButton fab;
    private ImageView ivForum;
    private ImageView ivMessage;
    private ImageView ivUser;
    private ViewPager viewPage;
    private ImageView ivRedPoint;
    private TextView tvNewsNum;
    private boolean hasNewTip = false;
    ForumFragment forumFragment;
    MessageFragment messageFragment;
    UserFragment userFragment;




    private List<Fragment> fragments = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity onCreate");

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        progressBar = findViewById(R.id.progress);
        fab = findViewById(R.id.view_fab);
        ivForum = (ImageView) findViewById(R.id.iv_forum);
        ivMessage = (ImageView) findViewById(R.id.iv_message);
        ivUser = (ImageView) findViewById(R.id.iv_user);
        viewPage = (ViewPager) findViewById(R.id.view_page);
        ivRedPoint = (ImageView) findViewById(R.id.iv_red_point);
        tvNewsNum = (TextView) findViewById(R.id.tv_news_num);
        setDisplay(false);
        viewPage.setOffscreenPageLimit(3);
        forumFragment = new ForumFragment();
        messageFragment = new MessageFragment();
        userFragment = new UserFragment();
        fragments.add(forumFragment);
        fragments.add(messageFragment);
        fragments.add(userFragment);



    }

    private void setDisplay(boolean isDisplay){
        if (isDisplay){
            ivRedPoint.setVisibility(View.VISIBLE);
            tvNewsNum.setVisibility(View.VISIBLE);
        }else {
            ivRedPoint.setVisibility(View.GONE);
            tvNewsNum.setVisibility(View.GONE);
        }
    }

    @Override
    public void initListener() {


        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,PostActivity.class);
            startActivityForResult(intent,SEND_POST);
        });
        ivForum.setOnClickListener(view -> {
            Glide.with(this).load(R.mipmap.forum_blue).into(ivForum);
            Glide.with(this).load(R.mipmap.message_black).into(ivMessage);
            Glide.with(this).load(R.mipmap.user_black).into(ivUser);
            viewPage.setCurrentItem(0);

        });
        ivMessage.setOnClickListener(view -> {
            Glide.with(this).load(R.mipmap.forum_black).into(ivForum);
            Glide.with(this).load(R.mipmap.message_blue).into(ivMessage);
            Glide.with(this).load(R.mipmap.user_black).into(ivUser);
            viewPage.setCurrentItem(1);
            if (hasNewTip){
                updateMessageReceive(Config.user.getId());
                dismissPointView();
            }

        });
        ivUser.setOnClickListener(view -> {
            Glide.with(this).load(R.mipmap.forum_black).into(ivForum);
            Glide.with(this).load(R.mipmap.message_black).into(ivMessage);
            Glide.with(this).load(R.mipmap.user_blue).into(ivUser);
            viewPage.setCurrentItem(2);
        });


        viewPage.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        viewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    title.setText("帖子");
                    Glide.with(MainActivity.this).load(R.mipmap.forum_blue).into(ivForum);
                    Glide.with(MainActivity.this).load(R.mipmap.message_black).into(ivMessage);
                    Glide.with(MainActivity.this).load(R.mipmap.user_black).into(ivUser);
                } else if (position == 1) {
                    title.setText("消息");
                    Glide.with(MainActivity.this).load(R.mipmap.forum_black).into(ivForum);
                    Glide.with(MainActivity.this).load(R.mipmap.message_blue).into(ivMessage);
                    Glide.with(MainActivity.this).load(R.mipmap.user_black).into(ivUser);
                } else if (position == 2) {
                    title.setText("用户中心");
                    Glide.with(MainActivity.this).load(R.mipmap.forum_black).into(ivForum);
                    Glide.with(MainActivity.this).load(R.mipmap.message_black).into(ivMessage);
                    Glide.with(MainActivity.this).load(R.mipmap.user_blue).into(ivUser);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private void setPointView(int size){
        hasNewTip = true;
        ivRedPoint.setVisibility(View.VISIBLE);
        tvNewsNum.setVisibility(View.VISIBLE);
        tvNewsNum.setText(size+"");
    }

    private void dismissPointView(){
        hasNewTip = false;
        ivRedPoint.setVisibility(View.GONE);
        tvNewsNum.setVisibility(View.GONE);
    }

    private void updateMessageReceive(int id){
        HttpUtils.getRequest().updateMessageReceive(id).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });

    }
    @Override
    public void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEND_POST && resultCode == SEND_POST){
            if (forumFragment == null) return;
            forumFragment.refresh();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(PointNum pointNum) {
        setPointView(pointNum.getNums());
    }
}