package com.hotcast.vr;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hotcast.vr.image3D.Image3DSwitchView;
import com.hotcast.vr.pageview.LandscapeView;
import com.hotcast.vr.tools.L;

import butterknife.InjectView;

/**
 * Created by lostnote on 16/1/7.
 */
public class LandscapeActivity extends BaseActivity {

    @InjectView(R.id.container1)
    RelativeLayout container1;
    @InjectView(R.id.container2)
    RelativeLayout container2;

    private LandscapeView view1,view2;
    private Image3DSwitchView image3D_1,image3D_2;
    @Override
    public int getLayoutId() {
        return R.layout.activity_vr_list;
    }

    @Override
    public void init() {
        view1 = new LandscapeView(this);
        view2 = new LandscapeView(this);
        image3D_1 = (Image3DSwitchView) view1.getRootView().findViewById(R.id.image);
        image3D_2 = (Image3DSwitchView) view2.getRootView().findViewById(R.id.image);

        image3D_1.setOnMovechangeListener(new Image3DSwitchView.OnMovechangeListener() {
            @Override
            public void OnMovechange(int dix) {
                System.out.println("---OnMovechange1");
                image3D_2.scrollBy(dix, 0);
                image3D_2.refreshImageShowing();
            }
            @Override
            public void Next() {
                System.out.println("---Next1");
                image3D_2.scrollToNext();
            }
            @Override
            public void Previous() {
                System.out.println("---Previous1");
                image3D_2.scrollToPrevious();
            }
            @Override
            public void Back() {
                System.out.println("---Back1");
                image3D_2.scrollBack();
            }
        });
        image3D_2.setOnMovechangeListener(new Image3DSwitchView.OnMovechangeListener() {
            @Override
            public void OnMovechange(int dix) {
                System.out.println("---OnMovechange2");
                image3D_1.scrollBy(dix, 0);
                image3D_1.refreshImageShowing();
            }
            @Override
            public void Next() {
                System.out.println("---Next2");
                image3D_1.scrollToNext();
            }
            @Override
            public void Previous() {
                System.out.println("---Previous2");
                image3D_1.scrollToPrevious();
            }
            @Override
            public void Back() {
                System.out.println("---Back2");
                image3D_1.scrollBack();
            }
        });


        container1.addView(view1.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container2.addView(view2.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void getIntentData(Intent intent) {

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println("---keyCode = " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                image3D_1.scrollToNext();
                image3D_2.scrollToNext();
                L.e("你点击了下一张");
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                image3D_1.scrollToPrevious();
                image3D_2.scrollToPrevious();
                L.e("你点击了上一张");
                break;

            case KeyEvent.KEYCODE_ENTER:
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_BUTTON_A:
                L.e("你点击了进入播放页");
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_BUTTON_B:
                finish();
                break;
        }
        return true;
    }
    /**
     * 记录上次触摸的横坐标值
     */
    private float mLastMotionX;
    /**
     * 滚动到下一张图片的速度
     */
    private static final int SNAP_VELOCITY = 600;
    private VelocityTracker mVelocityTracker;
    public com.hotcast.vr.image3D.Image3DSwitchView.OnMovechangeListener changeLisener;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        getParent().requestDisallowInterceptTouchEvent(true);

        if (image3D_1.getmScroller().isFinished()) {
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            mVelocityTracker.addMovement(event);
            int action = event.getAction();
            float x = event.getX();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    changeLisener = image3D_1.getChangeLisener();
                    // 记录按下时的横坐标
                    mLastMotionX = x;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int disX = (int) (mLastMotionX - x);
                    mLastMotionX = x;
                    // 当发生移动时刷新图片的显示状态
                    image3D_1.scrollBy(disX, 0);
                    image3D_1.refreshImageShowing();
                    if (changeLisener!=null){
                        changeLisener.OnMovechange(disX);

                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mVelocityTracker.computeCurrentVelocity(1000);
                    int velocityX = (int) mVelocityTracker.getXVelocity();
                    if (shouldScrollToNext(velocityX)) {
                        // 滚动到下一张图
                        image3D_1.scrollToNext();
                        if (changeLisener!=null){
                            changeLisener.Next();

                        }
                    } else if (shouldScrollToPrevious(velocityX)) {
                        // 滚动到上一张图
                        image3D_1.scrollToPrevious();
                        if (changeLisener!=null){
                            changeLisener.Previous();
                        }
                    } else {
                        // 滚动回当前图片
                        image3D_1.scrollBack();
                        if (changeLisener!=null){
                            changeLisener.Back();

                        }
                    }
                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                    break;
            }
        }
        return true;
    }
    /**
     * 记录每张图片的宽度
     */
    private int mImageWidth;
    /**
     * 判断是否应该滚动到上一张图片。
     */
    private boolean shouldScrollToPrevious(int velocityX) {
        return velocityX > SNAP_VELOCITY || image3D_1.getScrollX() < -mImageWidth / 2 ;
    }
    /**
     * 判断是否应该滚动到下一张图片。
     */
    private boolean shouldScrollToNext(int velocityX) {
        return velocityX < -SNAP_VELOCITY || image3D_1.getScrollX() > mImageWidth / 2 ;
    }

}
