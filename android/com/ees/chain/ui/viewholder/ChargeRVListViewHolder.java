package com.ees.chain.ui.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.domain.Fund;
import com.ees.chain.task.group.BonusPresenter;
import com.ees.chain.ui.view.support.WUTypeFaceSongSanTextView;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.ees.chain.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kesion on 2017/12/5.
 */

public class ChargeRVListViewHolder extends BaseViewHolder {
    List<Fund> mList;
    Context mContext;
    private int ctype;

    @BindView(R.id.type_view)
    View mTypeView;
    @BindView(R.id.user_view)
    View mUserView;
    @BindView(R.id.check_detail)
    View mDetailView;
    @BindView(R.id.bonus)
    WUTypeFaceSongSanTextView mBonus;
    @BindView(R.id.balance)
    WUTypeFaceSongSanTextView mBalance;
    @BindView(R.id.ctime)
    WUTypeFaceSongSanTextView mCtime;
    @BindView(R.id.type)
    WUTypeFaceSongSanTextView mType;
    @BindView(R.id.user)
    WUTypeFaceSongSanTextView mUser;
    @BindView(R.id.type_hint)
    WUTypeFaceSongSanTextView mTypeHint;

    public ChargeRVListViewHolder(View itemView, List list, int type, Context context) {
        super(itemView);
        mList = list;
        mContext = context;
        ctype = type;
    }

    @Override
    public void onBindViewHolder(int position) {
        mDetailView.setVisibility(View.GONE);
        mUserView.setVisibility(View.GONE);
        mType.setTextColor(Color.parseColor("#FF808080"));
        mUser.setText("");

        double qty = Utils.doubleFormat(mList.get(position).getQty());

        mBonus.setText(qty+" ees");
        Long date = mList.get(position).getCreateDate();
        mCtime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        double balance = Utils.doubleFormat(mList.get(position).getBalance());
        mBalance.setText(String.format(mContext.getString(R.string.cyue_hint), balance+""));
        if (ctype == BonusPresenter.TYPE_ALL_BONUS) {
            mBalance.setVisibility(View.VISIBLE);
        } else {
            mBalance.setVisibility(View.INVISIBLE);
        }

        if (!StringUtils.isBlank(mList.get(position).getPid())) {
            mUser.setText(mList.get(position).getPid());
        }

        mType.setText(R.string.b_subtype19);
        mTypeHint.setText(R.string.ctype_hint);

        String subtype = mList.get(position).getSubType();
        if (!StringUtils.isBlank(subtype)) {
            String tname = App.getInstance().mFundSubTypeNames.get(subtype);
            if (!StringUtils.isBlank(tname)){
                mType.setText(tname);
                String bonus = mContext.getString(R.string.bonus);
                if (tname.contains(bonus)) {
                    mTypeHint.setText(R.string.csource_hint);
                } else {
                    mTypeHint.setText(R.string.ctype_hint);
                }
            } else {
                mType.setText(R.string.b_subtype9);
            }
        }

        if (Fund.SUBTYPE_TRRANSFER_IN.equals(subtype)) {
            mType.setText(R.string.b_subtype2);
            mType.setTextColor(Color.parseColor("#FF99CC00"));
            mUserView.setVisibility(View.VISIBLE);
            mUser.setText(mList.get(position).getRefPid());
        } else if (Fund.SUBTYPE_TRRANSFER_OUT.equals(subtype)){
            mType.setText(R.string.b_subtype3);
            mType.setTextColor(Color.parseColor("#FFFFA42F"));
            mUserView.setVisibility(View.VISIBLE);
            mUser.setText(mList.get(position).getRefPid());
        }/* else if (Fund.SUBTYPE_MINING_BY_APP.equals(mList.get(position).getSubType())){
            mType.setText(R.string.b_subtype1);
            mTypeHint.setText(R.string.csource_hint);
        } else if (Fund.SUBTYPE_INVITE_ONE_LEVEL.equals(mList.get(position).getSubType())){
            mType.setText(R.string.b_subtype4);
        } else if (Fund.SUBTYPE_INVITE_TWO_LEVEL.equals(mList.get(position).getSubType())){
            mType.setText(R.string.b_subtype5);
        }  else if (Fund.SUBTYPE_ACTIVITY_AWARD.equals(mList.get(position).getSubType())){
            mType.setText(R.string.b_subtype6);
        } else if (Fund.SUBTYPE_TRANSFER_OUT_FEE.equals(mList.get(position).getSubType())) {
            mType.setText(R.string.b_subtype7);
        } else if (Fund.SUBTYPE_SHOPPING_SCALE.equals(mList.get(position).getSubType())) {
            mType.setText(R.string.b_subtype8);
        } else {
            mType.setText(R.string.b_subtype19);
        }*/
    }

    @Override
    public void onItemClick(View view, int position) {
//        Intent intent = new Intent();
//        intent.setClass(mContext, ServantActivity.class);
//        mContext.startActivity(intent);
    }
}
