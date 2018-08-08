package com.ees.chain.ui.interfc;


import com.ees.chain.domain.Error;
import com.ees.chain.task.core.BaseContract;

/**
 * Created by KESION on 2017/12/4.
 */

public interface ChargeContract {
    interface View extends BaseContract.BaseView {
        public void chargeSuccess();
        public void chargeFail(Error err);
        public void sendCodeFail(Error err);
    }

    interface Presenter extends BaseContract.BasePresenter<ChargeContract.View>  {
        public void charge(String pid, String cpid, String num, String code);
        public void sendCodeCharge(String pid);
    }
}
