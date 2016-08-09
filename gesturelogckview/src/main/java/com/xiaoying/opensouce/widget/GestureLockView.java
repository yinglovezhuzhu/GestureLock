/*
 * Copyright (C) 2016. The Android Open Source Project.
 *
 *          yinglovezhuzhu@gmail.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.xiaoying.opensouce.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义锁屏View
 * 
 * @author dalong
 *
 */
public class GestureLockView extends View implements Handler.Callback {

    private static final int MSG_RESET = 0x0;

    private final Handler mHandler = new Handler(this);

    private OnGestureFinishListener onGestureFinishListener;

	/**解锁圆点数组*/
	private Circle[] mCircles;
	/**存储触碰圆的序列*/
	private final List<Integer> mCheckedIndex = new ArrayList<>();

	/**空心外圆*/
	private Paint mNormalPaint;
	/**点击后内部圆*/
	private Paint mInnerCirclePaint;
	/**画路径*/
	private Paint mLinesPaint;
	/** 线条绘制路径 **/
	private Path mLinePath = new Path();

	/** 未选中颜色 */
	private int mNormalColor = Color.argb(0xFF, 0x95, 0x9B, 0xB4);// 正常外圆颜色
	/** 错误颜色 */
	private int mErrorColor = Color.argb(0xFF, 0xFF, 0x25, 0x25);// 错误颜色
	/** 选中时颜色 */
	private int mCheckedColor = Color.argb(0xFF, 0x40, 0x9D, 0xE5);// 选中颜色

	/** 当前手指X位置 */
	private int mPointX;
	/** 当前手指Y位置 */
    private int mPointY;

	/** 手势密码的最小长度 */
	private int mMinCircleNum = 4;
	/** 能否触摸界面 */
	private boolean mCanTouch = true;
	/**验证结果*/
	private boolean mValid = true;
    /** 自动重置 **/
    private boolean mAutoReset = true;

    public GestureLockView(Context context) {
        this(context, null);
    }

