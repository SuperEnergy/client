package com.ees.chain.ui.fragment;


import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.ble.BLEDevice;
import com.ees.chain.ble.BleHelper;
import com.ees.chain.ble.BleScanResultCallback;
import com.ees.chain.ble.Callback;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.domain.Battery;
import com.ees.chain.domain.CoinMarket;
import com.ees.chain.domain.Error;
import com.ees.chain.domain.Fund;
import com.ees.chain.domain.Goods;
import com.ees.chain.domain.MineType;
import com.ees.chain.domain.Mining;
import com.ees.chain.domain.Notice;
import com.ees.chain.domain.User;
import com.ees.chain.domain.UserMine;
import com.ees.chain.event.ClickNewMsgEvent;
import com.ees.chain.event.RenameDeviceEvent2;
import com.ees.chain.event.UpdateChargePlugginEvent;
import com.ees.chain.event.UpdateChargeUnplugginEvent;
import com.ees.chain.event.UpdateConsumeEvent;
import com.ees.chain.event.UpdateNewstNoticeEvent;
import com.ees.chain.event.UpdateUserInfoEvent;
import com.ees.chain.event.UpdateUserMineChangeEvent;
import com.ees.chain.event.UpdateVolumeEvent;
import com.ees.chain.manager.CacheManager;
import com.ees.chain.task.group.HomePresenter;
import com.ees.chain.task.group.MsgPresenter;
import com.ees.chain.ui.activity.CoinMarketListActivity;
import com.ees.chain.ui.activity.GoodsListActivity;
import com.ees.chain.ui.activity.MainActivity;
import com.ees.chain.ui.activity.MineListActivity;
import com.ees.chain.ui.activity.ShareActivity;
import com.ees.chain.ui.activity.UserLineListActivity;
import com.ees.chain.ui.activity.UserMineListActivity;
import com.ees.chain.ui.activity.VerifyOneActivity;
import com.ees.chain.ui.base.BaseFragment;
import com.ees.chain.ui.interfc.HomeContract;
import com.ees.chain.ui.view.support.CommomDialog;
import com.ees.chain.ui.view.support.WUTypeFaceSongSanTextView;
import com.ees.chain.utils.BLEAction;
import com.ees.chain.utils.LogUtils;
import com.ees.chain.utils.MyAnimationUtils;
import com.ees.chain.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * Created by KESION on 2017/12/5.
 */
public class HomeFragment extends BaseFragment<HomePresenter> implements HomeContract.View {
    private final String TAG = "HomeFragment";

    @BindView(R.id.cover_bg)
    ImageView mCoverBg;
    @BindView(R.id.cover0)
    SimpleDraweeView mCover0;
    @BindView(R.id.cover)
    SimpleDraweeView mCover;
    @BindView(R.id.mining)
    Button mMining;
    @BindView(R.id.task1)
    View mTask1;
    @BindView(R.id.task2)
    View mTask2;
    @BindView(R.id.msg_view0)
    View mMsgView0;
    @BindView(R.id.msg_view1)
    View mMsgView;
    @BindView(R.id.msg_content1)
    TextView mMsgContent;
    @BindView(R.id.msg_view2)
    View mMsgView2;
    @BindView(R.id.msg_content2)
    TextView mMsgContent2;
    @BindView(R.id.close)
    View mClose;
    @BindView(R.id.capacity)
    Button mCapacity;
    @BindView(R.id.speedRate)
    Button mSpeedRate;
    @BindView(R.id.percent)
    Button mPercent;
    @BindView(R.id.chart_top)
    LineChartView chartTop;
    @BindView(R.id.chart_bottom)
    ColumnChartView chartBottom;
    @BindView(R.id.mining_type)
    TextView mMiningType;
    @BindView(R.id.capacity_view)
    View mCapacityView;
    @BindView(R.id.current_capacity)
    WUTypeFaceSongSanTextView mCurrentCapacity;
    @BindView(R.id.limit_capacity)
    TextView mLimitCapacity;
    @BindView(R.id.submit_mining)
    Button mSubmitMining;
    @BindView(R.id.msg_view3)
    View mPlugingWarningView;
    @BindView(R.id.pluggin)
    View mPluggin;
    @BindView(R.id.msg_copy)
    View mCopyMsg;
    @BindView(R.id.sys_msg_view)
    View mSysMsgView;
    @BindView(R.id.sys_msg_cover)
    SimpleDraweeView mSysMsgCover;
    @BindView(R.id.sys_msg_title)
    TextView mSysMsgTitle;
    @BindView(R.id.market_view)
    View mMarketView;
    @BindView(R.id.name)
    TextView mMarketName;
    @BindView(R.id.refresh)
    View mRefresh;
    @BindView(R.id.cPrice1)
    WUTypeFaceSongSanTextView mPrice1;
    @BindView(R.id.unit)
    TextView mUnit;
    @BindView(R.id.cPrice2)
    TextView mPrice2;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.subtitle)
    TextView mSubtitle;
    @BindView(R.id.arrow)
    View mArrow;
    @BindView(R.id.msg_view4)
    View mMsgView4;
    @BindView(R.id.btn_mine4)
    View mBtnRefresh4;
    @BindView(R.id.base_capacity)
    TextView mBaseCapacity;


