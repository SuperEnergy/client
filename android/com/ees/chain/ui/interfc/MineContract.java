package com.ees.chain.ui.interfc;


import com.ees.chain.ble.BLEDevice;
import com.ees.chain.domain.Error;
import com.ees.chain.domain.MineType;
import com.ees.chain.task.core.BaseContract;

import java.util.List;

/**
 * Created by KESION on 2017/12/4.
 */

public interface MineContract {
    interface View extends BaseContract.BaseView {
        public void showMineListSuccess(List<BLEDevice> data);
        public void showMineListFail(Error err);
        public void bindSuccess(Boolean result);
        public void bindFail(Error err);
        public void getMineTypeListSuccess(List<MineType> minetypes);
        public void getMineTypeListFail(Error err);
    }

    interface Presenter extends BaseContract.BasePresenter<MineContract.View> {
        public void getMineTypeList(String pid);
        public void bindHardware(String pid, String uuid, String mac, String name);
    }
}
