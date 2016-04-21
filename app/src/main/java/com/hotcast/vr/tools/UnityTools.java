package com.hotcast.vr.tools;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.PlayerVRActivityNew2;
import com.hotcast.vr.bean.LocalBean2;
import com.hotcast.vr.download.DownLoadService;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.unity3d.player.UnityPlayer;

import java.util.HashMap;
import java.util.List;

/**
 * Created by liurongzhi on 2016/3/16.
 */
public class UnityTools {
    public static Context context;

    public static void startActivity(Context context) {

    }

    /**
     * 获取眼镜型号
     *
     * @return -1 表示没有选择（这种情况不会出现），1 多朵，2.cardboard 3.小宅，4暴风魔镜
     */
    public static String getGlasses() {
        SharedPreUtil sp = SharedPreUtil.getInstance(context);
        int g = sp.select("glass", -1);
        return g + "";
    }

    /**
     * 关闭unity的activity
     *
     * @param object
     */
    public static void finishUnity(Object object) {
        Intent intent = new Intent("unitywork");
        UnityPlayer.currentActivity.sendBroadcast(intent);
        System.out.println("---unity退出了" + object.getClass().toString() + DownLoadService.unitydoing);
        UnityPlayer.currentActivity.finish();
//        ((GoogleUnityActivity) UnityPlayer.currentActivity).getUnityPlayer().quit();

    }

    public static String isLoading(String url) {
        DbUtils db = DbUtils.create(UnityPlayer.currentActivity);
        LocalBean2 l = null;
        try {
            l = db.findById(LocalBean2.class, url);

        } catch (DbException e) {
            e.printStackTrace();
        }
        if (l == null || TextUtils.isEmpty(l.getUrl()) || TextUtils.isEmpty(l.getLocalurl())) {
            return "";
        } else {
            return l.getLocalurl();
        }
    }

    /**
     * 播放影片
     *
     * @param play_url 播放地址
     * @param title    影片标题
     * @param flag     是否横屏
     */
    public static void startPlay(String play_url, String title, boolean flag) {
        Intent intent = new Intent(context, PlayerVRActivityNew2.class);
        intent.putExtra("play_url", play_url);
        intent.putExtra("title", title);
        intent.putExtra("splite_screen", flag);
        context.startActivity(intent);
    }

    /**
     * 播放影片
     *
     * @param play_url 播放地址
     * @param title    影片标题
     */
    public static void startPlayLanscape(String play_url, String title) {
        Intent intent = new Intent(context, PlayerVRActivityNew2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("play_url", play_url);
        intent.putExtra("title", title);
        intent.putExtra("splite_screen", true);
        context.startActivity(intent);
    }

    public static int getPlayTime(String mUri) {
        String duration = "0";
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                mmr.setDataSource(mUri, headers);
            } else {
                //mmr.setDataSource(mFD, mOffset, mLength);
            }

            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
//            String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
//            String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高

        } catch (Exception ex) {
            System.out.println("---时长获取失败");
        } finally {
            mmr.release();
            return Integer.parseInt(duration);
        }
    }

    /**
     * 获取设备的ID
     *
     * @return
     */
    public static String getDeviceID() {
        System.out.println("---device:" + BaseApplication.device);
        return BaseApplication.device;
    }

    /**
     * 获取应用的版本号
     *
     * @return
     */
    public static String getAppversion() {
        return BaseApplication.version;
    }

    /**
     * 获取应用的包名
     *
     * @return 包名字符串
     */
    public static String getPackagename() {
        return BaseApplication.packagename;
    }

    static String url;
    static String title;
    static String imgurl;
    static String vid;
    static int qingxidu;

    /**
     * 开始下载新的任务
     */
    public static void startDownLoad(String u, String t, String i, String v, int q) {
        if (isNetworkConnected()) {
            url = u;
            title = t;
            imgurl = i;
            vid = v;
            qingxidu = q;
            Intent intent = new Intent("startDownLoad");
            intent.putExtra("url", url);
            intent.putExtra("imgurl", imgurl);
            intent.putExtra("title", title);
            intent.putExtra("vid", vid);
            intent.putExtra("qingxidu", qingxidu);
            UnityPlayer.currentActivity.sendBroadcast(intent);
            System.out.println("---点击了下载" + imgurl);
        } else {
            System.out.println("---无网络" + imgurl);
        }
    }

    /**
     * 暂停下载
     *
     * @param url 下载地址
     */
    public static void pauseDownLoad(String url) {
        Intent intent = new Intent("pauseDownLoad");
        intent.putExtra("url", url);
        UnityPlayer.currentActivity.sendBroadcast(intent);
    }

    /**
     * 继续下载
     *
     * @param url
     */
    public static void continueDownLoad(String url) {
        Intent intent = new Intent("continueDownLoad");
        intent.putExtra("url", url);
        UnityPlayer.currentActivity.sendBroadcast(intent);
    }

    public static boolean TestPhone() {
        boolean islow = SharedPreUtil.getInstance(UnityPlayer.currentActivity).select("islow", false);
        return islow;
    }

    /**
     * @return
     */
    public static String getLocalCatch(String str) {
        System.out.println("---str:" + str);
        List<LocalBean2> localBean2s = null;
        String json = "";
        DbUtils db = DbUtils.create(context);
        try {
            localBean2s = db.findAll(LocalBean2.class);
            Gson gson = new Gson();
            json = gson.toJson(localBean2s);
            System.out.println("---json:" + json);
        } catch (DbException e) {
            e.printStackTrace();
            //获取失败
        }
        if (localBean2s == null || localBean2s.size() < 1) {
            return "";
        } else {
            return json;
        }
    }

    //    判断是否有个网络连接
    public static boolean isNetworkConnected() {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //判断WIFI网络是否可用
    public static boolean isWifiConnected() {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //判断MOBILE网络是否可用
    public static boolean isMobileConnected() {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
