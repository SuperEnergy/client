package com.ees.chain.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.domain.Error;
import com.ees.chain.domain.Goods;
import com.ees.chain.domain.GoodsOrder;
import com.ees.chain.domain.User;
import com.ees.chain.domain.UserMine;
import com.ees.chain.event.UpdateGoodsPriceEvent;
import com.ees.chain.event.UpdateVolumeEvent;
import com.ees.chain.task.group.GoodsPresenter;
import com.ees.chain.ui.base.BaseRVActivity;
import com.ees.chain.ui.interfc.GoodsContract;
import com.ees.chain.ui.view.support.CommomDialog;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.ees.chain.ui.view.support.recycler.PullRecycler;
import com.ees.chain.ui.viewholder.GoodsRVListViewHolder;
import com.ees.chain.ui.viewholder.GoodsRVListViewHolder2;
import com.ees.chain.utils.LogUtils;
import com.ees.chain.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by KESION on 2018/1/12.
 */

public class GoodsListActivity extends BaseRVActivity<GoodsPresenter, Goods> implements GoodsContract.View {

    public static final String ARG_USERMINE = "ARG_USERMINE";
    public static final String ARG_SUBTYPE = "ARG_SUBTYPE";

    @BindView(R.id.no_data)
    View mNoData;
    @BindView(R.id.left)
    View mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.remain_number)
    TextView mRemainEES;
    @BindView(R.id.total_price)
    TextView mTotalPrice;
    @BindView(R.id.do_account)
    View mDoAccount;

    private User mUser;
    private UserMine mUsermine;
    public long mQtyLimit;  //当前容量
    private int mSubtype;    //扩容0，加速1

    public HashMap<String, Double> mSumees = new HashMap<String, Double>();

    public HashMap<String, Integer> mGoodsNum = new HashMap<String, Integer>();

    public HashMap<String, Long> mVolumeSum = new HashMap<String, Long>();

    public HashMap<String, Double> mSpeedrateSum = new HashMap<String, Double>();

    @OnClick(R.id.left)
    public void back(View view) {
        finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_goods_list;
    }

    @Override
    public void initDatas() {
        mUser = App.getInstance().getUser();
        mUsermine = (UserMine) getIntent().getSerializableExtra(ARG_USERMINE);
        mSubtype = getIntent().getIntExtra(ARG_SUBTYPE, 0);
        mQtyLimit = mUsermine.getMaxQtyLimit();
        if (mUser==null || mUsermine == null || mUsermine.getMineType()==null) finish();
    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        if (mSubtype == Goods.SUBTYPE_CAPACITY) {
            mTitle.setText(getString(R.string.larger_volume_title));
        } else {
            mTitle.setText(getString(R.string.larger_speedrate_title));
        }
        mNoData.setVisibility(View.GONE);
        double fund = App.getInstance().getLedger().getFund();
        mRemainEES.setText(String.format(getString(R.string.charge_number_limit_hint), Utils.doubleFormat(fund)+""));
        mTotalPrice.setText(String.format(getString(R.string.sum_ees), "0.0"));
        if (mUser != null) {
            String mtype = "1";
            if (mUsermine.getMineType()!=null && mUsermine.getMineType().getType()> 0) {
                mtype = mUsermine.getMineType().getType() + "";
            }
            mPresenter.getGoodslist(mUser.getPid(), mtype, mSubtype, page, PullRecycler.ACTION_PULL_TO_REFRESH);
        }
    }

    @OnClick(R.id.do_account)
    public void onClick(View view) {
        int allgoodsNum = 0;
        for (String key: mGoodsNum.keySet()) {
            allgoodsNum += mGoodsNum.get(key);
        }
        if (allgoodsNum==0) {
            Snackbar.make(recycler, getString(R.string.err_buy_num_zero), Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (App.getInstance().getCurrentGoods() == null) return;
        JSONArray jsonArray = new JSONArray();
        if (mGoodsNum != null && mGoodsNum.size()>0) {
            for (String gkey: mGoodsNum.keySet()) {
                int num = mGoodsNum.get(gkey);
                if (num > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("goods_id", gkey);
                        jsonObject.put("qty", num);
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (mPresenter!=null && mUsermine!=null) mPresenter.bookOrder(mUser.getPid(), mUsermine.getId(), jsonArray.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveUpdatePriceEvent(UpdateGoodsPriceEvent event){
        double allsumees = 0.0;
        for (String key: mSumees.keySet()) {
            allsumees += mSumees.get(key);
        }
        if (mTotalPrice!= null) mTotalPrice.setText(String.format(getString(R.string.sum_ees), allsumees+""));
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void showError() {
        checkEmpty();
        if (recycler!=null) recycler.onRefreshCompleted();
    }

    @Override
    public void complete() {
        checkEmpty();
        if (recycler!=null) recycler.onRefreshCompleted();
    }

    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods, parent, false);
        if (mSubtype == Goods.SUBTYPE_CAPACITY) {
            return new GoodsRVListViewHolder(view, mUsermine, mDataList, mSubtype, this);
        } else {
            return new GoodsRVListViewHolder2(view, mUsermine, mDataList, mSubtype, this);
        }
    }

    @Override
    public void onRefresh(int action) {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        if (action == PullRecycler.ACTION_PULL_TO_REFRESH) {
            page = 1;
        }
        if (mUser != null) {
            String mtype = "1";
            if (mUsermine.getMineType()!=null && mUsermine.getMineType().getType()> 0) {
                mtype = mUsermine.getMineType().getType() + "";
            }
            mPresenter.getGoodslist(mUser.getPid(), mtype, mSubtype, page, action);
        }
    }

    public void checkEmpty() {
        if (mDataList==null || mDataList.size()==0) {
            if (mNoData != null) mNoData.setVisibility(View.VISIBLE);
        } else {
            if (mNoData != null) mNoData.setVisibility(View.GONE);
        }
    }

    public void showAssureDialog(final Context context, final String content, final List<GoodsOrder> orders) {
        if (StringUtils.isBlank(content)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (orders!=null && orders.size()>0) {
            for (GoodsOrder order: orders) {
                sb.append(order.getId()).append(",");
            }
        }
        final String ids = sb.toString();
        CommomDialog dialog = new CommomDialog(this, R.style.dialog, content, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    mPresenter.pay(mUser.getPid(), ids);
                    showPayLoadingDialog(getString(R.string.paying));
                }
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton(getString(R.string.pay));
        dialog.setTitle(getString(R.string.buy_tips)).show();
        dialog.setContentGravity(Gravity.LEFT);
    }

    public void showPaySuccessDialog() {
        final String content = getString(R.string.buy_success);
        final CommomDialog dialog = new CommomDialog(this, R.style.dialog, content, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    finish();
                }
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton(getString(R.string.ok));
        dialog.setTitle(getString(R.string.tips)).show();
        dialog.setNegativeButtonVisible(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) dialog.dismiss();
                finish();
            }
        }, 3000);
    }

    @Override
    public void getGoodslistSuccess(List<Goods> data, int action) {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }

        if (action == PullRecycler.ACTION_PULL_TO_REFRESH) {
            mDataList.clear();
        }
        if (data == null || data.size() == 0) {
            recycler.enableLoadMore(false);
        } else {
            App.getInstance().setCurrentGoods(data.get(0));
            recycler.enableLoadMore(true);
            mDataList.addAll(data);
            adapter.notifyDataSetChanged();
            page ++;
        }
        if (recycler!=null) recycler.onRefreshCompleted();
        if (action == PullRecycler.ACTION_PULL_TO_REFRESH) {
            checkEmpty();
        }
    }

    @Override
    public void getGoodslistFail(Error err, int action) {
        Snackbar.make(recycler, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        if (recycler!=null) recycler.onRefreshCompleted();
        checkEmpty();
        checkError(err);
    }

    @Override
    public void bookOrderSuccess(List<GoodsOrder> orders) {
        if (orders==null) return;
        StringBuilder sb = new StringBuilder();
        for (GoodsOrder order: orders) {
            sb.append(String.format(getString(R.string.assure_content2), order.getGoods().getName()+"", order.getQty()+"", Utils.doubleFormat(order.getEesAmount())+""));
        }
        String content = sb.toString();
        showAssureDialog(this, content, orders);
    }

    @Override
    public void bookOrderFail(Error err) {
        Snackbar.make(recycler, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        checkEmpty();
        checkError(err);
    }

    @Override
    public void paySuccess(Boolean result) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissLoadingDialog();
                showPaySuccessDialog();
            }
        }, 2000);
        double allsumees = 0.0;
        for (String key: mSumees.keySet()) {
            allsumees += mSumees.get(key);
        }
        double remainees = App.getInstance().getLedger().getFund() - allsumees;
        App.getInstance().getLedger().setFund(remainees);
        EventBus.getDefault().post(new UpdateVolumeEvent());
        LogUtils.d(getString(R.string.buy_success));
        mGoodsNum.clear();
        mSumees.clear();
    }

    @Override
    public void payFail(Error err) {
        Snackbar.make(recycler, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        if (recycler!=null) recycler.onRefreshCompleted();
        checkEmpty();
        checkError(err);
    }

}
