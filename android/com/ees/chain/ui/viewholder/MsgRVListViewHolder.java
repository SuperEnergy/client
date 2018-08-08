package com.ees.chain.ui.viewholder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;


import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.R;
import com.ees.chain.domain.Notice;
import com.ees.chain.task.group.MsgPresenter;
import com.ees.chain.ui.activity.WebviewActivity;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kesion on 2017/12/5.
 */

public class MsgRVListViewHolder extends BaseViewHolder {
    List<Notice> mList;
    Context mContext;
    int msgType;

    @BindView(R.id.cover)
    SimpleDraweeView mCover;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.subtitle)
    TextView mSubtitle;
    @BindView(R.id.ctime)
    TextView mCtime;
    @BindView(R.id.check_detail)
    View mCheckDetail;
    @BindView(R.id.cardview)
    View mCardview;

    public MsgRVListViewHolder(View itemView, List list, int type, Context context) {
        super(itemView);
        mList = list;
        mContext = context;
        msgType = type;
    }

    @Override
    public void onBindViewHolder(final int position) {
        if (!StringUtils.isBlank(mList.get(position).getCover())) {
            mCover.setImageURI(mList.get(position).getCover());
            mCover.setVisibility(View.VISIBLE);
        } else {
            mCover.setVisibility(View.GONE);
        }
        mTitle.setText(mList.get(position).getTitle());
        mSubtitle.setText(mList.get(position).getExcerpt());
        Long date = mList.get(position).getCreateTime();
        mCtime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        if (msgType == MsgPresenter.TYPE_SYS_MSG) {
            mCheckDetail.setVisibility(View.VISIBLE);
        } else {
            mCheckDetail.setVisibility(View.GONE);
            mCover.setVisibility(View.GONE);
        }

        mCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(v, position);
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Notice notice = mList.get(position);
        if (notice!=null && msgType == MsgPresenter.TYPE_SYS_MSG) {
            Intent intent = new Intent();
            intent.putExtra("title", notice.getTitle());
            intent.putExtra("id", notice.getId());
            intent.putExtra("link", notice.getLink());
            intent.putExtra("fontlarge", 1);
            intent.setClass(mContext, WebviewActivity.class);
            mContext.startActivity(intent);
        }
    }
}
