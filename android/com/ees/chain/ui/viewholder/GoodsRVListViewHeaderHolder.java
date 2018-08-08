package com.ees.chain.ui.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ees.chain.R;
import com.ees.chain.domain.Notice;
import com.ees.chain.domain.UserMine;
import com.ees.chain.task.group.MsgPresenter;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kesion on 2017/12/5.
 */

public class GoodsRVListViewHeaderHolder extends BaseViewHolder {
    Context mContext;
    UserMine mUserMine;

    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.cmax_volume)
    TextView mCVolume;
    @BindView(R.id.max_volume)
    TextView mMaxVolume;

    public GoodsRVListViewHeaderHolder(View itemView, UserMine usermine, Context context) {
        super(itemView);
        mUserMine = usermine;
        mContext = context;
    }

    @Override
    public void onBindViewHolder(final int position) {
        mName.setText(mUserMine.getName());
        mCVolume.setText(String.format(mContext.getString(R.string.cmax_volum), mUserMine.getMaxQtyLimit()));
        mMaxVolume.setText(String.format(mContext.getString(R.string.max_volum), mUserMine.getMaxQtyLimit()));
    }

    @Override
    public void onItemClick(View view, int position) {
    }
}
