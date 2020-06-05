package com.summer.demo.ui.module.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.summer.demo.R;
import com.summer.demo.ui.module.colorpicker.ColorPanelView.OnColorChangedListener;
import com.summer.helper.dialog.BaseCenterDialog;
import com.summer.helper.utils.Logs;

import butterknife.BindView;

/**
 * 颜色选择器
 */
public class AmbilWarnaDialog extends BaseCenterDialog {
	@BindView(R.id.ambilwarna_viewSatBri)
	ColorPanelView viewSatVal;
	@BindView(R.id.ambilwarna_viewHue)
	ImageView viewHue;
	@BindView(R.id.ambilwarna_target)
	ImageView viewTarget;
	@BindView(R.id.ambilwarna_state)
	LinearLayout ambilwarnaState;
	@BindView(R.id.ambilwarna_viewContainer)
	RelativeLayout viewContainer;

	int color;

	public interface OnAmbilWarnaListener {
		void onOk(int color);
	}

	final OnAmbilWarnaListener listener;

	final float[] currentColorHsv = new float[3];

	/**
	 * create an AmbilWarnaDialog. call this only from OnCreateDialog() or from a background thread.
	 *
	 * @param context current context
	 */
	public AmbilWarnaDialog(final Context context, int color, OnAmbilWarnaListener listen) {
		super(context);
		this.listener = listen;
		this.color = color;

		// move cursor & target on first draw

	}

	protected void moveTarget() {
		float x = getSat() * viewSatVal.getMeasuredWidth();
		float y = (1.f - getVal()) * viewSatVal.getMeasuredHeight();
		LayoutParams layoutParams = (LayoutParams) viewTarget.getLayoutParams();
		layoutParams.leftMargin = (int) (viewSatVal.getLeft() + x - Math.floor(viewTarget.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
		layoutParams.topMargin = (int) (viewSatVal.getTop() + y - Math.floor(viewTarget.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
		viewTarget.setLayoutParams(layoutParams);
	}

	private int getColor() {
		return Color.HSVToColor(currentColorHsv);
	}

	private float getHue() {
		return currentColorHsv[0];
	}

	private float getSat() {
		return currentColorHsv[1];
	}

	private float getVal() {
		return currentColorHsv[2];
	}

	private void setSat(float sat) {
		currentColorHsv[1] = sat;
	}

	private void setVal(float val) {
		currentColorHsv[2] = val;
	}

	@Override
	public int setContainerView() {
		return R.layout.dialog_color_picker;
	}

	@Override
	public void initView(final View view) {
		viewSatVal.setOnColorChangedListener(new OnColorChangedListener() {
			@Override
			public void onColorChanged(ColorPanelView view, int color) {
				Logs.i("color:"+color);
				viewHue.setBackgroundColor(color);
			}
		});
		Color.colorToHSV(color, currentColorHsv);/**/
		ViewTreeObserver vto = view.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				moveTarget();
				view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});

		viewSatVal.setHue(getHue());
		viewSatVal.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE
						|| event.getAction() == MotionEvent.ACTION_DOWN
						|| event.getAction() == MotionEvent.ACTION_UP) {

					float x = event.getX(); // touch event are in dp units.
					float y = event.getY();

					if (x < 0.f) x = 0.f;
					if (x > viewSatVal.getMeasuredWidth()) x = viewSatVal.getMeasuredWidth();
					if (y < 0.f) y = 0.f;
					if (y > viewSatVal.getMeasuredHeight()) y = viewSatVal.getMeasuredHeight();

					setSat(1.f / viewSatVal.getMeasuredWidth() * x);
					setVal(1.f - (1.f / viewSatVal.getMeasuredHeight() * y));

					// update view
					moveTarget();
				}
				return AmbilWarnaDialog.super.onTouchEvent(event);
			}
		});
	}

}
