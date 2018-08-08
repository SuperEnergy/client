package com.ees.chain.ui.interfc;


import com.ees.chain.domain.Error;
import com.ees.chain.domain.Find;
import com.ees.chain.domain.Ledger;
import com.ees.chain.task.core.BaseContract;

import java.util.List;

/**
 * Created by KESION on 2017/12/4.
 */

public interface PersonContract {
    interface View extends BaseContract.BaseView {
        public void showLedgerRefreshSucess(Ledger data, long version);
        public void showLedgerRefreshFail(Error err, long version);
        public void syncSuccess();
        public void synFail(Error err);
        public void getSettinglistSuccess(List<Find> data);
        public void getSettinglistFail(Error err);
    }

    interface Presenter extends BaseContract.BasePresenter<PersonContract.View>  {
        public void userSync(String pid, long version);
        public void ledgerRefresh(String pid, long version);//刷新总资产
        public void getSettinglist(String pid, long version);
    }
}
