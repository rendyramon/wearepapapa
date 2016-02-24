package com.hotcast.vr;

import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.bean.ChannelList;
import com.hotcast.vr.bean.Details;
import com.hotcast.vr.bean.LocalBean;
import com.hotcast.vr.bean.Play;
import com.hotcast.vr.bean.VideosNew;
import com.hotcast.vr.imageView.Image3DSwitchView;
import com.hotcast.vr.imageView.Image3DView;
import com.hotcast.vr.pageview.VrListView;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Utils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by joey on 8/10/15.
 */
public class VrListActivity extends BaseActivity {
    @InjectView(R.id.container1)
    RelativeLayout container1;
    @InjectView(R.id.container2)
    RelativeLayout container2;
    private Button bt_up1, bt_down1, bt_up2, bt_down2;
    private VrListView view1, view2;
    private int type;
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> descs = new ArrayList<>();
    Spannable span;
    String page;
    boolean isSave;
    String play_url;
    String title;
    /**
     * 控件宽度
     */
    public static int mWidth;

    @Override
    public int getLayoutId() {
        return R.layout.activity_vr_list;
    }

    //    private int size;
    Image3DSwitchView img3D;
    Image3DSwitchView img3D2;
    TextView tv_page1;
    TextView tv_page2;
    TextView tv_title1;
    TextView tv_title2;
    TextView tv_desc1;
    TextView tv_desc2;
    TextView bt_ceach1;
    TextView bt_ceach2;
    int index = 1;
    List<LocalBean> dbList = null;
    List<String> localUrlList;
    DbUtils db;
    int mCurrentImg;
    LocalBean localBean;

