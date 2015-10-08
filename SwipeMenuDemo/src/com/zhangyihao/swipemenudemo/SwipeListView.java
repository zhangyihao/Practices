package com.zhangyihao.swipemenudemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ListView;

public class SwipeListView extends ListView {
	private static final int TOUCH_STATE_NONE = 0;
	// 水平滑动的时候
	private static final int TOUCH_STATE_X = 1;
	// 垂直滑动的时候
	private static final int TOUCH_STATE_Y = 2;
	// 这是设置的两个方向的阀值
	private int MAX_Y = 5;
	private int MAX_X = 3;
	// 记录初始时候的坐标
	private float mDownX;
	private float mDownY;
	// 状态标志符
	private int mTouchState;
	// 触摸的位置
	private int mTouchPosition;
	private SwipeListViewItemLayout mTouchView;
	// private OnSwipeListener mOnSwipeListener;

	private Interpolator mCloseInterpolator;
	private Interpolator mOpenInterpolator;

	public SwipeListView(Context context) {
		super(context);
		init();
	}

	public SwipeListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public SwipeListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		MAX_X = dp2px(MAX_X);
		MAX_Y = dp2px(MAX_Y);
		mTouchState = TOUCH_STATE_NONE;
	}
	
	public Interpolator getmCloseInterpolator() {
		return mCloseInterpolator;
	}

	public void setmCloseInterpolator(Interpolator mCloseInterpolator) {
		this.mCloseInterpolator = mCloseInterpolator;
	}

	public Interpolator getmOpenInterpolator() {
		return mOpenInterpolator;
	}

	public void setmOpenInterpolator(Interpolator mOpenInterpolator) {
		this.mOpenInterpolator = mOpenInterpolator;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(ev.getAction() != MotionEvent.ACTION_DOWN && this.mTouchView == null) {
			return super.onTouchEvent(ev);
		}
		int action = ev.getAction();
		switch(action) {
		case MotionEvent.ACTION_DOWN:
			int oldPos = mTouchPosition;
			this.mDownX = ev.getX();
			this.mDownY = ev.getY();
			this.mTouchState = TOUCH_STATE_NONE;

			 //这个方法就是获取当前的x，y坐标对应的是listView中的哪个position，是系统方法。
			this.mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
			if (this.mTouchPosition == oldPos && this.mTouchView != null && this.mTouchView.isOpen()) {
				this.mTouchState = TOUCH_STATE_X;
				this.mTouchView.onSwipe(ev);
                return true;
            }

			View view = getChildAt(mTouchPosition - getFirstVisiblePosition());

			if (mTouchView != null && mTouchView.isOpen()) {
				mTouchView.smoothCloseMenu();
				mTouchView = null;
				return super.onTouchEvent(ev);
			}
			if (view instanceof SwipeListViewItemLayout) {
				mTouchView = (SwipeListViewItemLayout) view;
			}
			if (mTouchView != null) {
				mTouchView.onSwipe(ev);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			float dy = Math.abs((ev.getY() - mDownY));
			float dx = Math.abs((ev.getX() - mDownX));
			if (mTouchState == TOUCH_STATE_X) {
				if (mTouchView != null) {
					mTouchView.onSwipe(ev);
				}
				getSelector().setState(new int[] { 0 });
				ev.setAction(MotionEvent.ACTION_CANCEL);
				super.onTouchEvent(ev);
				return true;
			} else if (mTouchState == TOUCH_STATE_NONE) {
				if (Math.abs(dy) > MAX_Y) {
					mTouchState = TOUCH_STATE_Y;
				} else if (dx > MAX_X) {
					mTouchState = TOUCH_STATE_X;
//					if (mOnSwipeListener != null) {
//						mOnSwipeListener.onSwipeStart(mTouchPosition);
//					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_X) {
				if (mTouchView != null) {
					mTouchView.onSwipe(ev);
					if (!mTouchView.isOpen()) {
						mTouchPosition = -1;
						mTouchView = null;
					}
				}
//				if (mOnSwipeListener != null) {
//					mOnSwipeListener.onSwipeEnd(mTouchPosition);
//				}
				ev.setAction(MotionEvent.ACTION_CANCEL);
				super.onTouchEvent(ev);
				return true;
			}
			break;
		}
		return super.onTouchEvent(ev);
	}
	
	public void smoothOpenMenu(int position) {
		if (position >= getFirstVisiblePosition()
				&& position <= getLastVisiblePosition()) {
			View view = getChildAt(position - getFirstVisiblePosition());
			if (view instanceof SwipeListViewItemLayout) {
				mTouchPosition = position;
				if (mTouchView != null && mTouchView.isOpen()) {
					mTouchView.smoothCloseMenu();
				}
				mTouchView = (SwipeListViewItemLayout) view;
				mTouchView.smoothOpenMenu();
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getContext().getResources().getDisplayMetrics());
	}
}
