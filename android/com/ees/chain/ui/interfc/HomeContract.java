package com.ees.chain.ui.interfc;


import com.ees.chain.domain.CoinMarket;
import com.ees.chain.domain.Error;
import com.ees.chain.domain.Fund;
import com.ees.chain.domain.Ledger;
import com.ees.chain.domain.Mining;
import com.ees.chain.domain.Notice;
import com.ees.chain.domain.UserMine;
import com.ees.chain.task.core.BaseContract;

import java.util.List;

/**
 * Created by KESION on 2017/12/22.
 */

public interface HomeContract {
    interface View extends BaseContract.BaseView {
        public void startMiningSucess();
        public void startMiningFail(Error err);
        public void submitMiningSucess();
        public void submitMiningFail(Error err);
        public void showSevenFoudsSucess(List<Fund> data, long version);
        public void showSevenFoudsFail(Error err, long version);
        public void showSevenMiningsSucess(List<Mining> data, long version);
        public void showSevenMiningsFail(Error err, long version);
        public void showNewestMsgSucess(List<Notice> data);
        public void showNewestMsgFail(Error err);
        public void showMiningStatusSucess(Mining data);
        public void showMiningStatusFail(Error err);
        public void showUserMineListSucess(List<UserMine> data);
        public void showUserMineListFail(Error err);
        public void showCoinMarketSuccess(CoinMarket market);
        public void showCoinMarketFail(Error err);
    }

    interface Presenter extends BaseContract.BasePresenter<HomeContract.View> {
        public void startMining(String pid, String usermineId);
        public void submitMining(String pid, long num, String usermineId);
        public void getSevenFouds(String pid, int type, String usermineId, long version);//7天挖矿收益
        public void getSevenMinings(String pid, String usermineId, long version);//7天挖矿量
        public void getMiningStatus(String pid, String usermineId);//查询挖矿状态接口
        public void getNewestMsg(String pid);
        public void getUserMineList(String pid);//用户矿机列表
        public void getCoinMarket(String pid);
    }
}
