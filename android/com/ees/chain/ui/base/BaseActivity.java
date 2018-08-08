package com.ees.chain.ui.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.domain.Error;
import com.ees.chain.event.FinishActivityEvent;
import com.ees.chain.manager.CacheManager;
import com.ees.chain.task.core.BaseContract;
import com.ees.chain.ui.activity.LoginActivity;
import com.ees.chain.ui.loading.LoadingDialog;
import com.ees.chain.ui.view.support.CommomDialog;
import com.ees.chain.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by chairs on 2017/4/25.
 */

public abstract class BaseActivity <T1 extends BaseContract.BasePresenter> extends AppCompatActivity {
    protected Unbinder unbinder;
//    @BindView(R.id.toolbar)
//    public Toolbar toolbar;
//    @BindView(R.id.toolbar_title)
//    public TextView toolTitle;
    protected Dialog mLoading;

    @Inject
    protected T1 mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        setupActivityComponent(App.getInstance().getAppComponent());
//        toolbar.setTitle("");
        Utils.setWindowStatusBarColor(this, R.color.black_2);
        attachView();
        initDatas();
        configViews(savedInstanceState);
        registerEventBus();
//        if(!(this instanceof HomeActivity)) {
//            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    onBackPressed();
//                }
//            });
//        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveFinishActivityEvent(FinishActivityEvent event){
        finish();
    }

    protected void showLoadingDialog() {
        if(!isFinishing()) {
            mLoading = LoadingDialog.createLoadingDialog(this);
        }
    }

    protected void showPayLoadingDialog(String content) {
        if(!isFinishing()) {
            mLoading = LoadingDialog.createLoadingDialog(this, content);
        }
    }

    protected void dismissLoadingDialog() {
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
                LoadingDialog.closeDialog(mLoading);
//            }
//        }, 1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterEventBus();
        if (unbinder != null) {
            unbinder.unbind();
        }

        if (mPresenter != null)
            mPresenter.detachView();
    }

    public void attachView() {
        if (mPresenter != null)
            mPresenter.attachView(this);
    }

    public abstract int getLayoutId();

    public abstract void initDatas();

    /**
     * 对各种控件进行设置、适配、填充数据
     */
    public abstract void configViews(Bundle savedInstanceState);

    protected abstract void setupActivityComponent(AppComponent appComponent);


//    public  void setToolTitle(int titleRes) {
//        toolTitle.setText(titleRes);
//    }
//
//    public  void setToolTitle(String title) {
//        toolTitle.setText(title);
//    }

    public void checkError(Error error) {
        if (error != null && Error.CODE_USER_LOGOUTED.equals(error.getCode())) {
            CacheManager.getInstance().clearAll();
            shwoLogoutDialog(this, error.getMessage());
        } else if (error != null && Error.CODE_SECRET_INVALID.equals(error.getCode())) {
            showNewVersionDialog();
        } else if (error != null && Error.CODE_FUND_ENCRYPT.equals(error.getCode())) {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (error != null && Error.CODE_MINING_ENCRYPT.equals(error.getCode())) {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void shwoLogoutDialog(final Context context, String content) {
        if (StringUtils.isBlank(content)) {
            return;
        }
        CommomDialog dialog = new CommomDialog(this, R.style.dialog, content, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    EventBus.getDefault().post(new FinishActivityEvent());

                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setClass(context, LoginActivity.class);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });
        dialog.setTitle(getString(R.string.tips)).show();
        dialog.setNegativeButtonVisible(View.GONE);
    }

    public void showNewVersionDialog() {
//        String url = CacheManager.getInstance().getAndroidUrl();
//        if (StringUtils.isBlank(url)) {
//            url = App.BASE_NEW_VERSION_URL;
//        }
        final String furl = App.BASE_NEW_VERSION_URL;
        CommomDialog dialog = new CommomDialog(this, R.style.dialog, getString(R.string.new_version_tips), new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
//                    DownloadManager downloadManager=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
//                    DownloadManager.Request request= new DownloadManager.Request(Uri.parse(url));
//                    request.setDestinationInExternalPublicDir("EES", "ees-" + System.currentTimeMillis() + ".apk");
//                    long downloadId = downloadManager.enqueue(request);
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(furl);
                    intent.setData(content_url);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });
        dialog.setTitle(getString(R.string.tips)).show();
        dialog.setNegativeButtonVisible(View.GONE);
    }

    public void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    public void unRegisterEventBus() {
        EventBus.getDefault().unregister(this);
    }
}
