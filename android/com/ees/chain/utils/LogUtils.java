package com.ees.chain.utils;

import android.util.Log;

import com.ees.chain.App;

/**
 * Created by KESION on 2017/12/6.
 */

public class LogUtils {

    private static String TAG = "EES";

    public static void d(String msg) {
        if (App.isLogEnable) {
            Log.d(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (App.isLogEnable) {
            Log.e(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (App.isLogEnable) {
            Log.i(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (App.isLogEnable) {
            Log.w(TAG, msg);
        }
    }

}
