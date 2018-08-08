package com.ees.chain.ui.interfc;


import com.ees.chain.domain.Error;
import com.ees.chain.task.core.BaseContract;

/**
 * Created by KESION on 2017/12/4.
 */

public interface VerifyContract {
    interface View extends BaseContract.BaseView {
        public  void verifySucces();
        public  void verifyFail(Error err);
        public  void getTokenSucces(String token);
        public  void getTokenFail(Error err);
        public  void uploadFileSuccess(int vid, String result);
        public  void uploadFileFail(int vid, String result);
    }

    interface Presenter extends BaseContract.BasePresenter<VerifyContract.View> {
        public void verify(String name, String cardno,String idcard1, String idcard2, String idcard3);
        public void getUploadToken();
        public void uploadFile(int vid, String path, String token, boolean compress);
    }
}
