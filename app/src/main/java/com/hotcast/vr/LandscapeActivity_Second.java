package com.hotcast.vr;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.VerticalGallery.ImageAdapter;
import com.hotcast.vr.VerticalGallery.VerticalGallery;
import com.hotcast.vr.VerticalGallery.VerticalGalleryAdapterView;
import com.hotcast.vr.bean.ChannelList;
import com.hotcast.vr.bean.ChannelLister;
import com.hotcast.vr.bean.LocalBean2;
import com.hotcast.vr.bean.Play;
import com.hotcast.vr.bean.PlayerBean;
import com.hotcast.vr.dialog.MyDialog;
import com.hotcast.vr.pageview.GalleryItemView;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.TokenUtils;
import com.hotcast.vr.tools.Utils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.util.ArrayList;
import java.util.List;

public class LandscapeActivity_Second extends BaseActivity {
    String channel_id;//当前频道的ID
    boolean isloading;
    boolean nodata = true;//是否有数据
    private List<String> titles = new ArrayList<>();
    private List<String> descs = new ArrayList<>();
    private VerticalGallery vg1;
    private VerticalGallery vg2;
    private int nowPosition;
    private int nowPage = 0;
    private GalleryItemView downitemView1;
    private GalleryItemView downitemView2;
    ImageAdapter adapter1;
    ImageAdapter adapter2;
    List<GalleryItemView> views1;
    List<GalleryItemView> views2;
    View nointernet1;
    View nointernet2;
    TextView tv_page1;
    TextView tv_page2;
    DbUtils db;
    List<String> localUrlList;
    List<LocalBean2> dbList;

    @Override

    public int getLayoutId() {
        return R.layout.activity_landscape_activity__second;
    }

    @Override
    public void init() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        views1 = new ArrayList<>();
        views2 = new ArrayList<>();
        db = DbUtils.create(this);
        System.out.println("---数据库版本：" + db.getDaoConfig().getDbVersion());
        localUrlList = new ArrayList<>();
        try {
            dbList = db.findAll(LocalBean2.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (dbList == null) {
            dbList = new ArrayList<>();
        } else {
            for (LocalBean2 localBean : dbList) {
                System.out.println("---localBean_url:" + localBean.getUrl());
                localUrlList.add(localBean.getVid());
            }
        }
        initView();
    }

    public void initView() {
        vg1 = (VerticalGallery) findViewById(R.id.vg1);
        vg2 = (VerticalGallery) findViewById(R.id.vg2);
        int size = 0;
        while (size < tmpList.size()) {
            if (size + 8 > tmpList.size()) {
                size = tmpList.size();
            } else {
                size = size + 8;
            }
            int start = 0;
            if (size - 8 < 0) {
                start = 0;
            } else {
                start = size - 8;
            }
            GalleryItemView itemView1 = new GalleryItemView(this, tmpList.subList(start, size), titles.subList(start, size), descs.subList(start, size), mhandler);
            GalleryItemView itemView2 = new GalleryItemView(this, tmpList.subList(start, size), titles.subList(start, size), descs.subList(start, size), mhandler);
            itemView1.setHandler(mhandler);
            itemView1.setLocalUrlList(localUrlList);
            itemView2.setHandler(mhandler);
            itemView2.setLocalUrlList(localUrlList);
            views1.add(itemView1);
            views2.add(itemView2);
        }
//        views2.add(downitemView2);
        adapter1 = new ImageAdapter(this, views1);
        adapter2 = new ImageAdapter(this, views2);
        vg1.setAdapter(adapter1);
        vg2.setAdapter(adapter2);
        vg1.setOnScrollStopListener(new VerticalGallery.OnScrollStopListener() {
            @Override
            public void onScrollStop(int p) {
                setText(nowPage);
            }
        });
        vg2.setOnScrollStopListener(new VerticalGallery.OnScrollStopListener() {
            @Override
            public void onScrollStop(int p) {

            }
        });

        tv_page1 = (TextView) findViewById(R.id.tv_page1);
        tv_page2 = (TextView) findViewById(R.id.tv_page2);

        nointernet1 = findViewById(R.id.nointernet1);
        nointernet2 = findViewById(R.id.nointernet2);
        vg1.setOnItemSelectedListener(new VerticalGalleryAdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(VerticalGalleryAdapterView<?> parent, View view, int position, long id) {
                nowPage = position;
            }

            @Override
            public void onNothingSelected(VerticalGalleryAdapterView<?> parent) {

            }
        });
    }

