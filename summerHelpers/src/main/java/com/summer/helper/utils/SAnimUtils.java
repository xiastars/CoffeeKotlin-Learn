package com.summer.helper.utils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.malata.summer.helper.R;
import com.summer.helper.listener.OnAnimEndListener;

/**
 * 属性动画
 */

public class SAnimUtils {

    static boolean isOnAnim;

    public static AnimManager build(){
        AnimManager animManager = new AnimManager();
        return animManager;
    }


    /**
     * 向左偏移
     *
     * @param view
     * @param offset
     */
    public static void hideTop(View view, float offset) {
        showPropertyAnim(view, View.GONE, "translationY", offset, 0f, 0f, 300);
    }

    /**
     * 透明消失
     *
     * @param view
     */
    public static void hideAlphat(View view) {

    }

    /**
     * 向左偏移
     *
     * @param view
     * @param offset
     */
    public static void hideTop(View view, float offset, final OnAnimEndListener listener) {
        showPropertyAnim(true, view, View.GONE, "translationY", 0, offset, offset, 300, listener);
    }

    /**
     * 向左偏移
     *
     * @param view
     * @param offset
     */
    public static void hideBottom(View view, float offset, final OnAnimEndListener listener) {
        showPropertyAnim(true, view, View.GONE, "translationY", 0, 0, offset, 300, listener);
    }

    /**
     * 向左偏移
     *
     * @param view
     * @param offset
     */
    public static void moveLeftHide(View view, float offset, final OnAnimEndListener listener) {
        showPropertyAnim(true, view, View.GONE, "translationX", 0f, offset, offset, 300, listener);
    }

    /**
     * 旋转动画
     *
     * @param view
     */
    public static ObjectAnimator rotation(View view) {
        return showPropertyAnim(true, view, View.VISIBLE, "rotation", 0, 360, 360, 3000, null);
    }

    /**
     * 旋转动画
     *
     * @param view
     */
    public static void rotation(View view, int radius, OnAnimEndListener listener) {
        showPropertyAnim(true, view, View.VISIBLE, "rotation", 0, radius, radius, 10000, listener);
    }

    /**
     * 旋转动画
     *
     * @param view
     */
    public static void rotationRepeat(View view) {
        showPropertyAnim(true, view, "rotation", 0, 359, 359, 8000, ObjectAnimator.INFINITE, null);
    }

    /**
     * 旋转动画
     *
     * @param view
     */
    public static void rotationX(View view, OnAnimEndListener listener) {
        showPropertyAnim(true, view, View.VISIBLE, "rotationY", 0, 180, 180, 1000, listener);
    }

    /**
     * 旋转动画
     *
     * @param view
     */
    public static void rotationX(View view, int time, float rotate, float endRotate, OnAnimEndListener listener) {
        showPropertyAnim(true, view, View.VISIBLE, "rotationY", 90, endRotate, endRotate, time, listener);
    }


    /**
     * 在下方，从隐藏到出现
     *
     * @param view
     * @param offset
     */
    public static void fromBottomToShow(View view, float offset, final OnAnimEndListener listener) {
        showPropertyAnim(false, view, View.VISIBLE, "translationY", offset, 0f, 0f, 300, listener);
    }

    /**
     * 在下方，向下隐藏
     *
     * @param view
     * @param offset
     */
    public static void fromBottomToHide(View view, float offset, final OnAnimEndListener listener) {
        showPropertyAnim(false, view, View.GONE, "translationY", 0, offset,  offset, 300, listener);
    }

    /**
     * 在上侧，向上隐藏
     *
     * @param view
     * @param offset
     */
    public static void fromTopMoveToHide(View view, float offset, final OnAnimEndListener listener) {
        showPropertyAnim(false, view, View.VISIBLE, "translationY", 0f, -offset, -offset, 300, listener);
    }

    /**
     * 在左侧，从隐藏到出现
     *
     * @param view
     * @param offset
     */
    public static void fromLeftToShow(View view, float offset, final OnAnimEndListener listener) {
        showPropertyAnim(false, view, View.VISIBLE, "translationX", -offset, 0, -0, 300, listener);
    }

