package com.ees.chain.ui.interfc;


import com.ees.chain.domain.Error;
import com.ees.chain.domain.User;
import com.ees.chain.task.core.BaseContract;

/**
 * Created by KESION on 2017/12/4.
 */

public interface LoginContract {
    interface View extends BaseContract.BaseView {
        public  void loginSuccess(User user);
        public  void loginFail(Error err);
        public void sendCodeFail(Error err);
    }

    interface Presenter extends BaseContract.BasePresenter<LoginContract.View>  {
        public void login(String pid,String password);
        public void quickLogin(String pid, String code);
        public void sendCode(String pid);
    }
}
