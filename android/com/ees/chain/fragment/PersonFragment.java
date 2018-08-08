package com.ees.chain.ui.fragment;


import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.domain.Error;
import com.ees.chain.domain.Find;
import com.ees.chain.domain.Ledger;
import com.ees.chain.domain.User;
import com.ees.chain.event.UpdateUserInfoEvent;
import com.ees.chain.manager.CacheManager;
import com.ees.chain.task.group.PersonPresenter;
import com.ees.chain.ui.activity.BonusActivity;
import com.ees.chain.ui.activity.ChargeActivity;
import com.ees.chain.ui.activity.EditProfileActivity;
import com.ees.chain.ui.activity.OrderListActivity;
import com.ees.chain.ui.activity.SettingActivity;
import com.ees.chain.ui.activity.WebviewActivity;
import com.ees.chain.ui.base.BaseFragment;
import com.ees.chain.ui.interfc.PersonContract;
import com.ees.chain.ui.view.support.CommomDialog;
import com.ees.chain.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by KESION on 2017/12/5.
 */
public class PersonFragment extends BaseFragment<PersonPresenter> implements PersonContract.View {
    @BindView(R.id.swipeRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.profile_view)
    View mProfileView;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.check_detail)
    TextView mCheckDetail;
    @BindView(R.id.total_money)
    TextView mTotalMoney;
    @BindView(R.id.spread_bonus)
    TextView mSpreadBonus;
    @BindView(R.id.mining_bonus)
    TextView mMiningBonus;
    @BindView(R.id.total_user)
    TextView mTotalUser;
    @BindView(R.id.level_one_user)
    TextView mLevelOneUser;
    @BindView(R.id.level_one_user_verify)
    TextView mLevelOneUserVerify;
    @BindView(R.id.level_two_user_verify)
    TextView mLevelTwoUserVerify;
    @BindView(R.id.cover)
    SimpleDraweeView mCover;
    @BindView(R.id.level_two_user)
    TextView mLevelTwoUser;
    @BindView(R.id.money_view)
    View mMoneyView;
    @BindView(R.id.new_version)
    View mNewVersion;
    @BindView(R.id.version)
    TextView mVersion;
    @BindView(R.id.total_money2)
    TextView mTotalMoneyRMB;
    @BindView(R.id.charge)
    View mChargeView;
    //    @BindView(R.id.notice)
//    View mNotice;
    //    @BindView(R.id.feedback)
