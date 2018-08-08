package com.ees.chain.ui.interfc;


import com.ees.chain.domain.Error;
import com.ees.chain.domain.Fund;
import com.ees.chain.task.core.BaseContract;

import java.util.List;

/**
 * Created by KESION on 2017/12/4.
 */

public interface BonusContract {
    interface View extends BaseContract.BaseView {
        public void showBonusSuccess(List<Fund> data, int action);
        public void showBonusFail(Error err, int action);
    }

    /**
     * action 0:refresh, 1:load more
     * btype 0：推广收益；1：挖矿收益；2：锁仓收益
     *
     */
    interface Presenter extends BaseContract.BasePresenter<BonusContract.View> {
        public void getBonusList(String pid, int type, int page, int action);
    }
}