    @Override
    public void init() {
        System.out.println("***VrListActivity *** init()" + channel_id);
        view1 = new VrListView(this);
        view2 = new VrListView(this);
        view1.getRootView().setBackgroundResource(R.mipmap.background_new);
        view2.getRootView().setBackgroundResource(R.mipmap.background_new);
        bt_up1 = (Button) view1.getRootView().findViewById(R.id.bt_up);
        bt_up2 = (Button) view2.getRootView().findViewById(R.id.bt_up);
        bt_up1.setOnClickListener(this);
        bt_up2.setOnClickListener(this);
        bt_down1 = (Button) view1.getRootView().findViewById(R.id.bt_down);
        bt_down2 = (Button) view2.getRootView().findViewById(R.id.bt_down);
        bt_down1.setOnClickListener(this);
        bt_down2.setOnClickListener(this);
        db = DbUtils.create(VrListActivity.this);
        localUrlList = new ArrayList<>();
        try {
            dbList = db.findAll(LocalBean.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (dbList == null) {
            dbList = new ArrayList<>();
        } else {
            for (LocalBean localBean : dbList) {
                System.out.println("---localBean_title:" + localBean.getTitle());
                localUrlList.add(localBean.getTitle());
            }
        }
        BitmapUtils bitmapUtils = new BitmapUtils(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        img3D = (Image3DSwitchView) view1.getRootView().findViewById(R.id.id_sv);
        img3D2 = (Image3DSwitchView) view2.getRootView().findViewById(R.id.id_sv);
        tv_page1 = (TextView) view1.getRootView().findViewById(R.id.tv_page);
        tv_page2 = (TextView) view2.getRootView().findViewById(R.id.tv_page);

        tv_title1 = (TextView) view1.getRootView().findViewById(R.id.tv_title);
        tv_title2 = (TextView) view2.getRootView().findViewById(R.id.tv_title);
        tv_desc1 = (TextView) view1.getRootView().findViewById(R.id.tv_desc);
        tv_desc2 = (TextView) view2.getRootView().findViewById(R.id.tv_desc);

        bt_ceach1 = (TextView) view1.getRootView().findViewById(R.id.bt_ceach);
        bt_ceach1.setOnClickListener(this);
        bt_ceach2 = (TextView) view2.getRootView().findViewById(R.id.bt_ceach);
        bt_ceach2.setOnClickListener(this);
//        homeRolls.addAll(homeRolls);
        mWidth = img3D.getMeasuredWidth();
        // 每张图片的宽度设定为控件宽度的百分之六十
        mImageWidth = (int) (mWidth * 0.6);

        System.out.println("****vrPlays = " + vrPlays);
        page = index + "/" + vrPlays.size();
        span = new SpannableString(page);
        span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_page1.setText(span);
        tv_page2.setText(span);
        System.out.println("*** VrListActivity***title = " + titles.get(index));
        System.out.println("*** VrListActivity***desc = " + descs.get(index));
        for (int i = 0; i < vrPlays.size(); i++) {
//            System.out.println(i+"---电影名称："+vrPlays.get(i).getTitle());
            Image3DView image3DView = new Image3DView(this);
            image3DView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            bitmapUtils.display(image3DView, vrPlays.get(i).getImage().get(0));
            image3DView.setLayoutParams(params);
//            final String url = vrPlays.get(i).getVideo_url();
            final String title = vrPlays.get(i).getTitle();
            //for jiuyou change
            final int finalI = i;
            final int finalI1 = i;
            image3DView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play(vrPlays.get(finalI1).getVideos().get(0).getVid());
                }
            });

            img3D.addView(image3DView);
        }
        for (int i = 0; i < vrPlays.size(); i++) {
            Image3DView image3DView = new Image3DView(this);
            image3DView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            bitmapUtils.display(image3DView, vrPlays.get(i).getImage().get(0));
            image3DView.setLayoutParams(params);
//            final String url = vrPlays.get(i).getVideo_url();
//            final String title = vrPlays.get(i).getTitle();
            final int finalI = i;
            image3DView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play(vrPlays.get(finalI).getVideos().get(0).getVid());
                    System.out.println("***你点击了item，准备播放**");
                }
            });

            img3D2.addView(image3DView);
        }
        mCurrentImg = img3D.getImgIndex() - 1;
        if (mCurrentImg < 0) {
            mCurrentImg = titles.size() - 1;
        }
        tv_title1.setText(titles.get(mCurrentImg));
        tv_title2.setText(titles.get(mCurrentImg));
        tv_desc1.setText(descs.get(mCurrentImg));
        tv_desc2.setText(descs.get(mCurrentImg));
        if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
