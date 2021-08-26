package com.kang.commentcomponent.utils;

import android.util.Log;


/**
 * @date: 2020/10/15
 * @author: BinKang
 * @description: 日志打印
 */
public class ZLog {

    public static final String TAG = "binkang";
    public static final boolean IS_LOG = true;

    public static void d(String msg) {
        if (IS_LOG) {
            Log.d(TAG, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (IS_LOG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String msg) {
        if (IS_LOG) {
            Log.i(TAG, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (IS_LOG) {
            Log.i(tag, msg);
        }
    }

    public static void e(String msg) {
        if (IS_LOG) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (IS_LOG) {
            Log.e(tag, msg);
        }
    }

}
