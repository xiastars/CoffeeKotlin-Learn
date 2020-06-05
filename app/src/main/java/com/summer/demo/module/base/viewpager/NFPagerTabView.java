package com.summer.demo.module.base.viewpager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.summer.demo.R;
import com.summer.helper.utils.SUtils;

public class NFPagerTabView extends TextView {

    private Paint mPaint;
    private int mTextColor;
    private int mTextSize;
    private int mIndicateColor;
    private int mIndicateRadius;
    private String mText;
    private boolean mShowIndicate;//显示指示图案

    public NFPagerTabView(Context context) {
        this(context, null, 0, null,0);
    }

    public NFPagerTabView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, null,0);
    }

    public NFPagerTabView(Context context, String text) {
        this(context, null, 0, text,0);
    }

    public NFPagerTabView(Context context, String text,int textSize) {
        this(context, null, 0, text,textSize);
    }

    public NFPagerTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, String text,int textSize) {
        super(context, attrs, defStyleAttr);
//        mPaint.setTextAlign(Paint.Align.CENTER);
        mTextColor = 0xEE060606;
        mIndicateColor = ContextCompat.getColor(context, R.color.red_d3);
        mText = text;
        mTextSize = sp2px(textSize == 0 ? 16 : textSize);
        mIndicateRadius = SUtils.getDip(context, 4);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setFakeBoldText(true);
        mPaint.setTextSize(mTextSize);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        int by = (int) (getHeight() - top- bottom)/2;

        Rect rect = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), rect);
        int bx = (getWidth()-rect.width())/2;
        if(TextUtils.isEmpty(getText().toString())){
            mPaint.setColor(    getCurrentTextColor());
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawText(mText, bx, by, mPaint);
        }
        if (mShowIndicate){
            mPaint.setColor(mIndicateColor);
            mPaint.setStyle(Paint.Style.FILL);
            int of = mTextSize/4;
            canvas.drawCircle(bx+rect.width()+of, by+top+of, mIndicateRadius, mPaint);
        }
    }

    public boolean isShowIndicate() {
        return mShowIndicate;
    }

    public void setShowIndicate(boolean showIndicate) {
        this.mShowIndicate = showIndicate;
        postInvalidate();
    }

    public void setContent(String mText) {
        this.mText = mText;
        postInvalidate();
    }

    public void setmTextSize(int size){
        mTextSize = sp2px(size);
        mPaint.setTextSize(mTextSize);
        postInvalidate();
    }

    private int sp2px(float spVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());
    }
}
