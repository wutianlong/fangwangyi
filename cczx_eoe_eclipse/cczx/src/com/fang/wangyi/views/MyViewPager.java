package com.fang.wangyi.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class MyViewPager extends ViewPager implements OnGestureListener {

    private GestureDetector mDetector;

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        GestureDetector detector = new GestureDetector(context, this);
        mDetector = detector;
    }

    public GestureDetector getGestureDetector() {
        return mDetector;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (listener != null) {
            listener.setOnSimpleClickListenr(getCurrentItem());
        }
        return true;
    }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        return false;
    }

    private onSimpleClickListener listener;

    public interface onSimpleClickListener {
        void setOnSimpleClickListenr(int position);
    }

    public void setOnSimpleClickListener(onSimpleClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height) height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
