package com.ees.chain.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

import com.ees.chain.R;

/**
 * Created by KESION on 2017/12/27.
 */

public class MyAnimationUtils {

    public static  void setHideAnimation(final View view, final int duration)
    {
        if (null == view || duration < 0)
        {
            return;
        }
        AlphaAnimation mHideAnimation = new AlphaAnimation(1.0f, 0.2f);
        if (null != mHideAnimation)
        {
            mHideAnimation.cancel();
        }
        // 监听动画结束的操作
        mHideAnimation.setDuration(duration);
        mHideAnimation.setFillAfter(true);
        mHideAnimation.setAnimationListener(new Animation.AnimationListener()
        {

            @Override
            public void onAnimationStart(Animation arg0)
            {

            }

            @Override
            public void onAnimationRepeat(Animation arg0)
            {

            }

            @Override
            public void onAnimationEnd(Animation arg0)
            {
                setShowAnimation(view , duration);
            }
        });
        view.startAnimation(mHideAnimation);
    }

    /**
     * View渐现动画效果
     */
    public static void setShowAnimation(final View view, final int duration)
    {
        if (null == view || duration < 0)
        {
            return;
        }
        AlphaAnimation mShowAnimation = new AlphaAnimation(0.2f, 1.0f);
        if (null != mShowAnimation)
        {
            mShowAnimation.cancel();
        }
        mShowAnimation.setDuration(duration);
        mShowAnimation.setFillAfter(true);
        mShowAnimation.setAnimationListener(new Animation.AnimationListener()
        {

            @Override
            public void onAnimationStart(Animation arg0)
            {

            }

            @Override
            public void onAnimationRepeat(Animation arg0)
            {

            }

            @Override
            public void onAnimationEnd(Animation arg0)
            {
                setHideAnimation(view , duration);
            }
        });
        view.startAnimation(mShowAnimation);
    }

    //360度转圈
    public static void setRotateAnimation360(Context context, final View view, final int duration) {
        if (null == view || duration < 0)
        {
            return;
        }
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.cover_rotate);
        view.startAnimation(animation);//开始动画
    }

    public static void setScaleAnimation(final View view, final int duration) {
        if (null == view || duration < 0)
        {
            return;
        }
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1,0.2f,1,0.2f,
                Animation.RELATIVE_TO_SELF,0.2f,Animation.RELATIVE_TO_SELF,0.2f);

        //3秒完成动画
        scaleAnimation.setDuration(duration);
        scaleAnimation.setRepeatMode(Animation.RESTART);
        scaleAnimation.setRepeatCount(-1);
        animationSet.addAnimation(scaleAnimation);
    }
}
