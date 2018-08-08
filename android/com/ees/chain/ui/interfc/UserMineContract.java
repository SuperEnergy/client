package com.ees.chain.ui.interfc;


import com.ees.chain.domain.Error;
import com.ees.chain.domain.UserMine;
import com.ees.chain.task.core.BaseContract;

import java.util.List;

/**
 * Created by KESION on 2017/12/4.
 */

public interface UserMineContract {
    interface View extends BaseContract.BaseView {
        public void showUserMineListSuccess(List<UserMine> data);
        public void showUserMineListFail(Error err);
        public void renameSuccess(Boolean result);
        public void renameFail(Error err);
        public void unbindSuccess(Boolean result);
        public void unbindFail(Error err);
    }

    interface Presenter extends BaseContract.BasePresenter<UserMineContract.View> {
        public void getUserMineList(String pid, int mtype);
        public void renameHardware(String pid, String usermine_id, String name);
        public void unbindHardware(String pid, String usermine_id);
    }
}
