package com.kang.commentcomponent;

import android.app.Application;

import com.sun.network.base.INetworkRequiredInfo;

/**
 * Date: 2020/8/9
 * Author: SunBinKang
 * Description: 请求需要的http头信息
 */
public class NetworkRequestInfo implements INetworkRequiredInfo {

    private Application mApplication;

    public NetworkRequestInfo(Application application) {
        this.mApplication = application;
    }

    @Override
    public String getAppVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    @Override
    public String getAppVersionCode() {
        return String.valueOf(BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    public Application getApplicationContext() {
        return mApplication;
    }
}
