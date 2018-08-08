package com.ees.chain.utils;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Message;
import android.provider.AlarmClock;

import com.ees.chain.App;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KESION on 2018/2/27.
 */

public class ClockUtils {

    private static List<PackageInfo> allPackageInfos = new ArrayList<PackageInfo>();//系统安装所有软件
    private static List<PackageInfo> clockPackageInfos = new ArrayList<PackageInfo>();//系统时钟软件

    private static final int HANDLE_MESSAGE_KEY = 1001;
    private static Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case HANDLE_MESSAGE_KEY:
                        LogUtils.d("--app scan over--");
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private static boolean isSystemApplication(ApplicationInfo applicationInfo) {
        boolean isSystemApp = false;
        if (applicationInfo != null) {
            if ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                    || (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                isSystemApp = true;
            }
        }
        return isSystemApp;
    }


    private static boolean isClockApplication(String packageName) {
        boolean isClockApp = false;
        if (packageName != null && packageName.contains("clock") && !packageName.contains("widget")) {
            isClockApp = true;
        }
        return isClockApp;
    }

    public static void startScanInstalledApps() {
        new Thread(new ScanInstalledAppsRunnable()).start();
    }

    public static void startSystemClock() {
        if (clockPackageInfos == null || clockPackageInfos.size() == 0) {
            LogUtils.e("--启动系统闹钟失败1--");
            //调用默认启动方式
            Intent alarms = new Intent(AlarmClock.ACTION_SET_ALARM);
            App.getInstance().startActivity(alarms);
            return;
        }

        try {
            Intent startSysClockIntent = App.getInstance().getPackageManager().getLaunchIntentForPackage(clockPackageInfos.get(0).packageName);
            App.getInstance().startActivity(startSysClockIntent);
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
            LogUtils.e("--启动系统闹钟失败2--");
        }
    }

    private static class ScanInstalledAppsRunnable implements Runnable {
        @Override
        public void run() {
            allPackageInfos = App.getInstance().getPackageManager()
                    .getInstalledPackages(0);
            clockPackageInfos = new ArrayList<PackageInfo>();

            if (allPackageInfos == null || allPackageInfos.size() == 0) {
                return;
            }

            PackageInfo tempPackageInfo = null;
            for (int i = 0; i < allPackageInfos.size(); i++) {
                tempPackageInfo = allPackageInfos.get(i);
                if (tempPackageInfo != null) {

                    if (isSystemApplication(tempPackageInfo.applicationInfo) &&
                            isClockApplication(tempPackageInfo.packageName)) {
                        clockPackageInfos.add(tempPackageInfo);
                    }

                }
            }

            Message message = myHandler.obtainMessage();
            message.what = ClockUtils.HANDLE_MESSAGE_KEY;
            myHandler.sendMessage(message);
        }
    }

}
