package com.ees.chain.ui.base;

import android.view.ViewGroup;

import com.ees.chain.task.core.BaseContract;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;


/**
 * Created by chairs on 2017/5/4.
 */

public abstract class BaseRVHActivity<T1 extends BaseContract.BasePresenter,T2>  extends BaseRVActivity<T1,T2> {
    protected static final int VIEW_TYPE_SECTION_HEADER = 1;
    protected static final int VIEW_TYPE_SECTION_CONTENT = 2;

    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SECTION_HEADER) {
            return onCreateHeaderViewHolder(parent, viewType);
        }
        return onCreateViewHolder(parent, viewType);
    }

    @Override
    protected int getItemType(int position) {
        if (position == 0) {
            return  VIEW_TYPE_SECTION_HEADER;
        }
        return  VIEW_TYPE_SECTION_CONTENT;
    }

    protected abstract BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    protected abstract BaseViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType);

    @Override
    protected boolean isSectionHeader(int position) {
        if (position == 0) {
            return  true;
        }
        return false;
    }
}
