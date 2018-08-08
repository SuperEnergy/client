package com.ees.chain.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by chairs on 2017/7/20.
 */

public class QuickLoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.View {

    @BindView(R.id.left)
    View mBack;
    @BindView(R.id.title)
    TextView mTitle;

    @BindView(R.id.et_mobile_number)
    ClearableEditText mUsername;

    @BindView(R.id.et_login_verify_code)
    EditText mVerfifyCode;

    @BindView(R.id.btn_login_verify_code)
    Button mGetVerifyCode;

    @BindView(R.id.sign_in_button)
    Button loginBtn;

    @BindView(R.id.disclaimer_title_text)
    View mDisclaimer;

    private String pid;
    private boolean sending = false;
    int sec = 60;
    Handler mHandler = new Handler(Looper.getMainLooper());

    @OnClick(R.id.left)
    public void back(View view) {
        this.setResult(0);
        finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_quick_login;
    }

    public void initDatas() {

    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @OnClick({R.id.btn_login_verify_code,R.id.sign_in_button, R.id.disclaimer_title_text})
    public void clickBtn(View view) {
        hideKeyboard();
        switch (view.getId()){
            case R.id.btn_login_verify_code:
                if(!sending) {
                    if (view.isEnabled()) {
                        String phNo = mUsername.getText().toString();
                        if (Utils.checkAccountMark(phNo)) {
                            sec = 60;
                            mPresenter.sendCode(phNo);
                            view.setEnabled(false);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(isFinishing()) {
                                        return;
                                    }
                                    if (sec>0) {
                                        mGetVerifyCode.setText(String.format(getString(R.string.have_seceds),sec+""));
                                        sec--;
                                        mHandler.postDelayed(this,1000);
                                        sending = true;
                                    } else {
                                        sending = false;
                                        mGetVerifyCode.setText(R.string.get_verify_code);
                                        mGetVerifyCode.setEnabled(true);
                                    }

                                }
                            },1000);
                        } else {
                            Snackbar.make(mUsername,R.string.err_username,Snackbar.LENGTH_SHORT).show();
                        }
                    }

                }
                break;
            case R.id.sign_in_button:
                if (view.isEnabled()) {
                    String username = mUsername.getText().toString();
                    if (Utils.isStringEmpty(username)) {
                        Snackbar.make(mUsername, getString(R.string.empty_username), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (!Utils.checkAccountMark(username)) {
                        Snackbar.make(mUsername, getString(R.string.err_username), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    String code = mVerfifyCode.getText().toString();
                    if (Utils.isStringEmpty(code)) {
                        Snackbar.make(mVerfifyCode, getString(R.string.empty_auth_code), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    CacheManager.getInstance().clearBattery();
                    mPresenter.quickLogin(mUsername.getText().toString(), mVerfifyCode.getText().toString());
                    showLoadingDialog();
                }
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        pid  = getIntent().getStringExtra("pid");
        if (!Utils.isStringEmpty(pid)) {
            mUsername.setText(pid);
            mGetVerifyCode.setEnabled(true);
        }
        mTitle.setText(getString(R.string.login_quickly));
        loginBtn.setEnabled(true);

        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(Utils.checkAccountMark(editable.toString())) {
                    if(!sending && mGetVerifyCode.isEnabled()==false) {
                        mGetVerifyCode.setEnabled(true);
                    }
                    if (mVerfifyCode.length()>0) {
                        loginBtn.setEnabled(true);
                    }
                } else {
                    if(!sending) {
                        mGetVerifyCode.setEnabled(false);
                    }
//                    loginBtn.setEnabled(false);
                }
            }
        });

        mVerfifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0) {
                    if (mUsername.length()>0) {
                        loginBtn.setEnabled(true);
                    }
                    if(!sending) {
                        mGetVerifyCode.setEnabled(true);
                    }
                } else {
                    if(!sending) {
                        mGetVerifyCode.setEnabled(false);
                    }
                }
            }
        });
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
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(mUsername,R.string.login_fail,Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void complete() {
        dismissLoadingDialog();;
    }

    @Override
    public void loginSuccess(User user) {
        LogUtils.d("loginSuccess");
        dismissLoadingDialog();
        if (user != null) {
            Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(QuickLoginActivity.this,MainActivity.class));
            this.setResult(1);
            finish();
        }
    }

    @Override
    public void loginFail(Error str) {
        LogUtils.d("loginFail " + str.getCode() + str.getMessage());
        dismissLoadingDialog();
        Snackbar.make(mUsername, str.getMessage(), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void sendCodeFail(Error err) {
        LogUtils.d("sendCodeFail " + err.getCode() + err.getMessage());
        Snackbar.make(mUsername, err.getMessage(), Snackbar.LENGTH_SHORT).show();
    }
}