//            System.out.println("--localUrlList = " + localUrlList);
            isSave = true;
            title = vrPlays.get(mCurrentImg).getVideos().get(0).getVname();
            play_url = BaseApplication.VedioCacheUrl+vrPlays.get(mCurrentImg).getVideos().get(0).getVname()+".mp4";
            System.out.println("--localUrl = " +BaseApplication.VedioCacheUrl+ vrPlays.get(mCurrentImg).getVideos().get(0).getVname()+".mp4");
            setDownloadText(true);
        }
        img3D.setOnMovechangeListener(new Image3DSwitchView.OnMovechangeListener() {
            @Override
            public void OnMovechange(int dix) {
                System.out.println("-----1執行");
                img3D2.scrollBy(dix, 0);
                img3D2.refreshImageShowing();
            }

            @Override
            public void Next() {
                System.out.println("-----2執行");
                img3D2.scrollToNext();
                ++index;
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = titles.size() - 1;
                }
                if (index <= vrPlays.size()) {
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        setDownloadText(true);
                    } else {
                        setDownloadText(false);
                    }
                } else {
                    index = 1;
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        setDownloadText(true);
                    } else {
                        setDownloadText(false);
                    }
                }
                System.out.println("-----mcurrentIndex1：" + img3D.getImgIndex());
            }

            @Override
            public void Previous() {
                System.out.println("-----3執行");
                img3D2.scrollToPrevious();
                --index;
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = titles.size() - 1;
                }
                if (index >= 0) {
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);

                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        setDownloadText(true);
                    } else {
                        setDownloadText(false);
                    }
                } else {
                    index = vrPlays.size();
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        setDownloadText(true);
                    } else {
                        setDownloadText(false);
                    }
                }
                System.out.println("-----mcurrentIndex1：" + img3D.getImgIndex());
            }

            @Override
            public void Back() {
                System.out.println("-----4執行");
                img3D2.scrollBack();
            }
        });
        img3D2.setOnMovechangeListener(new Image3DSwitchView.OnMovechangeListener() {
            @Override
            public void OnMovechange(int dix) {
                System.out.println("-----1執行");
                img3D.scrollBy(dix, 0);
                img3D.refreshImageShowing();

            }

            @Override
            public void Next() {
                System.out.println("-----2執行");
                img3D.scrollToNext();
                ++index;
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = titles.size() - 1;
                }
                if (index <= vrPlays.size()) {
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        setDownloadText(true);
                    } else {
                        setDownloadText(false);
                    }
                } else {
                    index = 1;
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        setDownloadText(true);
                    } else {
                        setDownloadText(false);
                    }
                }
                System.out.println("-----mcurrentIndex2：" + img3D2.getImgIndex());
            }

            @Override
            public void Previous() {
                System.out.println("-----3執行");
                img3D.scrollToPrevious();
                --index;
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = titles.size() - 1;
                }
                if (index >= 0) {
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        setDownloadText(true);
                    } else {
                        setDownloadText(false);
                    }
                } else {
                    index = vrPlays.size();
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        setDownloadText(true);
                    } else {
                        setDownloadText(false);
                    }
                }
                System.out.println("-----mcurrentIndex2：" + img3D2.getImgIndex());
            }

            @Override
            public void Back() {
                System.out.println("-----4執行");
                img3D.scrollBack();
            }
        });
        img3D.setOnImageSwitchListener(new Image3DSwitchView.OnImageSwitchListener() {
            @Override
            public void onImageSwitch(int currentImage) {
                System.out.println("---是否触发监听");
//                    img3D2.setCurrentImage(currentImage);
            }
        });
        img3D2.setOnImageSwitchListener(new Image3DSwitchView.OnImageSwitchListener() {
            @Override
            public void onImageSwitch(int currentImage) {
                System.out.println("---是否触发监听");
//                img3D.setCurrentImage(currentImage);
            }
        });

        container1.removeAllViews();
        container2.removeAllViews();
        container1.addView(view1.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container2.addView(view2.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onStart() {
//        getIntentData(getIntent());
        super.onStart();
    }

    String channel_id;
    List<ChannelList> vrPlays;
    final String START = "START";
    final String DOWNLOADING = "DOWNLOADING";
    final String FINISH = "FINISH";
    final String PAUSE = "PAUSE";
    //true表示已经点击了下载，正在请求网络url
    boolean doDownloadrequest = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ceach:
                if (!doDownloadrequest) {
                    mCurrentImg = img3D.getImgIndex() - 1;
                    if (mCurrentImg < 0) {
                        mCurrentImg = titles.size() - 1;
                    }
                    if (!localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        System.out.println("---点击了：" + vrPlays.get(mCurrentImg).getTitle() + "--url:" + vrPlays.get(mCurrentImg).getVideos().get(0).getVname());
                        doDownloadrequest = true;
                        setDownloadText(true);
                        getplayUrl(mCurrentImg, vrPlays.get(mCurrentImg).getVideos().get(0).getVid(), vrPlays.get(mCurrentImg).getVideos().get(0).getVname());
                    }
                    break;
                }
            case R.id.bt_up:
                view1.showOrHideProgressBar(true);
                view2.showOrHideProgressBar(true);
                BaseApplication.scapePage--;
                if (BaseApplication.scapePage > 0) {
                    getNetData(channel_id, BaseApplication.scapePage,0);
                }else{
                    view1.showOrHideProgressBar(false);
                    view2.showOrHideProgressBar(false);
                    view2.showNoInternetDialog("已经到第一页啦");
                    view1.showNoInternetDialog("已经到第一页啦");
                }
                break;
            case R.id.bt_down:
                view1.showOrHideProgressBar(true);
                view2.showOrHideProgressBar(true);
                BaseApplication.scapePage++;
                getNetData(channel_id, BaseApplication.scapePage,1);
                break;
        }
    }

    public void getplayUrl(final int mCurrent, String vid, final String name) {
        String mUrl = Constants.PLAY_URL;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", "123");
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
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Play play = new Gson().fromJson(responseInfo.result, Play.class);
                String play_url = null;

                if (!TextUtils.isEmpty(play.getSd_url())) {
                    play_url = play.getSd_url();
                } else if (!TextUtils.isEmpty(play.getHd_url())) {
                    play_url = play.getHd_url();
                } else if (!TextUtils.isEmpty(play.getUhd_url())) {
                    play_url = play.getUhd_url();
                }
                if (play_url == null || play_url == "") {
                    mCurrentImg = img3D.getImgIndex() - 1;
                    if (mCurrentImg < 0) {
                        mCurrentImg = titles.size() - 1;
                    }
                    if (mCurrentImg == mCurrent) {
                        setDownloadText(false);
                    }
                    view2.showNoInternetDialog("连接异常，" + name + "下载失败");
                    view1.showNoInternetDialog("连接异常，" + name + "下载失败");
                    doDownloadrequest = false;
                } else {
                    localUrlList.add(name);
                    downLoadMovie(mCurrent, name, play_url);
                    System.out.println("---play_url:" + play_url);
                    doDownloadrequest = false;
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("DetailActivity onFailure ");
                //下载失败
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = titles.size() - 1;
                }
                if (mCurrentImg == mCurrent) {
                    setDownloadText(false);
                }
                view2.showNoInternetDialog("连接异常，" + name + "下载失败");
                view1.showNoInternetDialog("连接异常，" + name + "下载失败");
                doDownloadrequest = false;
            }
        });


    }

    public void downLoadMovie(int mCurrentImg, String vname, String play_url) {
        LocalBean localBean = new LocalBean();
        localBean.setTitle(vname);
        localBean.setImage(vrPlays.get(mCurrentImg).getImage().get(0));
        localBean.setId(play_url);
        localBean.setUrl(play_url);
        localBean.setCurState(0);//還沒下載，準備下載
        try {
            db.delete(localBean);
            db.save(localBean);
        } catch (DbException e) {
            e.printStackTrace();
        }
        int i = BaseApplication.downLoadManager.addTask(play_url, play_url, vname + ".mp4", BaseApplication.VedioCacheUrl + vname + ".mp4");
        System.out.println("---加入任务返回值：" + i);
    }

