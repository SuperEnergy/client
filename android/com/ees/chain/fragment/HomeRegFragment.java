package com.ees.chain.ui.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.domain.User;
import com.ees.chain.manager.CacheManager;
import com.ees.chain.task.group.DefaultPresenter;
import com.ees.chain.ui.activity.MainActivity;
import com.ees.chain.ui.activity.VerifyOneActivity;
import com.ees.chain.ui.base.BaseFragment;
import com.ees.chain.ui.interfc.DefaultContract;
import com.ees.chain.ui.view.support.CommomDialog;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by KESION on 2017/12/5.
 */
public class HomeRegFragment extends BaseFragment<DefaultPresenter> implements DefaultContract.View {

    @BindView(R.id.cover_bg)
    ImageView mCoverBg;
    @BindView(R.id.cover)
    View mCover;
    @BindView(R.id.register_mining)
    Button mRegisterMining;
    @BindView(R.id.msg_view1)
    View mMsgView;
    @BindView(R.id.msg_content1)
    TextView mMsgContent;
    @BindView(R.id.declaratoin)
    TextView mDeclaratoin;

    private User mUser;

    public HomeRegFragment() {
        // Required empty public constructor
    }

    @OnClick({R.id.register_mining, R.id.cover, R.id.msg_view1})
    public void onClick(View view) {
        int vid = view.getId();
        switch (vid) {
            case R.id.register_mining:
            case R.id.cover:
                if (mUser!=null && mUser.getRna_Status() != 2) {
                    showVerifyDialog();
                }
                break;
            case R.id.msg_view1:
                ((MainActivity)getActivity()).changeTab(R.id.msg);
                break;
        }
    }


    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_reg;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    public void showVerifyDialog() {
        new CommomDialog(getActivity(), R.style.dialog, getString(R.string.verify_tips), new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    Intent intent2 = new Intent();
                    intent2.setClass(getApplicationContext(), VerifyOneActivity.class);
                    startActivity(intent2);
                }
                dialog.dismiss();
            }
        }).setTitle(getString(R.string.tips)).show();
    }

    @Override
    public void initDatas() {
        mUser = App.getInstance().getUser();
    }

    @Override
    public void configViews() {
        if (!StringUtils.isBlank(CacheManager.getInstance().getMiningNotice())) {
            mDeclaratoin.setText(Html.fromHtml(App.miningNotice));
        } else {
            mDeclaratoin.setText(R.string.wakuangxuzhi);
        }
        if (mCoverBg!=null) {
            if (App.changeSkn == 1) {
                mCoverBg.setBackgroundResource(R.drawable.cover_bg);
            }
        }
        if (mMsgView != null) mMsgView.setVisibility(View.GONE);
        if (mRegisterMining != null) mRegisterMining.setEnabled(false);
        if (mCover != null) mCover.setEnabled(false);
        if (mUser != null) {
            switch (mUser.getRna_Status()) {
                case 0:
                    mMsgView.setVisibility(View.GONE);
                    mRegisterMining.setText(R.string.register_mining);
                    mRegisterMining.setEnabled(true);
                    mCover.setEnabled(true);
                    break;
                case 1:
                    break;
                case -1:
                    mMsgView.setVisibility(View.VISIBLE);
                    mRegisterMining.setText(R.string.register_mining_again);
                    mRegisterMining.setEnabled(true);
                    if (!StringUtils.isBlank(mUser.getRna_remarks())) {
                        mMsgContent.setText(getString(R.string.fail_register_mining) + mUser.getRna_remarks());
                    }
                    mCover.setEnabled(true);
                    break;
                case 2:
                case 3:
                    mMsgView.setVisibility(View.GONE);
                    mRegisterMining.setText(R.string.verifing);
                    mRegisterMining.setEnabled(false);
                    mCover.setEnabled(false);
                    break;
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        configViews();
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {
        dismissLoadingDialog();
    }

    @Override
    public void show() {

    }
}
