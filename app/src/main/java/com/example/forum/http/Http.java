package com.example.forum.http;

import com.example.forum.bean.Comment;
import com.example.forum.bean.Message;
import com.example.forum.bean.PointNum;
import com.example.forum.bean.Post;
import com.example.forum.bean.BaseResponse;
import com.example.forum.bean.Reply;
import com.example.forum.bean.ReplyDetail;
import com.example.forum.bean.User;


import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Http {
    @FormUrlEncoded
    @POST("getPostList")
    Call<BaseResponse<List<Post>>> getPostList(@Field("start")int start,
                                                 @Field("size")int size);

    @POST("register")
    Call<BaseResponse> register (@Body RequestBody body);

    @FormUrlEncoded
    @POST("getCommentList")
    Call<BaseResponse<List<Comment>>> getCommentList(@Field("postId")int postId,
                                 @Field("start")int start,
                                 @Field("size")int size);

    @POST("addComment")
    Call<BaseResponse<Comment>> addComment(@Body RequestBody body);

    @POST("addPost")
    Call<BaseResponse> addPost(@Body RequestBody body);

    @FormUrlEncoded
    @POST("getUserByImei")
    Call<BaseResponse<List<User>>> getUser(@Field("imei") String imei);

    @POST("addMessage")
    Call<BaseResponse> addMessage(@Body RequestBody body);

    @FormUrlEncoded
    @POST("getNewMessage")
    Call<BaseResponse<List<PointNum>>> getNewMessage(@Field("uid")int uid);

    @FormUrlEncoded
    @POST("updateMessageReceive")
    Call<BaseResponse> updateMessageReceive(@Field("uid")int uid);

    @FormUrlEncoded
    @POST("updateMessageRead")
    Call<BaseResponse> updateMessageRead(@Field("id")int id);

    @FormUrlEncoded
    @POST("view")
    Call<BaseResponse> view(@Field("id")int id);

    @FormUrlEncoded
    @POST("comment")
    Call<BaseResponse> comment(@Field("id")int id,@Field("count")int count);

    @FormUrlEncoded
    @POST("reply")
    Call<BaseResponse> reply(@Field("id")int id,@Field("count")int count);

    @FormUrlEncoded
    @POST("star")
    Call<BaseResponse> star(@Field("id")int id,@Field("count")int count);

    @FormUrlEncoded
    @POST("getReplyList")
    Call<BaseResponse<List<Reply>>> getReplyList(@Field("commentId")int commentId);

    @Headers({"Content_Type:application/json", "charset:UTF-8"})
    @POST("addReply")
    Call<BaseResponse> addReply(@Body Reply reply);

    @FormUrlEncoded
    @POST("getMessageList")
    Call<BaseResponse<List<Message>>> getMessageList(@Field("uid")int uid,
                                                     @Field("start")int start,
                                                     @Field("size")int size);


    @FormUrlEncoded
    @POST("getReplyDetail")
    Call<BaseResponse<ReplyDetail>> getReplyDetail(@Field("commentId")int commentId);




}