//    List<VideosNew> videosNews = new ArrayList<>();

    @Override
    public void getIntentData(Intent intent) {
        channel_id = intent.getStringExtra("channel_id");
        vrPlays = (List<ChannelList>) getIntent().getSerializableExtra("vrPlays");
        for (int i = 0; i < vrPlays.size(); i++) {
            ChannelList vrPlay = vrPlays.get(i);
            titles.add(vrPlay.getTitle());
            descs.add(vrPlay.getDesc());
//            videosNews.add((VideosNew) vrPlay.getVideos());
        }
        System.out.println("*** VrListActivity ***vrPlays = " + vrPlays + "titles = " + titles + "descs = " + descs);
        System.out.println("-----titlessssss:" + titles.size());
        type = intent.getIntExtra("type", 0);
    }

    Intent intent;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println("---keyCode = " + keyCode);
        switch (keyCode) {

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (img3D.isFinishScroll() && img3D2.isFinishScroll()) {
                    ++index;
                    mCurrentImg = img3D.getImgIndex() - 1;
                    if (mCurrentImg < 0) {
                        mCurrentImg = titles.size() - 1;
                    }
                    if (index <= vrPlays.size()) {
                        page = index + "/" + vrPlays.size();
                        span = new SpannableString(page);
                        span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                                0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_page1.setText(span);
                        tv_page2.setText(span);
                        tv_title1.setText(titles.get(mCurrentImg));
                        tv_title2.setText(titles.get(mCurrentImg));
                        tv_desc1.setText(descs.get(mCurrentImg));
                        tv_desc2.setText(descs.get(mCurrentImg));
                        if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                            setDownloadText(true);
                        } else {
                            setDownloadText(false);
                        }
                    } else {
                        index = 1;
                        page = index + "/" + vrPlays.size();
                        span = new SpannableString(page);
                        span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                                0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_page1.setText(span);
                        tv_page2.setText(span);
                        tv_title1.setText(titles.get(mCurrentImg));
                        tv_title2.setText(titles.get(mCurrentImg));
                        tv_desc1.setText(descs.get(mCurrentImg));
                        tv_desc2.setText(descs.get(mCurrentImg));
                        if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                            setDownloadText(true);
                        } else {
                            setDownloadText(false);
                        }
                    }
                    img3D.scrollToNext();
                    img3D2.scrollToNext();
                    L.e("你点击了下一张");
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (img3D.isFinishScroll() && img3D2.isFinishScroll()) {
                    --index;
                    mCurrentImg = img3D.getImgIndex() - 1;
                    if (mCurrentImg < 0) {
                        mCurrentImg = titles.size() - 1;
                    }
                    if (index >= 0) {
                        page = index + "/" + vrPlays.size();
                        span = new SpannableString(page);
                        span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                                0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_page1.setText(span);
                        tv_page2.setText(span);
                        tv_title1.setText(titles.get(mCurrentImg));
                        tv_title2.setText(titles.get(mCurrentImg));
                        tv_desc1.setText(descs.get(mCurrentImg));
                        tv_desc2.setText(descs.get(mCurrentImg));
                        if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                            setDownloadText(true);
                        } else {
                            setDownloadText(false);
                        }
                    } else {
                        index = vrPlays.size();
                        page = index + "/" + vrPlays.size();
                        span = new SpannableString(page);
                        span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                                0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_page1.setText(span);
                        tv_page2.setText(span);
                        tv_title1.setText(titles.get(mCurrentImg));
                        tv_title2.setText(titles.get(mCurrentImg));
                        tv_desc1.setText(descs.get(mCurrentImg));
                        tv_desc2.setText(descs.get(mCurrentImg));
                        if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                            setDownloadText(true);
                        } else {
                            setDownloadText(false);
                        }
                    }
                    img3D.scrollToPrevious();
                    img3D2.scrollToPrevious();
                    L.e("你点击了上一张");
                }
                break;

            case KeyEvent.KEYCODE_ENTER:
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_BUTTON_A:
                for (int i = 0; i < vrPlays.size(); i++) {
                    if (i == index) {
//                        String url = getplayUrl(vrPlays.get(i).getVideos().get(0).getVid());
                        String title = vrPlays.get(i).getTitle();
                        play(vrPlays.get(i).getVideos().get(0).getVid());
                    }
                }
                L.e("你点击了进入播放页");
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_BUTTON_B:
                finish();
                break;
        }
        return true;
    }

    private void play(String vid) {
        intent = new Intent(VrListActivity.this, PlayerVRActivityNew.class);
        if (isSave){
            intent.putExtra("play_url", play_url);
            intent.putExtra("title", title);
        }else {
            intent.putExtra("vid", vid);
        }
//        intent.putExtra("title", title);
        intent.putExtra("splite_screen", true);
        VrListActivity.this.startActivity(intent);
        System.out.println("---你点击了item，准备播放**");
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
    public Image3DSwitchView.OnMovechangeListener changeLisener;


    int downX, downY;
    int upX, upY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        getParent().requestDisallowInterceptTouchEvent(true);
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                downX = (int) event.getX();
//                downY = (int) event.getY();
//                break;
//            case MotionEvent.ACTION_UP:
//                upX = (int) event.getX();
//                upY = (int) event.getY();
//                int xlen = Math.abs(downX - upX);
//                int ylen = Math.abs(downY - upY);
////        int length = (int) Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
//                if (ylen > 200) {
//                    if (downY < upY) {
//                        view1.showOrHideProgressBar(true);
//                        view2.showOrHideProgressBar(true);
//                        BaseApplication.scapePage--;
//                        System.out.println("---downY:"+downY);
//                        System.out.println("---upY:"+upY);
//                        if (BaseApplication.scapePage > 0) {
//                            getNetData(channel_id, BaseApplication.scapePage, 0);
//                        } else {
//                            view1.showOrHideProgressBar(false);
//                            view2.showOrHideProgressBar(false);
//                            view2.showNoInternetDialog("已经到第一页啦");
//                            view1.showNoInternetDialog("已经到第一页啦");
//                        }
//                    } else {
//                        view1.showOrHideProgressBar(true);
//                        view2.showOrHideProgressBar(true);
//                        BaseApplication.scapePage++;
//                        getNetData(channel_id, BaseApplication.scapePage, 1);
//                    }
//                    return true;
//                }
//                break;
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

    public void setDownloadText(boolean flag) {
        if (flag) {
            bt_ceach2.setText("已下载");
            bt_ceach1.setText("已下载");
            bt_ceach1.setTextColor(getResources().getColor(R.color.downloadtext2));
            bt_ceach2.setTextColor(getResources().getColor(R.color.downloadtext2));
        } else {
            bt_ceach2.setText("未下载");
            bt_ceach1.setText("未下载");
            bt_ceach1.setTextColor(getResources().getColor(R.color.downloadtext1));
            bt_ceach2.setTextColor(getResources().getColor(R.color.downloadtext1));
        }

    }

    public void addSelfToFive() {
        tmpList.addAll(tmpList);
        if (tmpList.size() < 6) {
            addSelfToFive();
        }
    }

    List<ChannelList> tmpList;

    public void getNetData(final String channel_id, int requestpage, final int upordown) {
        String mUlr = Constants.PROGRAM_LIST;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", "123");
        params.addBodyParameter("channel_id", channel_id);
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);

        params.addBodyParameter("page", requestpage + "");
        params.addBodyParameter("page_size", String.valueOf(8));
        this.httpPost(mUlr, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                view1.showOrHideProgressBar(false);
                view2.showOrHideProgressBar(false);
                System.out.println("---responseInfo.result" + responseInfo.result);
                if (responseInfo.result.length() < 5) {
                    view2.showNoInternetDialog("已经到最后一页啦");
                    view1.showNoInternetDialog("已经到最后一页啦");
                    return;
                }
                tmpList = new Gson().fromJson(responseInfo.result, new TypeToken<List<ChannelList>>() {
                }.getType());
                System.out.println("---tmpList.size()" + tmpList.size());
                if (tmpList.size() < 6) {
                    addSelfToFive();
                }
                Intent intent = new Intent(VrListActivity.this, VrListActivity.class);
                intent.putExtra("channel_id", channel_id);
                intent.putExtra("vrPlays", (Serializable) tmpList);
                System.out.println("跳转到VrListActivity vrPlays" + tmpList);
                VrListActivity.this.startActivity(intent);
                finish();
//                if (upordown == 1) {
//                    overridePendingTransition(R.anim.next_in, R.anim.next_out);
//                } else {
//                    overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
//                }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

//                initNewData(tmpList);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                view1.showOrHideProgressBar(false);
                view2.showOrHideProgressBar(false);
                view2.showNoInternetDialog();
                view1.showNoInternetDialog();
            }
        });
    }

    public void initNewData(List<ChannelList> tmpList) {
        vrPlays = tmpList;
        for (int i = 0; i < vrPlays.size(); i++) {
            ChannelList vrPlay = vrPlays.get(i);
            titles.add(vrPlay.getTitle());
            descs.add(vrPlay.getDesc());
        }
        System.out.println("*** VrListActivity ***vrPlays = " + vrPlays + "titles = " + titles + "descs = " + descs);
        System.out.println("-----titlessssss:" + titles.size());

        container1.removeAllViews();
        container2.removeAllViews();
        view1 = new VrListView(this);
        view2 = new VrListView(this);
        bt_up1 = (Button) view1.getRootView().findViewById(R.id.bt_up);
        bt_up2 = (Button) view2.getRootView().findViewById(R.id.bt_up);
        bt_up1.setOnClickListener(this);
        bt_up2.setOnClickListener(this);
        bt_down1 = (Button) view1.getRootView().findViewById(R.id.bt_down);
        bt_down2 = (Button) view2.getRootView().findViewById(R.id.bt_down);
        bt_down1.setOnClickListener(this);
        bt_down2.setOnClickListener(this);
        db = DbUtils.create(VrListActivity.this);
        localUrlList = new ArrayList<>();
        try {
            dbList = db.findAll(LocalBean.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (dbList == null) {
            dbList = new ArrayList<>();
        } else {
            for (LocalBean localBean : dbList) {
                System.out.println("---localBean_title:" + localBean.getTitle());
                localUrlList.add(localBean.getTitle());
            }
        }
        BitmapUtils bitmapUtils = new BitmapUtils(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        img3D = (Image3DSwitchView) view1.getRootView().findViewById(R.id.id_sv);
        img3D2 = (Image3DSwitchView) view2.getRootView().findViewById(R.id.id_sv);
        img3D.removeAllViews();
        img3D2.removeAllViews();
        tv_page1 = (TextView) view1.getRootView().findViewById(R.id.tv_page);
        tv_page2 = (TextView) view2.getRootView().findViewById(R.id.tv_page);

        tv_title1 = (TextView) view1.getRootView().findViewById(R.id.tv_title);
        tv_title2 = (TextView) view2.getRootView().findViewById(R.id.tv_title);
        tv_desc1 = (TextView) view1.getRootView().findViewById(R.id.tv_desc);
        tv_desc2 = (TextView) view2.getRootView().findViewById(R.id.tv_desc);

        bt_ceach1 = (TextView) view1.getRootView().findViewById(R.id.bt_ceach);
        bt_ceach1.setOnClickListener(this);
        bt_ceach2 = (TextView) view2.getRootView().findViewById(R.id.bt_ceach);
        bt_ceach2.setOnClickListener(this);
//        homeRolls.addAll(homeRolls);
        mWidth = img3D.getMeasuredWidth();
        // 每张图片的宽度设定为控件宽度的百分之六十
        mImageWidth = (int) (mWidth * 0.6);

        page = index + "/" + vrPlays.size();
        span = new SpannableString(page);
        span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_page1.setText(span);
        tv_page2.setText(span);
        System.out.println("*** VrListActivity***title = " + titles.get(index));
        System.out.println("*** VrListActivity***desc = " + descs.get(index));
        for (int i = 0; i < vrPlays.size(); i++) {
//            System.out.println(i+"---电影名称："+vrPlays.get(i).getTitle());
            Image3DView image3DView = new Image3DView(this);
            image3DView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            bitmapUtils.display(image3DView, vrPlays.get(i).getImage().get(0));
            image3DView.setLayoutParams(params);
//            final String url = vrPlays.get(i).getVideo_url();
            final String title = vrPlays.get(i).getTitle();
            //for jiuyou change
            final int finalI = i;
            final int finalI1 = i;
            image3DView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play(vrPlays.get(finalI1).getVideos().get(0).getVid());
                }
            });

            img3D.addView(image3DView);
        }
        for (int i = 0; i < vrPlays.size(); i++) {
            Image3DView image3DView = new Image3DView(this);
            image3DView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            bitmapUtils.display(image3DView, vrPlays.get(i).getImage().get(0));
            image3DView.setLayoutParams(params);
//            final String url = vrPlays.get(i).getVideo_url();
//            final String title = vrPlays.get(i).getTitle();
            final int finalI = i;
            image3DView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play(vrPlays.get(finalI).getVideos().get(0).getVid());
                    System.out.println("***你点击了item，准备播放**");
                }
            });

            img3D2.addView(image3DView);
        }

        mCurrentImg = img3D.getImgIndex() - 1;
        if (mCurrentImg < 0) {
            mCurrentImg = titles.size() - 1;
        }
        System.out.println("---mCurrentImg:" + mCurrentImg + "---" + vrPlays.size());
        tv_title1.setText(titles.get(mCurrentImg));
        tv_title2.setText(titles.get(mCurrentImg));
        tv_desc1.setText(descs.get(mCurrentImg));
        tv_desc2.setText(descs.get(mCurrentImg));
        if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
            setDownloadText(true);
        }
        setLisener();

        container1.removeAllViews();
        container2.removeAllViews();
        container1.addView(view1.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container2.addView(view2.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void setLisener() {
        img3D.setOnMovechangeListener(new Image3DSwitchView.OnMovechangeListener() {
            @Override
            public void OnMovechange(int dix) {
                System.out.println("-----1執行");
                img3D2.scrollBy(dix, 0);
                img3D2.refreshImageShowing();
            }

            @Override
            public void Next() {
                System.out.println("-----2執行");
                img3D2.scrollToNext();
                ++index;
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = titles.size() - 1;
                }
                if (index <= vrPlays.size()) {
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        setDownloadText(true);
                    } else {
                        setDownloadText(false);
                    }
                } else {
                    index = 1;
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        setDownloadText(true);
                    } else {
                        setDownloadText(false);
                    }
                }
                System.out.println("-----mcurrentIndex1：" + img3D.getImgIndex());
            }

            @Override
            public void Previous() {
                System.out.println("-----3執行");
                img3D2.scrollToPrevious();
                --index;
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = titles.size() - 1;
                }
                if (index >= 0) {
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);

                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        setDownloadText(true);
                    } else {
                        setDownloadText(false);
                    }
                } else {
                    index = vrPlays.size();
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        setDownloadText(true);
                    } else {
                        setDownloadText(false);
                    }
                }
                System.out.println("-----mcurrentIndex1：" + img3D.getImgIndex());
            }

            @Override
            public void Back() {
                System.out.println("-----4執行");
                img3D2.scrollBack();
            }
        });
        img3D2.setOnMovechangeListener(new Image3DSwitchView.OnMovechangeListener() {
            @Override
            public void OnMovechange(int dix) {
                System.out.println("-----1執行");
                img3D.scrollBy(dix, 0);
                img3D.refreshImageShowing();

            }

            @Override
            public void Next() {
                System.out.println("-----2執行");
                img3D.scrollToNext();
                ++index;
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = titles.size() - 1;
                }
                if (index <= vrPlays.size()) {
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        setDownloadText(true);
                    } else {
                        setDownloadText(false);
                    }
                } else {
                    index = 1;
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        setDownloadText(true);
                    } else {
                        setDownloadText(false);
                    }
                }
                System.out.println("-----mcurrentIndex2：" + img3D2.getImgIndex());
            }

            @Override
            public void Previous() {
                System.out.println("-----3執行");
                img3D.scrollToPrevious();
                --index;
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = titles.size() - 1;
                }
                if (index >= 0) {
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        setDownloadText(true);
                    } else {
                        setDownloadText(false);
                    }
                } else {
                    index = vrPlays.size();
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideos().get(0).getVname())) {
                        setDownloadText(true);
                    } else {
                        setDownloadText(false);
                    }
                }
                System.out.println("-----mcurrentIndex2：" + img3D2.getImgIndex());
            }

            @Override
            public void Back() {
                System.out.println("-----4執行");
                img3D.scrollBack();
            }
        });
    }
}
