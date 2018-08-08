package com.ees.chain.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.domain.Error;
import com.ees.chain.manager.CacheManager;
import com.ees.chain.task.group.VerifyPresenter;
import com.ees.chain.ui.base.BaseActivity;
import com.ees.chain.ui.interfc.VerifyContract;
import com.ees.chain.ui.view.support.ClearableEditText;
import com.ees.chain.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * by kesion 2017/12/8
 */
public class VerifyOneActivity extends BaseActivity<VerifyPresenter> implements VerifyContract.View {

    @BindView(R.id.left)
    View mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.usernane)
    ClearableEditText mName;
    @BindView(R.id.idcard_no)
    ClearableEditText mIdcardno;
    @BindView(R.id.next)
    View mNext;

    @OnClick(R.id.left)
    public void back(View view) {
        setResult(0);
        finish();
    }

    @OnClick(R.id.next)
    public void onClick(View view) {
        String name = mName.getText().toString();
        String idno = mIdcardno.getText().toString();
        if (Utils.isStringEmpty(name)) {
            Snackbar.make(mTitle, getString(R.string.empty_name), Toast.LENGTH_SHORT).show();
            return;
        }
        if (Utils.isStringEmpty(idno)) {
            Snackbar.make(mTitle, getString(R.string.empty_idcard), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Utils.validatorID(idno)) {
            Snackbar.make(mTitle, getString(R.string.err_idcard), Toast.LENGTH_SHORT).show();
            return;
        }
        CacheManager.getInstance().saveUserTrueName(name);
        CacheManager.getInstance().saveUserIdno(idno);
        verifySucces();
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_verify1;
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        mTitle.setText(getString(R.string.name_verify));
        String name = CacheManager.getInstance().getUserTruename();
        String idno = CacheManager.getInstance().getUserIdno();
        if (!StringUtils.isBlank(name)) {
            mName.setText(name);
        }
        if (!StringUtils.isBlank(idno)) {
            mIdcardno.setText(idno);
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

    }

    @Override
    public void complete() {

    }

    @Override
    public void verifySucces() {
        Intent intent = new Intent();
        intent.setClass(this, VerifyTwoActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void verifyFail(Error err) {
        Snackbar.make(mTitle, err.getMessage(), Toast.LENGTH_SHORT).show();
        checkError(err);
    }

    @Override
    public void getTokenSucces(String token) {

    }

    @Override
    public void getTokenFail(Error err) {
        checkError(err);
    }

    @Override
    public void uploadFileSuccess(int vid, String result) {

    }

    @Override
    public void uploadFileFail(int vid, String result) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1 && resultCode == 2) {
            setResult(2);
        }
        finish();
    }
}
