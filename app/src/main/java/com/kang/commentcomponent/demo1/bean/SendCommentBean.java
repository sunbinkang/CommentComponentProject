package com.kang.commentcomponent.demo1.bean;

/**
 * Created by BinKang on 2021/8/19.
 * Des :
 */
public class SendCommentBean {


    /**
     * type : "0"
     * content : 911216666212888
     * resourceId : "000001"
     * replyCommentId : 0
     * rootCommentId : 0
     */

    private String type;// 0 股吧评论	1 发一发评论
    private String resourceId;// 股票代码, 仅股吧有
    private String content;// 评论内容 //[300]
    private int replyCommentId;// 被评论的原评论 ID , 如果为0 ，默认为对帖子的评论，即一级评论//long
    private int rootCommentId;// 评论的根评论，如果为0，为一级评论
    private String comment_type;//本地字段：标识为几级评论
    private String hint;//输入框提示字段

    public SendCommentBean(String type, String resourceId, int replyCommentId, int rootCommentId, String hint) {
        this.type = type;
        this.resourceId = resourceId;
        this.replyCommentId = replyCommentId;
        this.rootCommentId = rootCommentId;
        this.hint = hint;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getComment_type() {
        return comment_type;
    }

    public void setComment_type(String comment_type) {
        this.comment_type = comment_type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}
