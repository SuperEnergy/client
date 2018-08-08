package com.ees.chain.ui.viewholder;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.domain.MineType;
import com.ees.chain.domain.User;
import com.ees.chain.domain.UserMine;
import com.ees.chain.event.RenameDeviceEvent;
import com.ees.chain.event.RenameDeviceEvent2;
import com.ees.chain.event.UnbindDeviceEvent;
import com.ees.chain.event.UpdateUserMineChangeEvent;
import com.ees.chain.event.UpdateUserMineChangeEvent2;
import com.ees.chain.ui.activity.UserMineListActivity;
import com.ees.chain.ui.view.support.ClearableEditText;
import com.ees.chain.ui.view.support.CommomDialog;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.ees.chain.utils.BLEAction;
import com.ees.chain.utils.LogUtils;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kesion on 2017/12/5.
 */

public class UserMineRVListViewHolder extends BaseViewHolder {
    Context mContext;
    User mUser;
    List<UserMine> mUsermines;
    List<String> mMacs;

    @BindView(R.id.cmax_volume)
    TextView mCVolume;
    @BindView(R.id.max_volume)
    TextView mMaxVolume;
    @BindView(R.id.cover)
    SimpleDraweeView mCover;
    @BindView(R.id.item_member_checked)
    SimpleDraweeView mChecked;
    @BindView(R.id.unbind)
    View mUnbind;
    @BindView(R.id.edit_name)
    EditText mEditName;
    @BindView(R.id.online)
    View mOnline;
    @BindView(R.id.loading)
    SimpleDraweeView mLoading;

    private String mOldName = null;
    private boolean isOnline = false;

    public UserMineRVListViewHolder(View itemView, List<UserMine> usermines,  List<String> macs, Context context) {
        super(itemView);
        mContext = context;
        mUser = App.getInstance().getUser();
        mUsermines = usermines;
        mMacs = macs;
    }

    @Override
    public void onBindViewHolder(final int position) {
        final UserMine usermine = mUsermines.get(position);

        if (!StringUtils.isBlank(usermine.getMineType().getCover())) {
            mCover.setImageURI(usermine.getMineType().getCover());
        }
        Uri uri = Uri.parse("res://" + mContext.getPackageName()+"/" + R.drawable.dialog_loading1);
        DraweeController mDraweeController = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                //设置uri,加载本地的gif资源
                .setUri(uri)
                .build();
        mLoading.setController(mDraweeController);
        mLoading.setVisibility(View.GONE);

        mCVolume.setText(String.format(mContext.getString(R.string.cmax_volum2), usermine.getMaxQtyLimit()));
        mMaxVolume.setText(String.format(mContext.getString(R.string.max_volum2), usermine.getMaxQty()));
        mChecked.setVisibility(View.INVISIBLE);
        mUnbind.setVisibility(View.VISIBLE);
        mEditName.setText(usermine.getName());
        mEditName.setSelection(usermine.getName().length());

        if (usermine.getMac() != null && mMacs.contains(usermine.getMac())) {
            mOnline.setVisibility(View.VISIBLE);
            isOnline = true;
        } else {
            mOnline.setVisibility(View.INVISIBLE);
            isOnline = false;
        }

        if (usermine.checked) {
            mChecked.setVisibility(View.VISIBLE);
            mUnbind.setVisibility(View.VISIBLE);
            mOnline.setVisibility(View.VISIBLE);
            isOnline = true;
//            onItemClick(mName, position);
        } else {
            mChecked.setVisibility(View.INVISIBLE);
//            mUnbind.setVisibility(View.GONE);
        }

        if (usermine.getMineType().getType() == MineType.TYPE_APP) {
            mUnbind.setVisibility(View.GONE);
            mEditName.setEnabled(false);
            mEditName.clearFocus();
            isOnline = true;
        }

        mUnbind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDialog(mContext, usermine);
            }
        });

        mOldName = mEditName.getText().toString();
        mEditName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && position !=0) {
                    String name = mEditName.getText().toString();
                    if (!StringUtils.isBlank(name)) {
                        if (!name.equals(mOldName)) {
                            usermine.setName(name);
                            ((UserMineListActivity)mContext).mSelectUserMine = usermine;
                            EventBus.getDefault().post(new RenameDeviceEvent());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(View view, final int position) {
        if (position<0 || position >= mUsermines.size()) {
            LogUtils.d("position err " + position);
            return;
        }
        if (isOnline) {
            mChecked.setVisibility(View.INVISIBLE);
            mLoading.setVisibility(View.VISIBLE);
//            BLEAction.getInstance(App.getInstance().getBle(), mContext).disconnect(App.getInstance().getCurrentDevice());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((UserMineListActivity)mContext).mSelectUserMine = mUsermines.get(position);
                    EventBus.getDefault().post(new UpdateUserMineChangeEvent2());
                }
            }, 1000);
        } else {
            Snackbar.make(mUnbind, mContext.getString(R.string.not_online), Snackbar.LENGTH_SHORT).show();
        }
    }

    public void showDialog(final Context context, final UserMine usermine) {
        String content = String.format(context.getString(R.string.dialog_content_unbind_device), App.mUnbindFee+"");
        CommomDialog dialog = new CommomDialog(context, R.style.dialog, content, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    double fund = App.getInstance().getLedger().getFund();
                    double fee = App.mUnbindFee;
                    if (fund - fee < 0) {
                        Snackbar.make(mEditName, mContext.getString(R.string.err_money_notenought), Snackbar.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        App.getInstance().setUnbindDevice(usermine);
                        EventBus.getDefault().post(new UnbindDeviceEvent());
                    }
                }
                dialog.dismiss();
            }
        });
        dialog.setTitle(context.getString(R.string.tips)).show();
    }
}
