package com.ees.chain.ui.viewholder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.media.utils.StringUtils;
import com.ees.chain.App;
import com.ees.chain.R;
import com.ees.chain.domain.Find;
import com.ees.chain.ui.activity.WebviewActivity;
import com.ees.chain.ui.view.support.recycler.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kesion on 2018/3/9.
 */

public class FindRVListViewHolder extends BaseViewHolder {
    List<ArrayList<Find>> mList;
    Context mContext;

    @BindView(R.id.item1)
    View mItem1;
    @BindView(R.id.item2)
    View mItem2;
    @BindView(R.id.item3)
    View mItem3;
    @BindView(R.id.item4)
    View mItem4;
    @BindView(R.id.item5)
    View mItem5;

    @BindView(R.id.icon1)
    SimpleDraweeView mIcon1;
    @BindView(R.id.icon2)
    SimpleDraweeView mIcon2;
    @BindView(R.id.icon3)
    SimpleDraweeView mIcon3;
    @BindView(R.id.icon4)
    SimpleDraweeView mIcon4;
    @BindView(R.id.icon5)
    SimpleDraweeView mIcon5;

    @BindView(R.id.title1)
    TextView mTitle1;
    @BindView(R.id.title2)
    TextView mTitle2;
    @BindView(R.id.title3)
    TextView mTitle3;
    @BindView(R.id.title4)
    TextView mTitle4;
    @BindView(R.id.title5)
    TextView mTitle5;

    @BindView(R.id.tips1)
    TextView mTips1;
    @BindView(R.id.tips2)
    TextView mTips2;
    @BindView(R.id.tips3)
    TextView mTips3;
    @BindView(R.id.tips4)
    TextView mTips4;
    @BindView(R.id.tips5)
    TextView mTips5;

    @BindView(R.id.tipsicon1)
    SimpleDraweeView mTipsicon1;
    @BindView(R.id.tipsicon2)
    SimpleDraweeView mTipsicon2;
    @BindView(R.id.tipsicon3)
    SimpleDraweeView mTipsicon3;
    @BindView(R.id.tipsicon4)
    SimpleDraweeView mTipsicon4;
    @BindView(R.id.tipsicon5)
    SimpleDraweeView mTipsicon5;

    @BindView(R.id.dot1)
    View mDot1;
    @BindView(R.id.dot2)
    View mDot2;
    @BindView(R.id.dot3)
    View mDot3;
    @BindView(R.id.dot4)
    View mDot4;
    @BindView(R.id.dot5)
    View mDot5;

    public FindRVListViewHolder(View itemView, List list, Context context) {
        super(itemView);
        mList = list;
        mContext = context;
    }

