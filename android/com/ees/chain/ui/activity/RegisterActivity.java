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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.domain.Error;
import com.ees.chain.task.group.RegisterPresenter;
import com.ees.chain.ui.base.BaseActivity;
import com.ees.chain.ui.interfc.RegisterContract;
import com.ees.chain.ui.view.support.ClearableEditText;
import com.ees.chain.utils.LogUtils;
import com.ees.chain.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity<RegisterPresenter> implements RegisterContract.View {

    @BindView(R.id.left)
    View mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.nickname)
    ClearableEditText mNickname;
    @BindView(R.id.phone)
    ClearableEditText mPhone;
    @BindView(R.id.verify_code)
    EditText mVerfifyCode;
    @BindView(R.id.get_verify_code)
    TextView mGetVerifyCode;
    @BindView(R.id.password)
    ClearableEditText mPassword;
    @BindView(R.id.invitecode)
    ClearableEditText mInvitecode;
    @BindView(R.id.register)
    View mRegister;
    @BindView(R.id.disclaimer_title_text)
    View mDisclaimer;

    @OnClick(R.id.left)
    public void back(View view) {
        setResult(0);
        finish();
    }

    private boolean sending = false;
    int sec = 60;
    Handler mHandler = new Handler(Looper.getMainLooper());

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @OnClick({R.id.get_verify_code, R.id.register, R.id.disclaimer_title_text})
    public void clickBtn(View view) {
        hideKeyboard();
        switch (view.getId()){
            case R.id.get_verify_code:
                if(!sending) {
                    if (view.isEnabled()) {
                        String phNo = mPhone.getText().toString();
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
                            Snackbar.make(mPhone,R.string.err_username,Snackbar.LENGTH_SHORT).show();
                        }
                    }

                }
                break;
            case R.id.register:
                if (view.isEnabled()) {
                    hideKeyboard();
                    String nickname = mNickname.getText().toString();
                    String phone = mPhone.getText().toString();
                    String authCode = mVerfifyCode.getText().toString();
                    String password = mPassword.getText().toString();
                    String invitecode = mInvitecode.getText().toString();
                    if (Utils.isStringEmpty(nickname)) {
                        Snackbar.make(mNickname, getString(R.string.empty_nickname), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (Utils.isStringEmpty(phone)) {
                        Snackbar.make(mNickname, getString(R.string.empty_username), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (!Utils.checkAccountMark(phone)) {
                        Snackbar.make(mNickname, getString(R.string.err_username), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (Utils.isStringEmpty(authCode)) {
                        Snackbar.make(mNickname, getString(R.string.empty_auth_code), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (Utils.isStringEmpty(password)) {
                        Snackbar.make(mNickname, getString(R.string.empty_password), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    showLoadingDialog();
                    mPresenter.registe(nickname, phone, password, authCode, invitecode, "app");
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
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        mTitle.setText(R.string.register);
        mRegister.setEnabled(true);

        mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (Utils.checkAccountMark(editable.toString())) {
                    if(!sending && mGetVerifyCode.isEnabled()==false) {
                        mGetVerifyCode.setEnabled(true);
                    }
                }  else {
                    if(!sending) {
                        mGetVerifyCode.setEnabled(false);
                    }
                }
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                if(editable.length()>0) {
//                    if (mPhone.length()>0) {
//                        mRegister.setEnabled(true);
//                    }
//                }
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
    }

    @Override
    public void complete() {
        dismissLoadingDialog();
    }

    @Override
    public void registerSuccess() {
        LogUtils.d("registerSuccess");
        dismissLoadingDialog();
        Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void registerFail(Error err) {
        LogUtils.d("registerFail " + err.getCode() + err.getMessage());
        dismissLoadingDialog();
        Snackbar.make(mNickname, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        checkError(err);
    }

    @Override
    public void sendCodeFail(Error err) {
        LogUtils.d("sendCodeFail " + err.getCode() + err.getMessage());
        Snackbar.make(mNickname, err.getMessage(), Snackbar.LENGTH_SHORT).show();
    }

}
