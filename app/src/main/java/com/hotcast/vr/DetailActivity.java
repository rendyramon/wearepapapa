package com.hotcast.vr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dlodlo.dvr.sdk.unity.DvrUnityActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.adapter.PinglunAdapter;
import com.hotcast.vr.bean.Details;
import com.hotcast.vr.bean.Detailser;
import com.hotcast.vr.bean.LocalBean2;
import com.hotcast.vr.bean.Pinglun;
import com.hotcast.vr.bean.Play;
import com.hotcast.vr.bean.PlayerBean;
import com.hotcast.vr.bean.Relation;
import com.hotcast.vr.bean.Relationer;
import com.hotcast.vr.bean.Urls;
import com.hotcast.vr.bean.UserData;
import com.hotcast.vr.bean.VideosNew;
import com.hotcast.vr.dialog.MyDialog;
import com.hotcast.vr.pageview.DetailScrollView;
import com.hotcast.vr.receiver.DownloadReceiver;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.DensityUtils;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.SharedPreUtil;
import com.hotcast.vr.tools.TokenUtils;
import com.hotcast.vr.tools.UnityTools;
import com.hotcast.vr.tools.Utils;
import com.hotcast.vr.u3d.UnityPlayerActivity;
import com.hotcast.vr.uikit.Util;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.sharesdk.wechat.utils.WechatHelper;

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
    @InjectView(R.id.ll_share)
    LinearLayout ll_share;

    @InjectView(R.id.rl_share)
    RelativeLayout rl_share;
    @InjectView(R.id.iv_weixin)
    ImageView iv_weixin;
    @InjectView(R.id.iv_friends)
    ImageView iv_friends;
    @InjectView(R.id.bt_qx)
    Button bt_qx;

    @InjectView(R.id.progressBar5)
    ProgressBar progressBar5;
    @InjectView(R.id.et_pinglun)
    EditText et_pinglun;
    @InjectView(R.id.bt_sendpinglun)
    Button bt_sendpinglun;
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
    String playurl;
    String type;
    ViewHolder holder;
    Intent intent;
    private IWXAPI wxApi;

    @OnClick({R.id.back, R.id.play, R.id.bt_qx, R.id.iv_weixin, R.id.iv_friends})
    void clickType(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.play:
                L.e("---你点击了播放的按钮");
                startPlay(play_url, qingxidu, play);
                break;
            case R.id.bt_qx:
                L.e("---你点击了取消按钮");
                rl_share.setVisibility(View.GONE);
                break;
            case R.id.iv_weixin:
                L.e("---你点击了分享到微信");
                shareToWeixin(0);//分享到微信好友
                break;
            case R.id.iv_friends:
                L.e("---你点击了分享到朋友圈");
                shareToWeixin(1);//分享到微信好友

                break;
        }
    }

    private void startPlay(String play_url, int qingxidu, Play play) {
        if (UnityTools.getGlasses().equals("1")) {
            intent = new Intent(this, DvrUnityActivity.class);
        } else {
            intent = new Intent(this, UnityPlayerActivity.class);
        }
        SharedPreUtil.getInstance(this).add("nowplayUrl", play_url);//默认播放地址
        SharedPreUtil.getInstance(this).add("qingxidu", qingxidu + "");//清晰度
        if (!TextUtils.isEmpty(play.getSd_url())) {
            SharedPreUtil.getInstance(this).add("sdurl", play.getSd_url());
        } else {
            SharedPreUtil.getInstance(this).add("sdurl", "");
        }
        if (!TextUtils.isEmpty(play.getHd_url())) {
            SharedPreUtil.getInstance(this).add("hdrul", play.getHd_url());
        } else {
            SharedPreUtil.getInstance(this).add("hdrul", "");
        }
        if (!TextUtils.isEmpty(play.getUhd_url())) {
            SharedPreUtil.getInstance(this).add("uhdrul", play.getUhd_url());
        } else {
            SharedPreUtil.getInstance(this).add("uhdrul", "");
        }
        SharedPreUtil.getInstance(this).add("type", type);
        DetailActivity.this.startActivity(intent);
    }

    class ViewHolder {
        Button bt_number;
    }

    String play_url;
    String title;
    String media_id;
    Play play;

    public void getplayUrl(String vid, final boolean flag) {
        mUrl = Constants.PLAY_URL;
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
                if (flag) {
                    initPlayUrl(responseInfo.result);
                } else {
                    progressBar5.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(responseInfo.result)) {
                        Play play = new Play();
                        String play_url = "";
                        int qingxidu = 1;
                        PlayerBean playerBean = new Gson().fromJson(responseInfo.result, PlayerBean.class);
                        if ("success".equals(playerBean.getMessage()) || 0 <= playerBean.getCode() && playerBean.getCode() <= 10) {
                            play = playerBean.getData();
                            type = play.getType();
                            if (SharedPreUtil.getBooleanData(DetailActivity.this, "islow", false)) {
                                if (!TextUtils.isEmpty(play.getSd_url())) {
                                    play_url = play.getSd_url();
                                    qingxidu = 0;
                                    BaseApplication.clarityText = getResources().getString(R.string.standard_definition);
                                } else if (!TextUtils.isEmpty(play.getHd_url())) {
                                    play_url = play.getHd_url();
                                    qingxidu = 1;
                                    BaseApplication.clarityText = getResources().getString(R.string.hd);
                                } else if (!TextUtils.isEmpty(play.getUhd_url())) {
                                    play_url = play.getUhd_url();
                                    qingxidu = 2;
                                    BaseApplication.clarityText = getResources().getString(R.string.super_clear);
                                }
                            } else {
                                if (!TextUtils.isEmpty(play.getHd_url())) {
                                    play_url = play.getHd_url();
                                    qingxidu = 1;
                                    BaseApplication.clarityText = getResources().getString(R.string.hd);
                                } else if (!TextUtils.isEmpty(play.getSd_url())) {
                                    play_url = play.getSd_url();
                                    qingxidu = 0;
                                    BaseApplication.clarityText = getResources().getString(R.string.standard_definition);
                                } else if (!TextUtils.isEmpty(play.getUhd_url())) {
                                    play_url = play.getUhd_url();
                                    qingxidu = 2;
                                    BaseApplication.clarityText = getResources().getString(R.string.super_clear);
                                }
                            }
                        }
                        if (play_url.length() > 1) {
                            startPlay(play_url, qingxidu, play);
                        }
                    }
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (!flag) {
                    progressBar5.setVisibility(View.GONE);
                }
                Toast.makeText(DetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                L.e("DetailActivity onFailure ");
            }
        });


    }

    private void initPlayUrl(String result) {
        if (!TextUtils.isEmpty(result)) {
            PlayerBean playerBean = new Gson().fromJson(result, PlayerBean.class);
            if ("success".equals(playerBean.getMessage()) || 0 <= playerBean.getCode() && playerBean.getCode() <= 10) {
                play = playerBean.getData();
                type = play.getType();
                if (SharedPreUtil.getBooleanData(this, "islow", false)) {
                    if (!TextUtils.isEmpty(play.getSd_url())) {
                        play_url = play.getSd_url();
                        qingxidu = 0;
                        BaseApplication.clarityText = getResources().getString(R.string.standard_definition);
                    } else if (!TextUtils.isEmpty(play.getHd_url())) {
                        play_url = play.getHd_url();
                        qingxidu = 1;
                        BaseApplication.clarityText = getResources().getString(R.string.hd);
                    } else if (!TextUtils.isEmpty(play.getUhd_url())) {
                        play_url = play.getUhd_url();
                        qingxidu = 2;
                        BaseApplication.clarityText = getResources().getString(R.string.super_clear);
                    }
                    download();
                    initCatch(play_url);
                    saveUrl = play_url;
                    title = play.getTitle();
                } else {
                    if (!TextUtils.isEmpty(play.getHd_url())) {
                        play_url = play.getHd_url();
                        qingxidu = 1;
                        BaseApplication.clarityText = getResources().getString(R.string.hd);
                    } else if (!TextUtils.isEmpty(play.getSd_url())) {
                        play_url = play.getSd_url();
                        qingxidu = 0;
                        BaseApplication.clarityText = getResources().getString(R.string.standard_definition);
                    } else if (!TextUtils.isEmpty(play.getUhd_url())) {
                        play_url = play.getUhd_url();
                        qingxidu = 2;
                        BaseApplication.clarityText = getResources().getString(R.string.super_clear);
                    }
                    download();
                    initCatch(play_url);
                    saveUrl = play_url;
                    title = play.getTitle();
                }


            }
        }
    }

    DbUtils db;
    boolean isdownLoad = false;

    //初始化下载按钮
    private void initCatch(String play_url) {
        try {
            List<LocalBean2> list = db.findAll(LocalBean2.class);
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (!TextUtils.isEmpty(play_url)) {
                        if (play_url.equals(list.get(i).getUrl())) {
                            isdownLoad = true;
                        }
//                        isdownLoad = play_url.equals(list.get(i).getUrl());
//                        System.out.println("---isdownLoad=" + isdownLoad);
                    }
                }
                if (isdownLoad) {
                    BaseApplication.isDownLoad = true;
                    tv_cache.setText(getResources().getString(R.string.cached));
                    ll_download.setEnabled(false);
                    setPlayUrl();
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    private void setPlayUrl() {
        try {
            LocalBean2 bean = db.findById(LocalBean2.class, play_url);
            if (bean != null) {
                play_url = bean.getLocalurl();
                qingxidu = bean.getQingxidu();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    private String videoset_id;
    //    private String resource;
    private String requestUrl;
    DownloadReceiver receiver;
    IntentFilter filter;
    String saveUrl;
    int qingxidu = 1;

    @Override
    public int getLayoutId() {
        return R.layout.movie_detail;
    }

    LocalBean2 localBean;

    @Override
    public void init() {
        //实例化微信分享
        wxApi = WXAPIFactory.createWXAPI(DetailActivity.this, Constants.WX_APP_ID);
        wxApi.registerApp(Constants.WX_APP_ID);

        ShareSDK.initSDK(this);
        db = DbUtils.create(DetailActivity.this);
        requestUrl = Constants.DETAIL;
        receiver = new DownloadReceiver();
        filter = new IntentFilter();
        filter.addAction(START);
        filter.addAction(DOWNLOADING);
        filter.addAction(FINISH);
        filter.addAction(PAUSE);
        registerReceiver(receiver, filter);
        tv_count = (TextView) findViewById(R.id.tv_count);
        lv_pinglun = (ListView) findViewById(R.id.lv_pinglun);
        sv = (DetailScrollView) findViewById(R.id.sv);
        getNetDate();
        et_pinglun.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = et_pinglun.getText().toString().trim();
                if (str.length() > 0) {
                    bt_sendpinglun.setClickable(true);
                    bt_sendpinglun.setOnClickListener(DetailActivity.this);
                    bt_sendpinglun.setTextColor(getResources().getColor(R.color.pinglunbutton1));
                } else {
                    bt_sendpinglun.setClickable(false);
                    bt_sendpinglun.setTextColor(getResources().getColor(R.color.pinglunbutton2));
                }
            }
        });

    }
//    class BtAsyncTask  extends AsyncTask<Integer, Integer, Bitmap>{
//        String url;
//        int flag = 0;
//        public BtAsyncTask(String url,int flag) {
//            super();
//            this.url = url;
//            this.flag = flag;
//
//        }
//        @Override
//        protected Bitmap doInBackground(Integer... params) {
//            Bitmap bitmap = null;
//            InputStream in = null;
//            BufferedOutputStream out = null;
//            try
//            {
//                in = new BufferedInputStream(new URL(url).openStream(), 2*1024);
//                final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
//                out = new BufferedOutputStream(dataStream, 2*1024);
//                copy(in, out);
//                out.flush();
//                byte[] data = dataStream.toByteArray();
//                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
////            data = null;
//                System.out.println("---bitmap="+bitmap);
//                return bitmap;
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            msg.setThumbImage(bitmap);
//            SendMessageToWX.Req req = new SendMessageToWX.Req();
//            req.transaction = String.valueOf(System.currentTimeMillis());
//            req.message = msg;
//            req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
//            wxApi.sendReq(req);
////            super.onPostExecute(bitmap);
//        }
//    }

//    /**
//     * 得到本地或者网络上的bitmap url - 网络或者本地图片的绝对路径,比如:
//     * <p/>
//     * A.网络路径: url="http://blog.foreverlove.us/girl2.png" ;
//     * <p/>
//     * B.本地路径:url="file://mnt/sdcard/photo/image.png";
//     * <p/>
//     * C.支持的图片格式 ,png, jpg,bmp,gif等等
//     *
//     * @param url
//     * @return
//     */
//    public  Bitmap GetLocalOrNetBitmap(String url) {
////        progressBar5.setVisibility(View.VISIBLE);
////        showToast("正在处理图片，请稍等");
//        Bitmap bitmap = null;
//        InputStream in = null;
//        BufferedOutputStream out = null;
//        try {
//            in = new BufferedInputStream(new URL(url).openStream(), 2 * 1024);
//            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
//            out = new BufferedOutputStream(dataStream, 2 * 1024);
//            copy(in, out);
//            out.flush();
//            byte[] data = dataStream.toByteArray();
//            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
////            data = null;
//            System.out.println("---bitmap=" + bitmap.getRowBytes());
//
//            return bitmap;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }finally {
////            progressBar5.setVisibility(View.GONE);
//        }
//
//    }
//    public  byte[] GetBtobyte(String url) {
////        progressBar5.setVisibility(View.VISIBLE);
////        showToast("正在处理图片，请稍等");
//        byte[] data;
//        InputStream in = null;
//        BufferedOutputStream out = null;
//        try {
//            in = new BufferedInputStream(new URL(url).openStream(), 2 * 1024);
//            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
//            out = new BufferedOutputStream(dataStream, 2 * 1024);
//            copy(in, out);
//            out.flush();
//            data = dataStream.toByteArray();
////            data = null;
//            System.out.println("---byte=:"+data.length );
//
//            return data;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new byte[1];
//        }finally {
////            progressBar5.setVisibility(View.GONE);
//        }
//
//    }
//
//
//    private static void copy(InputStream in, OutputStream out)
//            throws IOException {
//        byte[] b = new byte[2 * 1024];
//        int read;
//        while ((read = in.read(b)) != -1) {
//            out.write(b, 0, read);
//        }
//    }
//
//    /**
//     * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
//     *
//     * @param flag(0:分享到微信好友，1：分享到微信朋友圈)
//     */
//    WXWebpageObject webpage;
//    WXMediaMessage msg;
//
//    private void wechatShare(final int f) {
//        new Thread(new Runnable() {
//            int flag = f;
//            @Override
//            public void run() {
//                final Bitmap bmp = GetLocalOrNetBitmap(details.getImage().get(0));
////final byte[] bytes = GetBtobyte(details.getImage().get(0));
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        webpage = new WXWebpageObject();
//
//                        webpage.webpageUrl = "http://m.hotcast.cn/Home/Play/play?set_id=" + videoset_id;
//                        msg = new WXMediaMessage(webpage);
//                        msg.title = details.getTitle();
//                        msg.description = details.getDesc();
//                        //这里替换一张自己工程里的图片资源
////                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
//                        //msg.setThumbImage(bitmap);
//                        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
//                        bmp.recycle();
//
//                        msg.thumbData = Util.bmpToByteArray(thumbBmp,true);
////                        showToast("---bitmap="+bitmap);
////                        Bitmap bmp = BitmapFactory.decodeFile(details.getImage().get(0));
////                        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
////                        bmp.recycle();
////                        msg.thumbData = Uti
////                        showToast("bitmap="+bitmap.getRowBytes());
//                        SendMessageToWX.Req req = new SendMessageToWX.Req();
//                        req.transaction = String.valueOf(System.currentTimeMillis());
//                        req.message = msg;
//                        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
//                        wxApi.sendReq(req);
//                        showToast("发送微信分享，跳转到微信" + flag);
//                        rl_share.setVisibility(View.GONE);
//                    }
//                });
//            }
//        }).start();
//    }


    private void share() {
        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_share.setVisibility(View.VISIBLE);
//                wechatShare(0);//分享到微信好友
            }
        });
    }
    /**
     * 判断是否安装微信
     *
     * @return
     */
    private boolean isWeixinAvilible() {
        PackageManager packageManager = this.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }
    private void shareToWeixin(int flag){
        if (!isWeixinAvilible()){
            showToast("请安装微信后进行分享操作");
            return;
        }
        //设置分享内容
        Platform.ShareParams shareParams = new Platform.ShareParams();
        shareParams.setShareType(Platform.SHARE_WEBPAGE);//设置分享属性
        shareParams.setTitle(details.getTitle());
        shareParams.setText(details.getDesc());
//        showToast("--title=" + details.getTitle() + "--text=" + details.getDesc());

        shareParams.setImageUrl(details.getImage().get(0));
        shareParams.setUrl("http://m.hotcast.cn/Home/Play/play?set_id=" + videoset_id);
        switch (flag){
            case 0://分享到好友
                Platform wechat1 = ShareSDK.getPlatform(Wechat.NAME);
                wechat1.share(shareParams);
                rl_share.setVisibility(View.GONE);
                break;
            case 1://分享到好友
                Platform wechat2 = ShareSDK.getPlatform(WechatMoments.NAME);
                wechat2.share(shareParams);
                rl_share.setVisibility(View.GONE);
                break;
        }


    }
    private void download() {
        ll_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.Builder builder = new MyDialog.Builder(DetailActivity.this) {
                    @Override
                    public void setCarity1() {
                        if (!TextUtils.isEmpty(play.getSd_url())) {
                            saveUrl = play.getSd_url();
                            qingxidu = 0;
                        } else {
                            //将该button字体颜色设置为灰色并不可点击
                            showToast("没有标清连接");
                            System.out.println("---没有标清连接");
                        }
                    }

                    @Override
                    public void setCarity2() {
                        if (!TextUtils.isEmpty(play.getHd_url())) {
                            saveUrl = play.getHd_url();
                            qingxidu = 1;
                        } else {
                            //将该button字体颜色设置为灰色并不可点击
                            showToast("没有高清连接");
                            System.out.println("---没有高清连接");
                        }
                    }

                    @Override
                    public void setCarity3() {
                        if (!TextUtils.isEmpty(play.getUhd_url())) {
                            saveUrl = play.getUhd_url();
                            qingxidu = 2;
                        } else {
                            //将该button字体颜色设置为灰色并不可点击
                            showToast("没有超清连接");
                            System.out.println("---没有超清连接");
                        }
                    }
                };
                if (TextUtils.isEmpty(play.getSd_url())) {
                    System.out.println("---标清无");
                    builder.setIsFocusable1(false);
                } else {
                    builder.setIsFocusable1(true);
                }
                if ((!TextUtils.isEmpty(play.getSd_url())) && SharedPreUtil.getBooleanData(DetailActivity.this, "islow", true)) {
                    builder.setIsFocusable2(false);
                    builder.setIsFocusable3(false);
                } else {
                    if (TextUtils.isEmpty(play.getHd_url())) {
                        builder.setIsFocusable2(false);
                    } else {
                        builder.setIsFocusable2(true);
                    }
                    if (TextUtils.isEmpty(play.getUhd_url())) {
                        builder.setIsFocusable3(false);
                    } else {
                        builder.setIsFocusable3(true);
                    }
                }

                builder.setTitle(getResources().getString(R.string.select_downlod));
                builder.setPositiveButton(getResources().getString(R.string.determine), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("---您选择确定");
                        BaseApplication.detailsList.add(details);
                        BaseApplication.playUrls.add(saveUrl);
//                        showToast("已经加入下载列表");
//                        tv_cache.setText("已缓存");
                        String str = "";
                        ll_download.setFocusable(false);
                        if ("3d".equals(type)) {
                            str = "_3d_interaction";
                            title = title + "_3d_interaction";
                        } else if ("vr_interaction".equals(type)) {
                            str = "_vr_interaction";
                            title = title + "_vr_interaction";
                        } else if ("3d_noteraction".equals(type)) {
                            str = "_3d_noteraction";
                            title = title + "_3d_noteraction";
                        } else {
                            str = "";
                        }
                        DbUtils db = DbUtils.create(DetailActivity.this);
                        LocalBean2 localBean = new LocalBean2();
                        localBean.setTitle(title);
                        localBean.setImage(details.getImage().get(0));
                        localBean.setId(saveUrl);
                        localBean.setVid(details.getVideos().get(0).getVid());
                        localBean.setUrl(saveUrl);
                        localBean.setQingxidu(qingxidu);
                        localBean.setCurState(0);//還沒下載，準備下載
                        try {
                            db.saveOrUpdate(localBean);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        int i = BaseApplication.downLoadManager.addTask(saveUrl, saveUrl, title + str + ".mp4", BaseApplication.VedioCacheUrl + title + str + ".mp4");
                        showToast("已经加入下载列表");
                        tv_cache.setText(getResources().getString(R.string.cached));
                        dialog.dismiss();

                        //设置你的操作事项
                    }
                });

                builder.setNegativeButton(getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        });
    }


    private void initView() {
        bitmapUtils = new BitmapUtils(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (details.getImage() != null) {
            System.out.println("---details.getImage().get(0)=" + details.getImage().get(0));
            bitmapUtils.display(rl_movieimg, details.getImage().get(0));
        }
        if (details.getTitle() != null) {
            movie_name.setText(details.getTitle());
        }
        if (details.getUpdate_time() != null) {
            long datetime = Long.parseLong(details.getUpdate_time()) * 1000l;
            System.out.println("***datetime = " + new Date(datetime));
            String date = new SimpleDateFormat(getResources().getString(R.string.datetime)).format(new Date(datetime));
            tv_datetime.setText(getResources().getString(R.string.update_time) + date);
        }
        introduced.setText(details.getDesc());
        System.out.println("--title=" + details.getTitle() + "--text=" + details.getDesc()+"---");
    }

    public void refreshPinglun() {
        tv_count.setText(getResources().getString(R.string.evaluation) + count + getResources().getString(R.string.evaluation2));
        ViewGroup.LayoutParams params = lv_pinglun.getLayoutParams();
        params.height = DensityUtils.dp2px(this, 68 * datas.size() + 40);
        lv_pinglun.setLayoutParams(params);
        if (adapter == null) {
            adapter = new PinglunAdapter(this, datas);
            lv_pinglun.setAdapter(adapter);
        } else {
            lv_pinglun.setAdapter(adapter);
            adapter.setDatas(datas);
            adapter.notifyDataSetChanged();
        }
        isloadingPinglun = false;
    }

    private void getNetDate() {
        L.e("DetailActivity getNetDate()");
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(this));
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
                setViewData(responseInfo.result);
                getPinglun(false, 10);
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
        } else {
            Detailser detailser = new Gson().fromJson(json, Detailser.class);
            if ("success".equals(detailser.getMessage()) || 0 <= detailser.getCode() && detailser.getCode() <= 10) {
                details = detailser.getData();
                videoses = (ArrayList<VideosNew>) details.getVideos();
                L.e("DetailActivity details.getVideo() =" + details.getVideos());
                L.e("DetailActivity videoses =" + videoses);
                if (videoses != null && videoses.size() > 0) {
                    for (int i = 0; i < videoses.size(); i++) {
                        video_ids.add(videoses.get(i).getVid());
                    }
                    L.e("DetailActivity detailses = " + details + videoses.size());
                    L.e("DetailActivity video_ids = " + video_ids);
                    initView();
                } else {
                    finish();
                }
                getplayUrl(details.getVideos().get(0).getVid(), true);
                getRelationDate();
            }
        }
    }

    @Override
    public void getIntentData(Intent intent) {
        videoset_id = intent.getStringExtra("videoset_id");
    }

    List<Relation> relations;

    private void getRelationDate() {
        String url = Constants.RELATION;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(this));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("videoset_id", videoset_id);
        this.httpPost(url, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (Utils.textIsNull(responseInfo.result)) {
                    return;
                } else {
                    Relationer relationer = new Gson().fromJson(responseInfo.result, Relationer.class);
                    if ("success".equals(relationer.getMessage()) || 0 <= relationer.getCode() && relationer.getCode() <= 10) {
                        relations = relationer.getData();
                        progressBar5.setVisibility(View.GONE);
                        share();
                        setItemMovies(DetailActivity.this, relations);
                    } else {
                        showToast("亲，获取网络数据失败了T_T");
                    }

                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("---getRelationDate onFailure:" + s);
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

            LinearLayout contentView = (LinearLayout) View.inflate(context,
                    R.layout.item_item_img, null);
            ImageView iv_movie = (ImageView) contentView
                    .findViewById(R.id.iv_movie);
            BitmapUtils bitmapUtils = new BitmapUtils(DetailActivity.this);

            if (relation.getImage().get(0) != null) {
                bitmapUtils.display(iv_movie, relation.getImage().get(0));
            }
            TextView tv_movie = (TextView) contentView
                    .findViewById(R.id.tv_movie);
            if (relation != null && relation.getVideos().size() > 0) {
                tv_movie.setText(relation.getVideos().get(0).getVname());
            }
            iv_movie.setFocusable(true);
            iv_movie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar5.setVisibility(View.VISIBLE);
                    getplayUrl(relation.getVideos().get(0).getVid(), false);
                }
            });
            contentView.setLayoutParams(params);
            ll_correlation.addView(contentView);

        }
    }

    TextView tv_count;
    ListView lv_pinglun;
    PinglunAdapter adapter;
    DetailScrollView sv;
    boolean isloadingPinglun = false;

    public void initPinglun() {
        sv.setScrollBottomListener(new DetailScrollView.ScrollBottomListener() {
            @Override
            public void scrollBottom() {
                if (!isloadingPinglun && datas.size() < pinglun.getCount()) {
                    isloadingPinglun = true;
                    getPinglun(false, 10);
                }
            }
        });
        refreshPinglun();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    int tmp_page = 0;
    int count;
    int total_page;
    Pinglun pinglun;
    List<Pinglun.Data> datas;

    private void getPinglun(boolean flag, int number) {
        final boolean isrefresh = flag;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(this));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("per_page", number + "");
        params.addBodyParameter("tmp_page", tmp_page + 1 + "");
        params.addBodyParameter("videoset_id", videoset_id);
        this.httpPost(Constants.GETPINGLUN, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject j = new JSONObject(responseInfo.result);
                    String data = j.getString("data");
                    total_page = 0;
                    if (data != null && !"".equals(data) && data.length() > 3) {
                        pinglun = new Gson().fromJson(data, Pinglun.class);
                        total_page = pinglun.getTotal_page();
                        count = pinglun.getCount();
                        if (datas == null) {
                            datas = new ArrayList<Pinglun.Data>();
                        }
                        if (!isrefresh) {
                            tmp_page++;
                            datas.addAll(pinglun.getData());
                        } else {
                            datas = pinglun.getData();
                            if (adapter == null) {
                                adapter = new PinglunAdapter(DetailActivity.this, datas);
                            }
                            adapter.setDatas(datas);
                        }
                        if (tmp_page <= 1) {
                            if (tmp_page == 0) {
                                tmp_page = 1;
                            }
                            initPinglun();
                        } else {
                            refreshPinglun();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sendpinglun:
                if (BaseApplication.isLogin) {
                    sendPinglun();
                } else {
                    intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
        }
    }

    public void sendPinglun() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(this));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        UserData userData = new Gson().fromJson(sp.select("userData", ""), UserData.class);
        params.addBodyParameter("login_token", sp.select("login_token", ""));
        params.addBodyParameter("content", et_pinglun.getText().toString().trim());
        params.addBodyParameter("videoset_id", videoset_id);
        this.httpPost(Constants.SENDPINGLUN, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject j = new JSONObject(responseInfo.result);
                    String message = j.getString("message");
                    if ("success".equals(message)) {
                        et_pinglun.setText("");
                        Toast.makeText(DetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    if (datas == null) {
                        getPinglun(true, 10);
                    } else {
                        getPinglun(true, datas.size() + 2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }
}
