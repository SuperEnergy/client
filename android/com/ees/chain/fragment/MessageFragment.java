package com.ees.chain.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.event.ClickNewMsgEvent;
import com.ees.chain.event.UpdateChargeUnplugginEvent;
import com.ees.chain.task.group.DefaultPresenter;
import com.ees.chain.task.group.MsgPresenter;
import com.ees.chain.ui.adapter.MsgTabAdapter;
import com.ees.chain.ui.base.BaseFragment;
import com.ees.chain.ui.interfc.DefaultContract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;

/**
 * Created by KESION on 2017/12/5.
 */
public class MessageFragment extends BaseFragment<DefaultPresenter> implements DefaultContract.View {

    @BindView(R.id.tab_title)
    TabLayout mTabTitle;                            //定义TabLayout
    @BindView(R.id.vp_pager)
    ViewPager mTabViewPager;                             //定义viewPager
    @BindArray(R.array.msg)
    String [] msg;

    private FragmentPagerAdapter fAdapter;                               //定义adapter

    private List<Fragment> mListFragment;                                //定义要装fragment的列表
    private List<String> mListTitle;                                     //tab名称列表

    private BaseFragment mMyMsgFragment;              //我的消息fragment
    private BaseFragment mSysMsgFragment;            //系统消息fragment


    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_message;
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        //mTabTitle = (TabLayout) getParentView().findViewById(R.id.tab_title);
        //mTabViewPager = (ViewPager) getParentView().findViewById(R.id.vp_pager);

        //初始化各fragment
        if (mMyMsgFragment == null) mMyMsgFragment = MMsgFragment.newInstance(MsgPresenter.TYPE_MY_MSG);
        if (mSysMsgFragment == null) mSysMsgFragment = MMsgFragment.newInstance(MsgPresenter.TYPE_SYS_MSG);
        //将fragment装进列表中
        mListFragment = new ArrayList<>();
        mListFragment.add(mMyMsgFragment);
        mListFragment.add(mSysMsgFragment);
        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        mListTitle = new ArrayList<>();
        mListTitle.add(msg[0]);
        mListTitle.add(msg[1]);
        //设置TabLayout的模式
        mTabTitle.setTabMode(TabLayout.MODE_FIXED);
        //为TabLayout添加tab名称
        mTabTitle.addTab(mTabTitle.newTab().setText(mListTitle.get(0)));
        mTabTitle.addTab(mTabTitle.newTab().setText(mListTitle.get(1)));

        fAdapter = new MsgTabAdapter(getActivity().getSupportFragmentManager(),mListFragment,mListTitle);
        //viewpager加载adapter
        mTabViewPager.setAdapter(fAdapter);
        //mTabTitle.setViewPager(mTabViewPager);
        //TabLayout加载viewpager
        mTabTitle.setupWithViewPager(mTabViewPager);

        changeTab();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clickNewMsgEvent(ClickNewMsgEvent event){
        changeTab();
    }

    public void changeTab() {
        if (App.getInstance().mNewestMsg != null) {
            //系統公告，滑动到第2个TAB
            if (App.getInstance().mNewestMsg.getType()==1) {
                if (mTabViewPager!=null) {
                    if (mTabViewPager.getCurrentItem() != 0) {
                        mTabViewPager.setCurrentItem(0, true);
                    }
                }
            } else {
                if (fAdapter!=null && fAdapter.getCount() > 1 && mTabViewPager!=null) {
                    if (mTabViewPager.getCurrentItem() != 1) {
                        mTabViewPager.setCurrentItem(1, true);
                    }
                }
            }
            App.getInstance().mNewestMsg = null;
        }
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

    }

    @Override
    public void complete() {

    }

    @Override
    public void show() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