    /**
     * 在左侧，从隐藏到出现
     *
     * @param view
     * @param offset
     */
    public static void fromLeftToHide(View view, float offset, final OnAnimEndListener listener) {
        showPropertyAnim(false, view, View.VISIBLE, "translationX", 0, -offset, -offset, 300, listener);
    }

    /**
     * 在右侧，从隐藏到出现
     *
     * @param view
     * @param offset
     */
    public static void fromRightToShow(View view, float offset, final OnAnimEndListener listener) {
        showPropertyAnim(false, view, View.VISIBLE, "translationX", offset, 0, -0, 300, listener);
    }

    /**
     * 在右侧，从出现到隐藏
     *
     * @param view
     * @param offset
     */
    public static void fromRightToHide(View view, float offset, final OnAnimEndListener listener) {
        showPropertyAnim(false, view, View.GONE, "translationX", 0, offset, offset, 300, listener);
    }

    /**
     * 在上侧，向下展示
     *
     * @param view
     * @param offset
     */
    public static void fromTopMoveToShow(View view, float offset, final OnAnimEndListener listener) {
        showPropertyAnim(false, view, View.VISIBLE, "translationY", -offset, 0, 0, 300, listener);
    }

    /**
     * 透明度到0
     *
     * @param view
     */
    public static void alphaToInvisible(View view, long time, final OnAnimEndListener listener) {
        showPropertyAnim(true, view, View.GONE, "alpha", 1.0f, 0f, 0f, time, listener);
    }

    /**
     * 透明度到0
     *
     * @param view
     */
    public static void alphaToShow(View view, long time, final OnAnimEndListener listener) {
        showPropertyAnim(false, view, View.VISIBLE, "alpha", 0f, 1f, 1f, time, listener);
    }


    /**
     * 物体动画
     *
     * @param view
     * @param visible
     * @param animAction
     * @param x1
     * @param x2
     * @param x3
     */
    public static void showPropertyAnim(View view, int visible, String animAction, float x1, float x2, float x3, long time) {
        showPropertyAnim(true, view, visible, animAction, x1, x2, x3, time, null);
    }

    /**
     * 物体动画
     *
     * @param view
     * @param visible
     * @param animAction
     * @param x1
     * @param x2
     * @param x3
     */
    public static void showPropertyAnim(boolean anim, View view, int visible, String animAction, float x1, float x2, float x3) {
        showPropertyAnim(anim, view, visible, animAction, x1, x2, x3, 300, null);
    }

    /**
     * 物体动画
     *
     * @param view
     * @param visible
     * @param animAction
     * @param x1
     * @param x2
     * @param x3
     */
    public static void showPropertyAnim(boolean anim, View view, int visible, String animAction, float x1, float x2, float x3, long time) {
        showPropertyAnim(anim, view, visible, animAction, x1, x2, x3, time, null);
    }

    public static void scale(View view, boolean clearAnim, float width, float height) {
        circleScaleAnim(view, width, height, 0, 1.5f, 300, clearAnim, null);
    }

    public static void scale(View view, float endCtf, boolean clearAnim) {
        circleScaleAnim(view, 0, 0, 0, endCtf, 300, clearAnim, null);
    }

    public static void amplifyView(View view, OnAnimEndListener listener) {
        amplifyView(view, 0, 0, 0, 800, false, listener);
    }

    public static void scaleY(View view) {
    }

