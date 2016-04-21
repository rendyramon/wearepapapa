/*
 * SimpleStreamPlayer
 * Android example of Panframe library
 * The example plays back an panoramic movie from a resource.
 * 
 * (c) 2012-2013 Mindlight. All rights reserved.
 * Visit www.panframe.com for more information. 
 * 
 */

package com.hotcast.vr;

import java.util.*;


import com.google.gson.Gson;
import com.hotcast.vr.bean.LocalBean2;
import com.hotcast.vr.bean.Play;
import com.hotcast.vr.bean.PlayerBean;
import com.hotcast.vr.pageview.ChangeModeListener;
import com.hotcast.vr.pageview.PlayerContralView;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.SharedPreUtil;
import com.hotcast.vr.tools.TokenUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.panframe.android.lib.*;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.SeekBar.*;

import butterknife.InjectView;

public class PlayerVRActivityNew2 extends BaseLanActivity implements PFAssetObserver, OnSeekBarChangeListener {

    PFView _pfview;
    PFAsset _pfasset;
    PFNavigationMode _currentNavigationMode = PFNavigationMode.MOTION;
    private boolean ctr_vist = true;//控制条为显示状态
    boolean _updateThumb = true;
    Timer _scrubberMonitorTimer;

    @InjectView(R.id.framecontainer)
    ViewGroup _frameContainer;
    @InjectView(R.id.controller1)
    RelativeLayout controller1;
    @InjectView(R.id.controller2)
    RelativeLayout controller2;
    @InjectView(R.id.linCtr)
    View linCtr;
    @InjectView(R.id.rl_loadin2)
    RelativeLayout rl_loadin2;
    @InjectView(R.id.rl_loadin1)
    RelativeLayout rl_loadin1;
    @InjectView(R.id.tv_loadingmessage2)
    TextView tv_loadingmessage2;
    @InjectView(R.id.tv_loadingmessage1)
    TextView tv_loadingmessage1;
    @InjectView(R.id.ll_loading)
    LinearLayout ll_loading;

    private PlayerContralView mPlayerContralView1, mPlayerContralView2;
    private PlayerCtrMnger mPlayerContralView;
    private int totalDuration;
    public static final int MODE_SPLIT_SCREEN = 2;
    public static final int MODE_NORMAL = 0;
    private int curMode = MODE_SPLIT_SCREEN;
    private boolean isPause = false;

    private Handler mHideSystemUIHandler = new Handler() {

        public void handleMessage(Message message) {
            getWindow().getDecorView().setSystemUiVisibility(1542);
            if (null != playerContralView) {
//                playerContralView.hide();
                if (ctr_vist) {
                    ctr_vist = false;
                    linCtr.setVisibility(View.GONE);
                }
            }
        }
    };
    //    double nLenStart = 0;
    String play_url;
//    Play play;