    //设置文字布局
    public void setText(int nowPage) {
        tv_page1.setText("第" + (nowPage + 1) + "页");
        tv_page2.setText("第" + (nowPage + 1) + "页");
    }

    /**
     * 改变影片的信息及状态
     */
    public void changeVideoInfo() {

    }

    @Override
    public void getIntentData(Intent intent) {
        tmpList = (List<ChannelList>) getIntent().getSerializableExtra("tmpList");
        System.out.println("---数据的尺寸：" + tmpList.size());
        channel_id = getIntent().getStringExtra("channel_id");
        if (tmpList.size() == 0) {
            nodata = false;
        } else {
            for (int i = 0; i < tmpList.size(); i++) {
                ChannelList vrPlay = tmpList.get(i);
                titles.add(vrPlay.getTitle());
                descs.add(vrPlay.getDesc());
            }
        }
    }

    int downX;
    int moveX;
    int upX;
    int downY;
    int moveY;
    int upY;

    boolean nOrp = false;//false 表示next

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = (int) event.getX();
                moveY = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                upX = (int) event.getX();
                upY = (int) event.getY();
                if (downY < upY) {
                    nOrp = true;
                } else {
                    nOrp = false;
                }
                break;
        }
        int xlen = Math.abs(downX - upX);
        int ylen = Math.abs(downY - upY);
        int length = (int) Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
        int lengthY = Math.abs(moveY - downY);
        int lengthX = Math.abs(moveX - downX);
        System.out.println("---length:" + length);
