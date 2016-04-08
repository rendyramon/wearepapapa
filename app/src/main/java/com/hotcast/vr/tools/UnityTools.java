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
     * @return -1 表示没有选择（这种情况不会出现），1 多朵，2.cardboard 3.小宅
     */
    public String getGlasses() {
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

    /**
     * 获取设备的ID
     *
     * @return
     */
    public static String getDeviceID() {
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
