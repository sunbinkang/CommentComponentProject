package com.kang.commentcomponent.demo2.bean;

/**
 * Created by BinKang on 2021/8/20.
 * Des :
 */
public class CommentInfo {

    /**
     * type : 0
     * commentContent :
     * msgId : 11
     * resourceId : "002945"
     * replyCommentId : 0
     * rootCommentId : 0
     * feedMsgReplyCommentUserVo : {"replyCommentUserId":0,"replyCommentUserName":"","sImgUrl":""}
     * userId : 4
     * userName : 13300010002
     * userImgUrl :
     * likeCount : 0
     * replayTime : 2021-08-19 10:33:49
     * commentId : 3
     * isAuthor : 0
     * commentCount : 6
     * like：0
     */

    private String type;// 0 股吧评论	1 SNS评论
    private String commentContent;// 评论内容
    private int msgId;// 被评论的帖子 ID
    private String resourceId;//评论的资源id
    private int replyCommentId;// 被评论的原评论 ID
    private int rootCommentId;// 评论的根评论  // 一级评论的根评论为0，二三级评论的根评论为一级评论的id
    private FeedMsgReplyCommentUserVoBean feedMsgReplyCommentUserVo;// 被回复人的相关信息，可以扩展
    private int userId;// 评论者 ID
    private String userName;// 评论者姓名
    private String userImgUrl;// 评论者头像
    private int likeCount;// 点赞数
    private int like;// 当前用户是否点赞过该评论 0：未点赞， 1：点过赞
    private String replyTime;// 评论时间
    private int commentId;// 评论 Id
    private int isAuthor;// 是否是作者 针对二三级评论，即2、3级评论是否与1级评论为同一个user// 1是，默认为0，不是
    private int commentCount;// 一级评论下二、三级评论总数,其余情况为默认值0
    /**
     * 本地字段，标识type类型
     */
    private int comment_type;

    public int getComment_type() {
        return comment_type;
    }

    public void setComment_type(int comment_type) {
        this.comment_type = comment_type;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public int getReplyCommentId() {
        return replyCommentId;
    }

    public void setReplyCommentId(int replyCommentId) {
        this.replyCommentId = replyCommentId;
    }

    public int getRootCommentId() {
        return rootCommentId;
    }

    public void setRootCommentId(int rootCommentId) {
        this.rootCommentId = rootCommentId;
    }

    public FeedMsgReplyCommentUserVoBean getFeedMsgReplyCommentUserVo() {
        return feedMsgReplyCommentUserVo;
    }

    public void setFeedMsgReplyCommentUserVo(FeedMsgReplyCommentUserVoBean feedMsgReplyCommentUserVo) {
        this.feedMsgReplyCommentUserVo = feedMsgReplyCommentUserVo;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }

    public int getLikeCount() {
//        return likeCount == 0 ? 1 : likeCount;
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getIsAuthor() {
        return isAuthor;
    }

    public void setIsAuthor(int isAuthor) {
        this.isAuthor = isAuthor;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public static class FeedMsgReplyCommentUserVoBean {
        /**
         * replyCommentUserId : 0
         * replyCommentUserName :
         * sImgUrl :
         */

        private int replyCommentUserId;
        private String replyCommentUserName;
        private String sImgUrl;

        public int getReplyCommentUserId() {
            return replyCommentUserId;
        }

        public void setReplyCommentUserId(int replyCommentUserId) {
            this.replyCommentUserId = replyCommentUserId;
        }

        public String getReplyCommentUserName() {
            return replyCommentUserName;
        }

        public void setReplyCommentUserName(String replyCommentUserName) {
            this.replyCommentUserName = replyCommentUserName;
        }

        public String getSImgUrl() {
            return sImgUrl;
        }

        public void setSImgUrl(String sImgUrl) {
            this.sImgUrl = sImgUrl;
        }
    }

}
