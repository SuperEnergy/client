package com.ees.chain.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;


/**
 * Created by KESION on 2017/12/19.
 */

public class BatteryHelp {

    public static long getBatteryCapacity(Context context) {
        long cap = (long) getBatteryCapacity1(context);
        if (cap == 0) {
            cap = (long) getBatteryCapacity2(context);
        }
        return cap;
    }

    /**
     * 获取电池容量
     * @param context
     * @return
     */
    private static double getBatteryCapacity2(Context context) {
       
        return batteryCapacity;
    }
}
