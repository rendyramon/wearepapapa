package com.hotcast.vr;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.bean.Details;
import com.hotcast.vr.bean.LocalBean;
import com.hotcast.vr.bean.Play;
import com.hotcast.vr.bean.Relation;
import com.hotcast.vr.bean.Urls;
import com.hotcast.vr.bean.Videos;
import com.hotcast.vr.bean.VideosNew;
import com.hotcast.vr.receiver.DownloadReceiver;
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
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by lostnote on 15/11/28.
 */
public class DetailActivity extends BaseActivity {
    @InjectView(R.id.moviename)
    TextView movie_name;
    @InjectView(R.id.tv_datetime)
    TextView tv_datetime;
    @InjectView(R.id.tv_movietime)
    TextView movietime;
    @InjectView(R.id.movie_introduced)
    TextView introduced;
    @InjectView(R.id.tv_cache)
    TextView tv_cache;
    @InjectView(R.id.translucentview)
    View translucentview;
    @InjectView(R.id.rl_movieimg)
    RelativeLayout rl_movieimg;
    @InjectView(R.id.ll_correlation)
    LinearLayout ll_correlation;
    @InjectView(R.id.ll_download)
    LinearLayout ll_download;
    @InjectView(R.id.progressBar5)
    ProgressBar progressBar5;

//    private Play play;
    private Details details;
    //    可以播放的URL集合大小
    private int size;
    private BitmapUtils bitmapUtils;
    //    请求播放的URL
    private String mUrl;
    //    播放的URL
    private List<Urls> playUrl = new ArrayList<>();
    //    视频id集合
    private List<String> video_ids = new ArrayList<>();
    //    播放视频的标题
//    private List<String> playTitle = new ArrayList<>();


    final String START = "START";
    final String DOWNLOADING = "DOWNLOADING";
    final String FINISH = "FINISH";
    final String PAUSE = "PAUSE";

    ViewHolder holder;
    Intent intent;

