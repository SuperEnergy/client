package com.ees.chain.ui.interfc;


import com.ees.chain.domain.Config;
import com.ees.chain.domain.Error;
import com.ees.chain.domain.Ledger;
import com.ees.chain.domain.MineType;
import com.ees.chain.task.core.BaseContract;

import java.util.List;

/**
 * Created by KESION on 2017/12/4.
 */

public interface MainContract {
    interface View extends BaseContract.BaseView {
        public void showLedgerRefreshSucess(Ledger data, long version);
        public void showLedgerRefreshFail(Error err, long version);
        public void syncSuccess();
        public void synFail(Error err);
        public void showHuodong(Object obj);
        public void showConfigListSuccess(List<Config> configs);
        public void showConfigListFail(Error err);
    }

    interface Presenter extends BaseContract.BasePresenter<MainContract.View> {
        //同步用户数据信息
        public void userSync(String pid, long version);
        public void getHuodong(String pid);
        public void getConfigList(String pid);
        public void ledgerRefresh(String pid, long version);//刷新总资产
    }
}