//        if (length < 20 && !isloading) {
//            //执行点击事件
//            clickItem();
//            System.out.println("---点击事件");
//            return true;
//        }
        if (lengthX > lengthY) {
            views1.get(nowPage).scrollmy(event);
            views2.get(nowPage).scrollmy(event);
        } else if (lengthX < lengthY && lengthY > 100) {
            vg1.scrollmy(event);
            vg2.scrollmy(event);
        }
        return true;
    }

    public void clickItem() {
        if (!isloading) {
            showOrHideLoadingBar(true);
            String url = views1.get(nowPage).getImgurl();
            //获取地址并下载
            getplayUrl(views1.get(nowPage).downVideoData(), url, true);
        }
    }

    public void clickItemDown() {
        if (!isloading) {
            if (localUrlList.contains(views1.get(nowPage).downVideoData())) {
                showNoDataDialog("已经下载该影片");
            } else {
                showOrHideLoadingBar(true);
                String url = views1.get(nowPage).getImgurl();
                //获取地址并下载
                getplayUrl(views1.get(nowPage).downVideoData(), url, false);
            }
        }
    }

    public void showOrHideLoadingBar(boolean flag) {
        views1.get(nowPage).showOrHideLoadingBar(flag);
        views2.get(nowPage).showOrHideLoadingBar(flag);
    }

    public void showNoInternetDialog(boolean flag) {
        views1.get(nowPage).showNoInternetDialog(flag);
        views2.get(nowPage).showNoInternetDialog(flag);
    }

    public void showNoDataDialog(String text) {
        views1.get(nowPage).showNoDataDialog(text);
        views2.get(nowPage).showNoDataDialog(text);
    }

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    showNoInternetDialog(false);
                    break;
                case 1:
                    break;
                case 2:
                    clickItemDown();
                    break;
                case 3:
                    clickItem();
                    break;
            }
        }
    };
    List<ChannelList> tmpList;
    public void getNetData(final String channel_id, int page, boolean nOrp) {
        final String mUlr = Constants.PROGRAM_LIST;
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
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showOrHideLoadingBar(false);
                System.out.println("***VrListActivity *** onSuccess()" + responseInfo.result);
                if (Utils.textIsNull(responseInfo.result) || responseInfo.result.length() < 5) {
//                    showNoDataDialog("已经是最后一页了");
                    System.out.println("---最后一页");
                    return;
                } else {
                    ChannelLister channelLister = new Gson().fromJson(responseInfo.result,ChannelLister.class);
                    if ("success".equals(channelLister.getMessage())||0 <= channelLister.getCode() && channelLister.getCode() <= 10){
                        tmpList =channelLister.getData();
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showOrHideLoadingBar(false);
                showNoInternetDialog(true);
            }
        });
    }

    //获取播放地址
    public void getplayUrl(final String vid, String imgurl, final boolean flag) {
        final String img = imgurl;
        final String id = vid;
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
                if (!TextUtils.isEmpty(responseInfo.result)){
                    PlayerBean playerBean = new Gson().fromJson(responseInfo.result,PlayerBean.class);
                    if ("success".equals(playerBean.getMessage())||0 <= playerBean.getCode() && playerBean.getCode() <= 10){
                        Play play =playerBean.getData();
                        play.setImgurl(img);
                        if (flag) {
                            String play_url = null;
                            if (!TextUtils.isEmpty(play.getSd_url())) {
                                BaseApplication.clarityText = "标清";
                                play_url = play.getSd_url();
                            } else if (!TextUtils.isEmpty(play.getHd_url())) {
                                BaseApplication.clarityText = "高清";
                                play_url = play.getHd_url();

                            } else if (!TextUtils.isEmpty(play.getUhd_url())) {
                                BaseApplication.clarityText = "超清";
                                play_url = play.getUhd_url();
                            }
                            Intent intent = new Intent(LandscapeActivity_Second.this, PlayerVRActivityNew2.class);
                            intent.putExtra("play_url", play_url);
                            intent.putExtra("title", play.getTitle());
                            intent.putExtra("splite_screen", true);
                            startActivity(intent);
                        } else {
                            String urls[] = new String[3];
                            urls[0] = play.getSd_url();
                            urls[1] = play.getHd_url();
                            urls[2] = play.getUhd_url();
                            if (!TextUtils.isEmpty(urls[0])) {
                                saveUrl = urls[0];
                            } else if (!TextUtils.isEmpty(urls[1])) {
                                System.out.println("---默认选择高清：" + urls[1]);
                                saveUrl = urls[1];
                            } else if (!TextUtils.isEmpty(urls[2])) {
                                saveUrl = urls[2];
                            } else {
                                return;
                            }
                            showDialog(urls, play, vid);
                        }
                        showOrHideLoadingBar(false);
                    }
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("DetailActivity onFailure ");
                //show出来，网络异常，下载失败
                showOrHideLoadingBar(false);
                showNoDataDialog("网络异常，下载失败");
            }
        });
    }

    private void initPlayUrl(String result) {

    }

    public void startDownLoad(String play_url, Play play, String vid, int qingxidu) {
        System.out.println("---play_url:" + play_url + "--qingxidu:" + qingxidu);
        DbUtils db = DbUtils.create(this);
        LocalBean2 localBean = new LocalBean2();
        localBean.setTitle(play.getTitle());
        localBean.setImage(play.getImgurl());
        localBean.setId(play_url);
        localBean.setVid(vid);
        localBean.setUrl(play_url);
        localBean.setQingxidu(qingxidu);
        localBean.setCurState(0);//還沒下載，準備下載
        try {
            db.delete(localBean);
            db.save(localBean);
        } catch (DbException e) {
            e.printStackTrace();
        }
        BaseApplication.downLoadManager.addTask(play_url, play_url, play.getTitle() + ".mp4", BaseApplication.VedioCacheUrl + play.getTitle() + ".mp4");
        System.out.println("---尺寸1：" + views1.get(nowPage).getLocalUrlList().size());
        localUrlList.add(vid);
        System.out.println("---尺寸2：" + views1.get(nowPage).getLocalUrlList().size());
        views1.get(nowPage).setText(views1.get(nowPage).getSelectedItemPosition());
        views2.get(nowPage).setText(views1.get(nowPage).getSelectedItemPosition());
    }

    String saveUrl;
    int qingxidu = 1;

    public void showDialog(final String urls[], final Play play, final String vid) {
        MyDialog.Builder builder = new MyDialog.Builder(this) {
            @Override
            public void setCarity1() {
                if (!TextUtils.isEmpty(urls[0])) {
                    saveUrl = urls[0];
                    qingxidu = 0;
                } else {
                    //将该button字体颜色设置为灰色并不可点击
                    showToast("没有标清连接");
                    System.out.println("---没有标清连接");
                }
            }

            @Override
            public void setCarity2() {
                if (!TextUtils.isEmpty(urls[1])) {
                    saveUrl = urls[1];
                    qingxidu = 1;
                } else {
                    //将该button字体颜色设置为灰色并不可点击
                    showToast("没有高清连接");
                    System.out.println("---没有高清连接");
                }
            }

            @Override
            public void setCarity3() {
                if (!TextUtils.isEmpty(urls[2])) {
                    saveUrl = urls[2];
                    qingxidu = 2;
                } else {
                    //将该button字体颜色设置为灰色并不可点击
                    showToast("没有超清连接");
                    System.out.println("---没有超清连接");
                }
            }
        };
        if (TextUtils.isEmpty(urls[0])) {
            System.out.println("---标清无");
            builder.setIsFocusable1(false);
        } else {
            System.out.println("---play.getSd_url() = " + urls[0]);
            builder.setIsFocusable1(true);
        }
        if (TextUtils.isEmpty(urls[1])) {
            System.out.println("---高清无");
            builder.setIsFocusable2(false);
        } else {
            System.out.println("---play.getHd_url() = " + urls[1]);
            builder.setIsFocusable2(true);
        }
        if (TextUtils.isEmpty(urls[2])) {
            System.out.println("---超清无");
            builder.setIsFocusable3(false);
        } else {
            System.out.println("---play.getUhd_url() = " + urls[2]);
            builder.setIsFocusable3(true);
        }
        builder.setTitle("  请 选 择 下 载 清 晰 度 : ");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("---您选择确定");
                startDownLoad(saveUrl, play, vid, qingxidu);
                dialog.dismiss();
                //设置你的操作事项
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("---您选择取消");
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println("---keyCode second=" + keyCode);
        switch (keyCode) {
            case 19:
                System.out.println("---上一页");
                if (vg1.getSelectedItemPosition() > 0) {
                    int next = vg1.getSelectedItemPosition() - 1;
                    vg1.setSelection(next, true);
                    vg2.setSelection(next, true);
                }
                break;
            case 20:
                System.out.println("---下一页");
                if (vg1.getSelectedItemPosition() < views1.size() - 1) {
                    System.out.println("---下一页");
                    int next = vg1.getSelectedItemPosition() + 1;
                    vg1.setSelection(next, true);
                    vg2.setSelection(next, true);
                }
                break;
            case 21:
                views1.get(nowPage).myKeyDown(keyCode, event);
                views2.get(nowPage).myKeyDown(keyCode, event);
                System.out.println("---上一个");
                break;
            case 22:
                views1.get(nowPage).myKeyDown(keyCode, event);
                views2.get(nowPage).myKeyDown(keyCode, event);
                System.out.println("---下一个");
                break;
            case 96:
            case 23:
                System.out.println("---点击播放");
                clickItem();
                break;
            case 97:
            case 4:
                finish();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
