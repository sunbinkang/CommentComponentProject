package com.sun.network.beans;


/**
 * Created by BinKang on 2021/8/19.
 * Des :
 */
public class BaseResponse<T> {

    /**
     * code : 0
     * msg : 请求处理成功
     * detail :
     * head : {}
     * body : {}
     */

    private String code;
    private String msg;
    private String detail;
    private HeadBean head;
    private T body;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public HeadBean getHead() {
        return head;
    }

    public void setHead(HeadBean head) {
        this.head = head;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public static class HeadBean {
    }

}
