package com.ees.chain.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.domain.Battery;
import com.ees.chain.domain.Config;
import com.ees.chain.domain.Error;
import com.ees.chain.domain.Ledger;
import com.ees.chain.domain.MineType;
import com.ees.chain.domain.Notice;
import com.ees.chain.domain.User;
import com.ees.chain.event.UpdateConsumeEvent;
import com.ees.chain.manager.CacheManager;
import com.ees.chain.server.BatteryHelper;
import com.ees.chain.server.LongService;
import com.ees.chain.task.group.MainPresenter;
import com.ees.chain.ui.base.BaseActivity;
import com.ees.chain.ui.base.BaseFragment;
import com.ees.chain.ui.fragment.FindFragment;
import com.ees.chain.ui.fragment.HomeFragment;
import com.ees.chain.ui.fragment.HomeRegFragment;
import com.ees.chain.ui.fragment.MessageFragment;
import com.ees.chain.ui.fragment.PersonFragment;
import com.ees.chain.ui.interfc.MainContract;
import com.ees.chain.utils.BatteryHelp;
import com.ees.chain.utils.LogUtils;
import com.ees.chain.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by KESION on 2017/12/4.
 */
public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {
    private static final String TAG = "MainActivity";

    @BindView(R.id.home)
    View main;
    @BindView(R.id.home_img)
    ImageView mainImg;
    @BindView(R.id.home_txt)
    TextView mainTxt;
    @BindView(R.id.person)
    View person;
    @BindView(R.id.person_img)
    ImageView personImg;
    @BindView(R.id.person_txt)
    TextView personTxt;
    @BindView(R.id.msg)
    View msg;
    @BindView(R.id.msg_img)
    ImageView msgImg;
    @BindView(R.id.msg_txt)
    TextView msgTxt;
    @BindView(R.id.find)
    View find;
    @BindView(R.id.find_img)
    ImageView findImg;
    @BindView(R.id.find_txt)
    TextView findTxt;

    @BindView(R.id.home_noti)
    View mHomeNoti;
    @BindView(R.id.msg_noti)
    View mMsgNoti;
    @BindView(R.id.find_noti)
    View mFindNoti;
    @BindView(R.id.person_noti)
    View mPersonNoti;

    public BaseFragment mHomeFragment;
    public BaseFragment mMsgFragment;
    public BaseFragment mFindFragment;
    public BaseFragment mPersonFragment;
    public BaseFragment mLastFragment;
    public int mCurrentTabId = R.id.home;

    private User mUser;

    private boolean haseNewVersion = false;

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
//        saveBatteryLevel();
        randomBattery();
    }

    /**
     * 时间维度上做的能量收集补偿
     */
    public void randomBattery() {
        final long itime = 15 * 60 * 1000;
        long ctime = System.currentTimeMillis();
        long ltime = CacheManager.getInstance().getLastTime();
        if (ltime == 0) {
            CacheManager.getInstance().saveLastTime(ctime);
            return;
        }
        if (ctime - ltime < itime) {
            return;
        }
        long mc = (ctime-ltime) / itime;
        final int limit = 25;
        if (mc >= limit) mc = limit;
        CacheManager.getInstance().saveLastTime(ctime);

        Battery battery = App.getInstance().getBattery();
        if (battery == null) battery = new Battery();
        int cha = 0;
        for (int i=0; i<mc; i++) {
            int r = new Random().nextInt(9);
            cha += r;
        }
        battery.addComsume(cha);
        CacheManager.getInstance().saveBattery(battery);
        EventBus.getDefault().post(new UpdateConsumeEvent());

        if (App.isLogEnable) {
            Toast.makeText(this, "main cha " + cha, Toast.LENGTH_SHORT).show();
        }
    }

    public void saveBatteryLevel() {
        BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            if (level==0) {
                return;
            }
            if (App.isLogEnable) {
                Toast.makeText(this, "Main level " + level, Toast.LENGTH_LONG).show();
            }
            Battery battery = App.getInstance().getBattery();
            if (battery == null) battery = new Battery();
            if (battery.getBatteryCapacity() == 0) {
                long capactiy = BatteryHelp.getBatteryCapacity(App.getInstance().getApplicationContext());
                if (capactiy == 0) capactiy = 3000;
                battery.setBatteryCapacity(capactiy);
            }
            if (battery.getLocalLevel() == 0) {
                battery.setLocalLevel(level);
                CacheManager.getInstance().saveBattery(battery);
                return;
            }
            long cha = battery.getLocalLevel() - level;
            LogUtils.d("MainActivty cha：" + cha);
            if (cha > 0 && cha < 100) {
                LogUtils.d("MainActivty consume change " + cha + ", " + battery.getComsume());
                battery.setLocalLevel(level);
    //                      battery.addRecord(level);
                cha = (cha * battery.getBatteryCapacity() / 100) + new Random().nextInt(9);
                battery.addComsume(cha);
                CacheManager.getInstance().saveBattery(battery);
            } else if (cha < 0) {
                //充电结束
                LogUtils.d("MainActivty sonsume change " + cha + ", " + battery.getComsume());
                battery.setLocalLevel(level);
    //                      battery.addRecord(level);
                CacheManager.getInstance().saveBattery(battery);
            }
            if (App.isLogEnable) {
                Toast.makeText(this, "Main battery consume " + battery.getComsume() + ", " + battery.getLocalLevel(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initDatas() {
        LogUtils.d("MainActivity initDatas");
        mUser = App.getInstance().getUser();

        if (mUser != null && !StringUtils.isBlank(mUser.getToken())) {
            mPresenter.getConfigList(mUser.getPid());
            mPresenter.userSync(mUser.getPid(), mUser.getVersion());
            mPresenter.getHuodong(mUser.getPid());
            Ledger ledger = App.getInstance().getLedger();
            long version = 0;
            if (ledger != null) {
                version = ledger.getVersion();
            }
            mPresenter.ledgerRefresh(mUser.getPid(), version);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        App.changeSkn = CacheManager.getInstance().getSknChangeSwitch();
        Utils.registerAlarmNotification(this);
    }

    public void startMiningService() {
        if (mUser != null && mUser.getRna_Status() == 1) {//实名认证成功
            Intent intent = new Intent(this, LongService.class);
            startService(intent);
        }
    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        LogUtils.d("MainActivity configViews");
        if (App.changeSkn == 1) {
            mainImg.setImageResource(R.drawable.tab_main_selector1);
            personImg.setImageResource(R.drawable.tab_person_selector1);
            msgImg.setImageResource(R.drawable.tab_msg_selector1);
            findImg.setImageResource(R.drawable.tab_find_selector1);
        }
        changeTab(mCurrentTabId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        //刷新首页UI
//        if (mCurrentTabId == R.id.home && mUser!=null && mUser.getRna_Status() == 2) {
//            changeTab(R.id.home);
//        }
        startMiningService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @OnClick({R.id.person, R.id.home, R.id.msg, R.id.find})
    public void clickTab(View view) {
        changeTab(view.getId());
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void showLedgerRefreshSucess(Ledger data, long version) {
        if (data != null) {
            App.getInstance().setLedger(data);
        }
    }

    @Override
    public void showLedgerRefreshFail(Error err, long version) {
        LogUtils.d(err.getMessage());
    }

    @Override
    public void syncSuccess() {
        haseNewVersion = true;
    }

    @Override
    public void synFail(Error err) {
//        Snackbar.make(main, err, Snackbar.LENGTH_SHORT).show();
//        startActivity(new Intent(this, LoginActivity.class));
//        finish();
        if (Error.CODE_SYNC_EQUAL.equals(err.getCode())) {

        }
        checkError(err);
    }

    @Override
    public void showHuodong(Object obj) {
//        dismissLoadingDialog();
        if (obj != null) {
            Intent intent = new Intent();
            intent.putExtra(HuodongActivity.ARG_NOTICE, (Notice) obj);
            intent.setClass(this, HuodongActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void showConfigListSuccess(List<Config> configs) {
//        for (Config config : configs) {
//            if (config != null) {
//                String key = config.getKey();
//                if (Config.KEY_START_MINING_TIME.equals(key)) {
//                    CacheManager.getInstance().saveMiningRang(config.getValue());
//                } else if (Config.KEY_SUBMIT_MINING_TIME.equals(key)) {
//                    CacheManager.getInstance().saveSubmitRang(config.getValue());
//                } else if (Config.KEY_ANDROID_URL.equals(key)) {
//                    CacheManager.getInstance().saveAndroidUrl(config.getValue());
//                } else if (Config.KEY_SHARE_DESC.equals(key)) {
//                    CacheManager.getInstance().saveShareDesc(config.getValue());
//                } else if (Config.KEY_SHARE_URL.equals(key)) {
//                    CacheManager.getInstance().saveShareUrl(config.getValue());
//                } else if (Config.KEY_MINING_NOTICE.equals(key)) {
//                    CacheManager.getInstance().saveMiningNotice(config.getValue());
//                }
//            }
//        }
    }

    @Override
    public void showConfigListFail(Error err) {
        LogUtils.d("showConfigListFail." + err.getCode() + ", " + err.getMessage());
        checkError(err);
    }

    public void changeTab(int tabId) {
        LogUtils.i("click tab " + tabId);
        if (App.changeSkn == 1) {
            mainTxt.setTextColor(getResources().getColor(R.color.grey_40));
            msgTxt.setTextColor(getResources().getColor(R.color.grey_40));
            findTxt.setTextColor(getResources().getColor(R.color.grey_40));
            personTxt.setTextColor(getResources().getColor(R.color.grey_40));
        }

        mUser = App.getInstance().getUser();
//        if (mUser != null && (mUser.getRna_Status() == 2 || mUser.getRna_Status() == 3)) {
//            //实名认证审核中
//            mPresenter.userSync(mUser.getPid(), mUser.getVersion());
//        }
        if (mUser != null && haseNewVersion) {
            mHomeFragment = null;
            haseNewVersion = false;
        }
        startMiningService();

        BaseFragment newFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tabId + "");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        hideAllFragments(ft);
        switch (tabId) {
            case R.id.home:
                main.setSelected(true);
                msg.setSelected(false);
                find.setSelected(false);
                person.setSelected(false);
                if (App.changeSkn == 1) {
                    mHomeNoti.setVisibility(View.VISIBLE);
                    mMsgNoti.setVisibility(View.INVISIBLE);
                    mFindNoti.setVisibility(View.INVISIBLE);
                    mPersonNoti.setVisibility(View.INVISIBLE);
                }
                if (App.changeSkn == 1) {
                    Utils.setWindowStatusBarColor(this, R.color.red);
                    mainTxt.setTextColor(getResources().getColor(R.color.red));
                } else {
                    Utils.setWindowStatusBarColor(this, R.color.green_23);
                }
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
//                    if (mUser != null && mUser.getRna_Status() == 1) {
//                        mHomeFragment = new HomeFragment();
//                    } else {
//                        mHomeFragment = new HomeRegFragment();
//                    }
                }

                newFragment = mHomeFragment;
                break;
            case R.id.msg:
                Utils.setWindowStatusBarColor(this, R.color.white);
                main.setSelected(false);
                msg.setSelected(true);
                find.setSelected(false);
                person.setSelected(false);
                if (App.changeSkn == 1) {
                    mHomeNoti.setVisibility(View.INVISIBLE);
                    mMsgNoti.setVisibility(View.VISIBLE);
                    mFindNoti.setVisibility(View.INVISIBLE);
                    mPersonNoti.setVisibility(View.INVISIBLE);
                    msgTxt.setTextColor(getResources().getColor(R.color.red));
                }
                if (mMsgFragment == null) {
                    mMsgFragment = new MessageFragment();
                }
                newFragment = mMsgFragment;
                break;
            case R.id.find:
                Utils.setWindowStatusBarColor(this, R.color.white);
                main.setSelected(false);
                msg.setSelected(false);
                find.setSelected(true);
                person.setSelected(false);
                if (App.changeSkn == 1) {
                    mHomeNoti.setVisibility(View.INVISIBLE);
                    mMsgNoti.setVisibility(View.INVISIBLE);
                    mFindNoti.setVisibility(View.VISIBLE);
                    mPersonNoti.setVisibility(View.INVISIBLE);
                    findTxt.setTextColor(getResources().getColor(R.color.red));
                }
                if (mFindFragment == null) {
                    mFindFragment = new FindFragment();
                }
                newFragment = mFindFragment;
                break;
            case R.id.person:
                Utils.setWindowStatusBarColor(this, R.color.white);
                main.setSelected(false);
                msg.setSelected(false);
                find.setSelected(false);
                person.setSelected(true);
                if (App.changeSkn == 1) {
                    mHomeNoti.setVisibility(View.INVISIBLE);
                    mMsgNoti.setVisibility(View.INVISIBLE);
                    mFindNoti.setVisibility(View.INVISIBLE);
                    mPersonNoti.setVisibility(View.VISIBLE);
                    personTxt.setTextColor(getResources().getColor(R.color.red));
                }
                if (mPersonFragment == null) {
                    mPersonFragment = new PersonFragment();
                }
                newFragment = mPersonFragment;
                break;
        }

        if (!newFragment.isAdded()) { // 先判断是否被add过
            if (mLastFragment == null) {
                ft.add(R.id.container, newFragment, tabId + "");
//                ft.addToBackStack(tabId + "");
                ft.commit();
            } else {
                ft.hide(mLastFragment).add(R.id.container, newFragment, tabId + "");
//                ft.addToBackStack(tabId + "");
                ft.commit(); // 隐藏当前的fragment，add下一个到Activity中
            }
        } else {
            if (mLastFragment == null) {
                ft.show(newFragment).commit();
            } else {
                ft.hide(mLastFragment).show(newFragment).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
        mLastFragment = newFragment;
        mCurrentTabId = tabId;
        //刷新用户信息
//        if (mCurrentTabId == R.id.person) {
//            EventBus.getDefault().post(new UpdateUserInfoEvent());
//        }
    }

    private void hideAllFragments(FragmentTransaction transaction) {

        if (mHomeFragment != null) {
            transaction.hide(mHomeFragment);
        }
        if (mPersonFragment != null) {
            transaction.hide(mPersonFragment);
        }
        if (mFindFragment != null) {
            transaction.hide(mFindFragment);
        }
        if (mMsgFragment != null) {
            transaction.hide(mMsgFragment);
        }
    }

    //fix fragment 重叠bug
    @Override
    public void onAttachFragment(Fragment fragment) {
        if (mHomeFragment == null && fragment instanceof HomeFragment)
            mHomeFragment = (BaseFragment) fragment;
        if (mHomeFragment == null && fragment instanceof HomeRegFragment)
            mHomeFragment = (BaseFragment) fragment;
        if (mMsgFragment == null && fragment instanceof MessageFragment)
            mMsgFragment = (BaseFragment) fragment;
        if (mFindFragment == null && fragment instanceof FindFragment)
            mFindFragment = (BaseFragment) fragment;
        if (mPersonFragment == null && fragment instanceof PersonFragment)
            mPersonFragment = (BaseFragment) fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1) {
            finish();
        } else if (requestCode == 1 && resultCode == 2) {
            if (mHomeFragment != null) {
                mHomeFragment.initDatas();
                mHomeFragment.configViews();
            }
        } else if (requestCode == 1 && resultCode == 3) {
            if (mPersonFragment != null) {
                mPersonFragment.initDatas();
                mPersonFragment.configViews();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mCurrentTabId != R.id.home) {
            changeTab(R.id.home);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unRegisterReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d("MainActivity onDestroy");
//        getFragmentManager().popBackStack();
        mHomeFragment = null;
        mMsgFragment = null;
        mFindFragment = null;
        mPersonFragment = null;
        mLastFragment = null;
    }

    private BatteryReceiver mBatteryReceiver;
    private boolean isreg = false;

    public void registerReceiver() {
        try {
            if (!isreg) {
                LogUtils.d("BatteryChangeReceiver register.");
                isreg = true;
                IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                mBatteryReceiver = new BatteryReceiver();
                this.registerReceiver(mBatteryReceiver, intentFilter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unRegisterReceiver() {
        try {
            if (isreg) {
                LogUtils.d("BatteryChangeReceiver unregister.");
                isreg = false;
                if (mBatteryReceiver != null) unregisterReceiver(mBatteryReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_CHANGED)) {
                // 当前手机使用的是哪里的电源
                final int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
                // 电池当前的电量, 它介于0和 EXTRA_SCALE之间
                final int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                final int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
                BatteryHelper.doBattery(status, level, chargePlug);
            }
        }
    }
}
