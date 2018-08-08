package com.ees.chain.ui.interfc;


import com.ees.chain.domain.CoinMarket;
import com.ees.chain.domain.Error;
import com.ees.chain.task.core.BaseContract;

import java.util.List;

/**
 * Created by KESION on 2017/12/4.
 */

public interface CoinMarketContract {
    interface View extends BaseContract.BaseView {
        public void showCoinMarketListSuccess(List<CoinMarket> data);
        public void showCoinMarketListFail(Error err);
    }

    interface Presenter extends BaseContract.BasePresenter<CoinMarketContract.View> {
        public void getCoinMarketList(String pid);
    }
}
