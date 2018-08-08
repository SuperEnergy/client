package com.ees.chain.ui.interfc;


import com.ees.chain.domain.Error;
import com.ees.chain.domain.GoodsOrder;
import com.ees.chain.task.core.BaseContract;

import java.util.List;

/**
 * Created by KESION on 2017/12/4.
 */

public interface OrderContract {
    interface View extends BaseContract.BaseView {
        public void showOrderSuccess(List<GoodsOrder> data, int action);
        public void showOrderFail(Error err, int action);
    }

    /**
     * action 0:refresh, 1:load more
     * btype 0：推广收益；1：挖矿收益；2：锁仓收益
     *
     */
    interface Presenter extends BaseContract.BasePresenter<OrderContract.View> {
        public void getOrderList(String pid, int page, int action);
    }
}
