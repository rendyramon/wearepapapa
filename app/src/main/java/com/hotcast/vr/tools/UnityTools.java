package com.hotcast.vr.tools;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.MainActivity_new;
import com.hotcast.vr.SplashActivity;
import com.hotcast.vr.bean.LocalBean2;
import com.hotcast.vr.bean.LocalVideoBean;
import com.hotcast.vr.download.DownLoadService;
import com.hotcast.vr.services.UnityService;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liurongzhi on 2016/3/16.
 */
public class UnityTools {
    public static Context context;

    public static void startActivity() {
        UnityPlayer.currentActivity.startActivity(new Intent(UnityPlayer.currentActivity, SplashActivity.class));
    }

    /**
     * 获取手机本地视频
     * content 上下文环境
     * misSize 用于过滤小视屏的最小值，单位MB；
     *
     * @return LocalVideoBean的list集合；
     */
    public static ArrayList<LocalVideoBean> getLocalVideo() {
        int minSize = 10;

        if (context == null) {
            context = UnityPlayer.currentActivity;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        switch (options.inPreferredConfig = Bitmap.Config.ARGB_8888) {
        }
        ArrayList<LocalVideoBean> list = new ArrayList<>();
        final ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Video.Media.MIME_TYPE + "='video/mp4'",
                null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();
        int fileNum = cursor.getCount();
        for (int counter = 0; counter < fileNum; counter++) {
            long size = Long.parseLong(cursor.getString(1)) / (1024 * 1024);
            String path = cursor.getString(0);
//         如果视频大于最小大小并且路径不包含"/hostcast/vr/",则将此视频添加进list
            if (size > minSize && !path.contains("/hostcast/vr/")) {
                LocalVideoBean localVideoBean = new LocalVideoBean();

                localVideoBean.setVideoPath(path);
                localVideoBean.setVideoName(cursor.getString(3).replace(".mp4", ""));
                final long videoId = Long.parseLong(cursor.getString(2));
//                通过viewID获取该video的缩略图
                Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, videoId,
                        MediaStore.Images.Thumbnails.MINI_KIND, options);
                localVideoBean.setVideoImage(bitmap);
                list.add(localVideoBean);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public static String getlocal() {
        if (ContextCompat.checkSelfPermission(UnityPlayer.currentActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            String json = "[";
            ArrayList<LocalVideoBean> list = getLocalVideo();
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (i < list.size() - 1) {
                        json = json + "{" + "\"videoPath\":" + "\"" + "file://" + list.get(i).getVideoPath() + "\"" + "," + "\"videoName\":" + "\"" + list.get(i).getVideoName() + "\"" + "," + "\"imagePath\":" + "\"" + BaseApplication.ImgCacheUrl + list.get(i).getVideoName() + ".jpg" + "\"" + "},";
                    } else {
                        json = json + "{" + "\"videoPath\":" + "\"" + "file://" + list.get(i).getVideoPath() + "\"" + "," + "\"videoName\":" + "\"" + list.get(i).getVideoName() + "\"" + "," + "\"imagePath\":" + "\"" + BaseApplication.ImgCacheUrl + list.get(i).getVideoName() + ".jpg" + "\"" + "}]";
                    }
                }
            }
            return json;
        } else {
            return "";
        }
    }

    public static String[] getPlayUrl() {
        System.out.println("---UnityService.urls:" + UnityService.urls[0]);
        return UnityService.urls;
    }

    /**
     * 获取眼镜型号
     *
     * @return -1 表示没有选择（这种情况不会出现），1 多朵，2.cardboard 3.小宅，4暴风魔镜
     */
    public static String getGlasses() {
        return "2";
    }

    public static boolean getJump() {
        return UnityService.unityWork;
    }

    public static void setUnityWork(boolean unityWork, Context context) {
        SharedPreUtil.getInstance(context).add("unityWork", unityWork);
        UnityService.unityWork = unityWork;
    }

    /**
     * 关闭unity的activity
     *
     * @param object
     */
    public static void finishUnity(Object object) {
        Intent intent = new Intent("unitywork");
        UnityPlayer.currentActivity.sendBroadcast(intent);
//        UnityPlayer.currentActivity.finish();
        startActivity();
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
            if (l != null && l.getCurState() == 3) {
                return l.getLocalurl();
            }
            return "";
        }
    }

    public static void getPlayTime(final String mUri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String duration = "0";
                android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
                try {
                    if (mUri != null) {
                        if (mUri.contains("file://")) {
                            mmr.setDataSource(mUri.replace("file://", ""));
                        } else {
                            HashMap<String, String> headers = null;
                            if (headers == null) {
                                headers = new HashMap<String, String>();
                                headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                            }
                            mmr.setDataSource(mUri, headers);
                        }
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
                    if (duration != null && Integer.parseInt(duration) < 100) {
                        duration = "0";
                    }
                    System.out.println("---视频时长：" + duration);
                    UnityPlayer.UnitySendMessage("Manager", "SetTimeOnUI", duration);
                }
            }
        }).start();
    }

    /**
     * 获取设备的ID
     *
     * @return
     */
    public static String getDeviceID() {
        String device = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            device = tm.getDeviceId();
        } catch (Exception e) {
            SharedPreferences sp = UnityPlayer.currentActivity.getSharedPreferences("cache_config", Context.MODE_PRIVATE);
            device = sp.getString("device", "");
            if (device == null || device.length() < 5) {
                device = System.currentTimeMillis() + (int) (Math.random() * 100) + "";
                sp.edit().putString("device", device).commit();
            }
        } finally {
            if (device == null || device.length() < 3) {
                device = System.currentTimeMillis() + (int) (Math.random() * 100) + "";
            }
        }
        return device;
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

    public static String getPlatform() {
        String platform = SharedPreUtil.getInstance(UnityPlayer.currentActivity).select("platform", "android");
        System.out.println("---getPlatform:" + platform);
        return platform;
    }

    public static int getOpenangle() {
        int degree = SharedPreUtil.getInstance(UnityPlayer.currentActivity).select("degree", 60);
        System.out.println("---getOpenangle:" + degree);
        return degree;
    }

    /**
     * 开始下载新的任务
     */
    public static void startDownLoad(String u, String t, String i, String v, int q, String type) {
        if (isNetworkConnected()) {
            url = u;
            imgurl = i;
            vid = v;
            qingxidu = q;
            Intent intent = new Intent("startDownLoad");
            if ("3d".equals(type)) {
                intent.putExtra("type", "_3d_interaction");
                title = t + "_3d_interaction";
            } else if ("vr_interaction".equals(type)) {
                intent.putExtra("type", "_vr_interaction");
                title = t + "_vr_interaction";
            } else if ("3d_noteraction".equals(type)) {
                intent.putExtra("type", "_3d_noteraction");
                title = t + "_3d_noteraction";
            } else {
                intent.putExtra("type", "");
                title = t;
            }
            intent.putExtra("url", url);
            intent.putExtra("imgurl", imgurl);
            intent.putExtra("title", title);
            intent.putExtra("vid", vid);
            intent.putExtra("qingxidu", qingxidu);

            UnityPlayer.currentActivity.sendBroadcast(intent);
        } else {
            System.out.println("---无网络" + imgurl);
        }
    }

    public static String getToken() {
        return TokenUtils.createToken(UnityPlayer.currentActivity);
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
//        boolean islow = SharedPreUtil.getInstance(UnityPlayer.currentActivity).select("islow", false);
        return false;
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
