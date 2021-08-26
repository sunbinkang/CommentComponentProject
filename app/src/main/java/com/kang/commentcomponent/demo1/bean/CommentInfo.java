package com.kang.commentcomponent.demo1.bean;

import java.io.Serializable;

/**
 * Created by BinKang on 2021/8/13.
 * Des :
 */
public class CommentInfo implements Serializable {

    /**
     * 评论id
     */
    private String id;

    /**
     * 所属一级评论ID（当评论为一级时为空）
     */
    private String parent_id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论者ID
     */
    private String creator_id;

    /**
     * 评论者名字
     */
    private String creator_name;

    /**
     * 创建时间，毫秒
     */
    private long created_time;

    /**
     * 子评论的数量
     */
    private int child_count;

    /**
     * 回复人
     */
//    private Source source;

    /**
     * 本地字段，标识type类型
     */
    private int comment_type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getCreator_name() {
        return creator_name;
    }

    public void setCreator_name(String creator_name) {
        this.creator_name = creator_name;
    }

    public long getCreated_time() {
        return created_time;
    }

    public void setCreated_time(long created_time) {
        this.created_time = created_time;
    }

    public int getChild_count() {
        return child_count;
    }

    public void setChild_count(int child_count) {
        this.child_count = child_count;
    }

    public int getComment_type() {
        return comment_type;
    }

    public void setComment_type(int comment_type) {
        this.comment_type = comment_type;
    }
}
