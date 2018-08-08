package com.ees.chain.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
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
import com.ees.chain.manager.CacheManager;
import com.ees.chain.task.group.VerifyPresenter;
import com.ees.chain.ui.base.BaseActivity;
import com.ees.chain.ui.interfc.VerifyContract;
import com.ees.chain.ui.view.support.CommomDialog;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * by kesion 2017/12/8
 */
public class VerifyTwoActivity extends BaseActivity<VerifyPresenter> implements VerifyContract.View {

    @BindView(R.id.left)
    View mBack;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.idcard_1)
    ImageView mIdcard1;
    @BindView(R.id.idcard_2)
    ImageView mIdcard2;
    @BindView(R.id.finish)
    View mFinish;

    ImageView mCurrentImg = null;
    int mCurrentImgId = 0;

    String mImgSavePath = null;
    String mToken = null;

    String img1path;
    String img2path;

    User mUser;
    private Uri photoUri;
    private String filePath;

    @OnClick(R.id.left)
    public void back(View view) {
        setResult(0);
        finish();
    }

    @OnClick(R.id.finish)
    public void onClick(View view) {
        if (img1path==null || img2path==null) {
            Toast.makeText(this, getString(R.string.err_idcard_full), Toast.LENGTH_SHORT).show();
            return;
        }
        String name = CacheManager.getInstance().getUserTruename();
        String idno = CacheManager.getInstance().getUserIdno();
        mPresenter.verify(name, idno, img1path, img2path, "");
        showLoadingDialog();
    }

    @OnClick({R.id.idcard_1, R.id.idcard_2})
    public void onClick2(View view) {
        mCurrentImgId = view.getId();
        switch (view.getId()) {
            case R.id.idcard_1:
                mCurrentImg = mIdcard1;
                break;
            case R.id.idcard_2:
                mCurrentImg = mIdcard2;
                break;
        }
        showPopueWindow(view.getId());
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_verify2;
    }

    @Override
    public void initDatas() {
        if(mPresenter!=null) mPresenter.getUploadToken();
        mUser = App.getInstance().getUser();
    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        if(mTitle!=null) mTitle.setText(getString(R.string.step_two));
        if (mUser != null && mUser.getRna_Status()==-1) {
            showForbidenDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
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
    public void verifySucces() {
        showVerifyDialog(true);
        dismissLoadingDialog();
    }

    public void showVerifyDialog(boolean success) {
        if (success) {
            if(mUser!=null) mUser.setRna_Status(2);
            CacheManager.getInstance().saveUser(mUser);
        }
        String content = getString(R.string.success_upload_img);
        if (!success) {
            content = getString(R.string.fail_upload_img);
        }
        CommomDialog dialog = new CommomDialog(this, R.style.dialog, content, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    setResult(2);
                    finish();
                }
                dialog.dismiss();
            }
        }).setTitle(getString(R.string.tips));
        dialog.show();
        dialog.setNegativeButtonVisible(View.GONE);
    }

    public void showForbidenDialog() {
        String content = getString(R.string.forbiden_hint);
        CommomDialog dialog = new CommomDialog(this, R.style.dialog, content, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                } else {
                    finish();
                }
                dialog.dismiss();
            }
        }).setTitle(getString(R.string.tips2));
        dialog.show();
    }

    @Override
    public void verifyFail(Error err) {
        Snackbar.make(mTitle, err.getMessage(), Toast.LENGTH_SHORT).show();
        checkError(err);
        dismissLoadingDialog();
    }

    @Override
    public void getTokenSucces(String token) {
        mToken = token;
    }

    @Override
    public void getTokenFail(Error err) {
        Snackbar.make(mTitle, err.getMessage(), Toast.LENGTH_SHORT).show();
        dismissLoadingDialog();
    }

    @Override
    public void uploadFileSuccess(int vid, String result) {
        switch (vid) {
            case R.id.idcard_1:
                img1path = result;
                break;
            case R.id.idcard_2:
                img2path = result;
                break;
        }
        dismissLoadingDialog();
    }

    @Override
    public void uploadFileFail(int vid, String result) {
        switch (vid) {
            case R.id.idcard_1:
                mCurrentImg.setImageResource(R.drawable.v1);
                break;
            case R.id.idcard_2:
                mCurrentImg.setImageResource(R.drawable.v2);
                break;
        }
        dismissLoadingDialog();
        Snackbar.make(mTitle, result, Toast.LENGTH_SHORT).show();
    }

    private void showPopueWindow(int vid){
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
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 2);
                if(popupWindow!=null) popupWindow.dismiss();

            }
        });
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                filePath = Environment.getExternalStorageDirectory().toString()+File.separator+ "DCIM" +File.separator+name+".jpg";

                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoUri = Uri.fromFile(new File(filePath));
                it.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(it, 1);
                if(popupWindow!=null) popupWindow.dismiss();

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

    /**
     * 拍照上传
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case 1:
                    if (StringUtils.isBlank(filePath)) {
                        Toast.makeText(this, getString(R.string.err_load_local_image), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    File file = new File(filePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    Bitmap bitmap= BitmapFactory.decodeFile(filePath, getBitmapOption(4));
                    if (mCurrentImg!=null) mCurrentImg.setImageBitmap(bitmap);
                    mImgSavePath = filePath;
                    saveCurrentImgPath(mCurrentImgId, mImgSavePath);
                    break;
                case 2:
                    if (data == null) {
                        Toast.makeText(this, getString(R.string.err_load_local_image), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    Uri uri = data.getData();
                    try {
                        Bitmap bitmap2 = getBitmapFormUri(this, uri);
                        if (mCurrentImg!=null) mCurrentImg.setImageBitmap(bitmap2);
                        ContentResolver cr = this.getContentResolver();
                        Cursor c = cr.query(uri, null, null, null, null);
                        if (c == null) {
                            Toast.makeText(this, getString(R.string.err_load_local_image), Toast.LENGTH_SHORT).show();
                            break;
                        }
                        c.moveToFirst();
                        //这是获取的图片保存在sdcard中的位置
                        mImgSavePath = c.getString(c.getColumnIndex("_data"));
                        saveCurrentImgPath(mCurrentImgId, mImgSavePath);
//                    System.out.println(mImgSavePath +"----------保存路径2");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            };
        }
    }

    /**
     * 通过uri获取图片并进行压缩
     *
     * @param uri
     */
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以480x800为标准
        float hh = 300f;//这里设置高度为800f
        float ww = 180f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    private BitmapFactory.Options getBitmapOption(int inSampleSize){
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    public void saveCurrentImgPath(final int vid, final String path) {
        showLoadingDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPresenter!=null) mPresenter.uploadFile(vid, path, mToken, true);
            }
        }, 1500);

    }
}
