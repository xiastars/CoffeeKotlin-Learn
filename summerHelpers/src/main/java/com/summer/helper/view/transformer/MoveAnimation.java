package com.summer.helper.view.transformer;

import android.view.animation.Transformation;

/**
 * Created by xiaqiliang on 2017/4/13.
 */

public class MoveAnimation extends ViewPropertyAnimation {
    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;
    protected final int mDirection;
    protected final boolean mEnter;

    private MoveAnimation(int direction, boolean enter, long duration) {
        this.mDirection = direction;
        this.mEnter = enter;
        setDuration(duration);
    }

    public static MoveAnimation create(int direction, boolean enter, long duration) {
        switch (direction) {

            default:
                return new HorizontalMoveAnimation(direction, enter, duration);
        }
    }

    private static class HorizontalMoveAnimation extends MoveAnimation {

        private HorizontalMoveAnimation(int direction, boolean enter, long duration) {
            super(direction, enter, duration);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            float value = mEnter ? (interpolatedTime - 1.0f) : interpolatedTime;
            if (mDirection == RIGHT) value *= -1.0f;
            mTranslationX = -value * mWidth;
            super.applyTransformation(interpolatedTime, t);
            applyTransformation(t);
        }
    }
}
