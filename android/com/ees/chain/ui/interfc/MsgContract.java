package com.ees.chain.ui.interfc;


import com.ees.chain.domain.Error;
import com.ees.chain.domain.Notice;
import com.ees.chain.task.core.BaseContract;

import java.util.List;

/**
 * Created by KESION on 2017/12/4.
 */

public interface MsgContract {
    interface View extends BaseContract.BaseView {
        public void showMsgSuccess(List<Notice> data, int action, long version);
        public void showMsgFail(Error err, int action, long version);
    }

    /**
     * action 0:refresh, 1:load more
     * type 1：我的消息；2：系统消息
     *
     */
    interface Presenter extends BaseContract.BasePresenter<MsgContract.View> {
        public void getMsgList(String pid, int type, int page, int action, long version);
    }
}
