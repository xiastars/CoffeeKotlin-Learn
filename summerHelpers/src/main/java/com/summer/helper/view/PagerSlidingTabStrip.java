/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.summer.helper.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malata.summer.helper.R;
import com.summer.helper.utils.SUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class PagerSlidingTabStrip extends HorizontalScrollView {

  public interface IconTabProvider {
    int getPageIconResId(int position);
  }

  public interface ViewTabProvider {
    View getPageView(int position);
  }

  // @formatter:off
  private static final int[] ATTRS = new int[]{android.R.attr.textSize,
          android.R.attr.textColor};
  // @formatter:on

  private LinearLayout.LayoutParams defaultTabLayoutParams;
  private LinearLayout.LayoutParams expandedTabLayoutParams;

  private final PageListener pageListener = new PageListener();
  public OnPageChangeListener delegatePageListener;

  private LinearLayout tabsContainer;
  private ViewPager pager;

  private int tabCount;

  private int currentPosition = 0;
  private float currentPositionOffset = 0f;

  private Paint rectPaint;
  private Paint dividerPaint;

  private int indicatorColor = Color.parseColor("#5630EC");
  private int underlineColor = Color.parseColor("#5630EC");
  private int dividerColor = 0x1AFFFFFF;//TAB之间的线

  private boolean shouldExpand = true;
  private boolean textAllCaps = true;

  private int scrollOffset = 15;
  private int indicatorHeight = 55;
  private int underlineHeight = 0;
  private int dividerPadding = 20;
  private int tabPadding = 12;
  private int dividerWidth = 1;
  private int tabStrokeWith = 5;

  private int tabTextSize = 16;
  private int tabTextColor = 0xEE060606;
  private int assistTextColor = Color.parseColor("#828282");
  private Typeface tabTypeface = null;
  private int tabTypefaceStyle = Typeface.NORMAL;

  private int lastScrollX = 0;

  private int mTabWidth = 0;
  private int tabBackgroundResId = R.color.transparent;

  private Locale locale;
  private Context context;
  private List<TextView> mTitleViews;//用于保存文字的标题view，方便更新标题

  public PagerSlidingTabStrip(Context context) {
    this(context, null);
    this.context = context;
  }

  public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
    this.context = context;
  }

  public PagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.context = context;
    setFillViewport(true);
    setWillNotDraw(false);

    mTitleViews = new ArrayList<>();

    tabsContainer = new LinearLayout(context);
    tabsContainer.setGravity(Gravity.CENTER);
    tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
    tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
    addView(tabsContainer);

    DisplayMetrics dm = getResources().getDisplayMetrics();


    scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            scrollOffset, dm);
    indicatorHeight = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
    underlineHeight = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
    dividerPadding = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
    tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            tabPadding, dm);
    dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dividerWidth, dm);

    // get system attrs (android:textSize and android:textColor)

    TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

    tabTextColor = a.getColor(1, tabTextColor);

    a.recycle();

    // get custom attrs

    a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);

    indicatorColor = a.getColor(
            R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, indicatorColor);
    underlineColor = a.getColor(
            R.styleable.PagerSlidingTabStrip_pstsUnderlineColor, underlineColor);
    dividerColor = a.getColor(
            R.styleable.PagerSlidingTabStrip_pstsDividerColor, dividerColor);
    indicatorHeight = a.getDimensionPixelSize(
            R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight, indicatorHeight);
    underlineHeight = a.getDimensionPixelSize(
            R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight, underlineHeight);
    dividerPadding = a.getDimensionPixelSize(
            R.styleable.PagerSlidingTabStrip_pstsDividerPadding, dividerPadding);
    tabPadding = a.getDimensionPixelSize(
            R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight, tabPadding);
    tabBackgroundResId = a.getResourceId(
            R.styleable.PagerSlidingTabStrip_pstsTabBackground, tabBackgroundResId);
    shouldExpand = a.getBoolean(
            R.styleable.PagerSlidingTabStrip_pstsShouldExpand, shouldExpand);
    scrollOffset = a.getDimensionPixelSize(
            R.styleable.PagerSlidingTabStrip_pstsScrollOffset, scrollOffset);
    textAllCaps = a.getBoolean(
            R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, textAllCaps);

    a.recycle();

    rectPaint = new Paint();
    rectPaint.setAntiAlias(true);
    rectPaint.setStyle(Style.FILL);
    rectPaint.setStrokeWidth(underlineHeight);

    dividerPaint = new Paint();
    dividerPaint.setAntiAlias(true);
    dividerPaint.setStrokeWidth(dividerWidth);

    defaultTabLayoutParams = new LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    expandedTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
            LayoutParams.MATCH_PARENT, 1.0f);

    if (locale == null) {
      locale = getResources().getConfiguration().locale;
    }
  }

  public void setViewPager(ViewPager pager) {
    this.pager = pager;

    if (pager.getAdapter() == null) {
      throw new IllegalStateException(
              "ViewPager does not have adapter instance.");
    }

    pager.setOnPageChangeListener(pageListener);

    notifyDataSetChanged();
    setTabColor(0);
  }

  public void setOnPageChangeListener(OnPageChangeListener listener) {
    this.delegatePageListener = listener;
  }

  String[] titles;

  public void setTitles(String[] titles) {
    this.titles = titles;
  }

  public void notifyDataSetChanged() {

    tabsContainer.removeAllViews();
    if (pager != null) {
      tabCount = pager.getAdapter().getCount();
    } else if (titles != null) {
      tabCount = titles.length;
    }

    for (int i = 0; i < tabCount; i++) {

      if (pager != null) {
        if (pager.getAdapter() instanceof IconTabProvider) {
          addIconTab(i,
                  ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
        } else if (pager.getAdapter() instanceof ViewTabProvider) {
          addTab(i, ((ViewTabProvider) pager.getAdapter()).getPageView(i));
        } else {
          addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
        }
      } else {
        addTextTab(i, titles[i]);
      }

    }

    updateTabStyles();

    getViewTreeObserver().addOnGlobalLayoutListener(
            new OnGlobalLayoutListener() {

              @SuppressWarnings("deprecation")
              @SuppressLint("NewApi")
              @Override
              public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                  getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                  getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                if (pager != null) {
                  currentPosition = pager.getCurrentItem();
                }
                scrollToChild(currentPosition, 0);
              }
            });

    setTabColor(0);
  }

  /**
   * 只更新文字标题
   */
  public synchronized void notifyTitlesChanged() {
    for (int i = 0; i < mTitleViews.size(); i++) {
      if (pager != null) {
        mTitleViews.get(i).setText(pager.getAdapter().getPageTitle(i));
      }
    }

  }

  private void addTextTab(final int position, String title) {

    TextView tab = new TextView(getContext());
    tab.setText(title);
    tab.setGravity(Gravity.CENTER);
    tab.setSingleLine();
    setClick(tab);
    mTitleViews.add(tab);
    addTab(position, tab);
  }

  /**
   * 滚动或者
   *
   * @param position
   * @param resId
   */

  private void addIconTab(final int position, int resId) {

    ImageButton tab = new ImageButton(getContext());
    tab.setImageResource(resId);

    addTab(position, tab);

  }

  private void setClick(final TextView view) {
//	  Animation anim = AnimationUtils.loadAnimation(getContext(),
//				R.anim.zoom_in_anim);
//		view.startAnimation(anim);
    SUtils.clickTransColor(view);
  }

  private void addTab(final int position, View tab) {
    tab.setFocusable(true);
    tab.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        setCurposition(position);
        if (pager != null) {
          pager.setCurrentItem(position);
        }
        if (currentPosition == position) {
          if (notifyCurListener != null) {
            notifyCurListener.notifyCur(currentPosition);
          }

        }
      }
    });

    tab.setPadding(tabPadding, 0, tabPadding, 0);
    //expandedTabLayoutParams.width = SUtils.getSWidth((Activity) context,125);
    tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams
            : defaultTabLayoutParams);
  }

  public void setTabWithAndHeight(int width, int height, int margin) {
    defaultTabLayoutParams.width = SUtils.getDip(context, width);
    defaultTabLayoutParams.height = SUtils.getDip(context, height);
    defaultTabLayoutParams.leftMargin = SUtils.getDip(context, margin);
    defaultTabLayoutParams.rightMargin = SUtils.getDip(context, margin);
    expandedTabLayoutParams.width = SUtils.getDip(context, width);
    expandedTabLayoutParams.height = SUtils.getDip(context, height);
    expandedTabLayoutParams.leftMargin = SUtils.getDip(context, margin);
    expandedTabLayoutParams.rightMargin = SUtils.getDip(context, margin);
  }

  public void updateTabStyles() {

    for (int i = 0; i < tabCount; i++) {

      View v = tabsContainer.getChildAt(i);
      //设置背景
      v.setBackgroundResource(tabBackgroundResId);

      if (v instanceof TextView) {

        TextView tab = (TextView) v;
        tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, tabTextSize);
        tab.setTypeface(tabTypeface, tabTypefaceStyle);
        tab.setTextColor(tabTextColor);
        // setAllCaps() is only available from API 14, so the upper case is made
        // manually if we are on a
        // pre-ICS-build
        if (textAllCaps) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            tab.setAllCaps(true);
          } else {
            tab.setText(tab.getText().toString().toUpperCase(locale));
          }
        }
      }
    }

  }

  public void scrollToChild(int position, int offset) {

    if (tabCount == 0) {
      return;
    }

    View view = tabsContainer.getChildAt(position);
    if (view == null) {
      return;
    }
    int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

    if (position > 0 || offset > 0) {
      newScrollX -= scrollOffset;
    }

    if (newScrollX != lastScrollX) {
      lastScrollX = newScrollX;
      scrollTo(newScrollX, 0);
    }

  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if (isInEditMode() || tabCount == 0) {
      return;
    }

    final int height = getHeight();

    // draw indicator line

    rectPaint.setColor(indicatorColor);

    // default: line below current tab
    View currentTab = tabsContainer.getChildAt(currentPosition);
    float lineLeft = currentTab.getLeft();
    float lineRight = currentTab.getRight();

    // if there is an offset, start interpolating left and right coordinates
    // between current and next tab
    if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

      View nextTab = tabsContainer.getChildAt(currentPosition + 1);
      final float nextTabLeft = nextTab.getLeft();
      final float nextTabRight = nextTab.getRight();

      lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset)
              * lineLeft);
      lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset)
              * lineRight);
    }

    //画底部滑动那根线
    canvas.drawLine(lineLeft + mTabWidth, height - indicatorHeight, lineRight - mTabWidth, height, rectPaint);
    //canvas.drawOval(lineLeft + mTabWidth, height - 5, lineRight - mTabWidth, height - 5, rectPaint);
    // draw underline

    rectPaint.setColor(underlineColor);
    rectPaint.setStrokeWidth(3);
    canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(),
            height, rectPaint);

    // draw divider

    dividerPaint.setColor(dividerColor);
    for (int i = 0; i < tabCount - 1; i++) {
      View tab = tabsContainer.getChildAt(i);
      canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height
              - dividerPadding, dividerPaint);
    }
  }

  public void setTabStrokeWidth(int width) {
    tabStrokeWith = width;
  }

  public void setTabWidth(int width) {
    mTabWidth = width;
  }

  public void setCurposition(int position) {
    this.currentPosition = position;
    setTabColor(position);
  }


  private class PageListener implements OnPageChangeListener {

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
      if (tabsContainer == null || tabsContainer.getChildAt(position) == null) {
        return;
      }
      currentPosition = position;
      currentPositionOffset = positionOffset;

      scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));
      invalidate();
      if (delegatePageListener != null) {
        delegatePageListener.onPageScrolled(position, positionOffset,
                positionOffsetPixels);
      }

    }

    private int getCurrentItem() {
      if (pager == null) {
        return 0;
      }
      return pager.getCurrentItem();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
      if (state == ViewPager.SCROLL_STATE_IDLE) {
        scrollToChild(getCurrentItem(), 0);
      }

      if (delegatePageListener != null) {
        delegatePageListener.onPageScrollStateChanged(state);
      }
      if (notifyCurListener != null) {
        notifyCurListener.notifyCur(currentPosition);
      }
    }

    @Override
    public void onPageSelected(int position) {
      if (delegatePageListener != null) {
        delegatePageListener.onPageSelected(position);
      }
      setTabColor(position);
    }
  }

  public void setTabColor(int position) {
    for (int i = 0; i < tabsContainer.getChildCount(); i++) {
      TextView v = (TextView) tabsContainer.getChildAt(i);
      if (i == position) {
        if (v != null) {
          v.setTextColor(tabTextColor);
          v.setBackgroundResource(tabBackgroundResId);
        }
      } else {
        if (v != null) {
          v.setTextColor(assistTextColor);
          v.setBackgroundResource(R.drawable.trans);
        }
      }
    }
  }

  /**
   * 下面那根线的颜色
   *
   * @param indicatorColor
   */
  public void setIndicatorColor(int indicatorColor) {
    this.indicatorColor = indicatorColor;
    invalidate();
  }

  public void setIndicatorColorResource(int resId) {
    this.indicatorColor = getResources().getColor(resId);
    invalidate();
  }

  public int getIndicatorColor() {
    return this.indicatorColor;
  }

  public void setIndicatorHeight(int indicatorLineHeightPx) {
    this.indicatorHeight = indicatorLineHeightPx;
    invalidate();
  }

  public int getIndicatorHeight() {
    return indicatorHeight;
  }

  public void setUnderlineColor(int underlineColor) {
    this.underlineColor = underlineColor;
    invalidate();
  }

  public void setUnderlineColorResource(int resId) {
    this.underlineColor = getResources().getColor(resId);
    invalidate();
  }

  public int getUnderlineColor() {
    return underlineColor;
  }

  public void setDividerColor(int dividerColor) {
    this.dividerColor = dividerColor;
    invalidate();
  }

  public void setDividerColorResource(int resId) {
    this.dividerColor = getResources().getColor(resId);
    invalidate();
  }

  public int getDividerColor() {
    return dividerColor;
  }

  public void setUnderlineHeight(int underlineHeightPx) {
    this.underlineHeight = underlineHeightPx;
    invalidate();
  }

  public int getUnderlineHeight() {
    return underlineHeight;
  }

  public void setDividerPadding(int dividerPaddingPx) {
    this.dividerPadding = dividerPaddingPx;
    invalidate();
  }

  public int getDividerPadding() {
    return dividerPadding;
  }

  public void setScrollOffset(int scrollOffsetPx) {
    this.scrollOffset = scrollOffsetPx;
    invalidate();
  }

  public int getScrollOffset() {
    return scrollOffset;
  }

  public void setShouldExpand(boolean shouldExpand) {
    this.shouldExpand = shouldExpand;
    requestLayout();
  }

  public boolean getShouldExpand() {
    return shouldExpand;
  }

  public boolean isTextAllCaps() {
    return textAllCaps;
  }

  public void setAllCaps(boolean textAllCaps) {
    this.textAllCaps = textAllCaps;
  }

  public void setTextSize(int textSizePx) {
    this.tabTextSize = textSizePx;
    updateTabStyles();
  }

  public int getTextSize() {
    return tabTextSize;
  }

  public void setAssitTextColor(int textColor) {
    this.assistTextColor = textColor;
  }

  public void setTextColor(int textColor) {
    this.tabTextColor = textColor;
    updateTabStyles();
  }

  public void setTextColorResource(int resId) {
    this.tabTextColor = getResources().getColor(resId);
    updateTabStyles();
  }

  public int getTextColor() {
    return tabTextColor;
  }

  public void setTypeface(Typeface typeface, int style) {
    this.tabTypeface = typeface;
    this.tabTypefaceStyle = style;
    updateTabStyles();
  }

  public void setTabBackground(int resId) {
    this.tabBackgroundResId = resId;
  }

  public int getTabBackground() {
    return tabBackgroundResId;
  }

  public void setTabPaddingLeftRight(int paddingPx) {
    this.tabPadding = paddingPx;
    updateTabStyles();
  }

  public int getTabPaddingLeftRight() {
    return tabPadding;
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    SavedState savedState = (SavedState) state;
    super.onRestoreInstanceState(savedState.getSuperState());
    currentPosition = savedState.currentPosition;
    requestLayout();
  }

  @Override
  public Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();
    SavedState savedState = new SavedState(superState);
    savedState.currentPosition = currentPosition;
    return savedState;
  }

  static class SavedState extends BaseSavedState {
    int currentPosition;

    public SavedState(Parcelable superState) {
      super(superState);
    }

    private SavedState(Parcel in) {
      super(in);
      currentPosition = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeInt(currentPosition);
    }

    public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
      @Override
      public SavedState createFromParcel(Parcel in) {
        return new SavedState(in);
      }

      @Override
      public SavedState[] newArray(int size) {
        return new SavedState[size];
      }
    };
  }


  public TagNotifyCurrentListener getNotifyCurListener() {
    return notifyCurListener;
  }

  public void setNotifyCurListener(TagNotifyCurrentListener notifyCurListener) {
    this.notifyCurListener = notifyCurListener;
  }

  private TagNotifyCurrentListener notifyCurListener;

  public interface TagNotifyCurrentListener {
    void notifyCur(int position);
  }

}