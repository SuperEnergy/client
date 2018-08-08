package com.ees.chain.ui.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment<T1 extends BaseContract.BasePresenter> extends Fragment {

    @Inject
    protected T1 mPresenter;

    protected View parentView;
    protected FragmentActivity activity;
    protected LayoutInflater inflater;

    protected Context mContext;

    protected Dialog mLoading;

    protected Unbinder unbinder;

    public abstract
    @LayoutRes
    int getLayoutResId();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        parentView = inflater.inflate(getLayoutResId(), container, false);
        activity = getSupportActivity();
        mContext = activity;
        this.inflater = inflater;
        return parentView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        setupActivityComponent(App.getInstance().getAppComponent());
        attachView();
        initDatas();
        configViews();
    }

    protected void showLoadingDialog() {
        mLoading = LoadingDialog.createLoadingDialog(activity);
    }

    protected void dismissLoadingDialog() {
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
                LoadingDialog.closeDialog(mLoading);
//            }
//        }, 1500);
    }

    public void attachView() {
        if (mPresenter != null)
            mPresenter.attachView(this);
    }

    public abstract void initDatas();

    /**
     * 对各种控件进行设置、适配、填充数据
     */
    public abstract void configViews();


    protected abstract void setupActivityComponent(AppComponent appComponent);

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (mPresenter != null)
            mPresenter.detachView();
    }

    public FragmentActivity getSupportActivity() {
        return super.getActivity();
    }

    public Context getApplicationContext() {
        return this.activity == null ? (getActivity() == null ? null : getActivity()
                .getApplicationContext()) : this.activity.getApplicationContext();
    }

    protected LayoutInflater getLayoutInflater() {
        return inflater;
    }

    protected View getParentView() {
        return parentView;
    }

    public void checkError(Error error) {
        if (error != null && Error.CODE_USER_LOGOUTED.equals(error.getCode())) {
            CacheManager.getInstance().clearAll();
            shwoLogoutDialog(getContext(), error.getMessage());
        } else if (error != null && Error.CODE_SECRET_INVALID.equals(error.getCode())) {
            showNewVersionDialog();
        } else if (error != null && Error.CODE_FUND_ENCRYPT.equals(error.getCode())) {
            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (error != null && Error.CODE_MINING_ENCRYPT.equals(error.getCode())) {
            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void shwoLogoutDialog(final Context context, String content) {
        if (StringUtils.isBlank(content)) {
            return;
        }
        CommomDialog dialog = new CommomDialog(context, R.style.dialog, content, new CommomDialog.OnCloseListener() {
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
        CommomDialog dialog = new CommomDialog(getContext(), R.style.dialog, getString(R.string.new_version_tips), new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(furl);
                    intent.setData(content_url);
                    getActivity().startActivity(intent);
                }
                dialog.dismiss();
            }
        });
        dialog.setTitle(getString(R.string.tips)).show();
        dialog.setNegativeButtonVisible(View.GONE);
    }

}