package com.ees.chain.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.utils.ShareUtils;
import com.ees.chain.utils.Utils;
import com.ees.chain.utils.ZXingUtils;

import java.io.File;
import java.util.Random;

public class ShareActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();
    public static final String SHARE_URL = "shareUrl";
    public static final String SHARE_CONTENT = "";

    private View mLLLyouout;
    private ImageView mShareImage;
    private View mWeixin;
    private View mWeixinCircel;
    private View mQQ;
    private View mQQCircel;

    private Bitmap mBitmap;
    private String shareUrl = "http://www.eeschain.com";
    private String shareContent = "【EES超级能源】节能环保倡导者，首创能源补偿模式。已登陆全球最大交易平台FCOIN、全球最大场外交易平台OTCBTC、CEO交易平台(排名前15)、DigiFinex交易所(排名前30)；免费挖掘出您的专属数字资产，每天躺着赚钱。复制此消息，分享给小伙伴一起挖取吧!";
    private String bgImageUrl = "http://eeschain.com/site/img/share.jpg";
    private String fileName = "share.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setWindowStatusBarColor(this, R.color.white);

        setContentView(R.layout.activity_share);
        shareUrl = getIntent().getStringExtra(SHARE_URL);
        shareContent = getIntent().getStringExtra(SHARE_CONTENT);
        if (!Utils.isStringEmpty(App.mShareTempletUrl)) {
            String[] urls = App.mShareTempletUrl.split(",");
            //随机取一个
            if (urls.length > 1) {
                int r = new Random().nextInt(urls.length);
                bgImageUrl = urls[r].trim();
            } else {
                bgImageUrl = urls[0].trim();
            }
        }

        mLLLyouout = findViewById(R.id.ll_layout);
        mShareImage = (ImageView) findViewById(R.id.img_share);
        mWeixin = findViewById(R.id.weixin);
        mWeixinCircel = findViewById(R.id.weixincircle);
        mQQ = findViewById(R.id.qq);
        mQQCircel = findViewById(R.id.qqcircle);

        mLLLyouout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mShareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mShareImage.setVisibility(View.INVISIBLE);

        mWeixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.shareBitmap(ShareActivity.this, ShareUtils.TYPE_WEBCHAT, fileName, mBitmap, shareContent);
                doCopyContent(shareContent);
                finish();
            }
        });
        mWeixinCircel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.shareBitmap(ShareActivity.this, ShareUtils.TYPE_WEBCHAT_CIRCLE, fileName, mBitmap, shareContent);
                doCopyContent(shareContent);
                finish();
            }
        });
        mQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.shareBitmap(ShareActivity.this, ShareUtils.TYPE_QQ, fileName, mBitmap, shareContent);
                doCopyContent(shareContent);
                finish();
            }
        });

        mQQCircel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.shareBitmap(ShareActivity.this, ShareUtils.TYPE_QZONE, fileName, mBitmap, shareContent);
                doCopyContent(shareContent);
                finish();
            }
        });

        if (!StringUtils.isBlank(bgImageUrl)) {
            int i = bgImageUrl.lastIndexOf("/");
            fileName = bgImageUrl.substring(i+1, bgImageUrl.length());
        }

        if (ShareUtils.isFileExist(fileName)) {
            mBitmap = ShareUtils.file2Bitmap(fileName);
            mShareImage.setImageBitmap(mBitmap);
            doScaleAnimation(mShareImage);
        } else {
            download(bgImageUrl);
        }
    }

    //重写该方法，为界面上的按钮提供事件响应方法
    public void download(String url) {
        DownTask downTask = new DownTask(this);
        downTask.execute(url);
    }

    class DownTask extends AsyncTask<String, Integer, String> {

        Context context;
        Bitmap bitmap;

        public DownTask(Context ctx) {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            bitmap = Utils.returnBitMap(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Bitmap sbitmap = ZXingUtils.createQRImage(shareUrl, 250, 250);
            sbitmap = ZXingUtils.mixtureBitmap(bitmap, sbitmap);

            mBitmap = ShareUtils.compressImage(sbitmap);
            deleteExist(fileName);
            mShareImage.setImageBitmap(mBitmap);

            doScaleAnimation(mShareImage);
        }

        @Override
        protected void onPreExecute() {
        }

    }

    public void deleteExist(String fileName) {
        String path = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = ShareUtils.BPATH;//保存到sd根目录下
        }
        File f = new File(path, fileName);
        if (f.exists()) {
            f.delete();
        }
    }

    public void doScaleAnimation(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.3f,1.0f,1.3f,1.0f,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(500);
        view.startAnimation(scaleAnimation);
        view.setVisibility(View.VISIBLE);
    }

    public void doCopyContent(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(content);
        Toast.makeText(ShareActivity.this, getString(R.string.copy_success), Toast.LENGTH_SHORT).show();
    }
}
