package com.example.forum.http;

import com.example.forum.bean.Collections;
import com.example.forum.bean.Comment;
import com.example.forum.bean.Id;
import com.example.forum.bean.Message;
import com.example.forum.bean.PointNum;
import com.example.forum.bean.Post;
import com.example.forum.bean.BaseResponse;
import com.example.forum.bean.Reply;
import com.example.forum.bean.ReplyDetail;
import com.example.forum.bean.User;


import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface Http {




    @Headers({"Content_Type:application/json", "charset:UTF-8"})
    @POST("updateUser")
    Call<BaseResponse> updateUser(@Body User user);



    /**
     * 获取帖子列表
     *
     * @param start
     * @param size
     * @return
     */
    @FormUrlEncoded
    @POST("getPostList")
    Call<BaseResponse<List<Post>>> getPostList(@Field("start") int start,
                                               @Field("size") int size);

    /**
     * 获取某个帖子
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST("getPost")
    Call<BaseResponse<Post>> getPost(@Field("id") int id);


    @FormUrlEncoded
    @POST("getPostByUser")
    Call<BaseResponse<List<Post>>> getPostByUser(@Field("uid")int uid);

    @FormUrlEncoded
    @POST("deletePost")
    Call<BaseResponse> deletePost(@Field(("id"))int id);

    /**
     * 注册用户
     *
     * @param body
     * @return
     */
    @POST("register")
    Call<BaseResponse<User>> register(@Body RequestBody body);

    /**
     * 获取评论列表
     *
     * @param postId
     * @param start
     * @param size
     * @return
     */
    @FormUrlEncoded
    @POST("getCommentList")
    Call<BaseResponse<List<Comment>>> getCommentList(@Field("postId") int postId,
                                                     @Field("start") int start,
                                                     @Field("size") int size);

    /**
     * 增加一条评论
     *
     * @param comment
     * @return
     */
    @Headers({"Content_Type:application/json", "charset:UTF-8"})
    @POST("addComment")
    Call<BaseResponse<Comment>> addComment(@Body Comment comment);

    /**
     * 增加一个帖子
     *
     * @param body
     * @return
     */
    @POST("addPost")
    Call<BaseResponse> addPost(@Body RequestBody body);

    /**
     * 通过imei 获取用户
     *
     * @param imei
     * @return
     */
    @FormUrlEncoded
    @POST("getUserByImei")
    Call<BaseResponse<List<User>>> getUser(@Field("imei") String imei);

    /**
     * 增加一条message
     *
     * @param body
     * @return
     */
    @POST("addMessage")
    Call<BaseResponse> addMessage(@Body RequestBody body);

    /**
     * 获取新消息 未收到的消息
     *
     * @param uid
     * @return
     */
    @FormUrlEncoded
    @POST("getNewMessage")
    Call<BaseResponse<List<PointNum>>> getNewMessage(@Field("uid") int uid);

    /**
     * 更新帖子 已收到 红点展示
     *
     * @param uid
     * @return
     */
    @FormUrlEncoded
    @POST("updateMessageReceive")
    Call<BaseResponse> updateMessageReceive(@Field("uid") int uid);

    /**
     * 更新帖子 已读
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST("updateMessageRead")
    Call<BaseResponse> updateMessageRead(@Field("id") int id);

    /**
     * 帖子  浏览
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST("view")
    Call<BaseResponse> view(@Field("id") int id);

    /**
     * 帖子 评论
     *
     * @param id
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST("comment")
    Call<BaseResponse> comment(@Field("id") int id, @Field("count") int count);

    /**
     * 回复的数量   帖子详情中会展示有n个回复
     *
     * @param id
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST("reply")
    Call<BaseResponse> reply(@Field("id") int id, @Field("count") int count);

    /**
     * 帖子 赞
     *
     * @param id
     * @param count
     * @return
     */
    @FormUrlEncoded
    @POST("star")
    Call<BaseResponse> star(@Field("id") int id, @Field("count") int count);

    /**
     * 获取回复列表
     *
     * @param commentId
     * @return
     */
    @FormUrlEncoded
    @POST("getReplyList")
    Call<BaseResponse<List<Reply>>> getReplyList(@Field("commentId") int commentId);

    /**
     * 添加一条回复
     *
     * @param reply
     * @return
     */
    @Headers({"Content_Type:application/json", "charset:UTF-8"})
    @POST("addReply")
    Call<BaseResponse> addReply(@Body Reply reply);

    /**
     * 获取消息列表
     *
     * @param uid
     * @param start
     * @param size
     * @return
     */
    @FormUrlEncoded
    @POST("getMessageList")
    Call<BaseResponse<List<Message>>> getMessageList(@Field("uid") int uid,
                                                     @Field("start") int start,
                                                     @Field("size") int size);

    /**
     * 获取回复详情 包括comment 和 reply
     *
     * @param commentId
     * @return
     */
    @FormUrlEncoded
    @POST("getReplyDetail")
    Call<BaseResponse<ReplyDetail>> getReplyDetail(@Field("commentId") int commentId);

    @Multipart
    @POST("addImageReply")
    Call<BaseResponse<Id>> addImageReply(@PartMap Map<String, RequestBody> params,
                                         @Part List<MultipartBody.Part> multipartFiles

////                                     @Field("type")int type,
////                                     @Field("reply")String reply
                                 );

    @Multipart
    @POST("addImagePost")
    Call<BaseResponse> addImagePost(@PartMap Map<String, RequestBody> params,
                                    @Part List<MultipartBody.Part> multipartFiles
                                    );
    @Multipart
    @POST("updateImageUser")
    Call<BaseResponse> updateImageUser(@PartMap Map<String ,RequestBody> params,
                                        @Part MultipartBody.Part file
    );
    @FormUrlEncoded
    @POST("collection/getCollections")
    Call<BaseResponse<List<Collections>>> getCollections(@Field("uid")int uid);

    @FormUrlEncoded
    @POST("collection/addCollection")
    Call<BaseResponse> addCollection(@Field("uid")int uid,
                                     @Field("postId")int postId);

    @FormUrlEncoded
    @POST("collection/cancelCollection")
    Call<BaseResponse> cancelCollection(@Field(("uid"))int uid,
                                        @Field("postId")int postId);
}
