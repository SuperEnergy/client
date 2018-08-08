package com.ees.chain.ui.activity;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.task.group.BonusPresenter;
import com.ees.chain.task.group.DefaultPresenter;
import com.ees.chain.ui.adapter.BonusTabAdapter;
import com.ees.chain.ui.base.BaseActivity;
import com.ees.chain.ui.base.BaseFragment;
import com.ees.chain.ui.fragment.BonusFragment;
import com.ees.chain.ui.interfc.DefaultContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by KESION on 2017/12/5.
 */
public class BonusActivity extends BaseActivity<DefaultPresenter> implements DefaultContract.View {

    @BindView(R.id.left)
    View mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.tab_title)
    TabLayout mTabTitle;                            //定义TabLayout
    @BindView(R.id.vp_pager)
    ViewPager mTabViewPager;                             //定义viewPager
    @BindArray(R.array.bonus)
    String [] msg;

    private FragmentPagerAdapter fAdapter;                               //定义adapter

    private List<Fragment> mListFragment;                                //定义要装fragment的列表
    private List<String> mListTitle;                                     //tab名称列表

    private BaseFragment mAllBonusFragment;
    private BaseFragment mMiningFragment;
//    private BaseFragment mLockFragment;


    @OnClick(R.id.left)
    public void back(View view) {
        finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_money;
    }

    @Override
    public void initDatas() {
    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        mTitle.setText(R.string.fund_list);
        //初始化各fragment
        mMiningFragment = BonusFragment.newInstance(BonusPresenter.TYPE_MINING_BONUS);
        mAllBonusFragment = BonusFragment.newInstance(BonusPresenter.TYPE_ALL_BONUS);
//        mLockFragment = BonusFragment.newInstance(BonusPresenter.TYPE_LOCK_BONUS);
        //将fragment装进列表中
        mListFragment = new ArrayList<>();
        mListFragment.add(mMiningFragment);
        mListFragment.add(mAllBonusFragment);
//        mListFragment.add(mLockFragment);
        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        mListTitle = new ArrayList<>();
        mListTitle.add(msg[1]);
        mListTitle.add(msg[0]);
//        mListTitle.add(msg[2]);
        //设置TabLayout的模式
        mTabTitle.setTabMode(TabLayout.MODE_FIXED);
        //为TabLayout添加tab名称
        mTabTitle.addTab(mTabTitle.newTab().setText(mListTitle.get(1)));
        mTabTitle.addTab(mTabTitle.newTab().setText(mListTitle.get(0)));
//        mTabTitle.addTab(mTabTitle.newTab().setText(mListTitle.get(2)));

        fAdapter = new BonusTabAdapter(getSupportFragmentManager(), mListFragment, mListTitle);
        //viewpager加载adapter
        mTabViewPager.setAdapter(fAdapter);
        //mTabTitle.setViewPager(mTabViewPager);
        //TabLayout加载viewpager
        mTabTitle.setupWithViewPager(mTabViewPager);
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
}
