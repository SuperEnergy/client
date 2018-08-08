package com.ees.chain.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.domain.Error;
import com.ees.chain.domain.User;
import com.ees.chain.manager.CacheManager;
import com.ees.chain.task.group.LoginPresenter;
import com.ees.chain.ui.base.BaseActivity;
import com.ees.chain.ui.interfc.LoginContract;
import com.ees.chain.ui.view.support.ClearableEditText;
import com.ees.chain.utils.LogUtils;
import com.ees.chain.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.View {

    @BindView(R.id.username)
    ClearableEditText mUsername;
    @BindView(R.id.password)
    ClearableEditText mPassword;
    @BindView(R.id.forget_password)
    View mForgetPwd;
    @BindView(R.id.register)
    View mRegister;
    @BindView(R.id.login)
    View mLogin;
    @BindView(R.id.disclaimer_title_text)
    View mDisclaimer;

    private User mLocalUser;

    @OnClick(R.id.login)
    public void onClick(View view) {
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        if (Utils.isStringEmpty(username)) {
            Snackbar.make(mUsername, getString(R.string.empty_username), Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!Utils.checkAccountMark(username)) {
            Snackbar.make(mUsername, getString(R.string.err_username), Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (Utils.isStringEmpty(password)) {
            Snackbar.make(mUsername, getString(R.string.empty_password), Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (password.length()<6) {
            Snackbar.make(mUsername, getString(R.string.err_password_length), Snackbar.LENGTH_SHORT).show();
            return;
        }
        CacheManager.getInstance().clearBattery();
        mPresenter.login(username, password);
        showLoadingDialog();
    }

    @OnClick({R.id.forget_password,R.id.login_shortly,R.id.register, R.id.disclaimer_title_text})
    public void onClick2(View view) {
        switch (view.getId()){
            case R.id.forget_password:
                String username = mUsername.getText().toString();
                Intent intent = new Intent();
                if (!Utils.isStringEmpty(username)) {
                    intent.putExtra("pid", username);
                }
                intent.setClass(this, ResetPwdActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.login_shortly:
                String username2 = mUsername.getText().toString();
                Intent intent2 = new Intent();
                if (!Utils.isStringEmpty(username2)) {
                    intent2.putExtra("pid", username2);
                }
                intent2.setClass(this, QuickLoginActivity.class);
                startActivityForResult(intent2, 1);
                break;
            case R.id.register:
                Intent intent3 = new Intent();
                intent3.putExtra("title", getString(R.string.register));
                intent3.setClass(this, RegisterActivity.class);
                startActivity(intent3);
                break;
            case R.id.disclaimer_title_text:
                Intent intent5 = new Intent(this, WebviewActivity.class);
                intent5.putExtra("title", getString(R.string.agreement_title));
                intent5.putExtra("url", App.AGREEMENT);
                startActivity(intent5);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            finish();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initDatas() {
        mLocalUser = App.getInstance().getUser();
    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        if (mLocalUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void showError() {
        dismissLoadingDialog();
    }

    @Override
    public void complete() {
        dismissLoadingDialog();
    }

    @Override
    public void loginSuccess(User user) {
//        Snackbar.make(mUsername, getString(R.string.login_success), Snackbar.LENGTH_SHORT).show();
        dismissLoadingDialog();
        if (user!= null) {
            if (mLocalUser!=null && !user.getPid().equals(mLocalUser.getPid())) {
                CacheManager.getInstance().clearAll();
            }
            CacheManager.getInstance().saveUser(user);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
        LogUtils.d("loginSuccess");
    }

    @Override
    public void loginFail(Error err) {
        LogUtils.d("loginFail " + err.getCode() + err.getMessage());
        dismissLoadingDialog();
        Snackbar.make(mUsername, err.getMessage(), Snackbar.LENGTH_LONG).show();
        checkError(err);
    }

    @Override
    public void sendCodeFail(Error err) {
        LogUtils.d("sendCodeFail");
        Snackbar.make(mUsername, err.getMessage(), Snackbar.LENGTH_SHORT).show();
    }
}
