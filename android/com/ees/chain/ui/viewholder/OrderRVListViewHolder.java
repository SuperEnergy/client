package com.ees.chain.ui.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.ees.chain.R;
import com.ees.chain.domain.GoodsOrder;
import com.ees.chain.ui.view.support.WUTypeFaceSongSanTextView;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.ees.chain.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kesion on 2017/12/5.
 */

public class OrderRVListViewHolder extends BaseViewHolder {
    List<GoodsOrder> mList;
    Context mContext;

    @BindView(R.id.check_detail)
    View mDetailView;
    @BindView(R.id.order_no)
    WUTypeFaceSongSanTextView mOrderNo;
    @BindView(R.id.ctime)
    WUTypeFaceSongSanTextView mCtime;
    @BindView(R.id.goods_name)
    WUTypeFaceSongSanTextView mGoodsName;
    @BindView(R.id.buy_num)
    WUTypeFaceSongSanTextView mBuyNum;
    @BindView(R.id.ees_num)
    WUTypeFaceSongSanTextView mEESNum;
    @BindView(R.id.pay_status)
    WUTypeFaceSongSanTextView mPayStatus;

    public OrderRVListViewHolder(View itemView, List list, Context context) {
        super(itemView);
        mList = list;
        mContext = context;
    }

    @Override
    public void onBindViewHolder(int position) {
        mDetailView.setVisibility(View.GONE);
        mOrderNo.setText(mList.get(position).getId());
        mPayStatus.setTextColor(Color.parseColor("#FF808080"));
        Long date = mList.get(position).getCreateDate();
        mCtime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        mGoodsName.setText(mList.get(position).getGoods().getName());
        mBuyNum.setText(mList.get(position).getQty()+"");
        mEESNum.setText(Utils.doubleFormat(mList.get(position).getEesAmount()) + "ees");


        if (GoodsOrder.STATUS_PAID == mList.get(position).getStatus()) {
            mPayStatus.setTextColor(Color.parseColor("#FF99CC00"));
            mPayStatus.setText(mContext.getString(R.string.buy_success));
        } else if (GoodsOrder.STATUS_ORDERED == mList.get(position).getStatus()){
            mPayStatus.setText(R.string.pay_ordered);
            mPayStatus.setTextColor(Color.parseColor("#FFFFA42F"));
        } else if (GoodsOrder.STATUS_CANCELED == mList.get(position).getStatus()){
            mPayStatus.setText(R.string.pay_cancel);
            mPayStatus.setTextColor(Color.parseColor("#FF808080"));
        }
    }

    @Override
    public void onItemClick(View view, int position) {
//        Intent intent = new Intent();
//        intent.setClass(mContext, ServantActivity.class);
//        mContext.startActivity(intent);
    }
}
