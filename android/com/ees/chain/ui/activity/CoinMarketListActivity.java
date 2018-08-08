package com.ees.chain.ui.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.domain.CoinMarket;
import com.ees.chain.domain.Error;
import com.ees.chain.domain.User;
import com.ees.chain.task.group.CoinMarketPresenter;
import com.ees.chain.ui.base.BaseRVActivity;
import com.ees.chain.ui.interfc.CoinMarketContract;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.ees.chain.ui.viewholder.CoinMarketRVListViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CoinMarketListActivity extends BaseRVActivity<CoinMarketPresenter, CoinMarket> implements CoinMarketContract.View {

    @BindView(R.id.no_data)
    View mNoData;
    @BindView(R.id.left)
    View mBack;
    @BindView(R.id.title)
    TextView mTitle;

    private User mUser;

    @OnClick(R.id.left)
    public void back(View view) {
        finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_charge_list;
    }

    @Override
    public void initDatas() {
        mUser = App.getInstance().getUser();
        mPresenter.getCoinMarketList(mUser.getPid());
    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        mTitle.setText(R.string.market_list);
        mNoData.setVisibility(View.GONE);
    }

    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_market, parent, false);
        return new CoinMarketRVListViewHolder(view, mDataList, this);
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
        mPresenter.getCoinMarketList(mUser.getPid());
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
    public void showCoinMarketListSuccess(List<CoinMarket> data) {
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
        }
        if (recycler!=null) recycler.onRefreshCompleted();
        checkEmpty();
    }

    @Override
    public void showCoinMarketListFail(Error err) {
        Snackbar.make(recycler, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        if (recycler!=null) recycler.onRefreshCompleted();
        checkEmpty();
    }

    public void checkEmpty() {
        if (mDataList==null || mDataList.size()==0) {
            if (mNoData != null) mNoData.setVisibility(View.VISIBLE);
        } else {
            if (mNoData != null) mNoData.setVisibility(View.GONE);
        }
    }
}
