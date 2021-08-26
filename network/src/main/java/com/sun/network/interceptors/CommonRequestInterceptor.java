package com.sun.network.interceptors;

import com.sun.network.base.INetworkRequiredInfo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Date: 2020/8/9
 * Author: SunBinKang
 * Description:
 */
public class CommonRequestInterceptor implements Interceptor {

    private INetworkRequiredInfo requiredInfo;

    public CommonRequestInterceptor(INetworkRequiredInfo iNetworkRequiredInfo) {
        this.requiredInfo = iNetworkRequiredInfo;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request().newBuilder();
        //添加请求头通用参数
        builder.addHeader("os","android");
        builder.addHeader("appVersion",this.requiredInfo.getAppVersionCode());
        return chain.proceed(builder.build());
    }
}
