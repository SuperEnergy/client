package com.ees.chain.ui.interfc;


import com.ees.chain.domain.Error;
import com.ees.chain.domain.User;
import com.ees.chain.task.core.BaseContract;

/**
 * Created by KESION on 2017/12/4.
 */

public interface EditPersonContract {
    interface View extends BaseContract.BaseView {
        public void modifySucces(User user);
        public void modifyFail(Error err);
        public void getTokenSucces(String token);
        public void getTokenFail(Error err);
        public void uploadFileSuccess(String result);
        public void uploadFileFail(String result);
        public void logoutSuccess();
        public void logoutFail(Error err);
    }

    interface Presenter extends BaseContract.BasePresenter<EditPersonContract.View> {
        public void modify(String pid, String imgpath);
        public void getUploadToken();
        public void uploadFile(String path, String token, boolean compress);
        public void logout(String pid);
    }
}
