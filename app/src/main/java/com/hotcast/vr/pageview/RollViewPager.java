package com.hotcast.vr.pageview;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hotcast.vr.R;

import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

public class RollViewPager extends ViewPager {
    protected static final String tag = null;
    private Context context;
    private List<View> viewList;
    private TextView top_news_title;
    private List<String> titleList;
    private List<String> urlImgList;
    private BitmapUtils bitmapUtils;
    private MyAdapter myAdapter;
    private RunnableTask runnableTask;
    private int currentPosition = 0;

    private Handler mhandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            System.out.println("---切换轮播图：" + System.currentTimeMillis() + "---" + currentPosition);
//            RollViewPager.this.setCurrentItem(currentPosition);//处理了滑动
//            startRoll();
//        }
    };

    class RunnableTask implements Runnable {
        @Override
        public void run() {
            if (viewList.size() != 0) {
//                System.out.println("---切换轮播图：" + System.currentTimeMillis() + "---" + currentPosition);
                startRoll();
                //滚动viewpager
                currentPosition = (currentPosition + 1) % viewList.size();
                RollViewPager.this.setCurrentItem(currentPosition);//处理了滑动

//                mhandler.obtainMessage().sendToTarget();
            }
        }
    }

    private int downX;
    private int downY;
    private onPageClick pageClick;

    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //让当前viewpager 对应的夫控件不要去拦截事件
                getParent().requestDisallowInterceptTouchEvent(true);
                downX = (int) ev.getX();
                downY = (int) ev.getY();
//                System.out.println("---ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();

                //刷新
                if (Math.abs(moveY - downY) > Math.abs(moveX - downX)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    //滚动轮播图片
                    getParent().requestDisallowInterceptTouchEvent(true);
//                    System.out.println("---ACTION_MOVE");
                }
                break;
        }
//        System.out.println("---super.dispatchTouchEvent(ev) = "+super.dispatchTouchEvent(ev));
        return super.dispatchTouchEvent(ev);
    }


    //从界面移出的时候会调用方法
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //移除所有的任务
        mhandler.removeCallbacksAndMessages(null);
    }

    public RollViewPager(Context context, final List<View> viewList, onPageClick pageClick) {//new RollViewPager.onPageClick()
        super(context);
        this.context = context;
        this.viewList = viewList;
        this.pageClick = pageClick;
        bitmapUtils = new BitmapUtils(context);
        runnableTask = new RunnableTask();
        this.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
//                System.out.println("---切换文字：" + System.currentTimeMillis() + "---" + arg0);
                top_news_title.setText(titleList.get(arg0));
                for (int i = 0; i < urlImgList.size(); i++) {
//                    System.out.println("---viewlist:" + viewList.size() + "---i:" + i + "---arg0:" + arg0);
                    if (i == arg0) {
                        viewList.get(arg0).setBackgroundResource(R.drawable.dot_focus);
                    } else {
                        viewList.get(i).setBackgroundResource(R.drawable.dot_normal);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
//                System.out.println("---onPageScrolled arg0 = " + arg0 + " arg1 = " + arg1 + " arg2 = " + arg2);

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
//                System.out.println("---onPageScrolled arg0 = " + arg0 );
            }
        });
    }

    //将图片关联说明的文字集合,需要显示的控件传递进来
    public void initTitleList(TextView top_news_title, List<String> titleList) {
        if (null != top_news_title && null != titleList && titleList.size() > 0) {
            top_news_title.setText(titleList.get(0));
        }
        this.top_news_title = top_news_title;
        this.titleList = titleList;
    }

    //显示图片的url地址的集合
    public void initImgUrlList(List<String> urlImgList) {
        this.urlImgList = urlImgList;
//        System.out.println("---urlImgList的长度" + urlImgList.size());
    }

    public interface onPageClick {
        void onclick(int i);
    }

    public void startRoll() {
        //滚动viewpager
        if (myAdapter == null) {
            myAdapter = new MyAdapter();
            this.setAdapter(myAdapter);
        } else {
            myAdapter.notifyDataSetChanged();
        }
//        System.out.println("---startroll执行");
        mhandler.postDelayed(runnableTask, 4000l);
    }

    class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return urlImgList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = View.inflate(context, R.layout.viewpager_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            bitmapUtils.display(imageView, urlImgList.get(position));

            view.setOnTouchListener(new OnTouchListener() {
                private int downX;
                private long downTime;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mhandler.removeCallbacksAndMessages(null);//按住图片的时候移出图片的 轮播方法
                            downX = (int) event.getX();
                            downTime = System.currentTimeMillis();
//                            System.out.println("---ACTION_DOWN--onTouch");
                            break;
                        case MotionEvent.ACTION_UP:
//                            System.out.println("---ACTION_UP点击了轮播图--onTouch = " + (System.currentTimeMillis() - downTime < 500 && Math.abs(downX - event.getX())<20) );
                            if (System.currentTimeMillis() - downTime < 500 && Math.abs(downX - event.getX())<20) {
                                //点击事件被触发
                                if (pageClick != null) {
//                                    System.out.println("---ACTION_UP点击了轮播图--onTouch");
                                    pageClick.onclick(position);
                                }
                            }
                            startRoll();
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            startRoll();
                            break;
                    }
                    return true;
                }
            });

            ((RollViewPager) container).addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((RollViewPager) container).removeView((View) object);
        }
    }
}
