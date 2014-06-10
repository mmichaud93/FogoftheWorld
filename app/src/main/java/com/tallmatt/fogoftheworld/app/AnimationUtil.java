package com.tallmatt.fogoftheworld.app;/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by michaudm3 on 2/10/14.
 */
public class AnimationUtil {
    public static final int DURATION_NONE = 0;
    public static final int DURATION_SHORT = 350;
    public static final int DURATION_MEDIUM = 650;
    public static final int DURATION_LONG = 1000;

    public static Animation fadeIn(final View view, int duration, int delay, boolean start) {
        final AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(duration);
        animation.setStartOffset(delay);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override public void onAnimationEnd(Animation animation) {}

            @Override public void onAnimationRepeat(Animation animation) {}
        });
        if(start) {
            view.startAnimation(animation);
        }
        return animation;
    }
    public static Animation fadeOut(final View view, int duration, int delay, boolean start) {
        final AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(duration);
        animation.setStartOffset(delay);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override public void onAnimationRepeat(Animation animation) {}
        });
        if(start) {
            view.startAnimation(animation);
        }
        return animation;
    }
    public static Animation slide(final View view, Point startDelta, Point endDelta, int duration, int delay, boolean start) {
        final TranslateAnimation animation = new TranslateAnimation(startDelta.x, endDelta.x, startDelta.y, endDelta.y);
        animation.setDuration(duration);
        animation.setStartOffset(delay);
        animation.setFillAfter(true);
        if(start) {
            view.startAnimation(animation);
        }
        return animation;
    }
    public static Animation slide(final View view, Point startDelta, Point endDelta, int duration, int delay, boolean fill, boolean start, Animation.AnimationListener listener) {
        final TranslateAnimation animation = new TranslateAnimation(startDelta.x, endDelta.x, startDelta.y, endDelta.y);
        animation.setAnimationListener(listener);
        animation.setDuration(duration);
        animation.setStartOffset(delay);
        animation.setFillAfter(fill);
        if(start) {
            view.startAnimation(animation);
        }
        return animation;
    }
    public static Animation resize(final View view, Point fromScale, Point toScale, Point pivot, int duration, int delay, boolean start) {
        final ScaleAnimation animation = new ScaleAnimation(fromScale.x, toScale.x, fromScale.y, toScale.y, pivot.x, pivot.y);
        animation.setDuration(duration);
        animation.setStartOffset(delay);
        animation.setFillAfter(true);
        if(start) {
            view.startAnimation(animation);
        }
        return animation;
    }
    public static Animation resize(final View view, float[] fromScale, float[] toScale, float[] pivot, int duration, int delay, boolean start) {
        final ScaleAnimation animation = new ScaleAnimation(fromScale[0], toScale[0], fromScale[1], toScale[1], pivot[0], pivot[1]);
        animation.setDuration(duration);
        animation.setStartOffset(delay);
        animation.setFillAfter(true);
        if(start) {
            view.startAnimation(animation);
        }
        return animation;
    }
    public static ValueAnimator animateValue(final View view, int startValue, int endValue, int duration, int delay, boolean start, ValueAnimator.AnimatorUpdateListener updateCallback) {
        final ValueAnimator animation = new ValueAnimator();
        animation.setIntValues(startValue, endValue);
        animation.setDuration(duration);
        animation.setStartDelay(delay);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.addUpdateListener(updateCallback);
        if(start) {
            animation.start();
        }
        return animation;
    }
}