    public GestureLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public GestureLockView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int specMode= MeasureSpec.getMode(widthMeasureSpec);
		int specSize= MeasureSpec.getSize(widthMeasureSpec);
		heightMeasureSpec= MeasureSpec.makeMeasureSpec((int) (specSize * 0.85f + 0.5f), specMode);
		super.onMeasure(widthMeasureSpec,heightMeasureSpec);
	}


	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int perWidthSize= getWidth() / 7;
		int perHeightSize=getHeight() / 6;
		// 初始化圆的参数
		if(null == mCircles && perWidthSize > 0 && perHeightSize > 0){
			mCircles = new Circle[9];
            Circle circle;
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 3;j++) {
					circle = new Circle();
					circle.code = i * 3 + j;
					circle.x = perWidthSize * (j * 2 + 1.5f) + 0.5f;
					circle.y = perHeightSize * ( i * 2 + 1) + 0.5f;
					circle.r = perWidthSize * 0.6f;
					mCircles[i * 3 + j] = circle;
				}
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mCanTouch){
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				mPointX =(int) event.getX();
				mPointY =(int) event.getY();
				for(Circle circle : mCircles){
					if(circle.isPointIn(mPointX, mPointY)){
                        circle.checked = true;
						if(!mCheckedIndex.contains(circle.code)){
							mCheckedIndex.add(circle.code);
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				//手指离开暂停触碰
				mCanTouch = false;
				StringBuilder stringBuilder = new StringBuilder();
				for(Integer index : mCheckedIndex){
					stringBuilder.append(index);
				}

                mValid = true;
				if(mCheckedIndex.size() < mMinCircleNum){
					mValid = false;
				}

				if(null != onGestureFinishListener && mCheckedIndex.size() > 0){
					onGestureFinishListener.OnGestureFinish(mValid, stringBuilder.toString());
				}

                if(mAutoReset) {
                    mHandler.sendEmptyMessageDelayed(MSG_RESET, 1000);
                } else {
                    if(mHandler.hasMessages(MSG_RESET)) {
                        mHandler.removeMessages(MSG_RESET);
                    }
                }
				break;
			}
			invalidate();
		}
		return true;
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
        if(null == mCircles) {
            return;
        }
		for (int i = 0; i < mCircles.length; i++) {
			if (!mCanTouch && !mValid) {
			    // 画完并且错误
				if (mCircles[i].checked) {
					drawInnerCycle(mCircles[i], canvas, mErrorColor);
					drawOutsideCycle(mCircles[i], canvas, mErrorColor);
				} else
					drawOutsideCycle(mCircles[i], canvas, mNormalColor);
			} else {
                //绘画中
				if (mCircles[i].checked) {
					drawInnerCycle(mCircles[i], canvas, mCheckedColor);
					drawOutsideCycle(mCircles[i], canvas, mCheckedColor);
				} else
					drawOutsideCycle(mCircles[i], canvas, mNormalColor);
			}
		}

		if (!mCanTouch && !mValid) {
			drawLines(canvas, mErrorColor);
		} else {
			drawLines(canvas, mCheckedColor);
		}

	}

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_RESET:
                reset();
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * 设置手势密码的最小长度，默认最小长度为4
     * @param minCircleNum 最小长度
     */
    public void setMinCircleNum(int minCircleNum){
        this.mMinCircleNum = minCircleNum;
    }

    /**
     * 设置是否自动重置<br>
     *     如果自动重置，绘制完成后1秒会恢复没有绘制的状态，
     *     如果设置为非自动重置，需要手动调用{@linkplain #reset()}重置
     * @param autoReset 是否自动重置
     */
    public void setAutoReset(boolean autoReset) {
        this.mAutoReset = autoReset;
    }

    /**
     * 展示错误<br>
     *     可以在比对密码的时候，密码错误的时候展示错误
     */
    public void showError() {
        if(mCheckedIndex.isEmpty()) {
            // 如果已经重置或者没有绘制，不处理
            return;
        }
        mValid = false;
        postInvalidate();
    }

    /**
     * 重置，恢复到没有绘制的状态
     */
    public void reset() {
        mPointX = 0;
        mPointY = 0;
        for(Circle circle : mCircles) {
            circle.checked = false;
        }
        mCheckedIndex.clear();
        mLinePath.reset();
        mCanTouch = true;
        postInvalidate();//在非ui线程刷新界面
    }

    /**
     * 设置手势绘制完成监听
     * @param onGestureFinishListener 手势绘制完成监听
     */
    public void setOnGestureFinishListener(OnGestureFinishListener onGestureFinishListener) {
        this.onGestureFinishListener = onGestureFinishListener;
    }

    /**
     * 初始化
     */
    public void init() {
        mNormalPaint = new Paint();
        mNormalPaint.setAntiAlias(true);
        mNormalPaint.setStrokeWidth(5);
        mNormalPaint.setStyle(Paint.Style.STROKE);

        mInnerCirclePaint =new Paint();
        mInnerCirclePaint.setAntiAlias(true);
        mInnerCirclePaint.setStyle(Paint.Style.FILL);

        mLinesPaint = new Paint();
        mLinesPaint.setAntiAlias(true);
        mLinesPaint.setStyle(Paint.Style.STROKE);
        mLinesPaint.setStrokeWidth(10);
    }

    /**
     * 绘制外部空心的元
     * @param circle 圆数据
     * @param canvas Canvas对象
     * @param color 颜色
     */
	private void drawOutsideCycle(Circle circle, Canvas canvas, int color) {
		mNormalPaint.setColor(color);
		canvas.drawCircle(circle.x, circle.y, circle.r, mNormalPaint);
	}

    /**
     * 画中心圆形
     * @param cycle 圆形
     * @param canvas Canvas对象
     * @param color 颜色
     */
    private void drawInnerCycle(Circle cycle, Canvas canvas, int color) {
        mInnerCirclePaint.setColor(color);
        canvas.drawCircle(cycle.x, cycle.y, cycle.r / 3f, mInnerCirclePaint);
    }

    /**
     * 绘制圆圈连接线条
     * @param canvas Canvas对象
     * @param color 颜色
     */
	private void drawLines(Canvas canvas, int color) {
		//构建路径
		mLinePath.reset();
		if (mCheckedIndex.size() > 0) {
            // 绘制已经选中的圆圈的连接线
			for (int i = 0; i < mCheckedIndex.size(); i++) {
				int index = mCheckedIndex.get(i);
				if (i == 0) {
					mLinePath.moveTo(mCircles[index].x, mCircles[index].y);
				} else {
					mLinePath.lineTo(mCircles[index].x, mCircles[index].y);
				}
			}
            // 绘制最后选中圆圈到当前触摸点的连接线
			if (mCanTouch) {
				mLinePath.lineTo(mPointX, mPointY);
			} else {
                // 非触摸状态下，回到最后一个选中点
                Circle lastCheckedCircle = mCircles[mCheckedIndex.get(mCheckedIndex.size()-1)];
				mLinePath.lineTo(lastCheckedCircle.x, lastCheckedCircle.y);
			}
			mLinesPaint.setColor(color);
			canvas.drawPath(mLinePath, mLinesPaint);
		}
	}

    /**
	 * 圆点
	 */
	class Circle {
		/** 圆心x坐标 */
		public float x;
		/** 圆心y坐标 */
        public float y;
		/** 圆心半径 */
        public float r;
		/** 圆心数值 */
        public int code;
		/** 圆心是否处于选中状态 */
        public boolean checked;

		/**
         * 判定点是否在该圆内部
         */
		public boolean isPointIn(int x, int y) {
			double distance = Math.sqrt((x - this.x) * (x - this.x) + (y - this.y) * (y - this.y));
			return distance < r;
		}
	}


    /**
     * 手势输入完成后回调接口
     */
    public interface OnGestureFinishListener {
        /**
         * 手势输入完成后回调函数
         * @param valid 绘制的组合是否合法有效
         * @param key 组合字符串
         */
        public void OnGestureFinish(boolean valid, String key);
    }

}


