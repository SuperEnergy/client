package com.ees.chain.ui.fragment;


import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.domain.Error;
import com.ees.chain.domain.Find;
import com.ees.chain.domain.User;
import com.ees.chain.manager.CacheManager;
import com.ees.chain.task.group.FindPresenter;
import com.ees.chain.task.group.MsgPresenter;
import com.ees.chain.ui.base.BaseRVFragment;
import com.ees.chain.ui.interfc.FindContract;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.ees.chain.ui.view.support.recycler.PullRecycler;
import com.ees.chain.ui.viewholder.FindRVListViewHolder;
import com.ees.chain.ui.viewholder.MsgRVListViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;

/**
 * Created by KESION on 2018/3/9.
 */
public class FindFragment extends BaseRVFragment<FindPresenter, ArrayList<Find>> implements FindContract.View {

    @BindView(R.id.no_data)
    View mNoData;

    private User mUser;

    private Map<Integer, ArrayList<Find>> mHashFinds = new HashMap<Integer, ArrayList<Find>>();

    private final long TIMEOUT1 = 12 * 60 * 60 * 1000;

    private final long TIMEOUT2 = 10 * 1000;

    private boolean autoRefresh = false;

    public FindFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_listview;
    }

    @Override
    public void initDatas() {
        mUser = App.getInstance().getUser();
        mDataList = new ArrayList<>();
        List<Find> data = CacheManager.getInstance().getFindList();
        asortData(data);
        if (System.currentTimeMillis() - CacheManager.getInstance().getFindTime() > TIMEOUT1) {
            autoRefresh = true;
        }
        if (autoRefresh) {
            long version = App.getInstance().getFindVersion();
            mPresenter.getFindlist(mUser.getPid(), version);
        }
    }

    @Override
    public void configViews() {
        recycler.enableLoadMore(false);
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
        if (System.currentTimeMillis() - CacheManager.getInstance().getFindTime() < TIMEOUT2) {
            complete();
            return;
        }

        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        long version = App.getInstance().getFindVersion();
        mPresenter.getFindlist(mUser.getPid(), version);
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
    public void getFindlistSuccess(List<Find> data) {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        asortData(data);
//        mDataList.addAll(data);
        adapter.notifyDataSetChanged();
        if (recycler!=null) recycler.onRefreshCompleted();
        checkEmpty();
    }

    public void asortData(List<Find> data) {
        if (data == null || data.size() == 0) return;
        mDataList.clear();
        mHashFinds.clear();
        for (Find tmp : data) {
            int idx = tmp.getIndex();
            ArrayList<Find> finds = mHashFinds.get(idx);
            if (finds == null) {
                finds = new ArrayList<Find>();
            }
            finds.add(tmp);
            mHashFinds.put(idx, finds);
        }

        List<Map.Entry<Integer, ArrayList<Find>>> list = new ArrayList<Map.Entry<Integer, ArrayList<Find>>>(mHashFinds.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, ArrayList<Find>>>() {
            //升序排序
            public int compare(Map.Entry<Integer, ArrayList<Find>> o1,
                               Map.Entry<Integer, ArrayList<Find>> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        for (Map.Entry<Integer, ArrayList<Find>> mapping : list) {
            mDataList.add(mapping.getValue());
        }
    }

    @Override
    public void getFindlistFail(Error err) {
        checkEmpty();
        if (Error.CODE_SYNC_EQUAL.equals(err.getCode())) {
            getFindlistSuccess(CacheManager.getInstance().getFindList());
        } else {
            Snackbar.make(recycler, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        }
        if (recycler!=null) recycler.onRefreshCompleted();
        checkError(err);
    }

    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_find, parent, false);
        return new FindRVListViewHolder(view, mDataList, getActivity());
    }

    public void checkEmpty() {
        if (mDataList==null || mDataList.size()==0) {
            if (mNoData != null) mNoData.setVisibility(View.VISIBLE);
        } else {
            if (mNoData != null) mNoData.setVisibility(View.GONE);
        }
    }
}
