package com.ees.chain.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.domain.Error;
import com.ees.chain.domain.Ledger;
import com.ees.chain.domain.User;
import com.ees.chain.event.UpdateUserInfoEvent;
import com.ees.chain.manager.CacheManager;
import com.ees.chain.task.group.ChargePresenter;
import com.ees.chain.ui.base.BaseActivity;
import com.ees.chain.ui.interfc.ChargeContract;
import com.ees.chain.ui.view.support.ClearableEditText;
import com.ees.chain.ui.view.support.CommomDialog;
import com.ees.chain.utils.LogUtils;
import com.ees.chain.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by chairs on 2017/7/20.
 */

public class ChargeActivity extends BaseActivity<ChargePresenter> implements ChargeContract.View {

    @BindView(R.id.left)
    View mBack;
    @BindView(R.id.title)
    TextView mTitle;

    @BindView(R.id.et_mobile_number)
    ClearableEditText mUsername;

    @BindView(R.id.charge_number)
    ClearableEditText mChargeNum;

    @BindView(R.id.et_login_verify_code)
    EditText mVerfifyCode;

    @BindView(R.id.btn_login_verify_code)
    Button mGetVerifyCode;

    @BindView(R.id.check_all_charge)
    View mCheckAllCharge;

    @BindView(R.id.sign_in_button)
    Button loginBtn;

    @BindView(R.id.total_number)
    TextView mTotalNumber;

    @BindView(R.id.feiyong)
    TextView mFeiyong;

    @BindView(R.id.authcode_view)
    View mAuthcodeView;

    @BindView(R.id.charge_desc)
    TextView mChargeDesc;

    @BindView(R.id.password_view)
    View mPasswordView;

    @BindView(R.id.password)
    ClearableEditText mPassword;

    private boolean sending = false;
    int sec = 60;
    private double mTotalFund;
    private double mFeiyongees0 = 2.0;   //固定2ees
    private double mFeiyongees = 0.0;   //1.0%
    Handler mHandler = new Handler(Looper.getMainLooper());

    @OnClick(R.id.left)
    public void back(View view) {
        if (mSuccess) {
            this.setResult(3);
        } else {
            this.setResult(0);
        }
        finish();
    }

    private User mUser;
    private Ledger mLedger;
    private String mDesc;

    @Override
    public int getLayoutId() {
        return R.layout.activity_charge;
    }

