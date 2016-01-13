package com.hotcast.vr;

import android.content.Intent;
import android.media.ThumbnailUtils;
import android.net.Uri;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.bean.Classify;
import com.hotcast.vr.bean.VrPlay;
import com.hotcast.vr.image3D.Image3DSwitchView;
import com.hotcast.vr.imageView.Image3DView;
import com.hotcast.vr.pageview.VrListView;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.DensityUtils;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Utils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.io.Serializable;
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
    private Button bt_cancel_progressbar1, bt_cancel_progressbar2;
    private RelativeLayout rl_update1, rl_update2;
    UpdateAppManager updateAppManager;
    List<Classify> netClassifys;
    private HttpHandler httphandler;
    BitmapUtils bitmapUtils;

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
        bt_cancel_progressbar1 = (Button) view1.getRootView().findViewById(R.id.bt_cancel_progressbar);
        bt_cancel_progressbar1.setOnClickListener(this);
        bt_cancel_progressbar2 = (Button) view2.getRootView().findViewById(R.id.bt_cancel_progressbar);
        bt_cancel_progressbar2.setOnClickListener(this);
        rl_update1 = (RelativeLayout) view1.getRootView().findViewById(R.id.rl_update);
        rl_update2 = (RelativeLayout) view2.getRootView().findViewById(R.id.rl_update);
        img3D = (com.hotcast.vr.imageView.Image3DSwitchView) view1.getRootView().findViewById(R.id.id_sv);
        img3D2 = (com.hotcast.vr.imageView.Image3DSwitchView) view2.getRootView().findViewById(R.id.id_sv);
        for (int i = 0; i < netClassifys.size(); i++) {
            Image3DView image3DView = new Image3DView(this);
            bitmapUtils.display(image3DView, netClassifys.get(i).getImage());
            image3DView.setLayoutParams(params);
            final String channel_id = netClassifys.get(i).getChannel_id();
            image3DView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getNetData(channel_id);
                    System.out.println("***你点击了item，准备播放**");
                }
            });

            img3D.addView(image3DView);
        }
        Image3DView image3DView1 = new Image3DView(this);
        image3DView1.setImageResource(R.drawable.icon_4);
        image3DView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查询本地指定的缓存文件夹

            }
        });
        img3D.addView(image3DView1);
        for (int i = 0; i < netClassifys.size(); i++) {
            Image3DView image3DView = new Image3DView(this);
            bitmapUtils.display(image3DView, netClassifys.get(i).getImage());
            image3DView.setLayoutParams(params);
            final String channel_id = netClassifys.get(i).getChannel_id();
            image3DView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getNetData(channel_id);
                    System.out.println("***你点击了item，准备播放**");
                }
            });
            img3D2.addView(image3DView);
        }
        Image3DView image3DView2 = new Image3DView(this);
        image3DView2.setImageResource(R.drawable.icon_4);
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
        if (BaseApplication.isUpdate) {
            showUpdate();
        }
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
//                case STOP:

            }
        }
    };

    private void showUpdate() {
        if (force == 1) {
            updateAppManager = new UpdateAppManager(this, spec, force, newFeatures);
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
    private int force;
    //更新日志
    String newFeatures;

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("---netClassifys in on restart:" + netClassifys.size());
        BaseApplication.size = netClassifys.size()+1;
        System.out.println("---BaseApplication.size in on restart:" + BaseApplication.size);
    }

    @Override
    public void getIntentData(Intent intent) {
        spec = getIntent().getStringExtra("spec");

        force = getIntent().getIntExtra("force", 0);
        newFeatures = getIntent().getStringExtra("newFeatures");
        netClassifys = (List<Classify>) getIntent().getSerializableExtra("classifies");
        BaseApplication.size = netClassifys.size()+1;
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        getParent().requestDisallowInterceptTouchEvent(true);

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
                        updateAppManager = new UpdateAppManager(this, spec, force, newFeatures);
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

    List<VrPlay> vrPlays;

    public void getNetData(final String channel_id) {
        String mUlr = Constants.URL_VR_PLAY;
        System.out.println("***VrListActivity *** getNetData()" + mUlr);
        L.e("播放路径 mUrl=" + mUlr);
        RequestParams params = new RequestParams();
        System.out.println("***VrListActivity *** getNetData()" + params);
        params.addBodyParameter("token", "123");
        params.addBodyParameter("channel_id", channel_id);
        System.out.println("***VrListActivity *** getNetData()" + channel_id);
        this.httpPost(mUlr, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                System.out.println("***VrListActivity *** onStart()");
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                System.out.println("***VrListActivity *** onSuccess()" + responseInfo.result);
                if (Utils.textIsNull(responseInfo.result)) {
                    return;
                }
                vrPlays = new Gson().fromJson(responseInfo.result, new TypeToken<List<VrPlay>>() {
                }.getType());
                System.out.println("***VrListActivity *** onSuccess()" + vrPlays);
                System.out.println("***VrListActivity *** onSuccess()" + vrPlays.size());

                Intent intent = new Intent(LandscapeActivity.this, VrListActivity.class);
                intent.putExtra("channel_id", channel_id);
                intent.putExtra("vrPlays", (Serializable) vrPlays);
                System.out.println("跳转到VrListActivity vrPlays" + vrPlays);
                LandscapeActivity.this.startActivity(intent);
                BaseApplication.size = vrPlays.size();
//                for (int i = 0; i < vrPlays.size(); i++){
//                    vrPlayArrayList.add(vrPlays.get(i));
//                }
            }
            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }
}
