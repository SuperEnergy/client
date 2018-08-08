package com.ees.chain.ui.base;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;


import com.ees.chain.R;
import com.ees.chain.task.core.BaseContract;
import com.ees.chain.ui.view.support.recycler.BaseListAdapter;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.ees.chain.ui.view.support.recycler.PullRecycler;
import com.ees.chain.ui.view.support.recycler.layoutmanager.ILayoutManager;
import com.ees.chain.ui.view.support.recycler.layoutmanager.MyLinearLayoutManager;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by chairs on 2017/5/3.
 */

public abstract class BaseRVFragment<T1 extends BaseContract.BasePresenter,T2> extends BaseFragment <T1> implements PullRecycler.OnRecyclerRefreshListener{

    protected BaseListAdapter adapter;
    protected ArrayList<T2> mDataList;
    @BindView(R.id.pullRecycler)
    protected PullRecycler recycler;

    protected int page = 1;//获取第几页数据

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setUpAdapter();
        super.onViewCreated(view, savedInstanceState);
        recycler.setOnRefreshListener(this);
        recycler.setLayoutManager(getLayoutManager());
        recycler.setAdapter(adapter);
    }

    protected void setUpAdapter() {
        adapter = new ListAdapter();
    }

    protected ILayoutManager getLayoutManager() {
        return new MyLinearLayoutManager(getContext());
    }

    protected boolean isSectionHeader(int position) {
        return false;
    }

    protected int getItemType(int position) {
        return 0;
    }

    protected abstract BaseViewHolder getViewHolder(ViewGroup parent, int viewType);



    public class ListAdapter extends BaseListAdapter {

        @Override
        protected BaseViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
            return getViewHolder(parent, viewType);
        }

        @Override
        protected int getDataCount() {
            return mDataList != null ? mDataList.size() : 0;
        }

        @Override
        protected int getDataViewType(int position) {
            return getItemType(position);
        }

        @Override
        public boolean isSectionHeader(int position) {
            return BaseRVFragment.this.isSectionHeader(position);
        }
    }


}
