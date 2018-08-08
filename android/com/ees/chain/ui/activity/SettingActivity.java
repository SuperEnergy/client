package com.ees.chain.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.manager.CacheManager;
import com.ees.chain.ui.view.support.CommomDialog;
import com.ees.chain.utils.ClockUtils;
import com.ees.chain.utils.Utils;

public class SettingActivity extends AppCompatActivity {

    private View mBack;
    private TextView mTitle;
    private Switch mNotifSwitch;
    private View mAbout;
    private View mMiningNotif;
    private TextView mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ClockUtils.startScanInstalledApps();
        configView();
    }

    private void configView() {
        mBack  = findViewById(R.id.left);
        mTitle = (TextView) findViewById(R.id.title);
        mAbout = findViewById(R.id.about);
        mMiningNotif = findViewById(R.id.mining_notif);
        mVersion = (TextView) findViewById(R.id.version);
        mNotifSwitch = (Switch) findViewById(R.id.mining_switch);
        mNotifSwitch.setVisibility(View.GONE);

        mTitle.setText(getString(R.string.setting));
        mVersion.setText(Utils.getCurrentVersionName(this));

        long in = CacheManager.getInstance().getNotificateSubmining();
        if (in==1) {
            mNotifSwitch.setChecked(true);
        } else {
            mNotifSwitch.setChecked(false);
        }

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent5 = new Intent(SettingActivity.this, WebviewActivity.class);
                intent5.putExtra("title", getString(R.string.about));
                intent5.putExtra("url", App.ABOUT);
                startActivity(intent5);
            }
        });

        mMiningNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommomDialog dialog = new CommomDialog(SettingActivity.this, R.style.dialog, getString(R.string.open_clock), new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if(confirm){
                            ClockUtils.startSystemClock();
                        }
                        dialog.dismiss();
                    }
                });
                dialog.setTitle(getString(R.string.tips_open_clock)).show();
            }
        });

        mNotifSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                CacheManager.getInstance().setNotificateSubmining(isChecked);
            }
        });
    }
}
