package com.ees.chain.ui.interfc;


import com.ees.chain.domain.Error;
import com.ees.chain.domain.Notice;
import com.ees.chain.task.core.BaseContract;

/**
 * Created by KESION on 2017/12/4.
 */

public interface WebContract {
    interface View extends BaseContract.BaseView {
        public void showNoticeDetailSuccess(Notice result);
        public void showNoticeDetailFail(Error err);
    }

    interface Presenter extends BaseContract.BasePresenter<WebContract.View> {
        public void getNoticeDetail(String pid, String id);
    }
}
