package com.ees.chain.ui.view.support.recycler.layoutmanager;

import android.support.v7.widget.RecyclerView;

import com.ees.chain.ui.view.support.recycler.BaseListAdapter;


public interface ILayoutManager {
    RecyclerView.LayoutManager getLayoutManager();
    int findLastVisiblePosition();
    void setUpAdapter(BaseListAdapter adapter);
}