//    View mFeedback;

    @BindView(R.id.setting1)
    View mSetting1;
    @BindView(R.id.setting_cover1)
    SimpleDraweeView mSettingCover1;
    @BindView(R.id.setting_title1)
    TextView mSettingTitle1;

    @BindView(R.id.setting2)
    View mSetting2;
    @BindView(R.id.setting_cover2)
    SimpleDraweeView mSettingCover2;
    @BindView(R.id.setting_title2)
    TextView mSettingTitle2;

    @BindView(R.id.setting3)
    View mSetting3;
    @BindView(R.id.setting_cover3)
    SimpleDraweeView mSettingCover3;
    @BindView(R.id.setting_title3)
    TextView mSettingTitle3;

    @BindView(R.id.setting4)
    View mSetting4;
    @BindView(R.id.setting_cover4)
    SimpleDraweeView mSettingCover4;
    @BindView(R.id.setting_title4)
    TextView mSettingTitle4;

    @BindView(R.id.setting5)
    View mSetting5;
    @BindView(R.id.setting_cover5)
    SimpleDraweeView mSettingCover5;
    @BindView(R.id.setting_title5)
    TextView mSettingTitle5;

    @BindView(R.id.setting6)
    View mSetting6;
    @BindView(R.id.setting_cover6)
    SimpleDraweeView mSettingCover6;
    @BindView(R.id.setting_title6)
    TextView mSettingTitle6;

    @BindView(R.id.setting7)
    View mSetting7;
    @BindView(R.id.setting_cover7)
    SimpleDraweeView mSettingCover7;
    @BindView(R.id.setting_title7)
    TextView mSettingTitle7;

    @BindView(R.id.setting8)
    View mSetting8;
    @BindView(R.id.setting_cover8)
    SimpleDraweeView mSettingCover8;
    @BindView(R.id.setting_title8)
    TextView mSettingTitle8;

    private User mUser;

    private Ledger mLedger;

    private String mNewestVersionName = null;

    private List<Find> mSettings = new ArrayList<Find>();

    public PersonFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_person;
    }

    @Override
    public void initDatas() {
        mUser = App.getInstance().getUser();
        mLedger = App.getInstance().getLedger();
        mSettings = CacheManager.getInstance().getSettingList();
        long lversion = 0;
        if (mLedger != null) {
            lversion = mLedger.getVersion();
        }
        if (mUser != null) {
            mPresenter.ledgerRefresh(mUser.getPid(), lversion);
            mPresenter.userSync(mUser.getPid(), mUser.getVersion());
            mPresenter.getSettinglist(mUser.getPid(), App.getInstance().getSettingVersion());
        }
        mNewestVersionName = CacheManager.getInstance().getNewVersionName();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUserInfo(UpdateUserInfoEvent event) {
        initDatas();
        configViews();
    }

    @Override
    public void configViews() {
        mUser = App.getInstance().getUser();
        if (mSmartRefreshLayout!=null) {
            mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(RefreshLayout refreshlayout) {
                    initDatas();
                }
            });
        }
        mSetting1.setVisibility(View.GONE);
        mSetting2.setVisibility(View.GONE);
        mSetting3.setVisibility(View.GONE);
        mSetting4.setVisibility(View.GONE);
        mSetting5.setVisibility(View.GONE);
        mSetting6.setVisibility(View.GONE);
        mSetting7.setVisibility(View.GONE);
        mSetting8.setVisibility(View.GONE);

        if (mUser != null && mProfileView!=null && mName!=null) {
            mProfileView.setEnabled(true);
            mName.setText(mUser.getName());
            mCheckDetail.setVisibility(View.VISIBLE);
            if (mUser.getRna_Status() == 1) {
//                mCheckDetail.setText(R.string.has_name_verify1);
            } else if (mUser.getRna_Status() == 2) {
                mCheckDetail.setText(R.string.verifing3);
            } else if (mUser.getRna_Status() == 3) {
                mCheckDetail.setText(R.string.verifing3);
            } else {
                mCheckDetail.setText(R.string.not_name_verify1);
            }

            if (App.mPriceRMB == 0) {
                mTotalMoneyRMB.setVisibility(View.GONE);
            } else {
                double fund = 0.0;
                if (App.getInstance().getLedger() != null) {
                    fund = App.getInstance().getLedger().getFund();
                }
                double moneyrmb = App.mPriceRMB * fund;
                mTotalMoneyRMB.setText("≈￥" + Utils.doubleFormat(moneyrmb));
                mTotalMoneyRMB.setVisibility(View.VISIBLE);
            }

            if (mLedger != null) {
                double fund = Utils.doubleFormat(mLedger.getFund());
                double prorev = Utils.doubleFormat(mLedger.getPromotionRevenue());
                double minrev = Utils.doubleFormat(mLedger.getMiningRevenue());
                mTotalMoney.setText(fund + " ees");
                mSpreadBonus.setText(prorev + " ees");
                mMiningBonus.setText(minrev + " ees");
            } else {
                String zero = "0.0 ees";
                mTotalMoney.setText(zero);
                mSpreadBonus.setText(zero);
                mMiningBonus.setText(zero);
            }
            int totalUser = mUser.getReferral_OneLevelAmount()+mUser.getReferral_TwoLevelAmount();
            if (totalUser == 0) {
                mTotalUser.setText("0");
            } else {
                mTotalUser.setText(totalUser+"");
            }
            if (!StringUtils.isBlank(mUser.getAvatar())) {
                mCover.setImageURI(mUser.getAvatar());
            }
            mLevelOneUser.setText(mUser.getReferral_OneLevelAmount() + "");
            mLevelTwoUser.setText(mUser.getReferral_TwoLevelAmount() + "");
            mLevelOneUserVerify.setText(String.format(getString(R.string.has_verify_user), mUser.getReferral_OneLevelRealNamed()+""));
            mLevelTwoUserVerify.setText(String.format(getString(R.string.has_verify_user), mUser.getReferral_TwoLevelRealNamed()+""));
        } else {
            mProfileView.setEnabled(false);
            mName.setText(R.string.unlogin);
            mCheckDetail.setVisibility(View.INVISIBLE);
            mTotalMoney.setText("");
            mSpreadBonus.setText("0 ees");
            mMiningBonus.setText("0 ees");
            mTotalUser.setText("");
            mLevelOneUser.setText("0");
            mLevelTwoUser.setText("0");
            mLevelOneUserVerify.setText(String.format(getString(R.string.has_verify_user), "0"));
            mLevelTwoUserVerify.setText(String.format(getString(R.string.has_verify_user), "0"));
            mLevelOneUserVerify.setVisibility(View.GONE);
            mLevelTwoUserVerify.setVisibility(View.GONE);
        }

        if (App.mTransferShowHide == 0) {
            mChargeView.setVisibility(View.GONE);
        } else {
            mChargeView.setVisibility(View.VISIBLE);
        }

        String cVersionName = Utils.getCurrentVersionName(getActivity());
        if (!StringUtils.isBlank(mNewestVersionName) && !mNewestVersionName.equalsIgnoreCase(cVersionName)) {
            mNewVersion.setVisibility(View.VISIBLE);
            mVersion.setText(String.format(getString(R.string.new_version), mNewestVersionName));
        } else {
            mNewVersion.setVisibility(View.GONE);
        }
        refreshSettingView();
    }

    @OnClick({R.id.profile_view, R.id.money_view, R.id.charge, R.id.order, R.id.wallet, R.id.setting, R.id.new_version})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_view:
                Intent intent = new Intent();
                intent.setClass(getActivity(), EditProfileActivity.class);
                getActivity().startActivityForResult(intent, 1);
                break;
            case R.id.charge:
                if (mUser!=null && mUser.getRna_Status() == 1) {
                    if (StringUtils.isBlank(App.mTransferUrl)) {
                        getActivity().startActivityForResult(new Intent(getActivity(), ChargeActivity.class), 1);
                    } else {
                        Find find = new Find();
                        find.setTitle(getString(R.string.charge));
                        find.setTarget(Find.TARGET_DEFAULT);
                        find.setLink(App.mTransferUrl);
                        doAction(find);
                    }
                } else {
                    Snackbar.make(mName, getString(R.string.err_not_verify_charge_hint), Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.money_view:
                Intent intent2 = new Intent();
                intent2.setClass(getActivity(), BonusActivity.class);
                getActivity().startActivity(intent2);
                break;
//            case R.id.share:
//                Intent intent7 = new Intent();
//                intent7.setAction("android.intent.action.VIEW");
//                String url2 = App.SHARE_EES;
//                if (mUser != null) {
//                    url2 += mUser.getId();
//                }
//                Uri content_url2 = Uri.parse(url2);
//                intent7.setData(content_url2);
//                startActivity(intent7);
//                break;
//            case R.id.kefu:
//                Intent intent4 = new Intent(getActivity(), WebviewActivity.class);
//                intent4.putExtra("title", getString(R.string.kefu_online));
//                intent4.putExtra("url", App.KEFU);
//                getActivity().startActivity(intent4);
//                break;
//            case R.id.feedback:
//                Intent intent6 = new Intent();
//                intent6.setAction("android.intent.action.VIEW");
//                String url = App.FEEDBACK;
//                if (mUser != null) {
//                    url += mUser.getPid();
//                }
//                Uri content_url = Uri.parse(url);
//                intent6.setData(content_url);
//                startActivity(intent6);
//                break;
//            case R.id.notice:
//                Intent intent8 = new Intent(getActivity(), WebviewActivity.class);
//                intent8.putExtra("title", getString(R.string.declaration));
//                intent8.putExtra("url", App.DECLARATION);
//                getActivity().startActivity(intent8);
//                break;
            case R.id.order:
                Intent intent9 = new Intent(getActivity(), OrderListActivity.class);
                intent9.putExtra("title", getString(R.string.my_order));
                getActivity().startActivity(intent9);
                break;
            case R.id.wallet:
//                String not_wallet = getString(R.string.notice_wallet);
//                shwoWalletDialog(getActivity(), not_wallet);
                Intent int2 = new Intent();
                int2.putExtra("title", getString(R.string.wallet));
                int2.putExtra("id", "111");
                int2.putExtra("link", App.mWalletUrl);
                int2.setClass(mContext, WebviewActivity.class);
                mContext.startActivity(int2);
                break;
//            case R.id.about:
//                Intent intent5 = new Intent(getActivity(), WebviewActivity.class);
//                intent5.putExtra("title", getString(R.string.about));
//                intent5.putExtra("url", App.ABOUT);
//                getActivity().startActivity(intent5);
//                break;
            case R.id.setting:
                Intent intent10 = new Intent(getActivity(), SettingActivity.class);
                intent10.putExtra("title", getString(R.string.setting));
                getActivity().startActivity(intent10);
                break;
            case R.id.new_version:
                Intent intent11 = new Intent();
                intent11.setAction("android.intent.action.VIEW");
                Uri version_url = Uri.parse(App.BASE_NEW_VERSION_URL);
                intent11.setData(version_url);
                startActivity(intent11);
                break;
        }
    }

    public void shwoCopyDialog(final Context context, final String content) {
        if (StringUtils.isBlank(content)) {
            return;
        }
        CommomDialog dialog = new CommomDialog(getContext(), R.style.dialog, content, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setText(content);
                    Snackbar.make(mName, getString(R.string.copy_success), Snackbar.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton(getString(R.string.copy));
        dialog.setTitle(getString(R.string.tips)).show();
        dialog.setNegativeButtonVisible(View.GONE);
    }

    public void shwoWalletDialog(final Context context, final String content) {
        if (StringUtils.isBlank(content)) {
            return;
        }
        CommomDialog dialog = new CommomDialog(context, R.style.dialog, content, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){

                }
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton(getString(R.string.ok));
        dialog.setTitle(getString(R.string.tips)).show();
        dialog.setNegativeButtonVisible(View.GONE);
    }

    public void refreshSettingView() {
        if (mSettings != null && mSettings.size()>0) {
            if (mSettings.size()>0) {
                mSetting1.setVisibility(View.VISIBLE);
                mSettingCover1.setImageURI(mSettings.get(0).getIcon());
                mSettingTitle1.setText(mSettings.get(0).getTitle());
                mSetting1.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        doAction(mSettings.get(0));
                    }
                });
            }
            if (mSettings.size()>1) {
                mSetting2.setVisibility(View.VISIBLE);
                mSettingCover2.setImageURI(mSettings.get(1).getIcon());
                mSettingTitle2.setText(mSettings.get(1).getTitle());
                mSetting2.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        doAction(mSettings.get(1));
                    }
                });
            }
            if (mSettings.size()>2) {
                mSetting3.setVisibility(View.VISIBLE);
                mSettingCover3.setImageURI(mSettings.get(2).getIcon());
                mSettingTitle3.setText(mSettings.get(2).getTitle());
                mSetting3.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        doAction(mSettings.get(2));
                    }
                });
            }
            if (mSettings.size()>3) {
                mSetting4.setVisibility(View.VISIBLE);
                mSettingCover4.setImageURI(mSettings.get(3).getIcon());
                mSettingTitle4.setText(mSettings.get(3).getTitle());
                mSetting4.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        doAction(mSettings.get(3));
                    }
                });
            }
            if (mSettings.size()>4) {
                mSetting5.setVisibility(View.VISIBLE);
                mSettingCover5.setImageURI(mSettings.get(4).getIcon());
                mSettingTitle5.setText(mSettings.get(4).getTitle());
                mSetting5.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        doAction(mSettings.get(4));
                    }
                });
            }
            if (mSettings.size()>5) {
                mSetting6.setVisibility(View.VISIBLE);
                mSettingCover6.setImageURI(mSettings.get(5).getIcon());
                mSettingTitle6.setText(mSettings.get(5).getTitle());
                mSetting6.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        doAction(mSettings.get(5));
                    }
                });
            }
            if (mSettings.size()>6) {
                mSetting7.setVisibility(View.VISIBLE);
                mSettingCover7.setImageURI(mSettings.get(6).getIcon());
                mSettingTitle7.setText(mSettings.get(6).getTitle());
                mSetting7.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        doAction(mSettings.get(6));
                    }
                });
            }
            if (mSettings.size()>7) {
                mSetting8.setVisibility(View.VISIBLE);
                mSettingCover8.setImageURI(mSettings.get(7).getIcon());
                mSettingTitle8.setText(mSettings.get(7).getTitle());
                mSetting8.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        doAction(mSettings.get(7));
                    }
                });
            }
        }
    }

    public void doAction(Find find) {
        String target = find.getTarget();
        String url = find.getLink();
        Intent intent = new Intent();
        if (Find.TARGET_BROWSER.equals(target)) {
            intent.setAction("android.intent.action.VIEW");
            Uri content_url2 = Uri.parse(url);
            intent.setData(content_url2);
            mContext.startActivity(intent);
        } else {
            intent.putExtra("title", find.getTitle());
            intent.putExtra("id", mUser.getId());
            if (Find.TARGET_APP.equals(target)) {
                intent.putExtra("url", url);
            } else if (Find.TARGET_DEFAULT.equals(target)) {
                intent.putExtra("link", url);
            }
            intent.putExtra("fontlarge", 1);
            intent.setClass(mContext, WebviewActivity.class);
            mContext.startActivity(intent);
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
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.finishRefresh();
        }
    }

    @Override
    public void complete() {
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.finishRefresh();
        }
    }

    @Override
    public void showLedgerRefreshSucess(Ledger data, long version) {
        if (data != null) {
            mLedger = data;
            App.getInstance().setLedger(data);
            configViews();
        }
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.finishRefresh();
        }
    }

    @Override
    public void showLedgerRefreshFail(Error err, long version) {
        if (Error.CODE_SYNC_EQUAL.equals(err.getCode())) {
            showLedgerRefreshSucess(App.getInstance().getLedger(), version);
        } else {
            Snackbar.make(mName, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        }
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.finishRefresh();
        }
        checkError(err);
    }

    @Override
    public void syncSuccess() {
        configViews();
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.finishRefresh();
        }
        sendUpdateUserInfoBroadcast();
    }

    @Override
    public void synFail(Error err) {
        if (Error.CODE_SYNC_EQUAL.equals(err.getCode())) {

        }
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.finishRefresh();
        }
        checkError(err);
    }

    @Override
    public void getSettinglistSuccess(List<Find> data) {
        mSettings = data;
        refreshSettingView();
    }

    @Override
    public void getSettinglistFail(Error err) {
        checkError(err);
    }

    public static void sendUpdateUserInfoBroadcast() {
        EventBus.getDefault().post(new UpdateUserInfoEvent());
    }
}
