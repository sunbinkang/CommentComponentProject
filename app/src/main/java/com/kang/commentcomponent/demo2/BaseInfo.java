package com.kang.commentcomponent.demo2;

/**
 * Created by BinKang on 2021/8/21.
 * Des :
 */
public class BaseInfo {

    private String resourceId;//id

    private String resourceType;//// "0" 股吧评论	"1" 发一发评论

    public BaseInfo(String resourceType, String resourceId) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}
