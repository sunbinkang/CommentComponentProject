package com.kang.commentcomponent.demo2.bean;

import com.kang.commentcomponent.demo2.bean.CommentInfo;

import java.util.List;

/**
 * Created by BinKang on 2021/8/19.
 * Des :
 */
public class CommentResponse {

    /**
     * total : 0
     * data : [{"type":"0","commentContent":"","msgId":11,"replyCommentId":0,"feedMsgReplyCommentUserVo":{"replyCommentUserId":0,"replyCommentUserName":"","sImgUrl":""},"userId":4,"userName":"13300010002","userImgUrl":"","likeCount":0,"replayTime":"2021-08-19 10:33:49","commentId":3,"isAuthor":0,"commentCount":6}]
     */

    private int total;
    private List<CommentInfo> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<CommentInfo> getData() {
        return data;
    }

    public void setData(List<CommentInfo> data) {
        this.data = data;
    }
}