    public void initDatas() {
        mUser = App.getInstance().getUser();
        mLedger = App.getInstance().getLedger();
        if (mLedger != null) {
            mTotalFund = mLedger.getFund();
        }
        mDesc = CacheManager.getInstance().getChargeDesc();
        String fee = CacheManager.getInstance().getChargeFee();
        if (!StringUtils.isBlank(fee)) {
            mFeiyongees0 = Double.parseDouble(fee);
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @OnClick({R.id.btn_login_verify_code, R.id.sign_in_button, R.id.check_all_charge})
    public void clickBtn(View view) {
        hideKeyboard();
        switch (view.getId()){
            case R.id.btn_login_verify_code:
                if(!sending) {
                    if (view.isEnabled()) {
                        String username = mUsername.getText().toString();
                        if (Utils.isStringEmpty(username)) {
                            Snackbar.make(mUsername, getString(R.string.empty_charge_username), Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        if (username.equalsIgnoreCase(mUser.getPid())) {
                            Snackbar.make(mVerfifyCode, getString(R.string.err_charge_my), Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        String num = mChargeNum.getText().toString();
                        if (!Utils.checkNumberMark(num)) {
                            Snackbar.make(mChargeNum, getString(R.string.err_charge_num), Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        double dnum = Double.parseDouble(num);
                        if (dnum < 1) {//0.00000001
                            Snackbar.make(mVerfifyCode, getString(R.string.err_charge_num2), Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        if (dnum + mFeiyongees > mTotalFund) {
                            Snackbar.make(mVerfifyCode, getString(R.string.err_charge_num_limit), Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        String phNo = mUsername.getText().toString();
                        if (Utils.checkAccountMark(phNo)) {
                            sec = 60;
                            mPresenter.sendCodeCharge(mUser.getPid());
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
                        Snackbar.make(mUsername, getString(R.string.empty_charge_username), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (username.equalsIgnoreCase(mUser.getPid())) {
                        Snackbar.make(mVerfifyCode, getString(R.string.err_charge_my), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    String num = mChargeNum.getText().toString();
                    if (!Utils.checkNumberMark(num)) {
                        Snackbar.make(mChargeNum, getString(R.string.err_charge_num), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    double dnum = Double.parseDouble(num);
                    if (dnum < 1) {//0.00000001
                        Snackbar.make(mVerfifyCode, getString(R.string.err_charge_num2), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    if (dnum + mFeiyongees > mTotalFund) {
                        Snackbar.make(mVerfifyCode, getString(R.string.err_charge_num_limit), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
//                    String code = mVerfifyCode.getText().toString();
//                    if (Utils.isStringEmpty(code)) {
//                        Snackbar.make(mVerfifyCode, getString(R.string.empty_auth_code), Snackbar.LENGTH_SHORT).show();
//                        return;
//                    }
                    String pwd = mPassword.getText().toString();
                    if (StringUtils.isBlank(pwd) || pwd.length()!=6) {
                        Snackbar.make(mPassword, getString(R.string.password_hint2), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    shwoChargeDialog(getBaseContext());
                }
                break;
            case R.id.check_all_charge:
                startActivity(new Intent(this, ChargeListActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        mTitle.setText(getString(R.string.charge));
        mAuthcodeView.setVisibility(View.GONE);
        mPasswordView.setVisibility(View.VISIBLE);
        loginBtn.setEnabled(true);
        if (mUser != null) {
            double fund = Utils.doubleFormat(mTotalFund);
            mTotalNumber.setText(String.format(getString(R.string.charge_number_limit_hint), fund+""));
            String str = String.format(getString(R.string.charge_feiyong_hint), "1%", mFeiyongees0+"");
            if (mFeiyongees0 == 0) {
                str = String.format(getString(R.string.charge_feiyong_hint2), "1%");
            }
            mFeiyongees += mFeiyongees0;
            mFeiyongees = Utils.doubleFormat(mFeiyongees);
            mFeiyong.setText(str + mFeiyongees +"ees");
        }
        if (!StringUtils.isBlank(mDesc)) {
            mChargeDesc.setText(mDesc.replace("\\n", "\n"));
            mChargeDesc.setVisibility(View.VISIBLE);
        } else {
            mChargeDesc.setVisibility(View.GONE);
        }

//        App.getInstance().getWx().handleIntent(getIntent(),this);
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
                }
            }
        });

        mChargeNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0) {
                    String num = editable.toString();
                    Double fy = mFeiyongees;
                    try {
                        fy = Double.parseDouble(num);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mFeiyongees = Utils.doubleFormat(fy * 1.0 /100.0);
                } else {
                    mFeiyongees = 0.0;
                }
                String str = String.format(getString(R.string.charge_feiyong_hint), "1%", mFeiyongees0+"");
                mFeiyongees += mFeiyongees0;
                mFeiyongees = Utils.doubleFormat(mFeiyongees);
                mFeiyong.setText(str  + mFeiyongees + "ees");
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

    }

    @Override
    public void complete() {
    }

    private boolean mSuccess = false;

    @Override
    public void chargeSuccess() {
        dismissLoadingDialog();
        mSuccess = true;
        String snum = mChargeNum.getText().toString();
        LogUtils.d("chargeSuccess " + snum);
        double num = 0.0;
        if (!StringUtils.isBlank(snum)) {
            num = Double.parseDouble(snum);
        }
        if (mLedger != null && num>0) {
            mTotalFund = mTotalFund - num;
            mLedger.setFund(mTotalFund);
            double fund = Utils.doubleFormat(mTotalFund);
            mTotalNumber.setText(String.format(getString(R.string.charge_number_limit_hint), fund + ""));
//            CacheManager.getInstance().saveLedger(mLedger);
        }
        mUsername.setText("");
        mChargeNum.setText("");
        mVerfifyCode.setText("");
        mPassword.setText("");
        Snackbar.make(mUsername, getString(R.string.charge_success), Snackbar.LENGTH_LONG).show();
        EventBus.getDefault().post(new UpdateUserInfoEvent());
    }

    @Override
    public void chargeFail(Error str) {
        LogUtils.d("showBonusFail error code " + str.getCode());
        dismissLoadingDialog();
        Snackbar.make(mUsername, str.getMessage(), Snackbar.LENGTH_SHORT).show();
        checkError(str);
    }

    @Override
    public void sendCodeFail(Error err) {
        Snackbar.make(mUsername, err.getMessage(), Snackbar.LENGTH_SHORT).show();
    }

    public void shwoChargeDialog(final Context context) {
        String content = getString(R.string.charge_assure);
        CommomDialog dialog = new CommomDialog(this, R.style.dialog, content, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    mPresenter.charge(mUser.getPid(), mUsername.getText().toString(), mChargeNum.getText().toString(), mPassword.getText().toString());
                    showLoadingDialog();
                }
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton(getString(R.string.ok));
        dialog.setTitle(getString(R.string.tips)).show();
        dialog.setContentGravity(Gravity.LEFT);
    }
}