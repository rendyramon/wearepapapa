package com.hotcast.vr.pageview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

/**
 * Created by liurongzhi on 2016/2/18.
 */
public class LandGalleyView extends Gallery {
    Gallery anotherGalley;

    public LandGalleyView(Context context) {
        super(context);
    }

    public LandGalleyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LandGalleyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LandGalleyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public boolean scrollmy(MotionEvent event) {
        return super.onTouchEvent(event);
    }

//    @Override
//    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        System.out.println("---velocityX:"+velocityX);
//        velocityX = velocityX/5;
//
//        return super.onFling(e1, e2, velocityX, velocityY);
//    }
////
//    @Override
//    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        System.out.println("---distanceX"+distanceX);
//        distanceX = distanceX/2;
//        return super.onScroll(e1, e2, distanceX, distanceY);
//    }
}
