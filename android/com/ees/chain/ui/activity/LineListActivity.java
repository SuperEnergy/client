package com.ees.chain.ui.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.ble.BLEDevice;
import com.ees.chain.ble.BleHelper;
import com.ees.chain.ble.BleScanResultCallback;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.domain.Error;
import com.ees.chain.domain.MineType;
import com.ees.chain.domain.User;
import com.ees.chain.domain.UserMine;
import com.ees.chain.event.BindDeviceEvent;
import com.ees.chain.event.RefreshUserMinesEvent;
import com.ees.chain.task.group.MinePresenter;
import com.ees.chain.ui.base.BaseRVActivity;
import com.ees.chain.ui.interfc.MineContract;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.ees.chain.ui.viewholder.MineRVListViewHolder;
import com.ees.chain.utils.BLEAction;
import com.ees.chain.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

public class LineListActivity extends BaseRVActivity<MinePresenter, BLEDevice> implements MineContract.View {

    @BindView(R.id.no_data)
    View mNoData;
    @BindView(R.id.left)
    View mBack;
    @BindView(R.id.title)
    TextView mTitle;

    private User mUser;
    public HashMap<String, UserMine> mMapUserMines = new HashMap<String, UserMine> ();

    Handler myhandler = new Handler();
    BleHelper mBle;
    List<BLEDevice> devicelist = new ArrayList<BLEDevice>();
    boolean mScanning = false;


    public static final long SCAN_PERIOD = 30000;
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_FINE_LOCATION = 0;

    private Runnable stopRunnable = new Runnable() {
        @Override
        public void run() {
            mBle.stopScan();
            mScanning = false;
        }
    };

    @OnClick(R.id.left)
    public void back(View view) {
        finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_charge_list;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveBindDeviceEvent(BindDeviceEvent event){
        BLEDevice cdevice = App.getInstance().getCurrentDevice();
        String uuid = BLEAction.getInstance(mBle, this).getUuid();
        String name = cdevice.getBluetoothDevice().getName();
        String mac = cdevice.getBluetoothDevice().getAddress();

        mPresenter.bindHardware(mUser.getPid(), uuid, mac, name);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        if (devicelist != null) devicelist.clear();
    }

    @Override
    public void initDatas() {
        mUser = App.getInstance().getUser();
        mPresenter.getMineTypeList(mUser.getPid());

        if (!mScanning) {
            openBluetoothScanDevice();
        } else {
            myhandler.post(stopRunnable);
        }

        List<UserMine> usermines = App.getInstance().getUserMines();
        if (usermines != null && usermines.size()>0) {
            for (UserMine us: usermines) {
                if (us.getMineType().getType() != MineType.TYPE_APP) {
                    mMapUserMines.put(us.getMac(), us);
                }
            }
        }

        mBle = App.getInstance().getBle();
        if (mBle != null) {
            mBle.setDataCallback(BLEAction.dataCallback);
        }
    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        mTitle.setText(R.string.find_mine);
        mNoData.setVisibility(View.GONE);
        showLoadingDialog();
    }

    @Override
    public void showMineListSuccess(List<BLEDevice> data) {
        dismissLoadingDialog();

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
            adapter.notifyDataSetChanged();
            myhandler.post(stopRunnable);
        }
        if (recycler!=null) recycler.onRefreshCompleted();
        checkEmpty();
    }

    @Override
    public void showMineListFail(Error err) {
        Snackbar.make(recycler, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        dismissLoadingDialog();
        if (recycler!=null) recycler.onRefreshCompleted();
        checkEmpty();
        myhandler.post(stopRunnable);
        checkError(err);
    }

    @Override
    public void getMineTypeListSuccess(List<MineType> minetypes) {
        if (minetypes == null) return;
        for (MineType minetype: minetypes) {
            if (minetype != null) App.getInstance().mMapMineTypes.put(minetype.getSn(), minetype);
        }
    }

    @Override
    public void getMineTypeListFail(Error err) {
        LogUtils.d("showMineListFail." + err.getCode() + ", " + err.getMessage());
        checkError(err);
    }

    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mine, parent, false);
        return new MineRVListViewHolder(view, mDataList, mBle, mMapUserMines, this);
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_services, parent, false);
//        return new MineRVListViewHolder2(view, mDataList, mBle, this);
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
        initDatas();
    }

    @Override
    public void showError() {
        dismissLoadingDialog();
        checkEmpty();
        if (recycler!=null) recycler.onRefreshCompleted();
    }

    @Override
    public void complete() {
        dismissLoadingDialog();
        if (recycler!=null) recycler.onRefreshCompleted();
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
        if (devicelist != null) devicelist.clear();
    }

    @Override
    public void bindSuccess(Boolean result) {
        Snackbar.make(recycler, getBaseContext().getString(R.string.bind_success), Snackbar.LENGTH_SHORT).show();
        BLEAction.getInstance(mBle, LineListActivity.this).setDeviceActiveStatus(true);
        BLEDevice cdevice = App.getInstance().getCurrentDevice();
        String name = cdevice.getBluetoothDevice().getName();
        String mac = cdevice.getBluetoothDevice().getAddress();
        UserMine us = new UserMine();
        us.setName(name);
        us.setMac(mac);
        mMapUserMines.put(mac, us);
        adapter.notifyDataSetChanged();
        EventBus.getDefault().post(new RefreshUserMinesEvent());
    }

    @Override
    public void bindFail(Error err) {
        Snackbar.make(recycler, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        checkError(err);
    }

    private BleScanResultCallback resultCallback = new BleScanResultCallback() {
        @Override
        public void onSuccess() {
            LogUtils.d("开启扫描成功");
        }

        @Override
        public void onFail() {
            LogUtils.d("开启扫描失败");
            Error err = new Error();
            err.setCode("define");
            err.setMessage(App.getInstance().getString(R.string.scan_ble_fail));
            showMineListFail(err);
        }

        @Override
        public void onFindDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
            LogUtils.d("扫描到新设备：" + device.getName() + "     " + device.getAddress());
            if (devicelist != null) {
                //判断是否已存在
                boolean canadd = true;
                for (BLEDevice temp : devicelist) {
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
                    if (newdevice.getBluetoothDevice()!=null && !StringUtils.isBlank(newdevice.getBluetoothDevice().getName()) && newdevice.getBluetoothDevice().getName().toLowerCase().contains("eesline")) {
                        devicelist.add(newdevice);
                        LogUtils.d("新设备已添加");
                    }
                }
                showMineListSuccess(devicelist);
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
                                      Toast.makeText(LineListActivity.this, getString(R.string.err_open_ble), Toast.LENGTH_SHORT).show();
                                  }
                              });
                showError();
                return;
            }
            //停个半秒再检查一次
            SystemClock.sleep(500);
            runOnUiThread(new Runnable() {
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
                            Toast.makeText(LineListActivity.this, getString(R.string.err_open_ble), Toast.LENGTH_SHORT).show();
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
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
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
//                    Toast.makeText(MineListActivity.this, getString(R.string.err_ble_work), Toast.LENGTH_SHORT).show();
                    return;
                }
                mScanning = true;
                //扫描一分钟后停止扫描
                myhandler.postDelayed(stopRunnable, SCAN_PERIOD);
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
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, LineListActivity.REQUEST_FINE_LOCATION);
                Toast.makeText(this, getString(R.string.err_open_ble), Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
