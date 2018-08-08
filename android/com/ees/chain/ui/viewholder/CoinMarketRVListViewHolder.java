package com.ees.chain.ui.viewholder;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.ble.BLEDevice;
import com.ees.chain.ble.BleHelper;
import com.ees.chain.domain.CoinMarket;
import com.ees.chain.domain.MineType;
import com.ees.chain.event.BindDeviceEvent;
import com.ees.chain.ui.view.support.CommomDialog;
import com.ees.chain.ui.view.support.WUTypeFaceSongSanTextView;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.ees.chain.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kesion on 2017/12/5.
 */

public class CoinMarketRVListViewHolder extends BaseViewHolder {
    private final static String TAG = "CoinMarketRVListViewHolder";

    private List<CoinMarket> mDatas = new ArrayList<CoinMarket>();
    private Context mContext;

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

    public CoinMarketRVListViewHolder(View itemView, List list, Context context) {
        super(itemView);
        mDatas = list;
        mContext = context;
    }

    @Override
    public void onBindViewHolder(final int position) {
        mRefresh.setVisibility(View.INVISIBLE);
        CoinMarket coinMarket = mDatas.get(position);
        if (coinMarket != null) {
            mMarketName.setText(coinMarket.getMarketName());
            mPrice1.setText(Utils.decimalFormat(coinMarket.getLast()));
            mUnit.setText(coinMarket.getUnit());
            mPrice2.setText(coinMarket.getPriceRMB() + "");
            String updown = coinMarket.getUpDown();
            if (updown.startsWith("+")) {
                mTitle.setTextColor(Color.parseColor("#FF02C407"));
            } else {
                mTitle.setTextColor(Color.parseColor("#FFDD4B39"));
            }
            mTitle.setText(String.format(mContext.getString(R.string.market_updown), updown));
            mSubtitle.setText(coinMarket.getDetail());
        }
    }

    @Override
    public void onItemClick(View view, final int position) {
        CoinMarket coinMarket = mDatas.get(position);
        if (coinMarket!=null && !StringUtils.isBlank(coinMarket.getAction())) {
            Intent intent11 = new Intent();
            intent11.setAction("android.intent.action.VIEW");
            Uri version_url = Uri.parse(coinMarket.getAction());
            intent11.setData(version_url);
            mContext.startActivity(intent11);
        }
    }
}
