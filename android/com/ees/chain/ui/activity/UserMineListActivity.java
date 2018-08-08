package com.ees.chain.ui.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
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
import com.ees.chain.domain.Error;
import com.ees.chain.domain.MineType;
import com.ees.chain.domain.User;
import com.ees.chain.domain.UserMine;
import com.ees.chain.event.RefreshUserMinesEvent;
import com.ees.chain.event.RenameDeviceEvent;
import com.ees.chain.event.RenameDeviceEvent2;
import com.ees.chain.event.UnbindDeviceEvent;
import com.ees.chain.event.UpdateUserMineChangeEvent;
import com.ees.chain.event.UpdateUserMineChangeEvent2;
import com.ees.chain.manager.CacheManager;
import com.ees.chain.task.group.UserMinePresenter;
import com.ees.chain.ui.base.BaseRVActivity;
import com.ees.chain.ui.interfc.UserMineContract;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.ees.chain.ui.viewholder.UserMineRVListViewHolder;
import com.ees.chain.utils.BLEAction;
import com.ees.chain.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

public class UserMineListActivity extends BaseRVActivity<UserMinePresenter, UserMine> implements UserMineContract.View {

    @BindView(R.id.no_data)
    View mNoData;
    @BindView(R.id.left)
    View mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.btn_add)
    View mAdd;
    @BindView(R.id.cmining_type)
    Button mCMiningType;

    private User mUser;

    public UserMine mSelectUserMine;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveUpdateUserMineChangeEvent2(UpdateUserMineChangeEvent2 event){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                connectBLE(true);
            }
        }, 200);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveRefreshUserMinesEvent(RefreshUserMinesEvent event) {
        mPresenter.getUserMineList(mUser.getPid(), MineType.TYPE_CUBE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveUnbindDeviceEvent(UnbindDeviceEvent event){
        BLEAction.getInstance(App.getInstance().getBle(), UserMineListActivity.this).setDeviceActiveStatus(false);
        BLEAction.getInstance(App.getInstance().getBle(), UserMineListActivity.this).clearDeviceConsume(null);
        String usermine_id  = App.getInstance().getUnbindDevice().getId();
        mPresenter.unbindHardware(mUser.getPid(), usermine_id);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveRenameDeviceEvent(RenameDeviceEvent event){
        if (mSelectUserMine != null) {
            String usermine_id = mSelectUserMine.getId();
            String name = mSelectUserMine.getName();
            mPresenter.renameHardware(mUser.getPid(), usermine_id, name);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveRenameDeviceEvent2(RenameDeviceEvent2 event){
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.left)
    public void back(View view) {
        finish();
    }

    @OnClick({R.id.btn_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                startActivity(new Intent(App.getInstance().getApplicationContext(), MineListActivity.class));
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        if (mDeviceList!=null) mDeviceList.clear();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_usermine_list;
    }

    @Override
    public void initDatas() {
        mUser = App.getInstance().getUser();
        mDataList = (ArrayList<UserMine>) App.getInstance().getUserMines();
        mSelectUserMine = App.getInstance().getCurrentUserMine();
        refreshUsermineList(mDataList);
        startScanBLE();
    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        mTitle.setText(R.string.mine_list);
        recycler.enableLoadMore(false);
        refreshCMiningType();
        checkEmpty();
        hidSoftInputMode();
    }

    public void refreshCMiningType() {
        if (mCMiningType != null && App.getInstance().getCurrentUserMine()!=null) mCMiningType.setText(String.format(getString(R.string.cmining_type), App.getInstance().getCurrentUserMine().getName()));
    }

    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usermine, parent, false);
        return new UserMineRVListViewHolder(view, mDataList, mMacs, this);
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void onRefresh(int action) {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        mDataList.clear();
        mPresenter.getUserMineList(mUser.getPid(), MineType.TYPE_CUBE);
        startScanBLE();
    }

    @Override
    public void showError() {
        if (recycler!=null) recycler.onRefreshCompleted();
    }

    @Override
    public void complete() {
        if (recycler!=null) recycler.onRefreshCompleted();
    }

    @Override
    public void showUserMineListSuccess(List<UserMine> data) {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }

        mDataList.clear();
        if (recycler== null) return;
        recycler.enableLoadMore(false);
        if (data == null || data.size() == 0) {
//            recycler.enableLoadMore(false);
        } else {
            mDataList.addAll(data);
            refreshUsermineList(mDataList);
            adapter.notifyDataSetChanged();
        }
        App.getInstance().setUserMines(mDataList);
        if (recycler!=null) recycler.onRefreshCompleted();
        checkEmpty();
    }

    public void refreshUsermineList(List<UserMine> data) {
//        dismissLoadingDialog();
        if (data == null) return;
        if (data.size() == 1) {
            App.getInstance().setCurrentUserMine(data.get(0));
            data.get(0).checked = true;
            EventBus.getDefault().post(new UpdateUserMineChangeEvent());
            return;
        }
        for (UserMine usermine: data) {
            if (mSelectUserMine!=null && usermine.getId().equals(mSelectUserMine.getId())) {
                if (usermine.getMineType().getType() == MineType.TYPE_APP) {
                    usermine.checked = true;
                } else {
                    boolean connect = getConnectResult();
                    if (connect) {
                        usermine.checked = true;
                        UserMine cusermine = App.getInstance().getCurrentUserMine();
                        if (cusermine!=null && !cusermine.getId().equals(usermine.getId())) {
                            App.getInstance().setCurrentUserMine(usermine);
                            refreshCMiningType();
                            EventBus.getDefault().post(new UpdateUserMineChangeEvent());
                        }
                    } else {
                        usermine.checked = false;
                    }
                }
            } else {
                usermine.checked = false;
            }
        }
    }

    public void clearUsermineListStatus() {
        if (mDataList == null) return;
        for (UserMine usermine: mDataList) {
            usermine.checked = false;
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void showUserMineListFail(Error err) {
        Snackbar.make(recycler, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        if (recycler!=null) recycler.onRefreshCompleted();
        checkEmpty();
        checkError(err);
    }

    boolean mCrename = false;

    @Override
    public void renameSuccess(Boolean result) {
        CacheManager.getInstance().saveUserMineList(mDataList);
        if (App.getInstance().getCurrentUserMine().getId().equals(mSelectUserMine.getId())) {
            refreshCMiningType();
            App.getInstance().setCurrentUserMine(mSelectUserMine);
            mCrename = true;
        }
    }

    @Override
    public void renameFail(Error err) {
        Snackbar.make(recycler, err.getMessage(), Toast.LENGTH_SHORT).show();
        checkError(err);
    }

    @Override
    public void unbindSuccess(Boolean result) {
        Snackbar.make(recycler, getBaseContext().getString(R.string.unbind_success), Snackbar.LENGTH_SHORT).show();
        BLEAction.getInstance(App.getInstance().getBle(), UserMineListActivity.this).disconnect(App.getInstance().getCurrentDevice());
        if (mDeviceList != null && mDeviceList.size()>0) {
            mDataList.remove(App.getInstance().getCurrentUserMine());
            App.getInstance().setCurrentUserMine(mDataList.get(0));
            mDataList.get(0).checked = true;
            adapter.notifyDataSetChanged();
            refreshCMiningType();
            EventBus.getDefault().post(new UpdateUserMineChangeEvent());
        }
        mPresenter.getUserMineList(mUser.getPid(), MineType.TYPE_CUBE);
    }

    @Override
    public void unbindFail(Error err) {
        Snackbar.make(recycler, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        checkError(err);
    }

    public void checkEmpty() {
        if (mDataList==null || mDataList.size()==0) {
            if (mNoData != null) mNoData.setVisibility(View.VISIBLE);
        } else {
            if (mNoData != null) mNoData.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCrename) EventBus.getDefault().post(new RenameDeviceEvent2());
        mConnectHandler.removeCallbacksAndMessages(null);
        mTimeoutHandler.removeCallbacksAndMessages(null);
        mDataList.clear();
        mDataList = null;
        mSelectUserMine = null;
        mConnectHandler = null;
        mTimeoutHandler = null;
    }

    public void startScanBLE() {
        if (!mScanning) {
            openBluetoothScanDevice();
        } else {
            myhandler.post(stopRunnable);
        }

        mBle = App.getInstance().getBle();
        if (mBle != null) {
            mBle.setDataCallback(BLEAction.dataCallback);
        }
    }

    public void connectBLE(boolean showdialog) {
        BLEAction.getInstance(App.getInstance().getBle(), UserMineListActivity.this).disconnect(App.getInstance().getCurrentDevice());

        UserMine cUserMine = mSelectUserMine;
        if (cUserMine == null) return;
        if (cUserMine.getMineType().getType() == MineType.TYPE_APP) {
            mConnectHandler.sendEmptyMessageDelayed(0, 200);
        } else {
            if (cUserMine !=null && cUserMine.getMineType().getType() != MineType.TYPE_APP && mDeviceList!=null) {
                for (BLEDevice device: mDeviceList) {
                    if (device.getBluetoothDevice().getAddress().equals(cUserMine.getMac())) {
                        App.getInstance().setCurrentDevice(device);
                        mConnectHandler.sendEmptyMessageDelayed(2, 500);
                        break;
                    }
                }
            }
        }
        mTimeoutHandler.sendEmptyMessageDelayed(4, 15 * 1000);//timeout
    }

    public boolean getConnectResult() {
        if (BLEAction.isNotifOpen) return true;
        BLEDevice device = App.getInstance().getCurrentDevice();
        if (device != null) {
            return BLEAction.getInstance(App.getInstance().getBle(), this).openNotification();
         }
        return false;
    }

    Handler myhandler = new Handler();
    BleHelper mBle;
    ArrayList<BLEDevice> mDeviceList = new ArrayList<BLEDevice>();
    ArrayList<String> mMacs = new ArrayList<String>();
    boolean mScanning = false;

    private int mc = 0;

    private Runnable stopRunnable = new Runnable() {
        @Override
        public void run() {
            mBle.stopScan();
            mScanning = false;
        }
    };

    private Handler mTimeoutHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.e("EES", "timeout");
            if (adapter != null) adapter.notifyDataSetChanged();
            if(mConnectHandler!=null) mConnectHandler.removeCallbacksAndMessages(null);
        }
    };

    private Handler mConnectHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                App.getInstance().setCurrentUserMine(mSelectUserMine);
                refreshUsermineList(mDataList);
                if (adapter != null) adapter.notifyDataSetChanged();
                EventBus.getDefault().post(new UpdateUserMineChangeEvent());
                mc = 0;
            } else if (msg.what == 2) {
                mc = 0;
                BLEAction.getInstance(App.getInstance().getBle(), UserMineListActivity.this).connect(App.getInstance().getCurrentDevice(), new com.ees.chain.ble.Callback() {
                    @Override
                    public void onSuccess(Object obj) {
                        Log.e("EES", "connect success");
                        if(mConnectHandler!=null) mConnectHandler.sendEmptyMessageDelayed(3, 500);
                    }

                    @Override
                    public void onFail(Object obj) {
                        Log.e("EES", "connect fail");
                    }
                });
            } else if (msg.what == 3) {
                BLEAction.getInstance(App.getInstance().getBle(), UserMineListActivity.this).openNotification(new com.ees.chain.ble.Callback() {
                    @Override
                    public void onSuccess(Object obj) {
                        Log.e("EES", "open notif successs");
//                        BLEAction.getInstance(App.getInstance().getBle(), UserMineListActivity.this).setDeviceActiveStatus(true);
                        App.getInstance().setCurrentUserMine(mSelectUserMine);
                        refreshUsermineList(mDataList);
                        if (adapter != null) adapter.notifyDataSetChanged();
                        EventBus.getDefault().post(new UpdateUserMineChangeEvent());
                        if(mTimeoutHandler!=null) mTimeoutHandler.removeCallbacksAndMessages(null);
                    }

                    @Override
                    public void onFail(Object obj) {
                        Log.e("EES", "open notif fail");
                        mc++;
                        if (mc < 5) {
                            if(mConnectHandler!=null) mConnectHandler.sendEmptyMessageDelayed(2, 500);
                        }
                    }
                });
            }
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
                    if (newdevice.getBluetoothDevice() != null && !StringUtils.isBlank(newdevice.getBluetoothDevice().getAddress())) {
                        mMacs.add(newdevice.getBluetoothDevice().getAddress());
                        if (adapter!=null) adapter.notifyDataSetChanged();
                    }
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserMineListActivity.this, getString(R.string.err_open_ble), Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            //停个半秒再检查一次
            SystemClock.sleep(500);
            this.runOnUiThread(new Runnable() {
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
                            Toast.makeText(UserMineListActivity.this, getString(R.string.err_open_ble), Toast.LENGTH_SHORT).show();
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

    private final int lsc = 5;
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
            }
        }, 500);
    }

    private void scanLeDevice(final boolean enable) {
        //获取ble操作类
        mBle = App.getInstance().getBle();
        if (mBle == null) {
            App.getInstance().initBle();
            mBle = App.getInstance().getBle();
            return;
        }
        if (enable) {
            //开始扫描
            if (mBle != null) {
                boolean startscan = mBle.startScan(resultCallback);
                if (!startscan) {
//                    Toast.makeText(this, getString(R.string.err_ble_work), Toast.LENGTH_SHORT).show();
                    return;
                }
                mScanning = true;
                //扫描一分钟后停止扫描
                myhandler.postDelayed(stopRunnable, MineListActivity.SCAN_PERIOD);
                if (sc < lsc) {
                    sc++;
                    myhandler.sendEmptyMessageDelayed(0, 1000);
                } else {
                    sc = 0;
                    myhandler.removeCallbacksAndMessages(null);
                }
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
        //TODO 向用户请求权限
        try {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, MineListActivity.REQUEST_FINE_LOCATION);
                Toast.makeText(this, getString(R.string.err_open_ble), Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hidSoftInputMode() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
