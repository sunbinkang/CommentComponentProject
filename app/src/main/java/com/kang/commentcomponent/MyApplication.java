package com.kang.commentcomponent;

import android.app.Application;

import com.kang.commentcomponent.utils.ToastUtils;
import com.sun.network.base.NetworkApi;

/**
 * Created by BinKang on 2021/8/26.
 * Des :
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NetworkApi.init(new NetworkRequestInfo(this));
        ToastUtils.getInstance().init(this);
    }
}
