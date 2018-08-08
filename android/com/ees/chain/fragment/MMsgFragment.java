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
import com.ees.chain.domain.Error;
import com.ees.chain.domain.Notice;
import com.ees.chain.domain.User;
import com.ees.chain.event.UpdateChargePlugginEvent;
import com.ees.chain.event.UpdateNewstNoticeEvent;
import com.ees.chain.manager.CacheManager;
import com.ees.chain.task.core.BaseContract;
import com.ees.chain.task.group.MsgPresenter;
import com.ees.chain.ui.base.BaseRVFragment;
import com.ees.chain.ui.interfc.MsgContract;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.ees.chain.ui.view.support.recycler.PullRecycler;
import com.ees.chain.ui.viewholder.MsgRVListViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by KESION on 2017/12/5.
 */
public class MMsgFragment extends BaseRVFragment<MsgPresenter, Notice> implements MsgContract.View {

    @BindView(R.id.no_data)
    View mNoData;

    public int msg_type = MsgPresenter.TYPE_MY_MSG;

    private User mUser;

    private boolean autoRefresh = false;

    private final long TIMEOUT1 = 2 * 60 * 60 * 1000;

    private final long TIMEOUT2 = 10 * 1000;

    public MMsgFragment() {
        // Required empty public constructor
    }

    public static final MMsgFragment newInstance(int type)
    {
        MMsgFragment fragment = new MMsgFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment ;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msg_type = getArguments().getInt("type");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_my_msg;
    }

    @Override
    public void initDatas() {

        mUser = App.getInstance().getUser();
        if (mUser != null) {
            long version = 0;
            if (msg_type == MsgPresenter.TYPE_MY_MSG) {
                version = App.getInstance().getMyMsgVersion();
                mDataList = (ArrayList<Notice>) CacheManager.getInstance().getMyMsgList();
                if (System.currentTimeMillis() - CacheManager.getInstance().getMyMsgTime() > TIMEOUT1) {
                    autoRefresh = true;
                }
            } else {
                version = App.getInstance().getSysMsgVersion();
                mDataList = (ArrayList<Notice>) CacheManager.getInstance().getSysMsgList();
                if (System.currentTimeMillis() - CacheManager.getInstance().getSysMsgTime() > TIMEOUT1) {
                    autoRefresh = true;
                }
            }
            if (autoRefresh) {
                mDataList = new ArrayList<>();
                mPresenter.getMsgList(mUser.getPid(), msg_type, page, PullRecycler.ACTION_PULL_TO_REFRESH, version);
            }
        }
    }

    @Override
    public void configViews() {
        recycler.enableLoadMore(true);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sysmsg_list, parent, false);
        return new MsgRVListViewHolder(view, mDataList, msg_type, getActivity());
    }

    @Override
    public void onRefresh(int action) {
        if (msg_type == MsgPresenter.TYPE_MY_MSG) {
            if (System.currentTimeMillis() - CacheManager.getInstance().getMyMsgTime() < TIMEOUT2) {
                complete();
                return;
            }
        } else {
            if (System.currentTimeMillis() - CacheManager.getInstance().getSysMsgTime() < TIMEOUT2) {
                complete();
                return;
            }
        }

        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        if (action == PullRecycler.ACTION_PULL_TO_REFRESH) {
            page = 1;
        }
        if (mUser != null) {
            long version = 0;
            if (msg_type == MsgPresenter.TYPE_MY_MSG) {
                version = App.getInstance().getMyMsgVersion();
            } else {
                version = App.getInstance().getSysMsgVersion();
            }
            mPresenter.getMsgList(mUser.getPid(), msg_type, page, action, version);
        }
    }

    @Override
    public void showMsgSuccess(List<Notice> data, int action, long version) {
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
//        if (action == PullRecycler.ACTION_PULL_TO_REFRESH) {
//            if (mDataList != null && mDataList.size()>1) {
//                Notice notice = mDataList.get(0);
//                Notice cnotice = App.getInstance().mCurrentNewestMsg;
//                if (cnotice != null) {
//                    if (cnotice.getCreateTime() < notice.getCreateTime()) {
//                        App.getInstance().mCurrentNewestMsg = notice;
//                        sendNewestNoticeBroadcast();
//                    }
//                } else {
//                    App.getInstance().mCurrentNewestMsg = notice;
//                }
//            }
//        }
        if (recycler!=null) recycler.onRefreshCompleted();
        if (action == PullRecycler.ACTION_PULL_TO_REFRESH) {
            checkEmpty();
        }
    }

    public void sendNewestNoticeBroadcast() {
        EventBus.getDefault().post(new UpdateNewstNoticeEvent());
    }

    @Override
    public void showMsgFail(com.ees.chain.domain.Error err, int action, long version) {
        checkEmpty();
        if (Error.CODE_SYNC_EQUAL.equals(err.getCode())) {
            if (action == PullRecycler.ACTION_PULL_TO_REFRESH) {
                if (msg_type == MsgPresenter.TYPE_MY_MSG) {
                    showMsgSuccess(CacheManager.getInstance().getMyMsgList(), action, version);
                } else {
                    showMsgSuccess(CacheManager.getInstance().getSysMsgList(), action, version);
                }
            }
        } else {
            Snackbar.make(recycler, err.getMessage(), Snackbar.LENGTH_SHORT).show();
        }
        if (recycler!=null) recycler.onRefreshCompleted();
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
