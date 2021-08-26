package com.kang.commentcomponent.demo2;


import com.kang.commentcomponent.demo2.bean.CommentInfo;
import com.kang.commentcomponent.demo2.bean.CommentResponse;
import com.sun.network.beans.BaseResponse;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by BinKang on 2021/8/19.
 * Des :评论服务的接口
 */
public interface CommentApiInterface {

    /**
     * 发表评论
     * @param body
     * @return
     */
    @POST("/feed/comment")
    Observable<BaseResponse<CommentInfo>> publishComment(@Body RequestBody body);

    /**
     * 查询评论
     * @param body
     * @return
     */
    @POST("/feed/commentlist")
    Observable<BaseResponse<CommentResponse>> getComment(@Body RequestBody body);

    /**
     * 查询评论的数量
     * @param body
     * @return
     */
    @POST("/feed/commentnum")
    Observable<BaseResponse<CommentResponse>> getCommentNum(@Body RequestBody body);

    /**
     * 点赞、取消点赞
     * @param body
     * @return
     */
    @POST("/feed/like")
    Observable<BaseResponse<CommentResponse>> prise(@Body RequestBody body);

    /**
     * 删除评论
     * @param body
     * @return
     */
    @POST("/feed/commentdel")
    Observable<BaseResponse<CommentResponse>> commentDelete(@Body RequestBody body);

}