//    private int mMiningStatus = 0;  //0未开始挖矿，1挖矿中，2提交矿藏,3矿藏已提交

    public final int LENGTH = 7;
    public String[] mDays1 = new String[]{"昨日"};
    public String[] mDays2 = new String[]{"昨日"};

    public double[] mDatas1 = new double[]{0};
    public int[] mDatas2 = new int[]{0};

    private LineChartData lineData;
    private ColumnChartData columnData;
    private User mUser;
    private List<Fund> mSevenFouds;
    private List<Mining> mSevenMining;
    private UserMine mCurrentUserMine;
    private CoinMarket mCoinMarket;

    private long bcapacity; //空桶容量

    private ArrayList<String> mLastSevenDate = new ArrayList<String>();//不包括当天

    private HashMap<String, Double> mSevenFoudsData = new HashMap<String, Double>();

    private HashMap<String, Integer> mSevenMiningData = new HashMap<String, Integer>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mDeviceList != null) mDeviceList.clear();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initDatas() {
        mUser = App.getInstance().getUser();
        mCurrentUserMine = App.getInstance().getCurrentUserMine();
        mPresenter.getUserMineList(mUser.getPid());
        mPresenter.getNewestMsg(mUser.getPid());
        mPresenter.getCoinMarket(mUser.getPid());

//        int status = Mining.STATUS_NOT_START;
//        if (mCurrentUserMine!=null) {
//            status = mCurrentUserMine.getStatus();
//        }
//        mMiningStatus = status;
        mLastSevenDate = Utils.getPastSevenDate();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        startScanBLE();
    }

    @Override
    public void configViews() {
        mPluggin.setVisibility(View.INVISIBLE);
        mMiningType.setVisibility(View.VISIBLE);
        mPlugingWarningView.setVisibility(View.GONE);
        mBaseCapacity.setVisibility(View.INVISIBLE);
        chartTop.setInteractive(false);
        chartBottom.setInteractive(false);

        if (App.changeSkn == 1) {
            mCoverBg.setBackgroundResource(R.drawable.cover_bg);
        }
        Uri uri = Uri.parse("res://" + getContext().getPackageName() + "/" + R.drawable.mininging_animation);
        DraweeController mDraweeController = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                //设置uri,加载本地的gif资源
                .setUri(uri)
                .build();
        mCover.setController(mDraweeController);

        if (mCurrentUserMine != null && mCurrentUserMine.getMineType().getType() != MineType.TYPE_APP) {
            mMsgView4.setVisibility(View.VISIBLE);
        } else {
            mMsgView4.setVisibility(View.GONE);
        }

        if (mCurrentUserMine != null) {
            bcapacity = mCurrentUserMine.getMaxQtyLimit();
            double speedRate = mCurrentUserMine.getSpeedRate();
            speedRate = Utils.doubleFormat(speedRate);
            if (mCurrentUserMine != null) {
                speedRate = mCurrentUserMine.getSpeedRate();
                speedRate = speedRate <= 0.0 ? 0 : speedRate;
            }
            long consume = (long) (mCurrentUserMine.consume * speedRate);
            double percent = 0.0;
            if (bcapacity != 0) {
                percent = (double) consume * 100.0 / bcapacity;
            }
            percent = Utils.doubleFormat(percent);
            mPercent.setText(String.format(getString(R.string.mining_percent), percent + " %"));
            mCurrentCapacity.setText((int) (consume * speedRate) + "");

            if (speedRate > 1.0) {
                mBaseCapacity.setVisibility(View.VISIBLE);
                mBaseCapacity.setText(String.format(getString(R.string.base_capacity), mCurrentUserMine.consume+"", speedRate+""));
            }
            mCapacity.setText(String.format(getString(R.string.mining_capacity), bcapacity + ""));
            mSpeedRate.setText(String.format(getString(R.string.mining_speedrate), speedRate + ""));
            mLimitCapacity.setText(String.format(getString(R.string.mining_capacity2), bcapacity + ""));
            mMiningType.setText(String.format(getString(R.string.mining_type), mCurrentUserMine.getName()));
        } else {
            mCapacity.setText(String.format(getString(R.string.mining_capacity), "0"));
            mSpeedRate.setText(String.format(getString(R.string.mining_speedrate), "0.0"));
            mPercent.setText(String.format(getString(R.string.mining_percent), "0.0 %"));
            mLimitCapacity.setText(String.format(getString(R.string.mining_capacity2), "0"));
            mMiningType.setText(String.format(getString(R.string.mining_type), "APP"));
            mCurrentCapacity.setText("0");
        }

        mCapacity.setEnabled(true);
        mSpeedRate.setEnabled(true);
        mPercent.setEnabled(false);

        initLineChart(mDays1, mDatas1);
        initColumnChart(mDays2, mDatas2);
    }

    private boolean isCurrentUserMineChange = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveUpdateUserMineChangeEvent(UpdateUserMineChangeEvent event) {
        mCurrentUserMine = App.getInstance().getCurrentUserMine();
        if (mCurrentUserMine != null && mCurrentUserMine.getMineType().getType() == MineType.TYPE_APP) {
            Battery battery = App.getInstance().getBattery();
            if (battery == null) battery = new Battery();
            mCurrentUserMine.consume = battery.getComsume();
        }
        CacheManager.getInstance().clearSevenFoudVersion();
        CacheManager.getInstance().clearSevenMiningVersion();
        CacheManager.getInstance().clearSevenMining();
        CacheManager.getInstance().clearSevenFoud();
        isCurrentUserMineChange = true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveRenameDeviceEvent2(RenameDeviceEvent2 event) {
        mCurrentUserMine = App.getInstance().getCurrentUserMine();
        if (mCurrentUserMine != null)
            mMiningType.setText(String.format(getString(R.string.mining_type), mCurrentUserMine.getName()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveUpdateConsumeEvent(UpdateConsumeEvent event) {
        if (mCurrentUserMine != null && mCurrentUserMine.getMineType().getType() == MineType.TYPE_APP) {
            Battery battery = App.getInstance().getBattery();
            if (battery == null) battery = new Battery();
            mCurrentUserMine.consume = battery.getComsume();
            resetMiningStatus();
            refreshComsumeView();
            showPlugginView(false);
            CacheManager.getInstance().saveCurrentUserMine(mCurrentUserMine);
            refreshUserMineListConsume();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveUpdatePlugginEvent(UpdateChargePlugginEvent event) {
        showPlugginView(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveUpdateUnplugginEvent(UpdateChargeUnplugginEvent event) {
        showPlugginView(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveUpdateNewstNoticeEvent(UpdateNewstNoticeEvent event) {
        if (mMsgContent != null)
            mMsgContent.setText(App.getInstance().mCurrentNewestMsg.getExcerpt());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveUpdateVolumeEvent(UpdateVolumeEvent event) {
        if (mPresenter != null && mUser != null) mPresenter.getUserMineList(mUser.getPid());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveUpdateUserInfoEvent(UpdateUserInfoEvent event) {
        mUser = App.getInstance().getUser();
        refreshTaskView();
    }

    private boolean mBtnRefresh4Enable = true;

    @OnClick({R.id.mining, R.id.close, R.id.close2, R.id.msg_content0, R.id.msg_view1, R.id.cover, R.id.cover0,
            R.id.submit_mining, R.id.msg_copy, R.id.capacity, R.id.speedRate, R.id.mining_type,
            R.id.task1, R.id.task2, R.id.refresh, R.id.market_view, R.id.sys_msg_view, R.id.btn_mine4, R.id.share, R.id.layout_bind_line})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cover:
            case R.id.cover0:
            case R.id.mining:
                switch (mCurrentUserMine.miningStatus) {
                    case Mining.STATUS_NOT_START:
                        if (isStartMiningRange()) {
                            mCover.setEnabled(false);
                            mCover0.setEnabled(false);
                            mMining.setEnabled(false);
                            startMining();
                        } else {
                            Snackbar.make(mMining, getString(R.string.err_mining), Snackbar.LENGTH_SHORT).show();
                        }
                        break;
                    case Mining.STATUS_MINING_ING:
                        Snackbar.make(mMining, getString(R.string.err_mininging), Snackbar.LENGTH_SHORT).show();
                        break;
                    case Mining.STATUS_SUBMITED:
                    case Mining.STATUS_CLOSED:
                        Snackbar.make(mMining, getString(R.string.err_end_mining), Snackbar.LENGTH_SHORT).show();
                        break;
                }
                break;

            case R.id.layout_bind_line:
                Intent intentLine = new Intent(getActivity(), UserLineListActivity.class);
//                intentLine.putExtra(ShareActivity.SHARE_URL, App.shareUrl + mUser.getId());
                getActivity().startActivity(intentLine);
                break;

            case R.id.close:
                mMsgView.setVisibility(View.GONE);
                if (App.getInstance().mCurrentNewestMsg != null)
                    CacheManager.getInstance().saveNewestMsg(App.getInstance().mCurrentNewestMsg.getId());
                break;

            case R.id.close2:
                mSysMsgView.setVisibility(View.GONE);
                if (App.getInstance().mCurrentNewestSysMsg != null)
                    CacheManager.getInstance().saveNewestSysMsg(App.getInstance().mCurrentNewestSysMsg.getId());
                break;

            case R.id.msg_view1:
                App.getInstance().mNewestMsg = App.getInstance().mCurrentNewestMsg;
                ((MainActivity) getActivity()).changeTab(R.id.msg);
                EventBus.getDefault().post(new ClickNewMsgEvent());
                break;

            case R.id.sys_msg_view:
                App.getInstance().mNewestMsg = App.getInstance().mCurrentNewestSysMsg;
                ((MainActivity) getActivity()).changeTab(R.id.msg);
                EventBus.getDefault().post(new ClickNewMsgEvent());
                break;

            case R.id.submit_mining:
                if (mCurrentUserMine != null) {
                    showSubmitDialog();
                }
                break;

            case R.id.msg_content0:
                Intent intent6 = new Intent();
                intent6.setAction("android.intent.action.VIEW");
                String url = App.SHARE_EES;
                if (mUser != null) {
                    url += mUser.getId();
                }
                Uri content_url = Uri.parse(url);
                intent6.setData(content_url);
                startActivity(intent6);
                break;
            case R.id.task1:
            case R.id.msg_copy:
            case R.id.share:
                String desc = App.shareDesc;
                if (StringUtils.isBlank(desc)) {
                    desc = getString(R.string.share_desc);
                }
                String surl = App.shareUrl;
                if (StringUtils.isBlank(surl)) {
                    surl = getString(R.string.share_url);
                }
                String content = desc + surl;
                if (mUser != null) {
                    content += mUser.getId();
                }
//                shwoCopyDialog(getContext(), content);
                // open shareactivity.
                Intent intentShare = new Intent(getActivity(), ShareActivity.class);
                intentShare.putExtra(ShareActivity.SHARE_URL, App.shareUrl + mUser.getId());
                intentShare.putExtra(ShareActivity.SHARE_CONTENT, content);
                getActivity().startActivity(intentShare);
                break;
            case R.id.capacity:
                Intent goodsIntent = new Intent(getActivity(), GoodsListActivity.class);
                goodsIntent.putExtra(GoodsListActivity.ARG_USERMINE, mCurrentUserMine);
                goodsIntent.putExtra(GoodsListActivity.ARG_SUBTYPE, Goods.SUBTYPE_CAPACITY);
                getActivity().startActivity(goodsIntent);
                break;
            case R.id.speedRate:
                Intent srIntent = new Intent(getActivity(), GoodsListActivity.class);
                srIntent.putExtra(GoodsListActivity.ARG_USERMINE, mCurrentUserMine);
                srIntent.putExtra(GoodsListActivity.ARG_SUBTYPE, Goods.SUBTYPE_SPEEDRATE);
                getActivity().startActivity(srIntent);
                break;
            case R.id.mining_type:
                mConnectHandler.removeCallbacksAndMessages(null);
                mTimeoutHandler.removeCallbacksAndMessages(null);
                if (mConnectHandler != null) mConnectHandler.removeCallbacksAndMessages(null);
                getActivity().startActivity(new Intent(getActivity(), UserMineListActivity.class));
                break;
            case R.id.task2:
                Intent intent2 = new Intent();
                intent2.setClass(getApplicationContext(), VerifyOneActivity.class);
                startActivity(intent2);
                break;
            case R.id.refresh:
                mPresenter.getCoinMarket(mUser.getPid());
                break;
            case R.id.market_view:
                if (mCoinMarket != null) {
                    if (!StringUtils.isBlank(mCoinMarket.getAction())) {
                        Intent intent11 = new Intent();
                        intent11.setAction("android.intent.action.VIEW");
                        Uri version_url = Uri.parse(mCoinMarket.getAction());
                        intent11.setData(version_url);
                        startActivity(intent11);
                    } else {
                        startActivity(new Intent(getActivity(), CoinMarketListActivity.class));
                    }
                }
                break;
            case R.id.btn_mine4:
                if (mCurrentUserMine.miningStatus == Mining.STATUS_MINING_ING) {
                    if (mBtnRefresh4Enable) {
                        mBtnRefresh4Enable = false;
                        refreshCounter ++;
                        if (App.getInstance().getCurrentDevice() != null && App.getInstance().getCurrentDevice().isConntect()) {
                            BLEAction.getInstance(App.getInstance().getBle(), getActivity()).openNotification(new com.ees.chain.ble.Callback() {
                                @Override
                                public void onSuccess(Object obj) {
                                    Log.e("EES", "open notif successs");
                                    refreshCounter = 0;
                                    refreshDeviceConsume(true, true, true);
                                }

                                @Override
                                public void onFail(Object obj) {
                                    Log.e("EES", "open notif fail");
                                    if (refreshCounter > 3) {
                                        refreshCounter = 0;
                                        connectBLEDevice(App.getInstance().getCurrentDevice());
                                    } else {
                                        BLEAction.getInstance(App.getInstance().getBle(), getActivity()).openNotification();
                                    }
                                }
                            });
//                        BLEAction.getInstance(App.getInstance().getBle(), mContext).openNotification();
//                        refreshDeviceConsume(true, true, true);
                        } else {
                            refreshCounter = 0;
                            connectBLEDevice(App.getInstance().getCurrentDevice());
                        }
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBtnRefresh4Enable = true;
                        }
                    }, 5000);
                }
                break;
        }
    }

    private int refreshCounter = 0;

    public void showNoConnectionDialog(final Context context) {
        if (context == null) return;
        CommomDialog dialog = new CommomDialog(context, R.style.dialog, getString(R.string.err_notconnect_device), new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    getActivity().startActivity(new Intent(getActivity(), UserMineListActivity.class));
                }
                dialog.dismiss();
            }
        });
        dialog.setTitle(context.getString(R.string.tips)).show();
    }

    public void showReconnectDialog(final Context context) {
        if (context == null) return;
        CommomDialog dialog = new CommomDialog(context, R.style.dialog, getString(R.string.err_mine_reconnect), new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    startScanBLE();
                    connectBLE();
                }
                dialog.dismiss();
            }
        });
        dialog.setTitle(context.getString(R.string.tips)).show();
    }

    public void shwoCopyDialog(final Context context, final String content) {
        if (StringUtils.isBlank(content)) {
            return;
        }
        CommomDialog dialog = new CommomDialog(getContext(), R.style.dialog, content, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setText(content);
                    Snackbar.make(mCopyMsg, getString(R.string.copy_success), Snackbar.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton(getString(R.string.copy));
        dialog.setTitle(getString(R.string.tips)).show();
        dialog.setNegativeButtonVisible(View.GONE);
    }

    @OnClick({R.id.chart_top, R.id.chart_bottom})
    public void onChartClick(View view) {
        if (mUser == null || mCurrentUserMine == null) return;
        switch (view.getId()) {
            case R.id.chart_top:
                if (mSevenFouds == null || mSevenFouds.size() == 0) {
                    mPresenter.getSevenFouds(mUser.getPid(), mCurrentUserMine.getMineType().getType(), mCurrentUserMine.getId(), App.getInstance().getSevenFoudVersion());
                }
                break;
            case R.id.chart_bottom:
                if (mSevenMining == null || mSevenMining.size() == 0) {
                    mPresenter.getSevenMinings(mUser.getPid(), mCurrentUserMine.getId(), App.getInstance().getSevenMiningVersion());
                }
                break;
        }
    }

    public void showSubmitDialog() {
        long miningNum = mCurrentUserMine.consume;
        if (miningNum == 0) {
            Snackbar.make(mCover, getString(R.string.err_submint_mining), Snackbar.LENGTH_SHORT).show();
            return;
        }
        final long mnum = (long)(miningNum  * mCurrentUserMine.getSpeedRate());
        String content = String.format(getString(R.string.submit_content), bcapacity + "", mnum + "");

        new CommomDialog(getActivity(), R.style.dialog, content, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    if (mCurrentUserMine.getMineType().getType() == MineType.TYPE_APP) {
                        if (mSubmitMining != null) mSubmitMining.setEnabled(false);
                        submitMining();
                    } else {
                        if (App.getInstance().getCurrentDevice()!=null && App.getInstance().getCurrentDevice().isConntect()) {
                            mTimeoutHandler.removeCallbacksAndMessages(null);
                            if (mSubmitMining != null) mSubmitMining.setEnabled(false);
                            mCurrentUserMine.clearConsume = mCurrentUserMine.consume;
                            mCurrentUserMine.isCleared = false;
                            mCurrentUserMine.clearTime = Utils.getChinaTimeYYYYMMDD();
                            CacheManager.getInstance().saveCurrentUserMine(mCurrentUserMine);
                            refreshUserMineListConsume();
                            submitMining();
                            BLEAction.getInstance(App.getInstance().getBle(), getActivity()).openNotification();
                            boolean result = BLEAction.getInstance(App.getInstance().getBle(), getActivity()).clearDeviceConsume(new Callback() {
                                @Override
                                public void onSuccess(Object obj) {
                                    mTimeoutHandler.removeCallbacksAndMessages(null);
                                    boolean result = BLEAction.getInstance(App.getInstance().getBle(), getActivity()).clearDeviceConsumeResult();
                                    if (result) {
                                        mCurrentUserMine.isCleared = true;
                                        mCurrentUserMine.clearConsume = mCurrentUserMine.consume;
                                        mCurrentUserMine.clearTime = Utils.getChinaTimeYYYYMMDD();
                                        CacheManager.getInstance().saveCurrentUserMine(mCurrentUserMine);
                                        refreshUserMineListConsume();
                                    }
                                }

                                @Override
                                public void onFail(Object obj) {

                                }
                            });
                            mTimeoutHandler.sendEmptyMessageDelayed(2, 5 * 1000);
                        } else {
                            showNoConnectionDialog(mContext);
                        }
                    }
                }
                dialog.dismiss();
            }
        }).setTitle(getString(R.string.submit_mining)).show();
    }

    public void showDialog(String content) {
        CommomDialog dialog = new CommomDialog(getActivity(), R.style.dialog, content, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton(getString(R.string.ok));
        dialog.setTitle(getString(R.string.tips)).show();
        dialog.setNegativeButtonVisible(View.GONE);
    }

    public void showStartSuccessDialog() {
        if (CacheManager.getInstance().getStartNotifCount(getContext()) < 5) {
            CacheManager.getInstance().addStartNotifCount(getContext());
        } else {
            return;
        }

        String subrange = App.getInstance().getSubmitRange();
        if (Utils.isStringEmpty(subrange)) return;
        int sh = Integer.parseInt(subrange.split(",")[0]);
        int eh = 1;
        if (subrange.split(",").length > 1) {
            eh = Integer.parseInt(subrange.split(",")[1]);
        }
        String content = String.format(getString(R.string.start_mining_success_hint), sh, eh);
        CommomDialog dialog = new CommomDialog(getActivity(), R.style.dialog, content, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton(getString(R.string.ok));
        dialog.setTitle(getString(R.string.tips)).show();
        dialog.setNegativeButtonVisible(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        resetUsermineListClear();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isCurrentUserMineChange) {
            mPresenter.getUserMineList(mUser.getPid());
            mPresenter.getNewestMsg(mUser.getPid());
            configViews();
            isCurrentUserMineChange = false;
        }
//        if (mCurrentUserMine != null && mCurrentUserMine.getMineType().getType() != MineType.TYPE_APP && mCurrentUserMine.miningStatus == Mining.STATUS_MINING_ING) {
//            refreshDeviceConsume(false, false, true);
//        }
        resetMiningStatus();
        refreshComsumeView();
        refreshTaskView();
        refreshMarketView();
    }

    public void resetUsermineListClear() {
        String nowDate = Utils.getChinaTimeYYYYMMDD();
        if (mCurrentUserMine != null && mCurrentUserMine.getMineType().getType() != MineType.TYPE_APP
                && !StringUtils.isBlank(mCurrentUserMine.clearTime) && !nowDate.equals(mCurrentUserMine.clearTime)) {
            mCurrentUserMine.consume = 0;
            mCurrentUserMine.clearConsume = 0;
            mCurrentUserMine.isCleared = false;
            mCurrentUserMine.clearTime = "";
            CacheManager.getInstance().saveCurrentUserMine(mCurrentUserMine);
        }
        List<UserMine> usermines = App.getInstance().getUserMines();
        List<UserMine> nusermines = new ArrayList<UserMine>();
        if (usermines != null) {
            boolean findChange = false;
            for (UserMine usermine : usermines) {
                if (usermine != null && usermine.getMineType().getType() != MineType.TYPE_APP
                        && !StringUtils.isBlank(usermine.clearTime) && !nowDate.equals(usermine.clearTime)) {
                    findChange = true;
                    mCurrentUserMine.consume = 0;
                    usermine.clearConsume = 0;
                    usermine.isCleared = false;
                    usermine.clearTime = "";
                }
                nusermines.add(usermine);
            }
            if (nusermines != null && nusermines.size() > 0 && findChange)
                CacheManager.getInstance().saveUserMineList(nusermines);
        }
    }

    public void resetMiningStatus() {
        if (mCover == null || mMining == null) return;
        String miningDate = App.getInstance().getMiningDate();
        String nowDate = Utils.getChinaTimeYYYYMMDD();
        if (!nowDate.equals(miningDate) && mCurrentUserMine != null) {
            int status = Mining.STATUS_NOT_START;
            if (mCurrentUserMine.getMineType().getType() != MineType.TYPE_APP) {
                status = Mining.STATUS_MINING_ING;
            }
            mCurrentUserMine.miningStatus = status;
            CacheManager.getInstance().saveCurrentUserMine(mCurrentUserMine);
            refreshUserMineListStatus();
        }
        int status = Mining.STATUS_NOT_START;
        if (mCurrentUserMine != null) {
            status = mCurrentUserMine.miningStatus;
            if (mCurrentUserMine.getMineType().getType() != MineType.TYPE_APP && status == Mining.STATUS_NOT_START) {
                status = Mining.STATUS_MINING_ING;
                mCurrentUserMine.miningStatus = status;
                CacheManager.getInstance().saveCurrentUserMine(mCurrentUserMine);
                refreshUserMineListStatus();
            }
        }
        mCover.setEnabled(false);
        mMining.setEnabled(true);
        mCover.setVisibility(View.GONE);
        mCover0.setVisibility(View.GONE);
        switch (status) {
            case Mining.STATUS_NOT_START:
                if (isStartMiningRange()) {
                    mMining.setText(R.string.start_mining);
                    mMining.setVisibility(View.VISIBLE);
                    mCapacityView.setVisibility(View.GONE);
                    mMsgView2.setVisibility(View.GONE);
                    mCover0.setVisibility(View.VISIBLE);
                    mCover.setVisibility(View.GONE);
                } else {
                    mMining.setText(R.string.start_mining);
                    mMining.setVisibility(View.VISIBLE);
                    mCapacityView.setVisibility(View.GONE);
                    mMsgView2.setVisibility(View.GONE);
                    mCover0.setVisibility(View.VISIBLE);
                    mCover.setVisibility(View.GONE);
                }
                mSubmitMining.setVisibility(View.GONE);
                break;
            case Mining.STATUS_MINING_ING:
            default:
                mMining.setVisibility(View.GONE);
                mMsgView2.setVisibility(View.GONE);
                mCapacityView.setVisibility(View.VISIBLE);
                mMining.setEnabled(false);
                mCover.setEnabled(false);
                mCover0.setVisibility(View.GONE);
                mCover.setVisibility(View.VISIBLE);
                if (isSubmitMiningRange()) {
                    mSubmitMining.setVisibility(View.VISIBLE);
                } else {
                    mSubmitMining.setVisibility(View.GONE);
                }
                break;
            case Mining.STATUS_SUBMITED:
            case Mining.STATUS_CLOSED:
                mMining.setText(R.string.end_mining);
                mMining.setVisibility(View.VISIBLE);
                mCapacityView.setVisibility(View.GONE);
                mSubmitMining.setVisibility(View.GONE);
                mMsgView2.setVisibility(View.VISIBLE);
                mCover0.setVisibility(View.VISIBLE);
                mCover.setVisibility(View.GONE);
                startDaojishi();
                break;
        }
    }

    public void showPlugginView(boolean show) {
        LogUtils.d("showPlugginView " + show);
        if (mPluggin != null && mPlugingWarningView != null) {
            if (show) {
                mPluggin.setVisibility(View.VISIBLE);
                MyAnimationUtils.setHideAnimation(mPluggin, 5000);
                MyAnimationUtils.setScaleAnimation(mPluggin, 5000);
                if (mCurrentUserMine != null && mCurrentUserMine.getMineType().getType() == MineType.TYPE_APP) {
                    mPlugingWarningView.setVisibility(View.VISIBLE);
                } else {
                    mPlugingWarningView.setVisibility(View.GONE);
                }
            } else {
                mPluggin.setVisibility(View.INVISIBLE);
                mPlugingWarningView.setVisibility(View.GONE);
            }
        }
    }

    public void refreshComsumeView() {
        if (mCurrentCapacity == null || mPercent == null) return;

        if (bcapacity == 0 && mCurrentUserMine != null && mCurrentUserMine.getMineType() != null) {
            bcapacity = mCurrentUserMine.getMaxQtyLimit();
        }
        int status = Mining.STATUS_NOT_START;
        if (mCurrentUserMine != null) {
            status = mCurrentUserMine.miningStatus;
            if (mCurrentUserMine.getMineType().getType() != MineType.TYPE_APP && status == Mining.STATUS_NOT_START) {
                status = Mining.STATUS_MINING_ING;
                mCurrentUserMine.miningStatus = status;
                CacheManager.getInstance().saveCurrentUserMine(mCurrentUserMine);
                refreshUserMineListStatus();
            }
        }
        switch (status) {
            case Mining.STATUS_MINING_ING:
            default:
                mCurrentCapacity.setVisibility(View.VISIBLE);
                if (mCurrentUserMine == null) {
                    break;
                }
                double speedRate = 1.0;
                speedRate = mCurrentUserMine.getSpeedRate();
                speedRate = speedRate <= 0.0 ? 0 : speedRate;
                long consume = (long) (mCurrentUserMine.consume * speedRate);
                double percent = 0.0;
                if (bcapacity != 0) {
                    percent = (double) consume * 100.0 / bcapacity;
                }
                percent = Utils.doubleFormat(percent);
                mCurrentCapacity.setText(consume + "");
                if (speedRate > 1.0) {
                    mBaseCapacity.setVisibility(View.VISIBLE);
                    mBaseCapacity.setText(String.format(getString(R.string.base_capacity), mCurrentUserMine.consume+"", speedRate+""));
                } else {
                    mBaseCapacity.setVisibility(View.INVISIBLE);
                }
                mPercent.setText(String.format(getString(R.string.mining_percent), percent + " %"));
                break;
            case Mining.STATUS_NOT_START:
                mCurrentCapacity.setText("0");
                mCurrentCapacity.setVisibility(View.VISIBLE);
                mBaseCapacity.setVisibility(View.INVISIBLE);
                mPercent.setText(String.format(getString(R.string.mining_percent), "0.0 %"));
                break;
            case Mining.STATUS_SUBMITED:
            case Mining.STATUS_CLOSED:
                mCurrentCapacity.setText("0");
                mCurrentCapacity.setVisibility(View.GONE);
                mBaseCapacity.setVisibility(View.GONE);
                percent = 100.00;
                mPercent.setText(String.format(getString(R.string.mining_percent), percent + " %"));
                break;
        }
    }

    public void refreshUserMineListConsume() {
        List<UserMine> usermines = App.getInstance().getUserMines();
        List<UserMine> nusermines = new ArrayList<UserMine>();
        if (usermines != null && mCurrentUserMine != null) {
            for (UserMine usermine : usermines) {
                if (usermine.getId().equals(mCurrentUserMine.getId())) {
                    usermine.consume = mCurrentUserMine.consume;
                    usermine.clearTime = mCurrentUserMine.clearTime;
                    usermine.isCleared = mCurrentUserMine.isCleared;
                    usermine.clearConsume = mCurrentUserMine.clearConsume;
                }
                nusermines.add(usermine);
            }
            if (nusermines != null && nusermines.size() > 0)
                CacheManager.getInstance().saveUserMineList(nusermines);
        }
    }

    public void refreshDeviceConsume(final boolean refreshView, final boolean showDialog, final boolean reconnect) {
//        Log.e("EES", "refresh device consume + type: " + mCurrentUserMine.getMineType().getType() + ", show: " + showDialog);
        if (mCurrentUserMine.getMineType().getType() == MineType.TYPE_APP) return;
        if (App.getInstance().getCurrentDevice() != null && App.getInstance().getCurrentDevice().isConntect()) {
            boolean result = BLEAction.getInstance(App.getInstance().getBle(), mContext).readDeviceConsume(new Callback() {
                @Override
                public void onSuccess(Object obj) {
                    Log.e("EES", "read device consume success");
                    BLEAction.getInstance(App.getInstance().getBle(), mContext).emptyCallback();
                    mTimeoutHandler.removeCallbacksAndMessages(null);
                    int consume = BLEAction.getInstance(App.getInstance().getBle(), mContext).readDeviceConsumeResult();
                    if (mCurrentUserMine != null && consume >= 0) {
                        if (Utils.getChinaTimeYYYYMMDD().equals(mCurrentUserMine.clearTime)) {
                            if (mCurrentUserMine.isCleared) {
                                mCurrentUserMine.consume = consume + mCurrentUserMine.clearConsume;
                            } else {
                                if (mCurrentUserMine.clearConsume > consume) {
                                    mCurrentUserMine.consume = consume + mCurrentUserMine.clearConsume;
                                    mCurrentUserMine.isCleared = true;
                                } else if (mCurrentUserMine.clearConsume <= consume) {
                                    mCurrentUserMine.consume = consume;
                                    mCurrentUserMine.isCleared = false;
                                }
                            }
                        } else {
                            mCurrentUserMine.consume = consume;
                        }
                        if (refreshView) refreshComsumeView();
                        CacheManager.getInstance().saveCurrentUserMine(mCurrentUserMine);
                        refreshUserMineListConsume();
                    }
                }

                @Override
                public void onFail(Object obj) {
                    Log.e("EES", "read device consume fail");
                    BLEAction.getInstance(App.getInstance().getBle(), mContext).emptyCallback();
                    if (reconnect) {
                        connectBLEDevice(App.getInstance().getCurrentDevice());
                    } else {
                        if(showDialog) mTimeoutHandler.sendEmptyMessageDelayed(1, 500);
                    }
                }
            });
            if(showDialog) mTimeoutHandler.sendEmptyMessageDelayed(1, 5 * 1000);
        } else {
            if (showDialog) showReconnectDialog(getActivity());
        }
    }

    public void refreshTaskView() {
        if (mTask1 != null) mTask1.setVisibility(View.GONE);
        if (mCurrentUserMine != null && mCurrentUserMine.getMineType() != null) {
            if (mCurrentUserMine.getMineType().getType() == MineType.TYPE_APP) {
                long capacity = mCurrentUserMine.getMaxQtyLimit();
                if (capacity < 800) {
                    mTask1.setVisibility(View.VISIBLE);
                    mMsgView0.setVisibility(View.GONE);
                } else {
                    mTask1.setVisibility(View.GONE);
                    mMsgView0.setVisibility(View.VISIBLE);
                }
            } else {
                mTask1.setVisibility(View.GONE);
                mTask2.setVisibility(View.GONE);
                mMsgView0.setVisibility(View.VISIBLE);
            }
        }
        if (mUser != null) {
            int status = mUser.getRna_Status();
            if (status == 1) {
                mTask2.setVisibility(View.GONE);
            } else {
                mTask2.setVisibility(View.VISIBLE);
            }
        }
    }

    public void refreshMarketView() {
        mArrow.setVisibility(View.GONE);
        if (mCoinMarket != null) {
            mMarketView.setVisibility(View.VISIBLE);
            App.mPriceRMB = mCoinMarket.getPriceRMB();
            mMarketName.setText(mCoinMarket.getMarketName());
            mPrice1.setText(Utils.decimalFormat(mCoinMarket.getLast()));
            mUnit.setText(mCoinMarket.getUnit());
            mPrice2.setText(mCoinMarket.getPriceRMB() + "");
            String updown = mCoinMarket.getUpDown();
            if (updown.startsWith("+")) {
                mTitle.setTextColor(Color.parseColor("#FF02C407"));
            } else {
                mTitle.setTextColor(Color.parseColor("#FFDD4B39"));
            }
            mTitle.setText(String.format(getString(R.string.market_updown), updown));
            mSubtitle.setText(mCoinMarket.getDetail());
        } else {
            mMarketView.setVisibility(View.GONE);
        }
    }

    /**
     * 可以开始挖矿时间段
     *
     * @return
     */
    public boolean isStartMiningRange() {
        LogUtils.d("isStartMining " + App.getInstance().getMiningRange());
        try {
            String shour = Utils.getChinaTimeHH();

            int hour = Integer.parseInt(shour);
            int sh = Integer.parseInt(App.getInstance().getMiningRange().split(",")[0]);
            int eh = 20;
            if (App.getInstance().getMiningRange().split(",").length > 1) {
                eh = Integer.parseInt(App.getInstance().getMiningRange().split(",")[1]);
            }
            if (hour >= sh && hour < eh) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 可以提交矿藏时间段
     *
     * @return
     */
    public boolean isSubmitMiningRange() {
        LogUtils.d("isSubmitMining " + App.getInstance().getSubmitRange());
        String shour = Utils.getChinaTimeHH();
        int hour = Integer.parseInt(shour);
        int sh = 20;
        try {
            sh = Integer.parseInt(App.getInstance().getSubmitRange().split(",")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int eh = 24;
        try {
            if (App.getInstance().getSubmitRange().split(",").length > 1) {
                eh = Integer.parseInt(App.getInstance().getSubmitRange().split(",")[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (hour < sh || hour > eh) {
            return false;
        }
        return true;
    }

    /**
     * 开始挖矿
     */
    public void startMining() {
        if (mUser != null && mCurrentUserMine != null) {
            CacheManager.getInstance().clearBattery();
            EventBus.getDefault().post(new UpdateConsumeEvent());
            mPresenter.startMining(mUser.getPid(), mCurrentUserMine.getId());
        }
    }

    /**
     * 提交挖矿
     */
    public void submitMining() {
        if (mCurrentUserMine != null) {
            long consume = mCurrentUserMine.consume;
            double percent = 0;
            if (bcapacity != 0) {
                percent = Utils.doubleFormat((double) consume * 100.0 / bcapacity);
            }
            mCurrentUserMine.percent = percent;
        }
        if (mUser != null && mCurrentUserMine != null) {
            mPresenter.submitMining(mUser.getPid(), mCurrentUserMine.consume, mCurrentUserMine.getId());
        }
    }

    @Override
    public void showError() {
        dismissLoadingDialog();
        Snackbar.make(chartBottom, App.getInstance().getString(R.string.verifing_has_error), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void complete() {
        dismissLoadingDialog();
    }

    public double getMaxValue(double datas[]) {
        double maxvalue = 0;
        if (datas != null && datas.length > 0) {
            for (int i = 0; i < datas.length; i++) {
                if (datas[i] > maxvalue) {
                    maxvalue = datas[i];
                }
            }
        }
        return maxvalue;
    }

    private void initLineChart(String xData[], double yDatas[]) {
        double maxYvalue = getMaxValue(yDatas);
        maxYvalue += maxYvalue / 4;

        //设置柱、X、Y轴属性及添加数据
        List<PointValue> pointValues = new ArrayList<PointValue>();
        Axis axisY = new Axis().setHasLines(true);// Y轴属性
        Axis axisX = new Axis();// X轴属性
//        axisY.setName(getString(R.string.lable_y2));//设置Y轴显示名称
        axisX.setName(getString(R.string.lable_x));//设置X轴显示名称
        ArrayList<AxisValue> axisValuesX = new ArrayList<AxisValue>();//定义X轴刻度值的数据集合
        ArrayList<AxisValue> axisValuesY = new ArrayList<AxisValue>();//定义Y轴刻度值的数据集合
        axisX.setValues(axisValuesX);//为X轴显示的刻度值设置数据集合
//        axisX.setLineColor(Color.BLACK);// 设置X轴轴线颜色
//        axisY.setLineColor(Color.BLACK);// 设置Y轴轴线颜色
//        axisX.setTextColor(Color color);// 设置X轴文字颜色
//        axisY.setTextColor(Color color);// 设置Y轴文字颜色
        axisX.setTextSize(10);// 设置X轴文字大小
        axisY.setTextSize(10);// 设置X轴文字大小
//        axisX.setTypeface(Typeface.DEFAULT);// 设置文字样式，此处为默认
        axisX.setHasTiltedLabels(true);// 设置X轴文字向左旋转45度
//        axisY.setMaxLabelChars(5);//y轴坐标显示的最大个数
//        axisX.setHasLines(boolean isHasLines);// 是否显示X轴网格线
//        axisY.setHasLines(boolean isHasLines);// 是否显示Y轴网格线
//        axisX.setHasSeparationLine(boolean isHasSeparationLine);// 设置是否有分割线
//        axisX.setInside(boolean isInside);// 设置X轴文字是否在X轴内部
        for (int j = 0; j < yDatas.length; j++) {//循环为节点、X、Y轴添加数据
            pointValues.add(new PointValue(j, (float) yDatas[j]));// 添加节点数据
            axisValuesY.add(new AxisValue(j).setValue(j).setLabel(mDatas1[j] + ""));// 添加Y轴显示的刻度值
            axisValuesX.add(new AxisValue(j).setValue(j).setLabel(xData[j]));// 添加X轴显示的刻度值
        }
        //设置柱形Line的属性：
        List<Line> lines = new ArrayList<Line>();//定义线的集合
        Line line = new Line(pointValues);//将值设置给折线
        line.setColor(ChartUtils.COLOR_GREEN);// 设置折线颜色
        line.setStrokeWidth(1);// 设置折线宽度
//        line.setFilled(boolean isFilled);// 设置折线覆盖区域是否填充
        line.setCubic(true);// 是否设置为立体的
//        line.setPointColor(Color color);// 设置节点颜色
        line.setPointRadius(3);// 设置节点半径
//        line.setHasLabels(true);// 是否显示节点数据
//        line.setHasLines(boolean isHasLines);// 是否显示折线
//        line.setHasPoints(boolean isHasPoint);// 是否显示节点
        line.setShape(ValueShape.CIRCLE);// 节点图形样式 DIAMOND菱形、SQUARE方形、CIRCLE圆形
//        line.setHasLabelsOnlyForSelected(true);// 隐藏数据，触摸可以显示
        lines.add(line);// 将数据集合添加线
        //设置LineChartData属性及为chart设置数据：
        lineData = new LineChartData(lines);//将线的集合设置为折线图的数据
        lineData.setAxisYLeft(axisY);// 将Y轴属性设置到左边
        lineData.setAxisXBottom(axisX);// 将X轴属性设置到底部
//        lineData.setAxisYRight(axisYRight);//设置右边显示的轴
//        lineData.setAxisXTop(axisXTop);//设置顶部显示的轴
//        lineData.setBaseValue(20);// 设置反向覆盖区域颜色
//        lineData.setValueLabelBackgroundAuto(false);// 设置数据背景是否跟随节点颜色
//        lineData.setValueLabelBackgroundColor(Color.BLUE);// 设置数据背景颜色
//        lineData.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
//        lineData.setValueLabelsTextColor(Color.BLACK);// 设置数据文字颜色
//        lineData.setValueLabelTextSize(15);// 设置数据文字大小
//        lineData.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式

        // For build-up animation you have to disable viewport recalculation.
        chartTop.setViewportCalculationEnabled(false);

        // And set initial max viewport and current viewport- remember to set viewports after data.
        Viewport v = new Viewport(0, (float) maxYvalue, mDatas1.length - 0.5f, 0);
        chartTop.setMaximumViewport(v);
        chartTop.setCurrentViewport(v);

        //chart属性设置：
        chartTop.setZoomEnabled(false);//设置是否支持缩放
        chartTop.setZoomType(ZoomType.HORIZONTAL);
//        chartTop.setOnValueTouchListener(LineChartOnValueSelectListener touchListener);//为图表设置值得触摸事件
        chartTop.setInteractive(true);//设置图表是否可以与用户互动
//        chartTop.setValueSelectionEnabled(boolean idValueSelectionEnabled);//设置图表数据是否选中进行显示
        chartTop.setLineChartData(lineData);//为图表设置数据，数据类型为LineChartData
    }

    public void initColumnChart(String xData[], int yDatas[]) {
//        int maxYvalue = getMaxValue(yDatas) + 35;

        //设置柱、X、Y轴属性及添加数据
        List<SubcolumnValue> subValues;// 节点数据结合
        Axis axisY = new Axis().setHasLines(true);// Y轴属性
        Axis axisX = new Axis();// X轴属性
//        axisY.setName(getString(R.string.lable_y1));//设置Y轴显示名称
        axisX.setName(getString(R.string.lable_x));//设置X轴显示名称
        ArrayList<AxisValue> axisValuesX = new ArrayList<AxisValue>();//定义X轴刻度值的数据集合
        ArrayList<AxisValue> axisValuesY = new ArrayList<AxisValue>();//定义Y轴刻度值的数据集合
        axisX.setValues(axisValuesX);//为X轴显示的刻度值设置数据集合
//        axisX.setLineColor(Color.BLACK);// 设置X轴轴线颜色
//        axisY.setLineColor(Color.BLACK);// 设置Y轴轴线颜色
//        axisX.setTextColor(Color color);// 设置X轴文字颜色
//        axisY.setTextColor(Color color);// 设置Y轴文字颜色
        axisX.setTextSize(10);// 设置X轴文字大小
        axisY.setTextSize(10);// 设置X轴文字大小
//        axisX.setTypeface(Typeface.DEFAULT);// 设置文字样式，此处为默认
        axisX.setHasTiltedLabels(true);// 设置X轴文字向左旋转45度
        axisX.setHasLines(false);// 是否显示X轴网格线
        axisY.setHasLines(true);// 是否显示Y轴网格线
        axisY.setMaxLabelChars(5);//y轴坐标显示的最大个数
//        axisX.setHasSeparationLine(boolean isHasSeparationLine);// 设置是否有分割线
//        axisX.setInside(boolean isInside);// 设置X轴文字是否在X轴内部
        List<Column> columns = new ArrayList<Column>();//定义线的集合
        final int numSubcolumns = 1; //每一个柱子里头有几根子柱子
        for (int j = 0; j < yDatas.length; j++) {//循环为节点、X、Y轴添加数据
            subValues = new ArrayList<SubcolumnValue>();
            for (int i = 0; i < numSubcolumns; ++i) {
                subValues.add(new SubcolumnValue(yDatas[j], ChartUtils.nextColor()));// 添加节点数据并为其设置颜色
            }
            axisValuesY.add(new AxisValue(j).setValue(j).setLabel(yDatas[j] + ""));// 添加Y轴显示的刻度值
            axisValuesX.add(new AxisValue(j).setValue(j).setLabel(xData[j]));// 添加X轴显示的刻度值
            Column column = new Column(subValues);//将值设置给折线
            //设置柱形Column的属性：
//        column.setValues(values);//为柱形图这是数据
//            column.setHasLabels(true);// 是否显示节点数据
            column.setHasLabelsOnlyForSelected(true);// 隐藏数据，触摸可以显示
            columns.add(column);// 将数据集合添加线
        }
        //设置ColumnChartData属性及为chart设置数据：
        columnData = new ColumnChartData(columns);//将线的集合设置为折线图的数据
        columnData.setAxisYLeft(axisY);// 将Y轴属性设置到左边
        columnData.setAxisXBottom(axisX);// 将X轴属性设置到底部
//        columnData.setAxisYRight(axisYRight);//设置右边显示的轴
//        columnData.setAxisXTop(axisXTop);//设置顶部显示的轴
//        columnData.setBaseValue(20);// 设置反向覆盖区域颜色
//        columnData.setValueLabelBackgroundAuto(false);// 设置数据背景是否跟随节点颜色
//        columnData.setValueLabelBackgroundColor(Color.BLUE);// 设置数据背景颜色
//        columnData.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
//        columnData.setValueLabelsTextColor(Color.BLACK);// 设置数据文字颜色
//        columnData.setValueLabelTextSize(15);// 设置数据文字大小
//        columnData.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式

        //chart属性设置：
        chartBottom.setZoomEnabled(false);//设置是否支持缩放
//        chartBottom.setOnValueTouchListener(ColumnChartOnValueSelectListener touchListener);//为图表设置值得触摸事件
        chartBottom.setInteractive(true);//设置图表是否可以与用户互动
//        chartBottom.setValueSelectionEnabled(boolean idValueSelectionEnabled);//设置图表数据是否选中进行显示
        chartBottom.setColumnChartData(columnData);//为图表设置数据，数据类型为ColumnChartData
    }

    @Override
    public void startMiningSucess() {
        LogUtils.d("startMiningSucess");
//        dismissLoadingDialog();
        if (mCover != null) {
            mCover.setEnabled(true);
            mCover0.setEnabled(true);
            mMining.setEnabled(true);
        }

        mCurrentUserMine.miningStatus = Mining.STATUS_MINING_ING;
        resetMiningStatus();
        refreshComsumeView();
        Snackbar.make(mCover, getString(R.string.start_mining2), Snackbar.LENGTH_SHORT).show();
        showStartSuccessDialog();
    }

    @Override
    public void startMiningFail(Error err) {
        LogUtils.d("startMiningFail " + err.getCode() + err.getMessage());
//        dismissLoadingDialog();
        if (mCover != null) {
            mCover.setEnabled(true);
            mCover0.setEnabled(true);
            mMining.setEnabled(true);
        }
//        Snackbar.make(mCover, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        showDialog(err.getMessage());
        checkError(err);
    }

    public void refreshUserMineListStatus() {
        List<UserMine> usermines = App.getInstance().getUserMines();
        List<UserMine> nusermines = new ArrayList<UserMine>();
        if (usermines != null && mCurrentUserMine != null) {
            for (UserMine usermine : usermines) {
                if (usermine.getId().equals(mCurrentUserMine.getId())) {
                    usermine.miningStatus = mCurrentUserMine.miningStatus;
                }
                nusermines.add(usermine);
            }
            if (nusermines != null && nusermines.size() > 0)
                CacheManager.getInstance().saveUserMineList(nusermines);
        }
    }

    @Override
    public void submitMiningSucess() {
        LogUtils.d("submitMiningSucess");
        if (mSubmitMining != null) mSubmitMining.setEnabled(true);
        mCurrentUserMine.miningStatus = Mining.STATUS_SUBMITED;
        if (mCurrentUserMine.getMineType().getType() != MineType.TYPE_APP) {
            mCurrentUserMine.consume = 0;
            mCurrentUserMine.clearConsume = 0;
            mCurrentUserMine.clearTime = "";
            mCurrentUserMine.isCleared = false;
            BLEAction.getInstance(App.getInstance().getBle(), getActivity()).clearDeviceConsume(new Callback() {
                @Override
                public void onSuccess(Object obj) {

                }

                @Override
                public void onFail(Object obj) {

                }
            });
        }
        resetMiningStatus();
        refreshComsumeView();
        CacheManager.getInstance().saveCurrentUserMine(mCurrentUserMine);
        refreshUserMineListConsume();
        refreshUserMineListStatus();
        showDialog(getString(R.string.submit_succuss_hint));
    }

    @Override
    public void submitMiningFail(Error err) {
        LogUtils.d("submitMiningFail " + err.getCode() + err.getMessage());
        if (mSubmitMining != null) mSubmitMining.setEnabled(true);
        if (Error.CODE_MINING_STATUS_SUBMITED.equals(err.getCode())) {
            mCurrentUserMine.miningStatus = Mining.STATUS_SUBMITED;
            refreshUserMineListStatus();
        }
        resetMiningStatus();
        refreshComsumeView();
        showDialog(err.getMessage());
        checkError(err);
    }

    @Override
    public void showSevenFoudsSucess(List<Fund> data, long version) {
        LogUtils.d("showSevenFoudsSucess");
        mSevenFouds = data;

        if (mSevenFouds != null) mSevenFoudsData.clear();
        if (mSevenFouds != null && mSevenFouds.size() > 0) {
            for (int i = 0; i < mSevenFouds.size(); i++) {
                long time = mSevenFouds.get(i).getCreateDate();
                String date = Utils.getPreDate(time);
                mSevenFoudsData.put(date, mSevenFouds.get(i).getQty());
            }
            CacheManager.getInstance().saveSevenFoud(data);
        }

        mDatas1 = new double[LENGTH];
        mDays1 = new String[LENGTH];
        for (int i = 0; i < LENGTH; i++) {
            if (i >= LENGTH) break;
            String date = mLastSevenDate.get(i);
            if (mSevenFoudsData.containsKey(date)) {
                mDatas1[i] = mSevenFoudsData.get(date);
            } else {
                mDatas1[i] = 0;
            }
            mDays1[i] = date + getString(R.string.date);
        }

        initLineChart(mDays1, mDatas1);

        CacheManager.getInstance().saveSevenFoudVersion(getContext(), version);
    }

    @Override
    public void showSevenFoudsFail(Error err, long version) {
        LogUtils.d("showSevenFoudsFail " + err.getCode() + err.getMessage());
        if (Error.CODE_SYNC_EQUAL.equals(err.getCode())) {
            showSevenFoudsSucess(CacheManager.getInstance().getSevenFoud(), version);
        }
        checkError(err);
    }

    @Override
    public void showSevenMiningsSucess(List<Mining> data, long version) {
        LogUtils.d("showSevenMiningsSucess");
        mSevenMining = data;

        if (mSevenMining != null) mSevenMiningData.clear();
        if (mSevenMining != null && mSevenMining.size() > 0) {
            for (int i = 0; i < mSevenMining.size(); i++) {
                long time = mSevenMining.get(i).getStartTime();
                String date = Utils.getDate(time);
                mSevenMiningData.put(date, (int) mSevenMining.get(i).getActualQty());
            }
            CacheManager.getInstance().saveSevenMining(data);
        }

        mDatas2 = new int[LENGTH];
        mDays2 = new String[LENGTH];
        for (int i = 0; i < LENGTH; i++) {
            if (i >= LENGTH) break;
            String date = mLastSevenDate.get(i);
            if (mSevenMiningData.containsKey(date)) {
                mDatas2[i] = mSevenMiningData.get(date);
            } else {
                mDatas2[i] = 0;
            }
            mDays2[i] = date + getString(R.string.date);
        }

        initColumnChart(mDays2, mDatas2);

        CacheManager.getInstance().saveSevenMiningVersion(getContext(), version);
    }

    @Override
    public void showSevenMiningsFail(Error err, long version) {
        LogUtils.d("showSevenMiningsFail " + err.getCode() + err.getMessage());
        if (Error.CODE_SYNC_EQUAL.equals(err.getCode())) {
            showSevenMiningsSucess(CacheManager.getInstance().getSevenMining(), version);
        }
        checkError(err);
    }

    @Override
    public void showNewestMsgSucess(List<Notice> data) {
        LogUtils.d("showNewestMsgSucess");

        if (data != null && data.size() > 0) {
            for (Notice notice : data) {
                if (notice.getType() == MsgPresenter.TYPE_MY_MSG) {
                    App.getInstance().mCurrentNewestMsg = notice;
                } else if (notice.getType() == MsgPresenter.TYPE_SYS_MSG) {
                    App.getInstance().mCurrentNewestSysMsg = notice;
                }
            }
            Notice msgNotice = App.getInstance().mCurrentNewestMsg;
            if (msgNotice != null && !StringUtils.isBlank(msgNotice.getExcerpt())) {
                if (mMsgContent != null) mMsgContent.setText(msgNotice.getExcerpt());
                String mid = CacheManager.getInstance().getCloseNewestMsg();
                if (mMsgContent != null && msgNotice.getId().equals(mid)) {
                    if (mMsgContent != null) mMsgView.setVisibility(View.GONE);
                } else {
                    if (mMsgContent != null) mMsgView.setVisibility(View.VISIBLE);
                }
            } else {
                if (mMsgContent != null) mMsgView.setVisibility(View.GONE);
            }

            Notice sysNotice = App.getInstance().mCurrentNewestSysMsg;
            if (sysNotice != null) {
                if (mSysMsgView != null) {
                    String mid = CacheManager.getInstance().getCloseNewestSysMsg();
                    if (sysNotice.getId().equals(mid)) {
                        mSysMsgView.setVisibility(View.GONE);
                    } else {
                        mSysMsgCover.setImageURI(sysNotice.getCover());
                        mSysMsgTitle.setText(sysNotice.getTitle());
                    }
                }
            } else {
                if (mSysMsgView != null) mSysMsgView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showNewestMsgFail(com.ees.chain.domain.Error err) {
        LogUtils.d("showNewestMsgFail " + err.getCode() + err.getMessage());
        checkError(err);
    }

    @Override
    public void showMiningStatusSucess(Mining data) {
        if (data != null) {
            LogUtils.d("showMiningStatusSucess " + data.getStatus());
            mCurrentUserMine.miningStatus = Mining.STATUS_MINING_ING;
            resetMiningStatus();
            refreshComsumeView();
            refreshUserMineListStatus();
        }
    }

    @Override
    public void showMiningStatusFail(Error err) {
        LogUtils.d("showMiningStatusFail " + err.getCode() + err.getMessage());
        if (Error.CODE_MINING_STATUS_SUBMITED.equals(err.getCode()) || Error.CODE_MINING_STATUS_COSTED.equals(err.getCode())) {
            mCurrentUserMine.miningStatus = Mining.STATUS_SUBMITED;
            refreshUserMineListStatus();
        } else if (Error.CODE_MINING_NOT_STARTED.equals(err.getCode())) {
            mCurrentUserMine.miningStatus = Mining.STATUS_NOT_START;
            refreshUserMineListStatus();
        }
        resetMiningStatus();
        refreshComsumeView();
        checkError(err);
    }

    @Override
    public void showUserMineListSucess(List<UserMine> data) {
        LogUtils.d("showUserMineListSucess");
        if (data != null) {
            UserMine cUserMine = App.getInstance().getCurrentUserMine();
            if (cUserMine == null) {
                cUserMine = data.get(0);
            } else {
                boolean find = false;
                for (UserMine usermine : data) {
                    if (usermine.getId().equals(cUserMine.getId())) {
                        usermine.consume = cUserMine.consume;
                        usermine.clearConsume = cUserMine.clearConsume;
                        usermine.isCleared = cUserMine.isCleared;
                        usermine.clearTime = cUserMine.clearTime;
                        cUserMine = usermine;
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    cUserMine = data.get(0);
                }
            }
            CacheManager.getInstance().saveCurrentUserMine(cUserMine);
            mCurrentUserMine = cUserMine;
            configViews();
            if (mPresenter != null && mUser != null && mCurrentUserMine != null) {
                mPresenter.getSevenFouds(mUser.getPid(), mCurrentUserMine.getMineType().getType(), mCurrentUserMine.getId(), App.getInstance().getSevenFoudVersion());
                mPresenter.getSevenMinings(mUser.getPid(), mCurrentUserMine.getId(), App.getInstance().getSevenMiningVersion());
                mPresenter.getMiningStatus(mUser.getPid(), mCurrentUserMine.getId());
            }
        }
    }

    @Override
    public void showUserMineListFail(Error err) {
        LogUtils.d("showUserMineListFail " + err.getCode() + err.getMessage());
        checkError(err);
    }

    @Override
    public void showCoinMarketSuccess(CoinMarket market) {
        if (market != null) {
            mCoinMarket = market;
            refreshMarketView();
        }
    }

    @Override
    public void showCoinMarketFail(Error err) {
        LogUtils.d("showCoinMarketFail " + err.getCode() + err.getMessage());
        checkError(err);
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Calendar todayEnd = Calendar.getInstance();
            todayEnd.set(Calendar.HOUR_OF_DAY, 23);
            todayEnd.set(Calendar.MINUTE, 59);
            todayEnd.set(Calendar.SECOND, 59);
            todayEnd.set(Calendar.MILLISECOND, 999);

            long endTime = todayEnd.getTimeInMillis();
            long startTime = System.currentTimeMillis();
            final long midTime = (endTime - startTime) / 1000;
            long hh = midTime / 60 / 60 % 24;
            long mm = midTime / 60 % 60;
            long ss = midTime % 60;
            if (hh == 0 && mm == 0 && ss == 0) {
                if (mMsgContent2 != null) {
                    mMsgContent2.setVisibility(View.GONE);
                }
                if (mMsgView2 != null) {
                    resetMiningStatus();
                    refreshComsumeView();
                }
            } else {
                if (mMsgContent2 != null) {
                    if (hh >= 24) {
                        mMsgContent2.setVisibility(View.GONE);
                    } else {
                        mMsgContent2.setVisibility(View.VISIBLE);
                        mMsgContent2.setText(String.format(getString(R.string.start_mining_daoshi), hh, mm, ss));
                    }
                }
                mHandler.removeCallbacksAndMessages(null);
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    public void startDaojishi() {
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("HomeFragment onDestroy");
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BLEAction.getInstance(App.getInstance().getBle(), getContext()).disconnect(App.getInstance().getCurrentDevice());
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        mConnectHandler.removeCallbacksAndMessages(null);
        mConnectHandler = null;
        mTimeoutHandler.removeCallbacksAndMessages(null);
        mTimeoutHandler = null;
    }

    public void startScanBLE() {
        if (App.getInstance().getCurrentUserMine()==null ||
                App.getInstance().getCurrentUserMine().getMineType().getType()==MineType.TYPE_APP) {
            return;
        }

        mConnectHandler.sendEmptyMessageDelayed(2, 3 * 1000);

        if (!mScanning) {
            openBluetoothScanDevice();
        } else {
            myhandler.post(stopRunnable);
        }

        mBle = App.getInstance().getBle();
        if (mBle != null) {
            mBle.setDataCallback(BLEAction.dataCallback);
        } else {
            App.getInstance().initBle();
            mBle = App.getInstance().getBle();
        }
    }

    public void connectBLE() {
        BLEAction.getInstance(App.getInstance().getBle(), getContext()).disconnect(App.getInstance().getCurrentDevice());
        UserMine cUserMine = App.getInstance().getCurrentUserMine();
        if (cUserMine != null && cUserMine.getMineType().getType() != MineType.TYPE_APP && mDeviceList != null) {
            for (final BLEDevice device : mDeviceList) {
                if (device.getBluetoothDevice().getAddress().equals(cUserMine.getMac())) {
                    App.getInstance().setCurrentDevice(device);
                    mConnectHandler.sendEmptyMessageDelayed(2, 500);
                    break;
                }
            }
        }
    }

    public void connectBLEDevice(BLEDevice device) {
        BLEAction.getInstance(App.getInstance().getBle(), getContext()).disconnect(device);
        App.getInstance().setCurrentDevice(device);
        mConnectHandler.sendEmptyMessageDelayed(2, 200);
    }

    Handler myhandler = new Handler();
    BleHelper mBle;
    ArrayList<BLEDevice> mDeviceList = new ArrayList<BLEDevice>();
    boolean mScanning = false;
    private int mc = 0;

    private Handler mTimeoutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    if (mSubmitMining != null) mSubmitMining.setEnabled(true);
                    break;
            }
            Snackbar.make(mCapacityView, getString(R.string.err_mine_connect), Snackbar.LENGTH_SHORT).show();
//            BLEAction.getInstance(mBle, mContext).disconnect(App.getInstance().getCurrentDevice());
        }
    };

    private Handler mConnectHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                mc = 0;
                removeCallbacksAndMessages(null);
                BLEAction.getInstance(App.getInstance().getBle(), getActivity()).connect(App.getInstance().getCurrentDevice(), new com.ees.chain.ble.Callback() {
                    @Override
                    public void onSuccess(Object obj) {
                        Log.e("EES", "connect success");
                        mConnectHandler.sendEmptyMessageDelayed(3, 500);
                    }

                    @Override
                    public void onFail(Object obj) {
                        Log.e("EES", "connect fail");
                    }
                });
            } else if (msg.what == 3) {
                BLEAction.getInstance(App.getInstance().getBle(), getActivity()).openNotification(new com.ees.chain.ble.Callback() {
                    @Override
                    public void onSuccess(Object obj) {
                        Log.e("EES", "open notif successs");
//                        BLEAction.getInstance(mBle, UserMineListActivity.this).setDeviceActiveStatus(true);
                        refreshDeviceConsume(false, false, false);
                    }

                    @Override
                    public void onFail(Object obj) {
                        Log.e("EES", "open notif fail");
                        mc++;
                        if (mc < 5) {
                            mConnectHandler.sendEmptyMessageDelayed(2, 500);
                        }
                    }
                });
            }
        }
    };

    public boolean getConnectResult() {
        if (BLEAction.isNotifOpen) return true;
        BLEDevice device = App.getInstance().getCurrentDevice();
        if (device != null) {
            return BLEAction.getInstance(App.getInstance().getBle(), getContext()).openNotification();
        }
        return false;
    }

    private Runnable stopRunnable = new Runnable() {
        @Override
        public void run() {
            mBle.stopScan();
            mScanning = false;
            connectBLE();
        }
    };

    private BleScanResultCallback resultCallback = new BleScanResultCallback() {
        @Override
        public void onSuccess() {
            LogUtils.d("开启扫描成功");
        }

        @Override
        public void onFail() {
            LogUtils.d("开启扫描失败");
        }

        @Override
        public void onFindDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
            LogUtils.d("扫描到新设备：" + device.getName() + "     " + device.getAddress());
            if (mDeviceList != null) {
                //判断是否已存在
                boolean canadd = true;
                for (BLEDevice temp : mDeviceList) {
                    if (temp.getBluetoothDevice().getAddress().equals(device.getAddress())) {
                        //已存在则更新rssi
                        temp.setRssi(rssi);
                        canadd = false;
                        LogUtils.d("更新rssi");
                    }
                }
                if (canadd) {
                    try {
                        Thread.sleep(10 + new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //不存在则添加
                    BLEDevice newdevice = new BLEDevice();
                    newdevice.setBluetoothDevice(device);
                    newdevice.setRssi(rssi);
                    mDeviceList.add(newdevice);
                    LogUtils.d("新设备已添加");
                }
            }
        }
    };

    /**
     * 检测蓝牙是否打开
     */
    void openBluetoothScanDevice() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            //蓝牙没打开则去打开蓝牙
            boolean openresult = toEnable(BluetoothAdapter.getDefaultAdapter());
            if (!openresult) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), getString(R.string.err_open_ble), Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            //停个半秒再检查一次
            SystemClock.sleep(500);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int i = 0;
                    while (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (i >= 15) {
                            Toast.makeText(getActivity(), getString(R.string.err_open_ble), Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            i++;
                        }

                    }
                    //发现蓝牙打开了，则进行开启扫描的步骤
                    scanDevice();
                }
            });
        } else {
            //检查下当前是否在进行扫描 如果是则先停止
            if (mBle != null && mScanning) {
                mBle.stopScan();
            }
            scanDevice();
        }
    }

    private boolean toEnable(BluetoothAdapter bluertoothadapter) {
        boolean result = false;
        try {
            for (Method temp : Class.forName(bluertoothadapter.getClass().getName()).getMethods()) {
                if (temp.getName().equals("enableNoAutoConnect")) {
                    result = (boolean) temp.invoke(bluertoothadapter);
                }
            }
        } catch (Exception e) {
            //反射调用失败就启动通过enable()启动;
            result = bluertoothadapter.enable();
            LogUtils.d("启动蓝牙的结果:" + result);
            e.printStackTrace();
        }
        return result;

    }

    private final int lsc = 9;
    private int sc = 0;

    @UiThread
    void scanDevice() {
        //如果此时发蓝牙工作还是不正常 则打开手机的蓝牙面板让用户开启
        if (mBle != null && !mBle.adapterEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, MineListActivity.REQUEST_ENABLE_BT);
        }

        myhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //检查一下去那些，如果没有则动态请求一下权限
                requestPermission();
                //开启扫描
                scanLeDevice(true);
                if (sc < lsc) {
                    sc++;
                    myhandler.sendEmptyMessageDelayed(0, 1000);
                } else {
                    sc = 0;
                    myhandler.removeCallbacksAndMessages(null);
                }
            }
        }, 500);
    }

    private void scanLeDevice(final boolean enable) {
        //获取ble操作类
        mBle = App.getInstance().getBle();
        if (mBle == null) {
            return;
        }
        if (enable) {
            //开始扫描
            if (mBle != null) {
                boolean startscan = mBle.startScan(resultCallback);
                if (!startscan) {
//                    Toast.makeText(getActivity(), getString(R.string.err_ble_work), Toast.LENGTH_SHORT).show();
                    return;
                }
                mScanning = true;
                //扫描一分钟后停止扫描
                myhandler.postDelayed(stopRunnable, MineListActivity.SCAN_PERIOD);
            }
        } else {
            //停止扫描
            mScanning = false;
            if (mBle != null) {
                mBle.stopScan();
                myhandler.removeCallbacksAndMessages(null);
            }
        }
    }

    synchronized private void requestPermission() {
        try {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH}, MineListActivity.REQUEST_FINE_LOCATION);
                Toast.makeText(getActivity(), getString(R.string.err_open_ble), Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
