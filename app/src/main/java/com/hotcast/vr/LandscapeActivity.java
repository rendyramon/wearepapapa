package com.hotcast.vr;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.bean.ChanelData;
import com.hotcast.vr.bean.Channel;
import com.hotcast.vr.bean.ChannelList;
import com.hotcast.vr.bean.LocalBean2;
import com.hotcast.vr.imageView.Image3DView;
import com.hotcast.vr.pageview.VrListView;
import com.hotcast.vr.services.DownLoadingService;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.DensityUtils;
import com.hotcast.vr.tools.HotVedioCacheUtils;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Md5Utils;
import com.hotcast.vr.tools.SaveBitmapUtils;
import com.hotcast.vr.tools.TokenUtils;
import com.hotcast.vr.tools.Utils;
import com.hotcast.vr.tools.VedioBitmapUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by lostnote on 16/1/7.
 */
public class LandscapeActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.container1)
    RelativeLayout container1;
    @InjectView(R.id.container2)
    RelativeLayout container2;

    private VrListView view1, view2;
    private View updateV1, updateV2;
    private com.hotcast.vr.imageView.Image3DSwitchView img3D, img3D2;
    private ProgressBar progressBar_update1, progressBar_update2;
    private TextView bt_cancel_progressbar1, bt_cancel_progressbar2;
    private RelativeLayout rl_update1, rl_update2;
    UpdateAppManager updateAppManager;
    List<ChanelData> netClassifys;
    private HttpHandler httphandler;
    BitmapUtils bitmapUtils;
    private Intent cacheIntent;
    int mCurrentImg;

    @Override
    public int getLayoutId() {
        return R.layout.activity_vr_list;
    }

    @Override
    public void init() {
        view1 = new VrListView(this);
        view2 = new VrListView(this);
        view1.hideTextView();
        view1.setPageCenter();
        view2.hideTextView();
        view2.setPageCenter();
        bitmapUtils = new BitmapUtils(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        progressBar_update1 = (ProgressBar) view1.getRootView().findViewById(R.id.progressBar_update);
        progressBar_update2 = (ProgressBar) view2.getRootView().findViewById(R.id.progressBar_update);
        bt_cancel_progressbar1 = (TextView) view1.getRootView().findViewById(R.id.bt_cancel_progressbar);
        bt_cancel_progressbar1.setOnClickListener(this);
        bt_cancel_progressbar2 = (TextView) view2.getRootView().findViewById(R.id.bt_cancel_progressbar);
        bt_cancel_progressbar2.setOnClickListener(this);
        rl_update1 = (RelativeLayout) view1.getRootView().findViewById(R.id.rl_update);
        rl_update2 = (RelativeLayout) view2.getRootView().findViewById(R.id.rl_update);
        img3D = (com.hotcast.vr.imageView.Image3DSwitchView) view1.getRootView().findViewById(R.id.id_sv);
        img3D2 = (com.hotcast.vr.imageView.Image3DSwitchView) view2.getRootView().findViewById(R.id.id_sv);
        img3D.setimgWidthp(0.4);
        img3D2.setimgWidthp(0.4);
        for (int i = 0; i < netClassifys.size(); i++) {
            Image3DView image3DView1 = new Image3DView(this);
            System.out.println("---图片地址 ：" + netClassifys.get(i).getImg_big());
            bitmapUtils.display(image3DView1, netClassifys.get(i).getImg_big());
            image3DView1.setLayoutParams(params);
            final String channel_id = netClassifys.get(i).getId();
            image3DView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view1.showOrHideProgressBar(true);
                    view2.showOrHideProgressBar(true);
                    getNetData(channel_id);
                    System.out.println("***你点击了item，准备播放**");
                }
            });
            img3D.addView(image3DView1);
            Image3DView image3DView2 = new Image3DView(this);
            bitmapUtils.display(image3DView2, netClassifys.get(i).getImg_big());
            image3DView2.setLayoutParams(params);
            image3DView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view1.showOrHideProgressBar(true);
                    view2.showOrHideProgressBar(true);
                    getNetData(channel_id);
                    System.out.println("***你点击了item，准备播放**");
                }
            });
            img3D2.addView(image3DView2);
        }
        Image3DView image3DView1 = new Image3DView(this);
        image3DView1.setImageResource(R.mipmap.cache_icon);
        image3DView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查询本地指定的缓存文件夹
                DbUtils db = DbUtils.create(LandscapeActivity.this);
                try {
                    dbList = db.findAll(LocalBean2.class);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if (dbList == null) {
                    dbList = new ArrayList<>();
                }
//                if (dataCacheOk) {
                    cacheIntent = new Intent(LandscapeActivity.this, LocalCachelActivity.class);
                    cacheIntent.putExtra("dbList", (Serializable) dbList);
                    System.out.println("---传递数据的尺寸：" + dbList.size());
                    startActivity(cacheIntent);
//                } else {
//                    //显示小菊花
//                    view1.showOrHideProgressBar(true);
//                    view2.showOrHideProgressBar(true);
//                    Message msg = Message.obtain();
//                    msg.what = 100;
//                    mHandler.sendMessageDelayed(msg, 1000);
//                }
            }
        });
        img3D.addView(image3DView1);
        Image3DView image3DView2 = new Image3DView(this);
        image3DView2.setImageResource(R.mipmap.cache_icon);
        image3DView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查询本地指定的缓存文件夹
                DbUtils db = DbUtils.create(LandscapeActivity.this);
                try {
                    dbList = db.findAll(LocalBean2.class);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if (dbList == null) {
                    dbList = new ArrayList<>();
                }
//                if (dataCacheOk) {
                cacheIntent = new Intent(LandscapeActivity.this, LocalCachelActivity.class);
                cacheIntent.putExtra("dbList", (Serializable) dbList);
                System.out.println("---传递数据的尺寸：" + dbList.size());
                startActivity(cacheIntent);
//                if (dataCacheOk) {
//                    cacheIntent = new Intent(LandscapeActivity.this, LocalCachelActivity.class);
//                    cacheIntent.putExtra("dbList", (Serializable) dbList);
//                    startActivity(cacheIntent);
//                } else {
//                    //显示小菊花
//                    view1.showOrHideProgressBar(true);
//                    view2.showOrHideProgressBar(true);
//                    Message msg = Message.obtain();
//                    msg.what = 100;
//                    mHandler.sendMessageDelayed(msg, 1000);
//                }
            }
        });
        img3D2.addView(image3DView2);
        img3D.setOnMovechangeListener(new com.hotcast.vr.imageView.Image3DSwitchView.OnMovechangeListener() {
            @Override
            public void OnMovechange(int dix) {
                System.out.println("---OnMovechange1");
                img3D2.scrollBy(dix, 0);
                img3D2.refreshImageShowing();
            }

            @Override
            public void Next() {
                System.out.println("---Next1");
                img3D2.scrollToNext();
            }

            @Override
            public void Previous() {
                System.out.println("---Previous1");
                img3D2.scrollToPrevious();
            }

            @Override
            public void Back() {
                System.out.println("---Back1");
                img3D2.scrollBack();
            }
        });


        img3D2.setOnMovechangeListener(new com.hotcast.vr.imageView.Image3DSwitchView.OnMovechangeListener() {
            @Override
            public void OnMovechange(int dix) {
                System.out.println("---OnMovechange1");
                img3D.scrollBy(dix, 0);
                img3D.refreshImageShowing();
            }

            @Override
            public void Next() {
                System.out.println("---Next1");
                img3D.scrollToNext();
            }

            @Override
            public void Previous() {
                System.out.println("---Previous1");
                img3D.scrollToPrevious();
            }

            @Override
            public void Back() {
                System.out.println("---Back1");
                img3D.scrollBack();
            }
        });
        container1.addView(view1.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container2.addView(view2.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        System.out.println("---是否更新" + BaseApplication.isUpdate);
//        if (BaseApplication.isUpdate) { //主线版本不用这个
//            showUpdate();
//        }
//        container2.setOnClickListener(this);
//        container1.setOnClickListener(this);
    }

    // 更新应用版本标记
    private static final int UPDARE_TOKEN = 0x29;
    // 准备安装新版本应用标记
    private static final int INSTALL_TOKEN = 0x31;
    LinearLayout ll_updatetext1;
    LinearLayout ll_updatetext2;
    Button bt_ok1;
    Button bt_cancel1;
    Button bt_ok2;
    Button bt_cancel2;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDARE_TOKEN:
                    progressBar_update2.setProgress((int) msg.obj);
                    progressBar_update1.setProgress((int) msg.obj);
//                    System.out.println("***正在下载");
                    break;
                case INSTALL_TOKEN:
                    installApp();
                    break;
                case 100:
                    if (dataCacheOk) {
                        cacheIntent = new Intent(LandscapeActivity.this, LocalCachelActivity.class);
                        cacheIntent.putExtra("dbList", (Serializable) dbList);
                        startActivity(cacheIntent);
                    } else {
                        //显示小菊花
                        view1.showOrHideProgressBar(true);
                        view2.showOrHideProgressBar(true);
                        Message msg1 = Message.obtain();
                        msg1.what = 100;
                        mHandler.sendMessageDelayed(msg1, 1000);
                    }
                    break;
                case 101:
                    if (BaseApplication.doAsynctask) {
                        DbUtils db = DbUtils.create(LandscapeActivity.this);
                        try {
                            dbList = db.findAll(LocalBean2.class);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        if (dbList == null) {
                            dbList = new ArrayList<>();
                        }
                        dataCacheOk = true;
                    } else {
                        Message msg1 = Message.obtain();
                        msg1.what = 101;
                        mHandler.sendMessageDelayed(msg1, 500);
                    }
                    break;
//                case STOP:
            }
        }
    };

    private void showUpdate() {
        if ("1".equals(is_force)) {
            updateAppManager = new UpdateAppManager(this, spec, is_force, newFeatures);
            httphandler = updateAppManager.downloadAppInlandscape(mHandler);
            // TODO 显示进度条
            rl_update1.setVisibility(View.VISIBLE);
            rl_update2.setVisibility(View.VISIBLE);
        } else {
            updateV1 = View.inflate(this, R.layout.update_window, null);
            updateV2 = View.inflate(this, R.layout.update_window, null);
            ll_updatetext1 = (LinearLayout) updateV1.findViewById(R.id.ll_updatetext);
            ll_updatetext2 = (LinearLayout) updateV2.findViewById(R.id.ll_updatetext);
            bt_ok1 = (Button) updateV1.findViewById(R.id.bt_ok);
            bt_cancel1 = (Button) updateV1.findViewById(R.id.bt_cancel);
            bt_ok2 = (Button) updateV2.findViewById(R.id.bt_ok);
            bt_cancel2 = (Button) updateV2.findViewById(R.id.bt_cancel);
            bt_ok1.setOnClickListener(this);
            bt_cancel1.setOnClickListener(this);
            bt_ok2.setOnClickListener(this);
            bt_cancel2.setOnClickListener(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.width = DensityUtils.dp2px(this, 220);
//        params.height = DensityUtils.dp2px(this,80);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            updateV1.setLayoutParams(params);
            updateV2.setLayoutParams(params);
            container1.addView(updateV1);
            container2.addView(updateV2);
        }
    }

    //下载路径
    private String spec;
    //是否强制更新
    private String is_force;
    //更新日志
    String newFeatures;

    @Override
    protected void onRestart() {
        super.onRestart();
        dataCacheOk = false;
        System.out.println("---netClassifys in on restart:" + netClassifys.size());
        BaseApplication.size = netClassifys.size() + 1;
        System.out.println("---BaseApplication.size in on restart:" + BaseApplication.size);
    }

    @Override
    public void getIntentData(Intent intent) {
        spec = getIntent().getStringExtra("spec");

        is_force = getIntent().getStringExtra("is_force");
        newFeatures = getIntent().getStringExtra("newFeatures");
        Channel channel = (Channel) getIntent().getSerializableExtra("classifies");
        if (channel!=null) {
            netClassifys = channel.getData();
        }
        if (netClassifys != null) {
            BaseApplication.size = netClassifys.size() + 1;
        }
        System.out.println("---netClassifys:" + netClassifys.size());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println("---keyCode = " + keyCode);
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    img3D.scrollToNext();
                    img3D2.scrollToNext();
                    L.e("你点击了下一张");
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    img3D.scrollToPrevious();
                    img3D2.scrollToPrevious();
                    L.e("你点击了上一张");
                    break;

                case KeyEvent.KEYCODE_ENTER:
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_BUTTON_A:
                    L.e("你点击了进入播放页");
                    mCurrentImg = img3D.getImgIndex() - 1;
                    if (mCurrentImg < 0) {
                        mCurrentImg = netClassifys.size();
                    }
                    clickItem(mCurrentImg);
                    break;
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
    public com.hotcast.vr.imageView.Image3DSwitchView.OnMovechangeListener changeLisener;


    int downX, downY;
    int upX, upY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        getParent().requestDisallowInterceptTouchEvent(true);
        //九又版本全屏捕捉点击事件
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                downX = (int) event.getX();
//                downY = (int) event.getY();
//                break;
//            case MotionEvent.ACTION_UP:
//                upX = (int) event.getX();
//                upY = (int) event.getY();
//                break;
//        }
//        int xlen = Math.abs(downX - upX);
//        int ylen = Math.abs(downY - upY);
//        int length = (int) Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
//        if (length < 10) {
//            //执行点击事件
//            mCurrentImg = img3D.getImgIndex() - 1;
//            if (mCurrentImg < 0) {
//                mCurrentImg = netClassifys.size();
//            }
//            clickItem(mCurrentImg);
//            return true;
//        }
        if (img3D.getmScroller().isFinished()) {
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            mVelocityTracker.addMovement(event);
            int action = event.getAction();
            float x = event.getX();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    changeLisener = img3D.getChangeLisener();
                    // 记录按下时的横坐标
                    mLastMotionX = x;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int disX = (int) (mLastMotionX - x);
                    mLastMotionX = x;
                    // 当发生移动时刷新图片的显示状态
                    img3D.scrollBy(disX, 0);
                    img3D.refreshImageShowing();
                    if (changeLisener != null) {
                        changeLisener.OnMovechange(disX);

                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mVelocityTracker.computeCurrentVelocity(1000);
                    int velocityX = (int) mVelocityTracker.getXVelocity();
                    if (shouldScrollToNext(velocityX)) {
                        // 滚动到下一张图
                        img3D.scrollToNext();
                        if (changeLisener != null) {
                            changeLisener.Next();

                        }
                    } else if (shouldScrollToPrevious(velocityX)) {
                        // 滚动到上一张图
                        img3D.scrollToPrevious();
                        if (changeLisener != null) {
                            changeLisener.Previous();
                        }
                    } else {
                        // 滚动回当前图片
                        img3D.scrollBack();
                        if (changeLisener != null) {
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

    public void clickItem(int i) {
        System.out.println("---clickItem:"+i);
        if(i<netClassifys.size()){
            view1.showOrHideProgressBar(true);
            view2.showOrHideProgressBar(true);
            getNetData(netClassifys.get(i).getId());
        }else if(i == netClassifys.size()){
            //查询本地指定的缓存文件夹
            if (dataCacheOk) {
                cacheIntent = new Intent(LandscapeActivity.this, LocalCachelActivity.class);
                cacheIntent.putExtra("dbList", (Serializable) dbList);
                System.out.println("---传递数据的尺寸：" + dbList.size());
                startActivity(cacheIntent);
            } else {
                //显示小菊花
                view1.showOrHideProgressBar(true);
                view2.showOrHideProgressBar(true);
                Message msg = Message.obtain();
                msg.what = 100;
                mHandler.sendMessageDelayed(msg, 1000);
            }
        }
    }

    /**
     * 记录每张图片的宽度
     */
    private int mImageWidth;

    /**
     * 判断是否应该滚动到上一张图片。
     */
    private boolean shouldScrollToPrevious(int velocityX) {
        return velocityX > SNAP_VELOCITY || img3D.getScrollX() < -mImageWidth / 2;
    }

    /**
     * 判断是否应该滚动到下一张图片。
     */
    private boolean shouldScrollToNext(int velocityX) {
        return velocityX < -SNAP_VELOCITY || img3D.getScrollX() > mImageWidth / 2;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ok:
                Toast.makeText(this, "开始下载更新包", Toast.LENGTH_LONG).show();
                container1.removeView(updateV1);
                container2.removeView(updateV2);
                if (isNetworkConnected(this) || isWifiConnected(this) || isMobileConnected(this)) {
                    if (!TextUtils.isEmpty(spec)) {
                        updateAppManager = new UpdateAppManager(this, spec, is_force, newFeatures);
                        httphandler = updateAppManager.downloadAppInlandscape(mHandler);
                        rl_update1.setVisibility(View.VISIBLE);
                        rl_update2.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.bt_cancel:
                container1.removeView(updateV1);
                container2.removeView(updateV2);
                Toast.makeText(this, "取消更新", Toast.LENGTH_LONG).show();
                break;
            case R.id.bt_cancel_progressbar:
                httphandler.cancel();
                rl_update1.setVisibility(View.GONE);
                rl_update2.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 安装新版本应用
     */
    private void installApp() {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), "VR热播.apk")), "application/vnd.android.package-archive");//编者按：此处Android应为android，否则造成安装不了
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (httphandler != null) {
            httphandler.cancel();
        }

    }

    @Override
    protected void onStart() {
        BaseApplication.scapePage = 1;
        super.onStart();
//        dbList = new ArrayList<>();
//        if (BaseApplication.doAsynctask){
//            MyAsyncTask task = new MyAsyncTask();
//            task.execute();
//        }else{
//            mHandler.sendEmptyMessage(101);
//        }
        Intent intent = new Intent(LandscapeActivity.this, DownLoadingService.class);
        LandscapeActivity.this.startService(intent);
        view1.showOrHideProgressBar(false);
        view2.showOrHideProgressBar(false);
    }

    List<ChannelList> tmpList;

    public void getNetData(final String channel_id) {
        String mUlr = Constants.PROGRAM_LIST;
        System.out.println("***VrListActivity *** getNetData()" + mUlr);
        L.e("播放路径 mUrl=" + mUlr);
        RequestParams params = new RequestParams();
        System.out.println("***VrListActivity *** getNetData()" + params);
        params.addBodyParameter("token", TokenUtils.createToken(this));
        params.addBodyParameter("channel_id", channel_id);
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("page_size", String.valueOf(8));
        System.out.println("***VrListActivity *** getNetData()" + channel_id);
        this.httpPost(mUlr, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                System.out.println("***VrListActivity *** onStart()");
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                view1.showOrHideProgressBar(false);
                view2.showOrHideProgressBar(false);
                System.out.println("***VrListActivity *** onSuccess()" + responseInfo.result);
                if (Utils.textIsNull(responseInfo.result)) {
                    return;
                }
                tmpList = new Gson().fromJson(responseInfo.result, new TypeToken<List<ChannelList>>() {
                }.getType());
                System.out.println("***VrListActivity *** onSuccess()" + tmpList);
                System.out.println("***VrListActivity *** onSuccess()" + tmpList.size());

                Intent intent = new Intent(LandscapeActivity.this, VrListActivity.class);
                intent.putExtra("channel_id", channel_id);
                intent.putExtra("vrPlays", (Serializable) tmpList);
                System.out.println("跳转到VrListActivity vrPlays" + tmpList);
                LandscapeActivity.this.startActivity(intent);
                BaseApplication.size = tmpList.size();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                view1.showOrHideProgressBar(false);
                view2.showOrHideProgressBar(false);
//                Toast.makeText(LandscapeActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                view2.showNoInternetDialog();
                view1.showNoInternetDialog();
            }
        });
    }

    //本地缓存的集合，在异步中处理
    List<LocalBean2> dbList = null;
    boolean dataCacheOk = false;

    class MyAsyncTask extends AsyncTask<Integer, Integer, List<LocalBean2>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List doInBackground(Integer... params) {
            DbUtils db = DbUtils.create(LandscapeActivity.this);
            try {
                dbList = db.findAll(LocalBean2.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (dbList == null) {
                dbList = new ArrayList<>();
            }
            if (dbList != null) {
                System.out.println("---数据库原始尺寸：" + dbList.size());
            }
            String[] localNames = HotVedioCacheUtils.getVedioCache(BaseApplication.VedioCacheUrl);
            if (localNames != null) {
                int size = dbList.size();
                List<String> titles = new ArrayList<>();
                for (LocalBean2 localBean : dbList){
                    titles.add(localBean.getTitle());
                }
                for (int i = 0; i < localNames.length; i++) {
                    String title = localNames[i];
                    String title1 = title.replace(".mp4","");

                    if (size == 0) {
                        System.out.println("---title1"+title1);
                        LocalBean2 localBean = new LocalBean2();
                        localBean.setLocalurl(BaseApplication.VedioCacheUrl + localNames[i]);
                        localBean.setCurState(3);
                        SaveBitmapUtils.saveMyBitmap(title1, VedioBitmapUtils.getMiniVedioBitmap(BaseApplication.VedioCacheUrl + localNames[i]));
//                        System.out.println("---数据库没有数据。添加本地bitmap：" + VedioBitmapUtils.getMiniVedioBitmap(BaseApplication.VedioCacheUrl + localNames[i]));
                        localBean.setImage(BaseApplication.ImgCacheUrl + title1 + ".jpg");
                        localBean.setUrl("");
                        localBean.setId(BaseApplication.VedioCacheUrl + localNames[i]);
                        localBean.setTitle(title1);
                        dbList.add(localBean);
                        try {
                            db.saveOrUpdate(localBean);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (!titles.contains(title1)){
                            System.out.println("---不为空title1"+title1);
                            LocalBean2 localBean = new LocalBean2();
                            localBean.setLocalurl(BaseApplication.VedioCacheUrl + localNames[i]);
                            localBean.setCurState(3);
                            SaveBitmapUtils.saveMyBitmap(title.replace(".mp4", ""), VedioBitmapUtils.getMiniVedioBitmap(BaseApplication.VedioCacheUrl + localNames[i]));
                            localBean.setImage(BaseApplication.ImgCacheUrl + title1 + ".jpg");
                            localBean.setUrl("");
                            localBean.setTitle(title1);
                            localBean.setId(BaseApplication.VedioCacheUrl + localNames[i]);
                            dbList.add(localBean);
                            try {
                                db.saveOrUpdate(localBean);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            System.out.println("--"+title);
                        }
                    }
                }
            }
            System.out.println("----数据处理完毕");
            dataCacheOk = true;
            return dbList;
        }

        @Override
        protected void onPostExecute(List<LocalBean2> s) {
//            super.onPostExecute(s);
            System.out.println("----数据处理完毕");
            dataCacheOk = true;

        }
    }
}
