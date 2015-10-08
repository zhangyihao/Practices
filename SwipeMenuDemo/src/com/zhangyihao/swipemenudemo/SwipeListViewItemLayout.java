package com.zhangyihao.swipemenudemo;

import android.support.v4.widget.ScrollerCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

public class SwipeListViewItemLayout extends FrameLayout {
	//展示ListView Item的Viw
	private View mContentView;
	// 展示Item 的左划菜单项
	private View mMenuView;
	// 滑动菜单的动画速度控制器，暂时
	private Interpolator mCloseInterpolator;
	private Interpolator mOpenInterpolator;
	
	//控制控件滑动的，会平滑滑动，一个开一个关
	private ScrollerCompat mOpenScroller;
	private ScrollerCompat mCloseScroller;
	
	//左滑之后，contentView左边距离屏幕左边的距离，基线，用于滑回
    private int mBaseX;
    //手指点击的初始位置
    private int mDownX;
    //当前item的状态，open和close两种
    private int state = STATE_CLOSE;

    private static final int STATE_CLOSE = 0;
    private static final int STATE_OPEN = 1;
	

	public SwipeListViewItemLayout(View contentView, View menuView, 
			Interpolator closeInterpolator, Interpolator openInterpolator) {
		super(contentView.getContext());
		this.mContentView = contentView;
		this.mMenuView = menuView;
		this.mCloseInterpolator = closeInterpolator;
		this.mOpenInterpolator = openInterpolator;
		
		init();
	}
	
	private void init() {
		//设置一个item的宽和高，其实就是设置宽充满而已
		setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		//初始化mColoseScroller和mOpenScroller
		if(this.mOpenInterpolator == null) {
			this.mOpenScroller = ScrollerCompat.create(getContext());
		} else {
			this.mOpenScroller = ScrollerCompat.create(getContext(), this.mOpenInterpolator);
		}
		
		if(this.mCloseInterpolator!=null) {
			this.mCloseScroller = ScrollerCompat.create(getContext(), this.mCloseInterpolator);
		} else {
			this.mCloseScroller = ScrollerCompat.create(getContext());
		}
		
		this.mContentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		this.mMenuView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		addView(this.mContentView);
		addView(this.mMenuView);
	}
	
	/**
	 * 当用户在界面上滑动的时候，通过ListView的onTouch方法，将MotionEvent的动作传到这里来，通过这个函数执行操作。
	 * @param event
	 * @return
	 */
	public boolean onSwipe(MotionEvent event) {
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 记录当手指点击屏幕时的X坐标
			this.mDownX = (int) event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			//当手指移动的时候，获取这个差值
			int dist = (int) (this.mDownX - event.getX());
			if(state == STATE_OPEN) {
				dist += this.mMenuView.getWidth(); // 当右侧菜单为打开状态时，移动距离要加上菜单的宽度
			}
			swipe(dist);
			break;
		case MotionEvent.ACTION_UP:
			//这里其实是一个判断，当用户滑了menuView的一半的时候，自动滑出来，否则滑进去。
            if ((mDownX - event.getX()) > (this.mMenuView.getWidth() / 2)) {
                // 平滑的滑出
                smoothOpenMenu();
            } else {
                // 平滑的滑进
                smoothCloseMenu();
                return false;
            }
			break;
		}
		return true; //这个地方一定要return true，才能保证这个动作不会继续往下传递
	}
	
	public void smoothCloseMenu() {
		this.state = STATE_CLOSE;
		this.mBaseX = -this.mContentView.getLeft();
		this.mCloseScroller.startScroll(0, 0, this.mBaseX, 0, 350);
		postInvalidate();
	}

	public void smoothOpenMenu() {
		this.state = STATE_OPEN;
		this.mOpenScroller.startScroll(-this.mContentView.getLeft(), 0, this.mMenuView.getWidth(), 0, 350);
		postInvalidate();
	}

	// 判断是否滑出的状态
    public boolean isOpen() {
        return state == STATE_OPEN;
    }

    private void swipe(int dist) {
    	//这个方法就是滑动dis的距离，还记得那个 += 吗，如果dis > menuView.getWidth()的 话，dis = menuView.getWidth().
    	//这样，当滑到最大限度的时候，就不会滑动了
    	if(dist>this.mMenuView.getWidth()) {
    		dist = this.mMenuView.getWidth();
    	}
    	if(dist<0) {
    		dist = 0;
    	}
    	this.mContentView.layout(-dist, this.mContentView.getTop(), this.mContentView.getWidth() - dist, getMeasuredHeight());
    	this.mMenuView.layout(this.mContentView.getWidth()-dist, this.mMenuView.getTop(),
    			this.mContentView.getWidth()+this.mMenuView.getWidth()-dist, this.mMenuView.getBottom() );
    }


	@Override
	public void computeScroll() {
		if(this.state ==  STATE_OPEN) {
			if(this.mOpenScroller.computeScrollOffset()) {
				swipe(this.mOpenScroller.getCurrX());
				postInvalidate();
			}
		} else {
			if(this.mCloseScroller.computeScrollOffset()) {
				swipe(this.mBaseX - this.mCloseScroller.getCurrX());
				postInvalidate();
			}
		}
	}
	
	public void closeMenu() {
		if (mCloseScroller.computeScrollOffset()) {
			mCloseScroller.abortAnimation();
		}
		if (state == STATE_OPEN) {
			state = STATE_CLOSE;
			swipe(0);
		}
	}

	public void openMenu() {
		if (state == STATE_CLOSE) {
			state = STATE_OPEN;
			swipe(this.mMenuView.getWidth());
		}
	}

	public View getContentView() {
		return this.mContentView;
	}

	public View getMenuView() {
		return this.mMenuView;
	}
	
	//这个方法 其实就是获取menuView的宽和高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mMenuView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 
        		MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
    }
    
    //这个方法就把两个控件的相对布局表现出来了
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.mContentView.layout(0, 0, getMeasuredWidth(),this.mContentView.getMeasuredHeight());
        this.mMenuView.layout(getMeasuredWidth(), 0,
                getMeasuredWidth() + this.mMenuView.getMeasuredWidth(), this.mContentView.getMeasuredHeight());
    }
    
    
}
