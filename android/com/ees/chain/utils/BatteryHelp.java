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
    private static double getBatteryCapacity1(Context context) {
        Object mPowerProfile_ = null;

        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        double batteryCapacity = 0;
        try {
            batteryCapacity = (Double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return batteryCapacity;
    }

    /**
     * 获取电池容量
     * @param context
     * @return
     */
    private static double getBatteryCapacity2(Context context) {
        Object mPowerProfile_ = null;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        double batteryCapacity = 0;
        try {
            batteryCapacity = (Double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile_);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return batteryCapacity;
    }
}
