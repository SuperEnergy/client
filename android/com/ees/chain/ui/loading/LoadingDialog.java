package com.ees.chain.ui.loading;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * kesion 2017/12/12
 */

public class LoadingDialog {

    public static Dialog createLoadingDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v
                .findViewById(R.id.dialog_loading_view);// 加载布局
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView)v.findViewById(R.id.progressBar1);
        Uri uri = Uri.parse("res://"+context.getPackageName()+"/" + R.drawable.dialog_loading1);
        DraweeController mDraweeController = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                //设置uri,加载本地的gif资源
                .setUri(uri)
                .build();
        simpleDraweeView.setController(mDraweeController);

        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        tipTextView.setText(R.string.loading);// 设置加载信息
        tipTextView.setVisibility(View.GONE);

        Dialog loadingDialog = new Dialog(context, R.style.DialogLoadingStyle);// 创建自定义样式dialog
        try {
            loadingDialog.setCancelable(true); // 是否可以按“返回键”消失
            loadingDialog.setCanceledOnTouchOutside(false); // 点击加载框以外的区域
            loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
            /**
             *将显示Dialog的方法封装在这里面
             */
            Window window = loadingDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setGravity(Gravity.CENTER);
            window.setAttributes(lp);
            window.setWindowAnimations(R.style.PopWindowAnimStyle);
            loadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return loadingDialog;
    }

    public static Dialog createLoadingDialog(Context context, String content) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v
                .findViewById(R.id.dialog_loading_view);// 加载布局
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView)v.findViewById(R.id.progressBar1);
        Uri uri = Uri.parse("res://"+context.getPackageName()+"/" + R.drawable.dialog_loading1);
        DraweeController mDraweeController = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                //设置uri,加载本地的gif资源
                .setUri(uri)
                .build();
        simpleDraweeView.setController(mDraweeController);

        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        if (StringUtils.isBlank(content)) {
            tipTextView.setVisibility(View.GONE);
        } else {
            tipTextView.setVisibility(View.VISIBLE);
            tipTextView.setText(content);// 设置加载信息
        }

        Dialog loadingDialog = new Dialog(context, R.style.DialogLoadingStyle);// 创建自定义样式dialog
        try {
            loadingDialog.setCancelable(true); // 是否可以按“返回键”消失
            loadingDialog.setCanceledOnTouchOutside(false); // 点击加载框以外的区域
            loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
            /**
             *将显示Dialog的方法封装在这里面
             */
            Window window = loadingDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setGravity(Gravity.CENTER);
            window.setAttributes(lp);
            window.setWindowAnimations(R.style.PopWindowAnimStyle);
            loadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return loadingDialog;
    }

    /**
     * 关闭dialog
     *
     * http://blog.csdn.net/qq_21376985
     *
     */
    public static void closeDialog(final Dialog dialog) {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}