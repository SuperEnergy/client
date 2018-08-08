package com.ees.chain.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.domain.Fund;
import com.ees.chain.domain.User;
import com.ees.chain.task.core.BaseContract;
import com.ees.chain.task.group.BonusPresenter;
import com.ees.chain.ui.base.BaseRVFragment;
import com.ees.chain.ui.interfc.BonusContract;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.ees.chain.ui.view.support.recycler.PullRecycler;
import com.ees.chain.ui.viewholder.ChargeRVListViewHolder;
import com.ees.chain.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by KESION on 2017/12/5.
 */
public class BonusFragment extends BaseRVFragment<BonusPresenter, Fund> implements BonusContract.View {

    public int bonus_type = BonusPresenter.TYPE_ALL_BONUS;

    private User mUser;

    @BindView(R.id.no_data)
    View mNoData;

    public BonusFragment() {
        // Required empty public constructor
    }

    public static final BonusFragment newInstance(int type)
    {
        BonusFragment fragment = new BonusFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type",type);
        fragment.setArguments(bundle);
        return fragment ;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bonus_type = getArguments().getInt("type");
        super.onCreate(savedInstanceState);
        LogUtils.d("BonusFragment onCreate bonus type " + bonus_type);
    }


    @Override
    public int getLayoutResId() {
        return R.layout.fragment_my_msg;
    }

    @Override
    public void initDatas() {
        mUser = App.getInstance().getUser();
        if (mUser != null) {
            mPresenter.getBonusList(mUser.getPid(), bonus_type, page, PullRecycler.ACTION_PULL_TO_REFRESH);
            LogUtils.d("get bonus list page " + page);
        }
    }

    @Override
    public void configViews() {

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_charge, parent, false);
        return new ChargeRVListViewHolder(view, mDataList, bonus_type, getActivity());
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
            mPresenter.getBonusList(mUser.getPid(), bonus_type, page, action);
            LogUtils.d("get bonus list page " + page);
        }
    }

    @Override
    public void showBonusSuccess(List<Fund> data, int action) {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }

        if (action == PullRecycler.ACTION_PULL_TO_REFRESH) {
            mDataList.clear();
        }
        if (data == null || data.size() == 0) {
            recycler.enableLoadMore(false);
        } else {
            if (data.size() < BaseContract.LENGTH) {
                recycler.enableLoadMore(false);
            } else {
                recycler.enableLoadMore(true);
            }
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
    public void showBonusFail(com.ees.chain.domain.Error err, int action) {
        LogUtils.d("showBonusFail error code " + err.getCode());
        Snackbar.make(recycler, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        if (recycler!=null) recycler.onRefreshCompleted();
        checkEmpty();
        checkError(err);
    }

    public void checkEmpty() {
        if (mDataList==null || mDataList.size()==0) {
            if (mNoData != null) mNoData.setVisibility(View.VISIBLE);
        } else {
            if (mNoData != null) mNoData.setVisibility(View.GONE);
        }
    }
}