    @OnClick({R.id.back, R.id.play, R.id.ll_share})
    void clickType(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.play:
                L.e("你点击了播放的按钮");
//                if (!TextUtils.isEmpty(play_url)) {
                intent = new Intent(DetailActivity.this, PlayerVRActivityNew.class);
                intent.putExtra("play_url", play_url);
                intent.putExtra("title", title);
                intent.putExtra("splite_screen", false);
                DetailActivity.this.startActivity(intent);
                break;
            case R.id.ll_share:
//                TODO 弹出一个框放第三方的图标
                break;
        }
    }

    class ViewHolder {
        Button bt_number;
    }

    String play_url;
    String title;
    String media_id;
    Play play;
    public void getplayUrl() {
        mUrl = Constants.PLAY_URL;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", "123");
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("vid", details.getVideos().get(0).getVid());
        params.addBodyParameter("package", BaseApplication.packagename);
        params.addBodyParameter("app_version", BaseApplication.version);
        params.addBodyParameter("device", BaseApplication.device);

//        if (video_ids.size() > 0) {
//            media_id = video_ids.get(0);
//
//            L.e("DetailActivity media_id = " + video_ids.get(0));
//        } else {
//            finish();
//        }
        this.httpPost(mUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();

                L.e("DetailActivity onStart ");
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.e("---DetailActivity responseInfo:" + responseInfo.result);

//                               List<Play> playUrls = new Gson().fromJson(responseInfo.result, new TypeToken<List<Play>>() {
//                }.getType());
//                size = playUrls.size();
//                L.e("DetailActivity playUrls:" + playUrls + "***size = " + size);


//                for (int i = 0; i < size; i++) {
//                    playUrl.add(playUrls.get(i).getUrls());
//                    playTitle.add(playUrls.get(i).getTitle());
//                }
                play = new Gson().fromJson(responseInfo.result, Play.class);


                if (! TextUtils.isEmpty(play.getSd_url())){
                    play_url = play.getSd_url();
                }else if (! TextUtils.isEmpty(play.getHd_url())){
                    play_url = play.getHd_url();
                }else if (! TextUtils.isEmpty(play.getUhd_url())){
                    play_url = play.getUhd_url();
                }
                System.out.println("---play_url:" + play_url);
                title = play.getTitle();
                initCatch(play_url);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("DetailActivity onFailure ");
            }
        });


    }

    //初始化下载按钮
    private void initCatch(String play_url) {
        DbUtils db = DbUtils.create(DetailActivity.this);
        boolean isdownLoad = false;
        try {
            List<LocalBean> list = db.findAll(LocalBean.class);
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (!TextUtils.isEmpty(play_url)) {
                        System.out.println("***play_url:" + play_url + "***list.get(i).getUrl():" + list.get(i).getUrl());
                        isdownLoad = play_url.equals(list.get(i).getId());
                    }
                }
                if (isdownLoad) {
                    BaseApplication.isDownLoad = true;
                    tv_cache.setText("已缓存");
                    ll_download.setFocusable(false);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        progressBar5.setVisibility(View.GONE);
    }

    private String videoset_id;
//    private String resource;
    private String requestUrl;
    DownloadReceiver receiver;
    IntentFilter filter;

    @Override
    public int getLayoutId() {
        return R.layout.movie_detail;
    }

    @Override
    public void init() {
        requestUrl = Constants.DETAIL;
        receiver = new DownloadReceiver();
        filter = new IntentFilter();
        filter.addAction(START);
        filter.addAction(DOWNLOADING);
        filter.addAction(FINISH);
        filter.addAction(PAUSE);
        registerReceiver(receiver, filter);
        getNetDate();
        ll_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.this.showDialog(null, "是否下载影片?", null, null, new BaseActivity.OnAlertSureClickListener() {
                    @Override
                    public void onclick() {
                        BaseApplication.detailsList.add(details);
                        BaseApplication.playUrls.add(play_url);
                        showToast("已经加入下载列表");
                        tv_cache.setText("已缓存");
                        ll_download.setFocusable(false);
                        DbUtils db = DbUtils.create(DetailActivity.this);
                        LocalBean localBean = new LocalBean();
                        localBean.setTitle(title);
                        localBean.setImage(details.getImage().get(0));
                        localBean.setId(play_url);
                        localBean.setUrl(play_url);
                        localBean.setCurState(0);//還沒下載，準備下載
                        try {
                            db.delete(localBean);
                            db.save(localBean);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        int i = BaseApplication.downLoadManager.addTask(play_url, play_url, title + ".mp4", BaseApplication.VedioCacheUrl + title + ".mp4");
                        System.out.println("---加入任务返回值：" + i);
                        System.out.println("---详情下载的信息：" + play_url + "---本地：" + BaseApplication.VedioCacheUrl + title + ".mp4");
                    }
                });
            }
        });
    }


    private void initView() {
        L.e("***填充数据***" + details.getId());
        bitmapUtils = new BitmapUtils(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        img_movie.setLayoutParams(params);
//        bitmapUtils.
        if (details.getImage() != null) {
            bitmapUtils.display(rl_movieimg, details.getImage().get(0));
        }
        if (details.getTitle() != null) {
            movie_name.setText(details.getTitle());
        }
//        if (details.getId() != null) {
//            movietime.setText("片长：" + details.getVideo_length());
//        }
        if (details.getUpdate_time() != null) {
            long datetime = Long.parseLong(details.getUpdate_time()) * 1000l;
            System.out.println("***datetime = " + new Date(datetime));
            String date = new SimpleDateFormat("yyyy年MM月dd日").format(new Date(datetime));
//            Date d = new Date(Integer.parseInt(details.getUpdate_at()));
//            SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
////            sf.format(d);
//            tv_datetime.setText("更新时间：" + sf.format(d));
            tv_datetime.setText("更新时间：" + date);
        }
        introduced.setText(details.getDesc());
        System.out.println("---" + details.getUpdate_time() + "**" + details.getId() + "**" + Integer.parseInt(details.getUpdate_time()));
//        Integer.parseInt(details.getUpdate_at());


    }

    private void getNetDate() {
        L.e("DetailActivity getNetDate()");
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", "123");
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("videoset_id", videoset_id);
        this.httpPost(requestUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.e("****getNateDate *** DetailActivity responseInfo:" + responseInfo.result);
                setViewData(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }

    ArrayList<VideosNew> videoses;

    private void setViewData(String json) {
        L.e("DetailActivity setViewData()");
        if (Utils.textIsNull(json)) {
            finish();
            return;
        }

        details = new Gson().fromJson(json, Details.class);
//        size = detailses.size();
//        Details details = new Gson().fromJson(json,new );
        videoses = (ArrayList<VideosNew>) details.getVideos();
        L.e("DetailActivity details.getVideo() =" + details.getVideos());
        L.e("DetailActivity videoses =" + videoses);
        if (videoses != null) {
            for (int i = 0; i < videoses.size(); i++) {
                video_ids.add(videoses.get(i).getVid());
            }
            L.e("DetailActivity detailses = " + details + videoses.size());
            L.e("DetailActivity video_ids = " + video_ids);
            initView();
        } else {
            finish();
        }
        getplayUrl();
//        getRelationDate();
    }

    @Override
    public void getIntentData(Intent intent) {
        videoset_id = intent.getStringExtra("videoset_id");
//        resource = intent.getStringExtra("resource");
        L.e("---DetailActivity responseInfo:" + videoset_id );
    }

    List<Relation> relations;

    private void getRelationDate() {
        String url = Constants.RELATION;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", "123");
        params.addBodyParameter("vid", video_ids.get(0));
        System.out.println("***DetailActivity***getRelationDate() vid = " + video_ids.get(0));
        this.httpPost(url, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.e("****DetailActivity responseInfo:" + responseInfo.result);
                if (Utils.textIsNull(responseInfo.result)) {
                    return;
                }
                try {
                    relations = new Gson().fromJson(responseInfo.result, new TypeToken<List<Relation>>() {
                    }.getType());
                } catch (IllegalStateException e) {
                    DetailActivity.this.showToast("解析出现错误，请刷新数据");
                    finish();
                }

                setItemMovies(DetailActivity.this, relations);

            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }

    public void setItemMovies(final Context context, List<Relation> relations) {
        ll_correlation.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 30, 0);
        for (int i = 0; i < relations.size(); i++) {
            final Relation relation = relations.get(i);
//            System.out.println("---ItemView  roll---" + relation);

            LinearLayout contentView = (LinearLayout) View.inflate(context,
                    R.layout.item_item_img, null);
            ImageView iv_movie = (ImageView) contentView
                    .findViewById(R.id.iv_movie);
            BitmapUtils bitmapUtils = new BitmapUtils(DetailActivity.this);

            if (relation.getPosturl() != null) {
                bitmapUtils.display(iv_movie, relation.getPosturl());
            }
            TextView tv_movie = (TextView) contentView
                    .findViewById(R.id.tv_movie);
            tv_movie.setText(relation.getVname());
            iv_movie.setFocusable(true);
            iv_movie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PlayerVRActivityNew.class);
                    intent.putExtra("play_url", relation.getHd_url());
                    intent.putExtra("title", relation.getVname());
                    intent.putExtra("splite_screen", false);
                    MobclickAgent.onEvent(DetailActivity.this, "play_start");
                    context.startActivity(intent);
//                    System.out.println("---ItemView 条目被点击了---");
                }
            });
            contentView.setLayoutParams(params);
            ll_correlation.addView(contentView);

        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
