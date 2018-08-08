package com.ees.chain.ui.viewholder;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.domain.Goods;
import com.ees.chain.domain.User;
import com.ees.chain.domain.UserMine;
import com.ees.chain.event.UpdateGoodsPriceEvent;
import com.ees.chain.ui.activity.GoodsListActivity;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kesion on 2017/12/5.
 */

public class GoodsRVListViewHolder2 extends BaseViewHolder {
    List<Goods> mList;
    Context mContext;
    User mUser;
    UserMine mUsermine;
    int mSubtype;

    @BindView(R.id.listview_header)
    View mListviewHeader;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.cmax_volume)
    TextView mCVolume;
    @BindView(R.id.max_volume)
    TextView mMaxVolume;
    @BindView(R.id.cover)
    SimpleDraweeView mCover;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.desc)
    TextView mDesc;
    @BindView(R.id.price)
    TextView mPrice;
    @BindView(R.id.reduce)
    View mReduce;
    @BindView(R.id.num)
    TextView mNum;
    @BindView(R.id.add)
    View mAdd;

    public GoodsRVListViewHolder2(View itemView, UserMine userMine, List list, int subtype, Context context) {
        super(itemView);
        mList = list;
        mContext = context;
        mUser = App.getInstance().getUser();
        mUsermine = userMine;
        mSubtype = subtype;
    }

    @Override
    public void onBindViewHolder(final int position) {
        if (position==0) {
            mListviewHeader.setVisibility(View.VISIBLE);
            mName.setText(mUsermine.getName());
            mCVolume.setText(String.format(mContext.getString(R.string.cmax_speedrate), mUsermine.getSpeedRate()+""));
            mMaxVolume.setText(String.format(mContext.getString(R.string.max_speedrate), mUsermine.getMaxSpeedRate()+""));
        } else {
            mListviewHeader.setVisibility(View.GONE);
        }

        mCover.setVisibility(View.VISIBLE);
        if (!StringUtils.isBlank(mList.get(position).getCover())) {
            mCover.setImageURI(mList.get(position).getCover());
        }
        mTitle.setText(mList.get(position).getName());
        mDesc.setText(mList.get(position).getDes());
        mPrice.setText(String.format(mContext.getString(R.string.price), mList.get(position).getPriceEES()+""));


        mReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c = 0;
                try {
                    c = Integer.parseInt(mNum.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                c--;
                if (c<=0) {
                    c = 0;
                }
                mNum.setText(c+"");
                ((GoodsListActivity)mContext ).mGoodsNum.put(mList.get(position).getId(), c);
                double speedrate = mList.get(position).getSpeedRate();
                double sumSpeedrate = c * speedrate;
                ((GoodsListActivity)mContext).mSpeedrateSum.put(mList.get(position).getId(), sumSpeedrate);
                double sumees = c * mList.get(position).getPriceEES();
                ((GoodsListActivity)mContext).mSumees.put(mList.get(position).getId(), sumees);
                EventBus.getDefault().post(new UpdateGoodsPriceEvent());
            }
        });

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c = 0;
                try {
                    c = Integer.parseInt(mNum.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                c++;
                long allees = 0;
                for (String key: ((GoodsListActivity)mContext).mSumees.keySet()) {
                    if (!mList.get(position).getId().equals(key)) {
                        allees += ((GoodsListActivity)mContext).mSumees.get(key);
                    }
                }

                double sumees = c * mList.get(position).getPriceEES();
                double ees = App.getInstance().getLedger().getFund() - sumees - allees;
                if (ees < 0) {
                    Snackbar.make(mAdd, mContext.getString(R.string.err_money_notenought), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                double allVolumes = 0.0;
                for (String key: ((GoodsListActivity)mContext).mSpeedrateSum.keySet()) {
                    if (!mList.get(position).getId().equals(key)) {
                        allVolumes += ((GoodsListActivity)mContext).mSpeedrateSum.get(key);
                    }
                }
                double speedRate = mList.get(position).getSpeedRate();
                double sumSpeedrate = c * speedRate;
                if (sumSpeedrate + allVolumes  + mUsermine.getSpeedRate() > mUsermine.getMaxSpeedRate()) {
                    Snackbar.make(mAdd, mContext.getString(R.string.err_over_speed_limit), Snackbar.LENGTH_SHORT).show();
                    return;
                } else {
                    mNum.setText(c+"");
                    ((GoodsListActivity)mContext).mSpeedrateSum.put(mList.get(position).getId(), sumSpeedrate);
                    ((GoodsListActivity)mContext).mGoodsNum.put(mList.get(position).getId(), c);
                    ((GoodsListActivity)mContext).mSumees.put(mList.get(position).getId(), sumees);
                    EventBus.getDefault().post(new UpdateGoodsPriceEvent());
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
