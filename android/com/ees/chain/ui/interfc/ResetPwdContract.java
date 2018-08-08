package com.ees.chain.ui.interfc;


import com.ees.chain.domain.Error;
import com.ees.chain.task.core.BaseContract;

/**
 * Created by KESION on 2017/12/4.
 */

public interface ResetPwdContract {
    interface View extends BaseContract.BaseView {
        public void resetSuccess();
        public void resetFail(Error err);
        public void sendCodeFail(Error err);
    }

    interface Presenter extends BaseContract.BasePresenter<ResetPwdContract.View> {
        public void resetPwd(String pid, String password, String authcode);
        public void sendCode(String pid);
    }
}
