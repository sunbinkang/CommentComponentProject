package com.kang.commentcomponent.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by BinKang on 2021/8/26.
 * Des :
 */
public class NetUtils {
    public static final int NETTYPE_UNKNOWN = -1;
    public static long netUpdateTimeMs = -1L;
    public static boolean registerNetworkReceiver = false;
    private static final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (!this.isInitialStickyBroadcast()) {
                if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                    NetUtils.netUpdateTimeMs = System.currentTimeMillis();
                }
            }
        }
    };

    public NetUtils() {
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        if (context == null) {
            return null;
        } else {
            try {
                if (!registerNetworkReceiver) {
                    synchronized (networkReceiver) {
                        if (!registerNetworkReceiver) {
                            registerNetworkReceiver = true;
                            context.getApplicationContext().registerReceiver(networkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
                        }
                    }
                }

                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
                return connectivityManager.getActiveNetworkInfo();
            } catch (Throwable var4) {
                return null;
            }
        }
    }

    public static int getNetType(Context context) {
        try {
            NetworkInfo info = getNetworkInfo(context);
            return info != null && info.isAvailable() ? info.getType() : -1;
        } catch (Throwable var2) {
            return -1;
        }
    }

    public static String getNetExtraInfo(Context context) {
        try {
            NetworkInfo info = getNetworkInfo(context);
            return info != null && info.isAvailable() ? info.getExtraInfo() : null;
        } catch (Throwable var2) {
            return null;
        }
    }

    public static boolean isNetAvailable(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isConnected();
    }

    public static void release(Context context) {
        try {
            synchronized (networkReceiver) {
                if (registerNetworkReceiver && context != null) {
                    registerNetworkReceiver = false;
                    context.getApplicationContext().unregisterReceiver(networkReceiver);
                }
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }
}
