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
import com.ees.chain.domain.Error;
import com.ees.chain.domain.GoodsOrder;
import com.ees.chain.domain.User;
import com.ees.chain.task.group.OrderPresenter;
import com.ees.chain.ui.base.BaseRVActivity;
import com.ees.chain.ui.interfc.OrderContract;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.ees.chain.ui.view.support.recycler.PullRecycler;
import com.ees.chain.ui.viewholder.OrderRVListViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by KESION on 2017/12/5.
 */
public class OrderListActivity extends BaseRVActivity<OrderPresenter, GoodsOrder> implements OrderContract.View {

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
        if (mUser != null) {
            mPresenter.getOrderList(mUser.getPid(), page, PullRecycler.ACTION_PULL_TO_REFRESH);
        }
    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        mTitle.setText(R.string.order_list);
        mNoData.setVisibility(View.GONE);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderRVListViewHolder(view, mDataList, this);
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
            mPresenter.getOrderList(mUser.getPid(), page, action);
        }
    }

    public void checkEmpty() {
        if (mDataList==null || mDataList.size()==0) {
            if (mNoData != null) mNoData.setVisibility(View.VISIBLE);
        } else {
            if (mNoData != null) mNoData.setVisibility(View.GONE);
        }
    }

    @Override
    public void showOrderSuccess(List<GoodsOrder> data, int action) {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }

        if (action == PullRecycler.ACTION_PULL_TO_REFRESH) {
            mDataList.clear();
        }
        if (data == null || data.size() == 0) {
            recycler.enableLoadMore(false);
        } else {
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
    public void showOrderFail(Error err, int action) {
        Snackbar.make(recycler, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        if (recycler!=null) recycler.onRefreshCompleted();
        checkEmpty();
        checkError(err);
    }
}