    /**
     * 物体动画
     *
     * @param view
     * @param visible
     * @param animAction
     * @param x1
     * @param x2
     * @param x3
     */
    public static ObjectAnimator showPropertyAnim(final boolean anim, final View view, final int visible, String animAction, float x1, float x2, float x3, long time, final OnAnimEndListener listener) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, animAction, x1, x2, x3);
        if(view == null){
            return null;
        }
        if (visible == View.VISIBLE) {
            if (view.getVisibility() == View.GONE) {
                view.setVisibility(View.VISIBLE);
            }
        }
        view.setPivotX(view.getLayoutParams().width / 2);
        view.setPivotY(view.getLayoutParams().height / 2);
        animator.setDuration(time).start();
        animator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isOnAnim = false;
                if (listener != null) {
                    listener.onEnd();
                }
                if (anim) {
                    view.setTranslationX(0);
                    view.setTranslationY(0);
                    view.setAlpha(1.0f);
                    view.clearAnimation();
                }

                view.setVisibility(visible);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        return animator;
    }

    public static ObjectAnimator showPropertyAnim(final boolean anim, final View view, String animAction, float x1, float x2, float x3, long time, int repeatMode, final OnAnimEndListener listener) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, animAction, x1, x2);
        view.setPivotX(view.getLayoutParams().width / 2);
        view.setPivotY(view.getLayoutParams().height / 2);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setStartDelay(0);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.setDuration(time).start();
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null) {
                    listener.onEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        return animator;
    }

    /**
     * 放大缩小动画
     *
     * @param view
     * @param listener
     */
    public static void amplifyView(final View view, float width, float height, int repeatcount, int time, boolean clearAnim, final OnAnimEndListener listener) {
        if (view == null) return;
        SUtils.initScreenDisplayMetrics((Activity) view.getContext());
        view.setPivotX(SUtils.screenWidth / 2);
        view.setPivotY(SUtils.screenHeight / 2);
        float endX = 1.3f;
        float endY = 1.3f;
        float startX = 1f;
        float startY = 1f;
        PropertyValuesHolder valuesHolder1 = PropertyValuesHolder.ofFloat("scaleX", startX, endX, startX);
        if (!clearAnim) {
            valuesHolder1 = PropertyValuesHolder.ofFloat("scaleX", startX, endX, endX);
        }
        PropertyValuesHolder valuesHolder2 = PropertyValuesHolder.ofFloat("scaleY", startY, endY, startY);
        if (!clearAnim) {
            valuesHolder2 = PropertyValuesHolder.ofFloat("scaleY", startY, endY, endY);
        }

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, valuesHolder1, valuesHolder2);
        objectAnimator.setRepeatCount(repeatcount);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.setDuration(time).start();
        objectAnimator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.clearAnimation();
                if (listener != null) {
                    listener.onEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
    }

    /**
     * 放大缩小动画
     *
     * @param view
     * @param listener
     */
    public static void circleScaleAnim(final View view, float width, float height, int repeatcount, float endCtf, int time, boolean clearAnim, final OnAnimEndListener listener) {
        if (view == null) return;
        if (width == 0) {
            view.setPivotX(view.getWidth() / 2);
        } else {
            view.setPivotX(width);
        }
        if (height == 0) {
            view.setPivotY(view.getHeight() / 2);
        } else {
            view.setPivotY(height);
        }
        float endX = endCtf;
        float endY = endCtf;
        float startX = 1f;
        float startY = 1f;
        PropertyValuesHolder valuesHolder1 = PropertyValuesHolder.ofFloat("scaleX", startX, endX, startX);
        if (!clearAnim) {
            valuesHolder1 = PropertyValuesHolder.ofFloat("scaleX", startX, endX, endX);
        }
        PropertyValuesHolder valuesHolder2 = PropertyValuesHolder.ofFloat("scaleY", startY, endY, startY);
        if (!clearAnim) {
            valuesHolder2 = PropertyValuesHolder.ofFloat("scaleY", startY, endY, endY);
        }

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, valuesHolder1, valuesHolder2);
        objectAnimator.setRepeatCount(repeatcount);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.setDuration(time).start();
        objectAnimator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.clearAnimation();
                if (listener != null) {
                    listener.onEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
    }

    public static void removeScale(View view) {
        float x = 1.0f;
        PropertyValuesHolder valuesHolder1 = PropertyValuesHolder.ofFloat("scaleX", x, x, x);
        PropertyValuesHolder valuesHolder2 = PropertyValuesHolder.ofFloat("scaleY", x, x, x);

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, valuesHolder1, valuesHolder2);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.setDuration(0).start();
    }

    /**
     * 向下隐藏
     *
     * @param view
     */
    public static void slideToBottomHide(final View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_bottom);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}
