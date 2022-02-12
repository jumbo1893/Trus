package com.jumbo.trus;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {

    private boolean swipePageChangeEnabled;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.swipePageChangeEnabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipePageChangeEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return swipePageChangeEnabled && super.onTouchEvent(event);
    }

    public void setPagingEnabled(boolean enabled) {
        this.swipePageChangeEnabled = enabled;
    }
}
