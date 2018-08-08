package com.ees.chain.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.domain.Error;
import com.ees.chain.domain.Notice;
import com.ees.chain.domain.User;
import com.ees.chain.task.group.DefaultPresenter;
import com.ees.chain.task.group.MainPresenter;
import com.ees.chain.task.group.WebPresenter;
import com.ees.chain.ui.base.BaseActivity;
import com.ees.chain.ui.interfc.DefaultContract;
import com.ees.chain.ui.interfc.MainContract;
import com.ees.chain.ui.interfc.WebContract;
import com.ees.chain.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;

public class WebviewActivity extends BaseActivity<WebPresenter> implements WebContract.View {

    private String id = "";
    private String url = "";
    private String title = "";
    private String content = "";
    private String link = "";
    private int fontlarge = 0;
    private User mUser;

    @BindView(R.id.webview)
    WebView mWebView;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right)
    ImageView mRight;
    @BindView(R.id.back)
    View mBack;
    @BindView(R.id.close)
    View mClose;

    @OnClick({R.id.left, R.id.back})
    public void back(View view) {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    @OnClick(R.id.close)
    public void clsoe(View view) {
        finish();
    }

    @OnClick(R.id.right)
    public void clickBrowser(View view) {
        if (!StringUtils.isBlank(link)) {
            openBrowser(link);
            return;
        }
        if (!StringUtils.isBlank(url)) {
            openBrowser(url);
            return;
        }
    }

    public void openBrowser(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        mUser = App.getInstance().getUser();
        id = getIntent().getStringExtra("id");
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        link = getIntent().getStringExtra("link");
        content = getIntent().getStringExtra("content");
        fontlarge = getIntent().getIntExtra("fontlarge", 0);
        if (!Utils.isStringEmpty(title)) {
            mTitle.setText(title);
        }
        mBack.setVisibility(View.INVISIBLE);
        mClose.setVisibility(View.INVISIBLE);
        mRight.setImageResource(R.drawable.ic_browser);
        mRight.setVisibility(View.INVISIBLE);
        if (!StringUtils.isBlank(url)) {
            mRight.setVisibility(View.INVISIBLE);
        }
        if (!StringUtils.isBlank(link)) {
            mRight.setVisibility(View.VISIBLE);
        }
        if (!StringUtils.isBlank(link) || !StringUtils.isBlank(url)) {
            fontlarge = 0;
            link = StringUtils.isBlank(link) ? url : link;
            loadWeb(link, null);
            return;
        }

        if (!StringUtils.isBlank(content)) {
            loadWeb(null, content);
            return;
        }

        if (mUser != null && !StringUtils.isBlank(id)) {
            mPresenter.getNoticeDetail(mUser.getPid(), id);
            return;
        }
    }

    public void loadWeb(String url, String data) {
        if (mWebView == null) return;
        showLoadingDialog();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //loading timeout
                dismissLoadingDialog();
            }
        }, 2 * 1000);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setPluginsEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setSupportZoom(false); //支持缩放，默认为true
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        webSettings.supportMultipleWindows(); //多窗口
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
//        webSettings.setDefaultFontSize(46); //设置默认字体大小
        webSettings.setTextZoom(100);
        if (fontlarge == 1) {
            webSettings.setTextSize(WebSettings.TextSize.LARGEST);
        } else {
            webSettings.setTextSize(WebSettings.TextSize.NORMAL);
        }

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 60) {
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onReceivedTouchIconUrl(WebView view, String url,
                                               boolean precomposed) {
                super.onReceivedTouchIconUrl(view, url, precomposed);
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog,
                                          boolean isUserGesture, Message resultMsg) {
                return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
            }

        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                fontlarge = 0;
                link = url;
                loadWeb(url, null);
                if (mClose!=null) mClose.setVisibility(View.VISIBLE);
                if (mBack!=null) mBack.setVisibility(View.VISIBLE);
//                if (mRight!=null) mRight.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dismissLoadingDialog();
            }
        });

        if (!StringUtils.isBlank(url)) {
            String pid = "";
            String token = "";
            if (mUser != null) {
                pid = mUser.getPid();
                token = mUser.getToken();
            }

            String secret = App.APP_SECRET;
            url = url.replace("{id}", id);
            url = url.replace("{pid}", pid);
            url = url.replace("{token}", token);
            url = url.replace("{secret}", secret);
            this.link = url;
            mWebView.loadUrl(url);
        } else if (!StringUtils.isBlank(data)) {
            mWebView.loadData(data, "text/html; charset=UTF-8", null);
        }
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    //点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showNoticeDetailSuccess(Notice result) {
        loadWeb(null, result.getContent());
    }

    @Override
    public void showNoticeDetailFail(Error err) {
        Snackbar.make(mWebView, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        checkError(err);
    }
}
