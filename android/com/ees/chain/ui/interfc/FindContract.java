package com.ees.chain.ui.interfc;


import com.ees.chain.domain.Error;
import com.ees.chain.domain.Find;
import com.ees.chain.task.core.BaseContract;

import java.util.List;

/**
 * Created by KESION on 2017/12/4.
 */

public interface FindContract {
    interface View extends BaseContract.BaseView {
        public void getFindlistSuccess(List<Find> data);
        public void getFindlistFail(Error err);
    }

    interface Presenter extends BaseContract.BasePresenter<FindContract.View>  {
        public void getFindlist(String pid, long version);
    }
}
