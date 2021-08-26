package com.sun.network.base;

import android.app.Application;

/**
 * Date: 2020/8/9
 * Author: SunBinKang
 * Description:
 */
public interface INetworkRequiredInfo {

    String getAppVersionName();

    String getAppVersionCode();

    boolean isDebug();

    Application getApplicationContext();

}