    public void getplayUrl(String vid) {
        String mUrl = Constants.PLAY_URL;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(this));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("vid", vid);
        params.addBodyParameter("package", BaseApplication.packagename);
        params.addBodyParameter("app_version", BaseApplication.version);
        params.addBodyParameter("device", BaseApplication.device);
        this.httpPost(mUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                L.e("DetailActivity onStart ");
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.e("---DetailActivity responseInfo:" + responseInfo.result);
                initPlayUrl(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("DetailActivity onFailure ");
                finish();
            }
        });


    }

    private void initPlayUrl(String result) {
        if (!TextUtils.isEmpty(result)){
            PlayerBean playerBean = new Gson().fromJson(result,PlayerBean.class);
            if ("success".equals(playerBean.getMessage())||0 <= playerBean.getCode() && playerBean.getCode() <= 10){
                play = playerBean.getData();
                if (SharedPreUtil.getBooleanData(this,"islow",true)){
                    if (!TextUtils.isEmpty(play.getSd_url())) {
                        play_url = play.getSd_url();
                        BaseApplication.clarityText = "标清";
                    } else if (!TextUtils.isEmpty(play.getHd_url())) {
                        play_url = play.getHd_url();
                        BaseApplication.clarityText = "高清";
                    } else if (!TextUtils.isEmpty(play.getUhd_url())) {
                        play_url = play.getUhd_url();
                        BaseApplication.clarityText = "超清";
                    }
                    System.out.println("---play_url:" + play_url);
                    title = play.getTitle();
                    initView();
                }else {
                    if (!TextUtils.isEmpty(play.getHd_url())) {
                        play_url = play.getHd_url();
                        BaseApplication.clarityText = "高清";
                    } else if (!TextUtils.isEmpty(play.getSd_url())) {
                        play_url = play.getSd_url();
                        BaseApplication.clarityText = "标清";
                    } else if (!TextUtils.isEmpty(play.getUhd_url())) {
                        play_url = play.getUhd_url();
                        BaseApplication.clarityText = "超清";
                    }
                    System.out.println("---play_url:" + play_url);
                    title = play.getTitle();
                    initView();
                }

            }
        }
    }

    /**
     * Start the video with a local file path
     *
     * @param filename The file path on device storage
     */
    DbUtils db;
    List<LocalBean2> localBeans;
    ArrayList<String> urls = new ArrayList<>();

    public void loadVideo(String filename) {

        System.out.println("---播放页面接受到的url：" + filename);
        _pfview = PFObjectFactory.view(this);
        _pfview.setMode(curMode, 0);

        _pfasset = PFObjectFactory.assetFromUri(this, Uri.parse(filename), this);
        System.out.println("---_pfasset" + _pfasset);
        _pfview.displayAsset(_pfasset);
        _pfview.setNavigationMode(_currentNavigationMode);

        _frameContainer.addView(_pfview.getView(), 0);
        _pfasset.play();

    }

    /**
     * Status callback from the PFAsset instance.
     * Based on the status this function selects the appropriate action.
     *
     * @param asset  The asset who is calling the function
     * @param status The current status of the asset.
     */
    float oldTime = 0;
    long stime = 0;
    boolean isplaying = false;
    boolean iShowLoading = false;

    public void onStatusMessage(final PFAsset asset, PFAssetStatus status) {
        switch (status) {
            case LOADED:
                System.out.println("---118--LOADED ");
                Log.d("SimplePlayer", "Loaded");
//                final
                break;
            case DOWNLOADING:
                Log.d("SimplePlayer", "Downloading 360� movie: " + _pfasset.getDownloadProgress() + " percent complete");
                System.out.println("---123--Downloading 360� movie: " + _pfasset.getDownloadProgress() + " percent complete");

                break;
            case DOWNLOADED:
//                System.out.println("---126--Downloaded to " + asset.getUrl());
                Log.d("SimplePlayer", "Downloaded to " + asset.getUrl());
                break;
            case DOWNLOADCANCELLED:
//                System.out.println("---130--DOWNLOADCANCELLED");
                Log.d("SimplePlayer", "Download cancelled");
                break;
            case PLAYING:
//                hideLoading();
                hideLoadingView();
                iShowLoading = false;
                isplaying = true;
//                System.out.println("---135--结束加载的时间 = " + (System.currentTimeMillis() - startTime));
                Log.d("SimplePlayer", "Playing");
//		        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                initPlaying();
//				_scrubber.setEnabled(true);
//				_playButton.setText("pause");
                _scrubberMonitorTimer = new Timer();

                final TimerTask task = new TimerTask() {
                    public void iShowLoading() {
                        iShowLoading = false;
                    }

                    public void run() {
                        if (_updateThumb) {
                            int playbackTime = (int) asset.getPlaybackTime();
                            L.e("playbackTime = " + playbackTime);
                            if (totalDuration <= 0) {
                                mPlayerContralView.setMin(0);
                                totalDuration = (int) asset.getDuration();
                                mPlayerContralView.setMax(totalDuration);
                                mPlayerContralView.setTotalDuration(totalDuration);
                            }
                            mPlayerContralView.setCurTime((int) asset.getPlaybackTime());
                            if (!urls.contains(play_url)) {
                                if (asset.getPlaybackTime() == oldTime && !isPause) {
//                                    System.out.println("----12345");
                                    isplaying = false;

                                    if (!iShowLoading) {

                                        stime = System.currentTimeMillis();
//                                        System.out.println("----初始化时间+"+stime);
                                        iShowLoading = true;
                                    }
//                                    System.out.println("----时间差"+(System.currentTimeMillis() - stime));
                                    if (System.currentTimeMillis() - stime > 1200) {
                                        if (BaseApplication.clarityText.equals("标清")) {
//                                            showLoading("正在缓冲");
                                            showLoadingView("正在缓冲···");
                                        } else {
                                            Spannable span = new SpannableString("若时间过长请切换低清晰度");
                                            span.setSpan(new AbsoluteSizeSpan(20), 0, span.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

//                                            showLoading("正在缓冲\n" + span);
                                            showLoadingView("正在缓冲...\n" + span);
                                        }
                                        if (System.currentTimeMillis() - loadingTime > 30000l) {
                                            //表示加载超时
//                                            System.out.println("---缓冲超时，切换低清晰度电影");
                                        }
                                    }
//                                System.out.println("----显示loading");
                                } else if (oldTime > asset.getPlaybackTime()) {
                                    oldTime = asset.getPlaybackTime();
                                    isplaying = true;
//                                    hideLoading();
                                    hideLoadingView();
                                    iShowLoading();
//                                System.out.println("----隐藏loading1");
                                } else if (oldTime < asset.getPlaybackTime()) {
                                    oldTime = asset.getPlaybackTime();
                                    isplaying = true;
//                                    hideLoading();
                                    hideLoadingView();
                                    iShowLoading();
//                                System.out.println("----隐藏loading2");
                                } else {
                                    isplaying = true;
//                                    hideLoading();
                                    hideLoadingView();
                                    iShowLoading();
//                                System.out.println("----隐藏loading3");
                                }
                            }
                            L.e("asset.getPlaybackTime() = " + asset.getPlaybackTime());
//                            if (asset.getPlaybackTime() == asset.getPlaybackTime() && !isPause){
//                                showLoading("正在缓冲");
//
//                            }else {
//                                hideLoading();
////                                System.out.println("---154--CuurTime = "+asset.getPlaybackTime());
//                            }
//
                        }
                    }
                };

                _scrubberMonitorTimer.schedule(task, 0, 33);
                break;
            case PAUSED:
                Log.d("SimplePlayer", "Paused");
                System.out.println("---162--PAUSED");
//				_playButton.setText("play");
                break;
            case STOPPED:
                Log.d("SimplePlayer", "Stopped");
                System.out.println("---167--STOPPED");
//				_playButton.setText("play");
                _scrubberMonitorTimer.cancel();
                _scrubberMonitorTimer = null;
//				_scrubber.setProgress(0);
//				_scrubber.setEnabled(false);
//		        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
            case COMPLETE:
                Log.d("SimplePlayer", "Complete");
//				_playButton.setText("play");
                if (null != _scrubberMonitorTimer) {
                    _scrubberMonitorTimer.cancel();
                    _scrubberMonitorTimer = null;
                }
                BaseApplication.playbacktime = 0;
                finish();
//		        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
            case ERROR:
                Log.d("SimplePlayer", "Error");
                break;
        }
    }

    public void setPlayerContralView(View playerContralView) {
        this.playerContralView = playerContralView;
    }

    private View playerContralView;

    private void initPlaying() {
        System.out.println("---194--initplaying()");
        setPlayerContralView(linCtr);
        mPlayerContralView.setIsPlaying(true);
        ctr_vist = false;
        linCtr.setVisibility(View.GONE);
        mPlayerContralView.setStatusPlay();
        _pfview.setFieldOfView(75);
        _pfasset.setPLaybackTime(BaseApplication.playbacktime);
        System.out.println("---播放时间 = " + BaseApplication.playbacktime);
        mPlayerContralView.setmPlayerContrallerInterface(new PlayerContrallerInterface() {
            @Override
            public void play() {
                if (null != _pfasset) {
                    _pfasset.play();
                    System.out.println("---205--开始播放了");
                    isPause = false;
                }
            }

            @Override
            public void pause() {
                if (null != _pfasset) {
                    _pfasset.pause();
                    isPause = true;
                    BaseApplication.playbacktime = _pfasset.getPlaybackTime();
                    System.out.println("---213--暂停播放了");
                }
            }

            @Override
            public void seekTo(int time) {
                if (null != _pfasset) {
//					_pfasset.seekTo(time * 1000);
                    _pfasset.setPLaybackTime(time);
                    System.out.println("---222--seekTo time = " + time);
                }
            }
        });
    }

    /**
     * Click listener for the play/pause button
     */
    private OnClickListener playListener = new OnClickListener() {
        public void onClick(View v) {
            if (_pfasset.getStatus() == PFAssetStatus.PLAYING) {
                _pfasset.pause();
                System.out.println("---235--暂停播放了");
            } else
                _pfasset.play();
            System.out.println("---238--开始播放了");
        }
    };


    /**
     * Called when pausing the app.
     * This function pauses the playback of the asset when it is playing.
     */
    public void onPause() {
        super.onPause();
        if (_pfasset != null) {
            if (_pfasset.getStatus() == PFAssetStatus.PLAYING)
                _pfasset.pause();
            System.out.println("---253--暂停播放了");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != _pfasset) {
            _pfasset.stop();
            _pfasset.release();
            _pfasset = null;
            _pfview.release();
            System.out.println("---265PlayerVRActivity***onDestroy()");
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_player_360;
    }

    private String title;
    private String titleSplitScreen;
    private AudioManager audioManager; //音频
    long startTime = 0;
    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ll_loading.setVisibility(View.VISIBLE);
                    if (curMode == MODE_NORMAL) {
                        rl_loadin1.setVisibility(View.VISIBLE);
                        rl_loadin2.setVisibility(View.GONE);
                    } else {
                        rl_loadin1.setVisibility(View.VISIBLE);
                        rl_loadin2.setVisibility(View.VISIBLE);
                    }
                    tv_loadingmessage1.setText((String) msg.obj);
                    tv_loadingmessage2.setText((String) msg.obj);
                    break;
                case 2:
                    ll_loading.setVisibility(View.GONE);
                    break;
            }
        }
    };
    String message;

    public void showLoadingView(String message) {
        if (!isShowing) {
            this.message = message;
            Message msg = Message.obtain();
            msg.what = 1;
            msg.obj = message;
            mhandler.sendMessage(msg);
            isShowing = true;
        } else if (!message.equals(this.message)) {
            this.message = message;
            System.out.println("---message2" + message);
            Message msg = Message.obtain();
            msg.what = 1;
            msg.obj = message;
            mhandler.sendMessage(msg);
            isShowing = true;
        }
    }

    public void hideLoadingView() {
        if (isShowing) {
            Message msg = Message.obtain();
            msg.what = 2;
            mhandler.sendMessage(msg);
            isShowing = false;
        }
    }

    @Override
    public void init() {
        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        startTime = System.currentTimeMillis();
        System.out.println("---104--开始加载的时间 = " + System.currentTimeMillis());
        showLoadingView("正在加载···");
        System.out.println("---" + vid + "---");
        if (vid == null || TextUtils.isEmpty(vid)) {
            initView();
        } else {
            System.out.println("---是网络数据播放");
            getplayUrl(vid);
        }

    }

    private void initView() {
        db = DbUtils.create(this);
        try {
            localBeans = db.findAll(LocalBean2.class);
            if (localBeans != null) {
                System.out.println("---localBeans = " + localBeans);
                for (int i = 0; i < localBeans.size(); i++) {
                    urls.add(localBeans.get(i).getLocalurl());
                }
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

        mPlayerContralView1 = new PlayerContralView(this, PlayerContralView.TYPE_360, BaseApplication.clarityText);
        mPlayerContralView2 = new PlayerContralView(this, PlayerContralView.TYPE_360, BaseApplication.clarityText);
        mPlayerContralView = new PlayerCtrMnger(mPlayerContralView1, mPlayerContralView2);
        if (play != null) {
            if (!SharedPreUtil.getBooleanData(this, "islow", true)){
                if (!TextUtils.isEmpty(play.getSd_url())) {
                    mPlayerContralView1.setCanclick1(true);
                    mPlayerContralView2.setCanclick1(true);
                }
                if (!TextUtils.isEmpty(play.getHd_url())) {
                    mPlayerContralView1.setCanclick2(true);
                    mPlayerContralView2.setCanclick2(true);
                }
                if (!TextUtils.isEmpty(play.getUhd_url())) {
                    mPlayerContralView1.setCanclick3(true);
                    mPlayerContralView2.setCanclick3(true);
                }
            }else {
                if (!TextUtils.isEmpty(play.getSd_url())) {
                    mPlayerContralView1.setCanclick1(true);
                    mPlayerContralView2.setCanclick1(true);
                }else {
                    if (!TextUtils.isEmpty(play.getHd_url())) {
                        mPlayerContralView1.setCanclick2(true);
                        mPlayerContralView2.setCanclick2(true);
                    }
                }

//                if (!TextUtils.isEmpty(play.getUhd_url())) {
//                    mPlayerContralView1.setCanclick3(true);
//                    mPlayerContralView2.setCanclick3(true);
//                }
            }

        } else {
            mPlayerContralView1.setCanclick(false);
            mPlayerContralView2.setCanclick(false);
        }
        setPlayerContralView(linCtr);
        System.out.println("***PlaylerVRActivity***setPlayerContralView()");
        controller1.addView(mPlayerContralView1.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        controller2.addView(mPlayerContralView2.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (curMode == MODE_NORMAL) {
            mPlayerContralView1.setTitle(title);
            mPlayerContralView2.setTitle(title);
            mPlayerContralView1.setSplitScreen(true);
            mPlayerContralView2.setSplitScreen(true);
            controller2.setVisibility(View.GONE);
        } else {
            if(!TextUtils.isEmpty(title)){
                if (title.length() > 4) {
                    titleSplitScreen = title.substring(0, 4);
                    System.out.println("---length = " + titleSplitScreen.length());
                } else {
                    titleSplitScreen = title;
                }
            }
            mPlayerContralView1.setSplitScreen(false);
            mPlayerContralView2.setSplitScreen(false);
            controller2.setVisibility(View.VISIBLE);
            mPlayerContralView1.setTitle(titleSplitScreen);
            mPlayerContralView2.setTitle(titleSplitScreen);
        }
        mPlayerContralView.setChangeMode(new ChangeModeListener() {
            @Override
            public void clickTouchMode() {
                System.out.println("***PlayerVRActivity***clickTouchMode()");
                if (_currentNavigationMode == PFNavigationMode.TOUCH) {
                    mPlayerContralView1.setTouchMode(true);
                    mPlayerContralView2.setTouchMode(true);
                    _currentNavigationMode = PFNavigationMode.MOTION;
                } else {
                    mPlayerContralView1.setTouchMode(false);
                    mPlayerContralView2.setTouchMode(false);
                    _currentNavigationMode = PFNavigationMode.TOUCH;
                }
                _pfview.setNavigationMode(_currentNavigationMode);
            }

            @Override
            public void clickSplitScreen() {

                if (curMode == MODE_NORMAL) {
                    if (title.length() > 4) {
                        titleSplitScreen = title.substring(0, 4);
                        System.out.println("---length = " + titleSplitScreen.length());
                    } else {
                        titleSplitScreen = title;
                    }
                    System.out.println("111***PlayerVRActivity***clickSplitScreen()");
                    mPlayerContralView1.setTitle(titleSplitScreen);
                    mPlayerContralView2.setTitle(titleSplitScreen);
                    curMode = MODE_SPLIT_SCREEN;
                    mPlayerContralView1.setSplitScreen(false);
                    mPlayerContralView2.setSplitScreen(false);
                    controller2.setVisibility(View.VISIBLE);
                } else {
                    mPlayerContralView1.setSplitScreen(true);
                    controller2.setVisibility(View.GONE);
                    curMode = MODE_NORMAL;
                    mPlayerContralView1.setTitle(title);
                    mPlayerContralView2.setTitle(title);
                    System.out.println("222***PlayerVRActivity***clickSplitScreen()");

                }
                linCtr.setVisibility(View.VISIBLE);
                _pfview.setMode(curMode, 0);

            }
//            int paybackTime;

            @Override
            public void clickChoiceClarity() {

                showLinCtr();
            }

            @Override
            public void clickSd() {
                showLinCtr();
                BaseApplication.playbacktime = _pfasset.getPlaybackTime();
                System.out.println("---播放到-->" + BaseApplication.playbacktime);
                play_url = play.getSd_url();
                if (!TextUtils.isEmpty(play_url)) {
                    BaseApplication.clarityText = "标清";
                    Intent intent = new Intent(PlayerVRActivityNew2.this, PlayerVRActivityNew2.class);
                    intent.putExtra("play_url", play_url);
                    intent.putExtra("play", play);
                    intent.putExtra("title", title);
                    intent.putExtra("qingxidu", 0);
                    intent.putExtra("splite_screen", false);
                    startActivity(intent);
//                    _pfasset.setPLaybackTime(BaseApplication.playbacktime);
                    System.out.println("---切换清晰度为标清 url = " + play_url + " --time = " + BaseApplication.playbacktime + "清晰度：" + BaseApplication.clarityText);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

//                    BaseApplication.playbacktime = 0;
//                    _pfasset.setPLaybackTime(BaseApplication.playbacktime);
                }
            }

            @Override
            public void clickHd() {
                showLinCtr();
                BaseApplication.playbacktime = _pfasset.getPlaybackTime();
                System.out.println("---播放到-->" + BaseApplication.playbacktime);
                play_url = play.getHd_url();
                if (!TextUtils.isEmpty(play_url)) {
                    BaseApplication.clarityText = "高清";
                    Intent intent = new Intent(PlayerVRActivityNew2.this, PlayerVRActivityNew2.class);
                    intent.putExtra("play_url", play_url);
                    intent.putExtra("play", play);
                    intent.putExtra("title", title);
                    intent.putExtra("qingxidu", 1);
                    intent.putExtra("splite_screen", false);
                    startActivity(intent);
//                    _pfasset.setPLaybackTime(BaseApplication.playbacktime);
                    System.out.println("---切换清晰度为高清 url = " + play_url + " --time = " + BaseApplication.playbacktime + "清晰度：" + BaseApplication.clarityText);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

//                    _pfasset.setPLaybackTime(BaseApplication.playbacktime);
//                    BaseApplication.playbacktime = 0;
                }
            }

            @Override
            public void clickUhd() {
                showLinCtr();
                BaseApplication.playbacktime = _pfasset.getPlaybackTime();
                System.out.println("---播放到-->" + BaseApplication.playbacktime);
                play_url = play.getUhd_url();
                if (!TextUtils.isEmpty(play_url)) {
                    BaseApplication.clarityText = "超清";
                    Intent intent = new Intent(PlayerVRActivityNew2.this, PlayerVRActivityNew2.class);
                    intent.putExtra("play_url", play_url);
                    intent.putExtra("play", play);
                    intent.putExtra("title", title);
                    intent.putExtra("qingxidu", 2);
                    intent.putExtra("splite_screen", false);
                    startActivity(intent);
//                    _pfasset.setPLaybackTime(BaseApplication.playbacktime);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    System.out.println("---切换清晰度为超清 url = " + play_url + " --time = " + BaseApplication.playbacktime + "清晰度：" + BaseApplication.clarityText);
//                    _pfasset.setPLaybackTime(BaseApplication.playbacktime);
//                    BaseApplication.playbacktime = 0;
                }
            }
        });
        if (getIntent().getData() == null) {
            loadVideo(play_url);
            System.out.println("---337--PlayerVRActivity***filename " + getIntent().getStringExtra("play_url"));
        } else {
            loadVideo(getIntent().getData().getPath());
            System.out.println("---340--PlayerVRActivity***filename " + getIntent().getData().getPath());
        }
        mPlayerContralView1.setTitle(title);
        mPlayerContralView2.setTitle(title);
//        System.out.println("--titleLength = " + title.length());

    }

    String vid;
    Play play;
    int qingxidu;

    @Override
    public void getIntentData(Intent intent) {
        vid = intent.getStringExtra("vid");
        title = getIntent().getStringExtra("title");
        play_url = getIntent().getStringExtra("play_url");
        play = (Play) getIntent().getSerializableExtra("play");

        qingxidu = getIntent().getIntExtra("qingxidu", 1);
        switch (qingxidu) {
            case 0:
                BaseApplication.clarityText = "标清";
                break;
            case 1:
                BaseApplication.clarityText = "高清";
                break;
            case 2:
                BaseApplication.clarityText = "超清";
                break;
        }
        boolean b = intent.getBooleanExtra("splite_screen", false);
        if (b) {
            curMode = MODE_SPLIT_SCREEN;
        } else {
            curMode = MODE_NORMAL;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        _pfasset.stop();
        this.showDialog("提示：", "是否退出播放？", "确定", "取消", new OnAlertSureClickListener() {
            @Override
            public void onclick() {
                _pfview.release();
                finish();
            }
        });

    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     *
     * @param seekbar  The SeekBar whose progress has changed
     * @param progress The current progress level.
     * @param fromUser True if the progress change was initiated by the user.
     */
    public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
    }

    /**
     * Notification that the user has started a touch gesture.
     * In this function we signal the timer not to update the playback thumb while we are adjusting it.
     *
     * @param seekbar The SeekBar in which the touch gesture began
     */
    public void onStartTrackingTouch(SeekBar seekbar) {
        System.out.println("***PlayerVRActivity*** onStartTrackingTouch()");
        _updateThumb = false;
    }

    /**
     * Notification that the user has finished a touch gesture.
     * In this function we request the asset to seek until a specific time and signal the timer to resume the update of the playback thumb based on playback.
     *
     * @param seekbar The SeekBar in which the touch gesture began
     */
    public void onStopTrackingTouch(SeekBar seekbar) {
        System.out.println("***PlayerVRActivity*** onStopTrackingTouch()");
        _updateThumb = true;
    }

    double nLenStart = 0;
    //    当前视距值
    float fov = 75;
    //    视距缩放值
    float bfov = 0;
    double lastTime = 0;


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            System.out.println("--双手指不拦截");
            int nCnt = event.getPointerCount();
            System.out.println("----事件响应");

            int n = event.getAction();
            if (n == MotionEvent.ACTION_MOVE) {
                System.out.println("----移动");
            }
            if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN && 2 == nCnt) {
                System.out.println("----双手指Down");

                int xlen = Math.abs((int) event.getX(0) - (int) event.getX(1));
                int ylen = Math.abs((int) event.getY(0) - (int) event.getY(1));

                nLenStart = Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
                lastTime = 0;

            } else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE && 2 == nCnt) {
                System.out.println("----双手指移动");

                int xlen = Math.abs((int) event.getX(0) - (int) event.getX(1));
                int ylen = Math.abs((int) event.getY(0) - (int) event.getY(1));

                double nLenEnd = Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
                bfov = (float) (nLenEnd - nLenStart);
                if (lastTime == 0 || Math.abs((int) nLenEnd - lastTime) > 2) {
                    lastTime = nLenEnd;
                    if (nLenEnd > nLenStart)//通过两个手指开始距离和结束距离，来判断放大缩小
                    {
                        float in = fov - bfov / 180;
                        if (in > 30) {
                            _pfview.setFieldOfView(in);
                            fov = in;
                        } else {
                            _pfview.setFieldOfView(30);
                            fov = 30;
                        }
                    } else {
                        float in = fov - bfov / 120;
                        if (in < 150) {
                            _pfview.setFieldOfView(in);
                            fov = in;
                        } else {
                            _pfview.setFieldOfView(150);
                            fov = 150;
                        }
                    }
                }

            } else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP && 2 == nCnt) {

                int xlen = Math.abs((int) event.getX(0) - (int) event.getX(1));
                int ylen = Math.abs((int) event.getY(0) - (int) event.getY(1));

                double nLenEnd = Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
                System.out.println("----差值---" + (nLenEnd - nLenStart));

            }
            return true;
        } else {
            System.out.println("单手指触碰");
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    showLinCtr();
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }

    private void showLinCtr() {
        if (linCtr != null) {
            if (!ctr_vist) {
                ctr_vist = true;
                linCtr.setVisibility(View.VISIBLE);
                System.out.println("---显示进度条1");
                delayedHide(4000);
            } else {
                ctr_vist = false;
                linCtr.setVisibility(View.GONE);
                System.out.println("---隐藏进度条1");
            }
        }
    }

    private void delayedHide(int i) {
        mHideSystemUIHandler.removeMessages(0);
        mHideSystemUIHandler.sendEmptyMessageDelayed(0, i);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        L.e("---keycode = " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_SHOW_UI);//调低声音
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_SHOW_UI);
                break;
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_BUTTON_B:
//                _pfasset.stop();
//                finish();
                System.out.println("---返回键");
                this.showDialog("提示：", "是否退出播放？", "确定", "取消", new OnAlertSureClickListener() {
                    @Override
                    public void onclick() {
//                                _pfview.release();
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_BUTTON_A:
                if (_pfasset != null) {
                    if (_pfasset.getStatus() == PFAssetStatus.PLAYING) {
                        _pfasset.pause();
                        this.showToast("暂停");
                        L.e("---你点击了暂停");
                    } else {
                        _pfasset.play();
                        this.showToast("播放");
                        L.e("---你点击了播放");
                    }
                }

                break;
        }
        return true;
    }

}
