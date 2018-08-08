package com.ees.chain.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareUtils {

    public static final String TYPE_WEBCHAT = "webchat";
    public static final String TYPE_WEBCHAT_CIRCLE = "webchatCircle";
    public static final String TYPE_QQ = "qq";
    public static final String TYPE_QZONE = "qzone";

    private static final String[] WEBCHAT = new String[] {"com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI"};
    private static final String[] WEBCHAT_CIRCLE = new String[] {"com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI"};
    private static final String[] QQ = new String[] {"com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"};
    private static final String[] QZONE = new String[] {"com.qzone", "com.qzonex.module.operation.ui.QZonePublishMoodActivity"};

    public static final String BPATH = Environment.getExternalStorageDirectory().getPath() + "/DCIM/";//保存到图库目录下
    /*
      * 压缩图片
      * */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 10, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 520) {  //循环判断如果压缩后图片是否大于400kb,大于继续压缩（这里可以设置大些）
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static boolean isFileExist(String fileName) {
        String path = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = BPATH;//保存到sd根目录下
        }
        File f = new File(path, fileName);
        if (f.exists()) {
            return true;
        }
        return false;
    }

    /*
    * 把file转化为bitmap
    * */
    public static Bitmap file2Bitmap(String fileName) {
        String path = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = BPATH;//保存到sd根目录下
        }
        File f = new File(path, fileName);
        if (f.exists()) {
            path += fileName;
            return BitmapFactory.decodeFile(path, getBitmapOption(1)); //将图片的长和宽缩小味原来的1/2
        }
        return null;
    }

    private static BitmapFactory.Options getBitmapOption(int inSampleSize){
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    /*
    * 把bitmap转化为file
    * */
    public static File bitMap2File(String fileName, Bitmap bitmap) {
        String path = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = BPATH;//保存到sd根目录下
        }
        //        File f = new File(path, System.currentTimeMillis() + ".jpg");
        File f = new File(path, fileName);
        if (f.exists()) {
            return f;
        } else {
            f.getParentFile().mkdir();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return f;
        }
    }

    public static void shareBitmap(Context context, String type, String fileName, Bitmap bitmap, String content) {
        File file = ShareUtils.bitMap2File(fileName, bitmap);
        if (file != null && file.exists() && file.isFile()) {
            //由文件得到uri
            Uri imageUri = Uri.fromFile(file);
            Intent shareIntent = new Intent();
            //发送图片到朋友圈
            ComponentName comp = null;
            if (TYPE_WEBCHAT.equals(type)) {
                comp = new ComponentName(WEBCHAT[0], WEBCHAT[1]);
            } else if (TYPE_WEBCHAT_CIRCLE.equals(type)) {
                comp = new ComponentName(WEBCHAT_CIRCLE[0], WEBCHAT_CIRCLE[1]);
            } else if (TYPE_QQ.equals(type)) {
                comp = new ComponentName(QQ[0], QQ[1]);
            } else if (TYPE_QZONE.equals(type)) {
                comp = new ComponentName(QZONE[0], QZONE[1]);
            } else {
                comp = new ComponentName(WEBCHAT[0], WEBCHAT[1]);
            }
            shareIntent.setComponent(comp);
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, content);//  分享文本
            shareIntent.setType("image/*");
            context.startActivity(Intent.createChooser(shareIntent, "分享图片"));
        }
    }
}
