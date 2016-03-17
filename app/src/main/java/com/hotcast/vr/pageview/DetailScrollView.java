package com.hotcast.vr.pageview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by liurongzhi on 2016/3/17.
 */
public class DetailScrollView extends ScrollView {
    private ScrollBottomListener scrollBottomListener;

    public DetailScrollView(Context context) {
        super(context);
    }

    public DetailScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (t + getHeight() >= computeVerticalScrollRange()) {
            //ScrollView滑动到底部了
            scrollBottomListener.scrollBottom();
        }
    }

    public void setScrollBottomListener(ScrollBottomListener scrollBottomListener) {
        this.scrollBottomListener = scrollBottomListener;
    }

    public interface ScrollBottomListener {
        public void scrollBottom();
    }

}
