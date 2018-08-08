package com.ees.chain.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.receiver.AlarmNotificationReceiver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * Created by KESION on 2017/12/6.
 */

public class Utils {
    public static final String DIRECTORY;
    public static final ExecutorService SERVICE;

    static {
        SERVICE = Executors.newCachedThreadPool();
        String DCIM = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).toString();
        DIRECTORY = DCIM + "/Camera";
    }


    public static boolean isStringEmpty(String str) {
        if (null == str || "".equals(str)) {
            return true;
        }
        return false;
    }

    /**
     * 验证用户名只包含数字
     *
     * @param account
     * @return
     */
    public static boolean checkAccountMark(String account) {
        if (account != null && account.length() != 11) {
            return false;
        }
        String all = "^[0-9]+$";
        Pattern pattern = Pattern.compile(all);
        return pattern.matches(all, account);
    }

    /**
     * 验证转账内容只包含数字和"."
     *
     * @param account
     * @return
     */
    public static boolean checkNumberMark(String account) {
        if (account != null && account.length() >= 11) {
            return false;
        }
        String all = "^[0-9\\.]+$";
        Pattern pattern = Pattern.compile(all);
        return pattern.matches(all, account);
    }

    /**
     * 我国公民的身份证号码特点如下
     * 1.长度18位
     * 2.第1-17号只能为数字
     * 3.第18位只能是数字或者x
     * 4.第7-14位表示特有人的年月日信息
     * 请实现身份证号码合法性判断的函数，函数返回值：
     * 1.如果身份证合法返回true
     * 2.如果身份证长度不合法返回false
     * 3.如果第1-17位含有非数字的字符返回2
     * 4.如果第18位不是数字也不是x返回3
     * 5.如果身份证号的出生日期非法返回4
     *
     * @since 0.0.1
     */
    public static boolean validatorID(String id) {
        String str ="[0-9]{17}[0-9xX]{1}";
        Pattern pattern = Pattern.compile(str);
        return pattern.matcher(id).matches()? true : false;
    }

    public static double doubleFormat(double dot) {
        String format = "######0.0000";
        if (dot == 0.0 || dot == 0) return 0;
        DecimalFormat df = new DecimalFormat(format);
        String dotStr = df.format(dot);
        return Double.valueOf(dotStr);
    }

    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public static String getChinaTimeYYYYMMDD() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        sf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sf.format(new Date());//设置日期格式
    }

    public static String getChinaTimeHH() {
        SimpleDateFormat sf = new SimpleDateFormat("HH");
        sf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sf.format(new Date());//设置日期格式
    }

    private static final String EKEY = "comeeschain";

    public static String encode(String enc) {
        EncodeUtils des = null;//自定义密钥
        try {
            des = new EncodeUtils(EKEY);
            return des.encrypt(enc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "4b5988a9baa2e1e5";//0
    }

    public static String decode(String dec) {
        EncodeUtils des = null;//自定义密钥
        try {
            des = new EncodeUtils(EKEY);
            return des.decrypt(dec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    public static double getFileSizeK(File file) {
        long size = 0;
        try {
            size = getFileSize(file);
            double msize = (double)size/1024.0;
            LogUtils.d("ksize K: " + msize);
            return msize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double getFileSizeM(File file) {
        long size = 0;
        try {
            size = getFileSize(file);
            double msize = (double)size/1048576.0;
            LogUtils.d("msize M: " + msize);
            return msize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 转换文件大小
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize="0B";
        if(fileS==0){
            return wrongSize;
        }
        if (fileS < 1024){
            fileSizeString = df.format((double) fileS) + "B";
        }
        else if (fileS < 1048576){
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        }
        else if (fileS < 1073741824){
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        }
        else{
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 获取指定文件大小
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception
    {
        long size = 0;
        if (file.exists()){
            size = file.length();
        }
        else{
            file.createNewFile();
            LogUtils.e("文件不存在!");
        }
        return size;
    }

    public static ArrayList<String> getNowSevenDate() {
        final int len = 6;
        ArrayList<String> list = new ArrayList<String>();
        for (int i=len;i>=0;i--) {
            String date = getPastDate(i);
            if (!StringUtils.isBlank(date)) {
                list.add(date);
            }
        }
        return list;
    }

    public static ArrayList<String> getPastSevenDate() {
        final int len = 7;
        ArrayList<String> list = new ArrayList<String>();
        for (int i=len;i>0;i--) {
            String date = getPastDate(i);
            if (!StringUtils.isBlank(date)) {
                list.add(date);
            }
        }
        return list;
    }

    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("dd");
        String result = format.format(today);
        return result;
    }

    /**
     * 获取给定时间的前一天的日期
     *
     * @return
     */
    public static String getPreDate(long time) {
        if (time==0) return "";
        Date today = new Date(time-24*60*60*1000);
        SimpleDateFormat format = new SimpleDateFormat("dd");
        String result = format.format(today);
        return result;
    }

    public static String getDate(long time) {
        if (time==0) return "";
        Date today = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("dd");
        String result = format.format(today);
        return result;
    }

    public static void registerAlarmNotification(Context context) {
        if (App.isLogEnable) {
            Toast.makeText(context, "registerAlarmNotification", Toast.LENGTH_SHORT).show();
        }

        int INTERVAL = 1000 * 60 * 60 * 24;// 24h
        AlarmManager alarmService = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, AlarmNotificationReceiver.HOUR);
        calendar.set(Calendar.MINUTE, AlarmNotificationReceiver.MINUTE);
        calendar.set(Calendar.SECOND, AlarmNotificationReceiver.SECOND);
        calendar.set(Calendar.MILLISECOND, 0);
        Intent alarmIntent = new Intent(context, AlarmNotificationReceiver.class);
        alarmIntent.setAction(AlarmNotificationReceiver.ACTION);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmService.cancel(broadcast);
        alarmService.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL , broadcast);
    }

    private static String hexStr =  "0123456789ABCDEF";  //全局
    public static String binaryToHexString(byte[] bytes){
        String result = "";
        String hex = "";
        for(int i=0;i<bytes.length;i++){
            //字节高4位
            hex = String.valueOf(hexStr.charAt((bytes[i]&0xF0)>>4));
            //字节低4位
            hex += String.valueOf(hexStr.charAt(bytes[i]&0x0F));
            result +=hex;
        }
        return result;
    }

    public static String getCurrentVersionName(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            String version = packInfo.versionName;
            return "V"+version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "V1.0.0";
    }

    public static String decimalFormat(double d) {
        DecimalFormat decimalFormat=new DecimalFormat();
        decimalFormat.applyPattern("#.#######");
        return decimalFormat.format(d);
    }

    /*
       *    get image from network
       *    @param [String]imageURL
       *    @return [BitMap]image
    */
    public static Bitmap returnBitMap(String url){
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /** 从assets 文件夹中读取图片 */
    public static Drawable loadImageFromAsserts(final Context ctx, String fileName) {
        try {
            InputStream is = ctx.getResources().getAssets().open(fileName);
            return Drawable.createFromStream(is, null);
        } catch (IOException e) {
            if (e != null) {
                e.printStackTrace();
            }
        } catch (OutOfMemoryError e) {
            if (e != null) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
