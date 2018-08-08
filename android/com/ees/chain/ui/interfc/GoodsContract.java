package com.ees.chain.ui.interfc;


import com.ees.chain.domain.Error;
import com.ees.chain.domain.Goods;
import com.ees.chain.domain.GoodsOrder;
import com.ees.chain.domain.Notice;
import com.ees.chain.task.core.BaseContract;

import java.util.List;

/**
 * Created by KESION on 2017/12/4.
 */

public interface GoodsContract {
    interface View extends BaseContract.BaseView {
        public void getGoodslistSuccess(List<Goods> data, int action);
        public void getGoodslistFail(Error err, int action);
        public void bookOrderSuccess(List<GoodsOrder> orders);
        public void bookOrderFail(Error err);
        public void paySuccess(Boolean result);
        public void payFail(Error err);
    }

    interface Presenter extends BaseContract.BasePresenter<GoodsContract.View>  {
        public void getGoodslist(String pid, String mtype, int subtype, int page, int action);
        public void bookOrder(String pid, String mid, String orderjson);
        public void pay(String pid, String orderids);
    }
}
