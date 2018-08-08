package com.ees.chain.ui.interfc;


import com.ees.chain.domain.Error;
import com.ees.chain.domain.User;
import com.ees.chain.task.core.BaseContract;

/**
 * Created by KESION on 2017/12/4.
 */

public interface RegisterContract {
    interface View extends BaseContract.BaseView {
        public void registerSuccess();
        public void registerFail(Error err);
        public void sendCodeFail(Error err);
    }

    interface Presenter extends BaseContract.BasePresenter<RegisterContract.View> {
        public void registe(String nickname, String pid, String password, String code, String ref, String from);
        public void sendCode(String pid);
    }
}
