package com.ees.chain.ui.interfc;


import com.ees.chain.task.core.BaseContract;

/**
 * Created by KESION on 2017/12/4.
 */

public interface DefaultContract {
    interface View extends BaseContract.BaseView {
        public void show();
    }

    interface Presenter extends BaseContract.BasePresenter<DefaultContract.View> {
        public void get(String pid);
    }
}
