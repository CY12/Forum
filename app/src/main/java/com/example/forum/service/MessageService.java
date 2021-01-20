package com.example.forum.service;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.forum.bean.BaseResponse;
import com.example.forum.bean.Message;
import com.example.forum.bean.PointNum;
import com.example.forum.http.HttpUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageService extends Service {
    private static String TAG = "Test";
    private MessageBinder messageBinder = new MessageBinder();



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messageBinder;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, " onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, " onStartCommand");

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, " onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " onDestroy");
        super.onDestroy();
    }

    public void getMessage(int uid){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("Test","getMessage"+uid);
                getNewMessage(uid);
            }
        }).start();


    }
    private void getNewMessage(int uid){
        HttpUtils.getRequest().getNewMessage(uid).enqueue(new Callback<BaseResponse<List<PointNum>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<PointNum>>> call, Response<BaseResponse<List<PointNum>>> response) {
                if (response.code() == 200 && response.body().getData() != null){
                    if (messageCallBack != null){
                        messageCallBack.sendNewMessage(response.body().getData().get(0));
                        getMessage(uid);
                    }

                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<PointNum>>> call, Throwable t) {

            }
        });
    }

    public interface MessageCallBack {
        void sendNewMessage(PointNum pointNum);
    }
    MessageCallBack messageCallBack;

    public void setMessageCallBack(MessageCallBack messageCallBack){
        this.messageCallBack = messageCallBack;

    }

    public class MessageBinder extends Binder{
        public MessageService getMessageService(){
            return MessageService.this;
        }
    }
}
