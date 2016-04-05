package com.hotcast.vr.tools;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.PlayerVRActivityNew2;
import com.hotcast.vr.bean.LocalBean2;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liurongzhi on 2016/3/16.
 */
public class UnityTools {
    public static void startActivity(Context context) {

    }

    /**
     * 播放影片
     *
     * @param context  上下文对象
     * @param play_url 播放地址
     * @param title    影片标题
     * @param flag     是否横屏
     */
    public static void startPlay(Context context, String play_url, String title, boolean flag) {
        Intent intent = new Intent(context, PlayerVRActivityNew2.class);
        intent.putExtra("play_url", play_url);
        intent.putExtra("title", title);
        intent.putExtra("splite_screen", flag);
        context.startActivity(intent);
    }
    /**
     * 播放影片
     *
     * @param context  上下文对象
     * @param play_url 播放地址
     * @param title    影片标题
     */
    public static void startPlayLanscape(Context context, String play_url, String title) {
        Intent intent = new Intent(context, PlayerVRActivityNew2.class);
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
     * @return
     */
    public static String getPackagename() {
        return BaseApplication.packagename;
    }

    /**
     * 开始下载新的任务
     *
     * @param url   下载地址
     * @param title 视频的标题
     */
    public static void startDownLoad(Context context, String url, String title, String imgurl, String vid, int qingxidu) {
        DbUtils db = DbUtils.create(context);
        LocalBean2 localBean = new LocalBean2();
        localBean.setTitle(title);
        localBean.setImage(imgurl);
        localBean.setId(url);
        localBean.setVid(vid);
        localBean.setUrl(url);
        localBean.setQingxidu(qingxidu);
        localBean.setCurState(0);//還沒下載，準備下載
        try {
            db.saveOrUpdate(localBean);
        } catch (DbException e) {
            System.out.println("---新添加的失败：" + e);
            e.printStackTrace();
        }
        BaseApplication.downLoadManager.addTask(url, url, title + ".mp4", BaseApplication.VedioCacheUrl + title + ".mp4");
    }

    /**
     * 暂停下载
     *
     * @param url 下载地址
     */
    public static void pauseDownLoad(String url) {
        BaseApplication.downLoadManager.stopTask(url);
    }

    /**
     * 继续下载
     *
     * @param url
     */
    public static void continueDownLoad(String url) {
        BaseApplication.downLoadManager.startTask(url);
    }

    /**
     * @return
     */
    public String getLocalCatch(Context context) {
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
    public boolean isNetworkConnected(Context context) {
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
    public boolean isWifiConnected(Context context) {
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
    public boolean isMobileConnected(Context context) {
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
