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

import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.domain.Error;
import com.ees.chain.task.group.ResetPwdPresenter;
import com.ees.chain.ui.base.BaseActivity;
import com.ees.chain.ui.interfc.ResetPwdContract;
import com.ees.chain.ui.view.support.ClearableEditText;
import com.ees.chain.utils.LogUtils;
import com.ees.chain.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;

public class ResetPwdActivity extends BaseActivity<ResetPwdPresenter> implements ResetPwdContract.View {

    String pid;

    @BindView(R.id.username_view)
    View mUsernameView;
    @BindView(R.id.left)
    View mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.username)
    ClearableEditText mUsername;
    @BindView(R.id.verify_code)
    EditText mVerfifyCode;
    @BindView(R.id.get_verify_code)
    TextView mGetVerifyCode;
    @BindView(R.id.new_password)
    ClearableEditText mNewPassword;
    @BindView(R.id.confirm_password)
    ClearableEditText mConfirmPassword;
    @BindView(R.id.reset)
    View mReset;

    private int mType = 0;  //0重置密码，1修改密码

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

    @OnClick({R.id.get_verify_code, R.id.reset})
    public void clickBtn(View view) {
        hideKeyboard();
        switch (view.getId()){
            case R.id.get_verify_code:
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
            case R.id.reset:
                if (view.isEnabled()) {
                    hideKeyboard();
                    String username = mUsername.getText().toString();
                    String authCode = mVerfifyCode.getText().toString();
                    String password = mNewPassword.getText().toString();
                    String cpassword = mConfirmPassword.getText().toString();
                    if (Utils.isStringEmpty(username)) {
                        Snackbar.make(mUsername, getString(R.string.empty_username), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (!Utils.checkAccountMark(username)) {
                        Snackbar.make(mUsername, getString(R.string.err_username), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (Utils.isStringEmpty(authCode)) {
                        Snackbar.make(mUsername, getString(R.string.empty_auth_code), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (Utils.isStringEmpty(password)) {
                        Snackbar.make(mUsername, getString(R.string.empty_password), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (!password.equals(cpassword)) {
                        Snackbar.make(mUsername, getString(R.string.password_not_mix), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    mPresenter.resetPwd(username, password, authCode);
                    showLoadingDialog();
                }
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_resetpwd;
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        pid  = getIntent().getStringExtra("pid");
        mType = getIntent().getIntExtra("type", 0);
        if (!Utils.isStringEmpty(pid)) {
            mUsername.setText(pid);
            mGetVerifyCode.setEnabled(true);
        }
        if (mType == 1) {
            mTitle.setText(R.string.modify_password);
            mUsernameView.setVisibility(View.GONE);
        } else {
            mTitle.setText(getString(R.string.reset_password));
            mUsernameView.setVisibility(View.VISIBLE);
        }
        mReset.setEnabled(true);

        mUsername.addTextChangedListener(new TextWatcher() {
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

        mConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                if(editable.length()>0) {
//                    if (mUsername.length()>0) {
//                        mReset.setEnabled(true);
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
    public void resetSuccess() {
        LogUtils.d("resetSuccess");
        dismissLoadingDialog();
        if (mType == 1) {
            Toast.makeText(this, getString(R.string.modify_password_success), Toast.LENGTH_SHORT).show();
            setResult(0);
        } else {
            Snackbar.make(mUsername, getString(R.string.reset_password_success), Snackbar.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            setResult(1);
        }
        finish();
    }

    @Override
    public void resetFail(Error err) {
        LogUtils.d("resetFail " + err.getCode() + err.getMessage());
        dismissLoadingDialog();
        Snackbar.make(mUsername, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        checkError(err);
    }

    @Override
    public void sendCodeFail(Error err) {
        Snackbar.make(mUsername, err.getMessage(), Snackbar.LENGTH_SHORT).show();
    }
}
