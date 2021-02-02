package com.example.forum.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.forum.R;
import com.example.forum.bean.Post;
import com.example.forum.utils.ImageUtil;
import com.hitomi.tilibrary.style.index.NumberIndexIndicator;
import com.hitomi.tilibrary.style.progress.ProgressBarIndicator;
import com.hitomi.tilibrary.transfer.TransferConfig;
import com.hitomi.tilibrary.transfer.Transferee;
import com.vansz.glideimageloader.GlideImageLoader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ForumAdapter extends BaseQuickAdapter<Post, BaseViewHolder> {
    private Context context;
   
    public ForumAdapter(Context context,int layoutResId, @Nullable List<Post> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Post post) {
        Log.d("Test",""+post.toString());
        ImageView avatar = baseViewHolder.getView(R.id.iv_avatar);
        ImageUtil.displayRadius(context,avatar,post.getAvatar(),5);
        baseViewHolder.setText(R.id.tv_user,post.getName());
        baseViewHolder.setText(R.id.tv_updatetime,post.getUpdatetime().substring(5,16)+"更新");
        baseViewHolder.setText(R.id.tv_title,post.getTitle());
        baseViewHolder.setText(R.id.tv_content,post.getContent());
        baseViewHolder.setText(R.id.tv_star,post.getStarts()+"");
        baseViewHolder.setText(R.id.tv_comment,post.getComments()+"");
        baseViewHolder.setText(R.id.tv_view,post.getViews()+"");

        if (post.getUrlList().size() > 0) {
            baseViewHolder.getView(R.id.rv_img).setVisibility(View.VISIBLE);
            RecyclerView rvImg = baseViewHolder.getView(R.id.rv_img);
            if (post.getUrlList().size() == 1){
                Log.e("Test","url size == 1");
                ImageAdapter imageAdapter = new ImageAdapter(context,R.layout.item_img,post.getUrlList());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                rvImg.setLayoutManager(linearLayoutManager);
                rvImg.setAdapter(imageAdapter);
                imageAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                        showPreview(post.getUrlList(),rvImg,R.id.iv_img,position);
                    }
                });
            }else {
                Log.e("Test","url size == "+post.getUrlList().size());
                List<String> urls = new ArrayList<>();
                if (post.getUrlList().size() > 3){
                    for (int i =0;i<3;i++){
                        urls.add(post.getUrlList().get(i));
                    }
                }else {
                    urls.addAll(post.getUrlList());
                }
                ImageAdapter imageAdapter = new ImageAdapter(context, R.layout.item_img_horizontal, urls,post.getUrlList().size() - 3);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                rvImg.setLayoutManager(staggeredGridLayoutManager);
                rvImg.setAdapter(imageAdapter);
                imageAdapter.addChildClickViewIds(R.id.iv_img);
                imageAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                        showPreview(post.getUrlList(),rvImg,R.id.iv_img,position);
                    }
                });

            }


        }else {
            baseViewHolder.getView(R.id.rv_img).setVisibility(View.GONE);
        }
    }


    private void showPreview(List<String> urlList,RecyclerView recyclerView,int id,int position){
        Transferee transferee = Transferee.getDefault(context);
        TransferConfig recyclerTransConfig = TransferConfig.build()
                .setSourceUrlList(urlList)
                .setProgressIndicator(new ProgressBarIndicator())
                .setIndexIndicator(new NumberIndexIndicator())
                .setImageLoader(GlideImageLoader.with(context.getApplicationContext()))
                .enableHideThumb(false)
                .bindRecyclerView(recyclerView, id);
        recyclerTransConfig.setNowThumbnailIndex(position);
        transferee.apply(recyclerTransConfig).show();
    }


}