    @Override
    public void onBindViewHolder(final int position) {
        mItem1.setVisibility(View.GONE);
        mItem2.setVisibility(View.GONE);
        mItem3.setVisibility(View.GONE);
        mItem4.setVisibility(View.GONE);
        mItem5.setVisibility(View.GONE);

        mDot1.setVisibility(View.GONE);
        mDot2.setVisibility(View.GONE);
        mDot3.setVisibility(View.GONE);
        mDot4.setVisibility(View.GONE);
        mDot5.setVisibility(View.GONE);

        ArrayList<Find> items = mList.get(position);
        if (items.size()>0) {
            final Find find = items.get(0);
            if (find == null) return;
            mItem1.setVisibility(View.VISIBLE);
            mIcon1.setImageURI(find.getIcon());
            mTitle1.setText(find.getTitle());
            mTips1.setVisibility(View.GONE);
            if (!StringUtils.isBlank(find.getTips())) {
                mTips1.setVisibility(View.VISIBLE);
                mTips1.setText(find.getTips());
            }
            mTipsicon1.setVisibility(View.GONE);
            if (!StringUtils.isBlank(find.getTipIcon())) {
                mTipsicon1.setVisibility(View.VISIBLE);
                mTipsicon1.setImageURI(find.getTipIcon());
            }
            mItem1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    doAction(find);
                }
            });
        }

        if (items.size()>1) {
            final Find find = items.get(1);
            mItem2.setVisibility(View.VISIBLE);
            mIcon2.setImageURI(find.getIcon());
            mTitle2.setText(find.getTitle());
            mTips2.setVisibility(View.GONE);
            if (!StringUtils.isBlank(find.getTips())) {
                mTips2.setVisibility(View.VISIBLE);
                mTips2.setText(find.getTips());
            }
            mTipsicon2.setVisibility(View.GONE);
            if (!StringUtils.isBlank(find.getTipIcon())) {
                mTipsicon2.setVisibility(View.VISIBLE);
                mTipsicon2.setImageURI(find.getTipIcon());
            }
            mItem2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    doAction(find);
                }
            });
        }

        if (items.size()>2) {
            final Find find = items.get(2);
            mItem3.setVisibility(View.VISIBLE);
            mIcon3.setImageURI(find.getIcon());
            mTitle3.setText(find.getTitle());
            mTips3.setVisibility(View.GONE);
            if (!StringUtils.isBlank(find.getTips())) {
                mTips3.setVisibility(View.VISIBLE);
                mTips3.setText(find.getTips());
            }
            mTipsicon3.setVisibility(View.GONE);
            if (!StringUtils.isBlank(find.getTipIcon())) {
                mTipsicon3.setVisibility(View.VISIBLE);
                mTipsicon3.setImageURI(find.getTipIcon());
            }
            mItem3.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    doAction(find);
                }
            });
        }

        if (items.size()>3) {
            final Find find = items.get(3);
            mItem4.setVisibility(View.VISIBLE);
            mIcon4.setImageURI(find.getIcon());
            mTitle4.setText(find.getTitle());
            mTips4.setVisibility(View.GONE);
            if (!StringUtils.isBlank(find.getTips())) {
                mTips4.setVisibility(View.VISIBLE);
                mTips4.setText(find.getTips());
            }
            mTipsicon4.setVisibility(View.GONE);
            if (!StringUtils.isBlank(find.getTipIcon())) {
                mTipsicon4.setVisibility(View.VISIBLE);
                mTipsicon4.setImageURI(find.getTipIcon());
            }
            mItem4.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    doAction(find);
                }
            });
        }

        if (items.size()>4) {
            final Find find = items.get(4);
            mItem5.setVisibility(View.VISIBLE);
            mIcon5.setImageURI(find.getIcon());
            mTitle5.setText(find.getTitle());
            mTips5.setVisibility(View.GONE);
            if (!StringUtils.isBlank(find.getTips())) {
                mTips5.setVisibility(View.VISIBLE);
                mTips5.setText(find.getTips());
            }
            mTipsicon5.setVisibility(View.GONE);
            if (!StringUtils.isBlank(find.getTipIcon())) {
                mTipsicon5.setVisibility(View.VISIBLE);
                mTipsicon5.setImageURI(find.getTipIcon());
            }
            mItem5.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    doAction(find);
                }
            });
        }
    }

    public void doAction(Find find) {
        String target = find.getTarget();
        String url = find.getLink();
        Intent intent = new Intent();
        if (Find.TARGET_BROWSER.equals(target)) {
            intent.setAction("android.intent.action.VIEW");
            Uri content_url2 = Uri.parse(url);
            intent.setData(content_url2);
            mContext.startActivity(intent);
        } else {
            intent.putExtra("title", find.getTitle());
            intent.putExtra("id", find.getId());
            if (Find.TARGET_APP.equals(target)) {
                intent.putExtra("url", url);
            } else if (Find.TARGET_DEFAULT.equals(target)) {
                intent.putExtra("link", url);
            }
            intent.putExtra("fontlarge", 1);
            intent.setClass(mContext, WebviewActivity.class);
            mContext.startActivity(intent);
        }
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
