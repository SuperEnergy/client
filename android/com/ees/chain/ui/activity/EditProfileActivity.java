package com.ees.chain.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.component.AppComponent;
import com.ees.chain.component.DaggerMainComponent;
import com.ees.chain.domain.Error;
import com.ees.chain.domain.User;
import com.ees.chain.event.UpdateUserInfoEvent;
import com.ees.chain.manager.CacheManager;
import com.ees.chain.task.group.EditPersonPresenter;
import com.ees.chain.ui.base.BaseActivity;
import com.ees.chain.ui.interfc.EditPersonContract;
import com.ees.chain.ui.view.support.CommomDialog;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

public class EditProfileActivity extends BaseActivity<EditPersonPresenter> implements EditPersonContract.View {

    @BindView(R.id.left)
    View mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.nickname)
    TextView mNickName;
    @BindView(R.id.cover)
    SimpleDraweeView mCover;
    @BindView(R.id.phone)
    TextView mPhone;
    @BindView(R.id.password_view)
    View mChangePassword;
    @BindView(R.id.verify_view)
    View mVerifyView;
    @BindView(R.id.verify_state)
    TextView mVerifyState;
    @BindView(R.id.logout)
    View mLogout;

    User mUser;
    String mToken = null;

    @OnClick(R.id.left)
    public void back(View view) {
        finish();
    }

    @OnClick({R.id.password_view, R.id.logout, R.id.verify_view, R.id.cover})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.password_view:
                Intent intent = new Intent();
                intent.putExtra("type", 1);
                if (mUser != null) {
                    intent.putExtra("pid", mUser.getPid());
                }
                intent.setClass(this, ResetPwdActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.verify_view:
                Intent intent2 = new Intent();
                intent2.setClass(this, VerifyOneActivity.class);
                startActivity(intent2);
                break;
            case R.id.logout:
                showLogoutDialog();
                break;
            case R.id.cover:
                showPopueWindow();
                break;
        }
    }

    public void showLogoutDialog() {
        new CommomDialog(this, R.style.dialog, getString(R.string.logout_tips), new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    showLoadingDialog();
                    mPresenter.logout(mUser.getPid());
                }
                dialog.dismiss();
            }
        }).setTitle(getString(R.string.tips)).show();
    }

    @Override
    public void showError() {
        dismissLoadingDialog();
    }

    @Override
    public void complete() {
        dismissLoadingDialog();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    public void initDatas() {
        mPresenter.getUploadToken();
    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        mTitle.setText(getString(R.string.edit_profile2));
        mUser = App.getInstance().getUser();
        if (mUser != null) {
            mNickName.setText(mUser.getName());
            mPhone.setText(mUser.getPid());
            if (mUser.getRna_Status() == 1) {
                mVerifyState.setText(R.string.has_name_verify);
                mVerifyView.setEnabled(false);
            } else if (mUser.getRna_Status() == 2) {
                mVerifyState.setText(R.string.verifing2);
                mVerifyView.setEnabled(false);
            } else if (mUser.getRna_Status() == 3) {
                mVerifyState.setText(R.string.verifing2);
                mVerifyView.setEnabled(false);
            } else {
                mVerifyState.setText(R.string.not_name_verify);
                mVerifyView.setEnabled(true);
            }
            if (!StringUtils.isBlank(mUser.getAvatar())) {
                mCover.setImageURI(mUser.getAvatar());
            }
        } else {
            mNickName.setText("");
            mPhone.setText("");
            mVerifyState.setText("");
        }
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    private Uri imgUri;

    private String IMAGE_FILE_NAME = "AVTAR.jpg";

    private void showPopueWindow(){
        View popView = View.inflate(this, R.layout.popupwindow_camera,null);
        Button bt_album = (Button) popView.findViewById(R.id.btn_pop_album);
        Button bt_camera = (Button) popView.findViewById(R.id.btn_pop_camera);
        Button bt_cancle = (Button) popView.findViewById(R.id.btn_pop_cancel);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels*1/3;

        final PopupWindow popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setAnimationStyle(R.style.anim_popupwindow);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);

        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 方式1，直接打开图库，只能选择图库的图片
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_FROM_FILE);
                popupWindow.dismiss();

            }
        });
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //下面这句指定调用相机拍照后的照片存储的路径
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                startActivityForResult(takeIntent, PICK_FROM_CAMERA);
                popupWindow.dismiss();

            }
        });
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,50);

    }

    private static final int PICK_FROM_CAMERA = 2;
    private static final int CROP_FROM_CAMERA = 3;
    private static final int PICK_FROM_FILE = 4;

    /**
     * 拍照上传
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1) {
            setResult(1);
            finish();
            return;
        }

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PICK_FROM_CAMERA:
                File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                startPhotoZoom(Uri.fromFile(temp));
                break;
            case PICK_FROM_FILE:
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case CROP_FROM_CAMERA:
                if (null != data) {
                    setCropImg(data);
                }
                break;
        }
    }

    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_FROM_CAMERA);
    }

    /**
     * set the bitmap
     *
     * @param picdata
     */
    private void setCropImg(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (null != bundle) {
            Bitmap mBitmap = bundle.getParcelable("data");
//            mCover.setImageBitmap(mBitmap);
            String path = Environment.getExternalStorageDirectory() + "/crop_"
                    + System.currentTimeMillis() + ".JPG";
            saveBitmap(path, mBitmap);
            saveCurrentImgPath(path);
        }
    }

    /**
     * save the crop bitmap
     *
     * @param fileName
     * @param mBitmap
     */
    public void saveBitmap(String fileName, Bitmap mBitmap) {
        File f = new File(fileName);
        FileOutputStream fOut = null;
        try {
            f.createNewFile();
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fOut.close();
//                Toast.makeText(this, "save success", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void saveCurrentImgPath(final String path) {
        showLoadingDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.uploadFile(path, mToken, true);
            }
        }, 1500);
    }

    @Override
    public void modifySucces(User user) {
        if (user != null) {
            mCover.setImageURI(user.getAvatar());
            CacheManager.getInstance().saveUser(user);
        }
        dismissLoadingDialog();
        EventBus.getDefault().post(new UpdateUserInfoEvent());
    }

    @Override
    public void modifyFail(Error err) {
        Snackbar.make(mTitle, err.getMessage(), Toast.LENGTH_SHORT).show();
        dismissLoadingDialog();
        checkError(err);
    }

    @Override
    public void getTokenSucces(String token) {
        mToken = token;
    }

    @Override
    public void getTokenFail(Error err) {
        Snackbar.make(mTitle, err.getMessage(), Toast.LENGTH_SHORT).show();
        dismissLoadingDialog();
        checkError(err);
    }

    @Override
    public void uploadFileSuccess(String result) {
        mPresenter.modify(mUser.getPid(), result);
        dismissLoadingDialog();
    }

    @Override
    public void uploadFileFail(String result) {
        dismissLoadingDialog();
        Snackbar.make(mTitle, result, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void logoutSuccess() {
        dismissLoadingDialog();
        CacheManager.getInstance().clearAll();
        App.getInstance().clearAll();
        Intent intent3 = new Intent();
        intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent3.setClass(getApplicationContext(), LoginActivity.class);
        startActivity(intent3);
        setResult(1);
        finish();
    }

    @Override
    public void logoutFail(Error err) {
        dismissLoadingDialog();
        checkError(err);
    }
}
