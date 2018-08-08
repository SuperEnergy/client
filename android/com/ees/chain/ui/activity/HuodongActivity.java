package com.ees.chain.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ees.chain.R;
import com.ees.chain.domain.Notice;
import com.ees.chain.ui.base.BaseActivity;
import com.facebook.drawee.view.SimpleDraweeView;

public class HuodongActivity extends Activity {
    public static final String ARG_NOTICE = "ARG_NOTICE";

    SimpleDraweeView mCover;
    View mClose;
    Notice mNotice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huodong);
        setFinishOnTouchOutside(false);
        mNotice = (Notice) getIntent().getSerializableExtra(ARG_NOTICE);
        if (mNotice==null) return;
        mCover = (SimpleDraweeView) findViewById(R.id.cover);
        mClose = findViewById(R.id.close);

        if (mNotice != null) {
            mCover.setImageURI(mNotice.getPoster());
            mCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("title", mNotice.getTitle());
                    intent.putExtra("id", mNotice.getId());
                    intent.putExtra("content", mNotice.getContent());
                    intent.putExtra("link", mNotice.getLink());
                    intent.putExtra("fontlarge", 1);
                    intent.setClass(HuodongActivity.this, WebviewActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            finish();
        }

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
