package com.ees.chain.ui.viewholder;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.ble.BLEDevice;
import com.ees.chain.ble.BleHelper;
import com.ees.chain.domain.MineType;
import com.ees.chain.domain.UserMine;
import com.ees.chain.event.BindDeviceEvent;
import com.ees.chain.ui.view.support.CommomDialog;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kesion on 2017/12/5.
 */

public class MineRVListViewHolder extends BaseViewHolder {
    private final static String TAG = "MineRVListViewHolder";

    private List<BLEDevice> mDevices = new ArrayList<BLEDevice>();
    private Context mContext;
    private BleHelper mBle;
    private HashMap<String, UserMine> mMapUserMines = new HashMap<String, UserMine> ();
    private boolean binded;

    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.max_volume)
    TextView mMaxVolume;
    @BindView(R.id.cover)
    SimpleDraweeView mCover;
    @BindView(R.id.bind)
    TextView mBind;


    public MineRVListViewHolder(View itemView, List list, BleHelper ble, HashMap<String, UserMine> mapUserMines, Context context) {
        super(itemView);
        mDevices = list;
        mContext = context;
        mBle = ble;
        mMapUserMines = mapUserMines;
    }

    @Override
    public void onBindViewHolder(final int position) {
        final String deviceName = mDevices.get(position).getBluetoothDevice().getName();
        final String address = mDevices.get(position).getBluetoothDevice().getAddress().toUpperCase();
        final MineType minetype = App.getInstance().mMapMineTypes.get(deviceName);
        mBind.setVisibility(View.INVISIBLE);
        if (minetype != null) {
            mCover.setImageURI(minetype.getCover());
            mName.setText(minetype.getName());
            mMaxVolume.setText(String.format(mContext.getString(R.string.max_volum2), minetype.getMaxQty()));
            mBind.setVisibility(View.VISIBLE);
            if (mMapUserMines.get(address) == null) {
                mBind.setText(mContext.getString(R.string.bind));
                binded = false;
                mBind.setEnabled(true);
            } else {
                mBind.setText(mContext.getString(R.string.binded));
                binded = true;
                mBind.setEnabled(false);
            }
        } else {
            mCover.setImageResource(R.mipmap.ic_launcher);
            mName.setText("unknow");
            mMaxVolume.setText("unknow");
            mBind.setVisibility(View.INVISIBLE);
            binded = true;
        }

        mBind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!binded) {
                    onItemClick(mBind, position);
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, final int position) {
        if (!binded) showBindDeviceDialog(mContext, position);
    }

    public void showBindDeviceDialog(final Context context, final int position) {
        String content = context.getString(R.string.dialog_content_bind_device);
        if (StringUtils.isBlank(content)) {
            return;
        }
        CommomDialog dialog = new CommomDialog(context, R.style.dialog, content, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    App.getInstance().setCurrentDevice(mDevices.get(position));
                    EventBus.getDefault().post(new BindDeviceEvent());
                }
                dialog.dismiss();
            }
        });
        dialog.setTitle(context.getString(R.string.tips)).show();
    }
}
