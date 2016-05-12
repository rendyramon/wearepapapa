package com.hotcast.vr;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.hotcast.vr.bean.Channel;
import com.hotcast.vr.bean.Classify;
import com.hotcast.vr.bean.Details;
import com.hotcast.vr.download.DownLoadManager;
import com.hotcast.vr.download.DownLoadService;
import com.hotcast.vr.services.AndroidService;
import com.hotcast.vr.services.FileCacheService;
import com.hotcast.vr.services.UnityService;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Md5Utils;
import com.hotcast.vr.tools.UnityTools;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;


public class BaseApplication extends Application {
    public static List<String> downLoadings = new ArrayList<>();

    public static boolean doAsynctask = false;//本地视频同步数据库处理
    public static List<String> strs = new ArrayList<>();
    public static int size;
    public static boolean isLogin = false;
    public static float playbacktime = 0;
    public static String clarityText = "";
    //横屏时的图片数量
    public static int scapePage = 1;
    public static boolean isDownLoad = false;
    public static boolean isUpdate = false;
    //    public static List<Classify> netClassifys;
    public static Channel channel;
    public static final String TAG = BaseApplication.class.getSimpleName();
    //    public static BitmapUtils mFinalBitmap;
    private static BaseApplication instance;
    public static DownLoadManager downLoadManager;

    public static BaseApplication getInstance() {
        return instance;
    }

    public static String version = "v1.0.5";//版本号
    public static String platform;//平台号
    public static String device = "weihuoqu";//设备号
    public static String packagename;//包名


    public static final String IMG_DISCCACHE_DIR = "/mnt/sdcard/jarvis/imgcache";
    public static boolean pagerf = false;
    public static boolean cacheFileChange = false;
    public static  String VedioCacheUrl;
    public static String ImgCacheUrl ;


    public static List<Classify> classifies = new ArrayList<>();
    public static List<String> playUrls = new ArrayList<>();//需要下載的電影地址
    public static List<Details> detailsList = new ArrayList<>();//需要下載的電影地址
    public static SharedPreferences sp;
    public static BitmapUtils bu;

    public static BitmapUtils getDisplay(Context context, int failedImgId) {
        BitmapUtils mFinalBitmap = new BitmapUtils(context, IMG_DISCCACHE_DIR);
        mFinalBitmap.configDefaultLoadFailedImage(failedImgId);
        mFinalBitmap.configDefaultLoadingImage(failedImgId);
        return mFinalBitmap;
    }

    public static PackageInfo info;
    private PackageManager packageManager;

    @Override
    public void onCreate() {
        if (hasSDCard()){
            VedioCacheUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hostcast/vr/";
            ImgCacheUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hostcast/vr/vedioImg/";
        }else {
            VedioCacheUrl = Environment.getDataDirectory() + "/hostcast/vr/";
            ImgCacheUrl = Environment.getDataDirectory()+ "/hostcast/vr/vedioImg/";
        }
//        updateDb();
        UnityTools.context = getApplicationContext();
        Thread.currentThread().setUncaughtExceptionHandler(new MyExecptionHandler());
        super.onCreate();
        bu = new BitmapUtils(this);
        instance = this;
        this.startService(new Intent(this, DownLoadService.class));
        this.startService(new Intent(this, FileCacheService.class));
        this.startService(new Intent(this, AndroidService.class));
        this.startService(new Intent(this, UnityService.class));
        initMeta();
        sp = getSharedPreferences("cache_config", Context.MODE_PRIVATE);
        getIMEI(this);
        System.out.println("--deviceID:" + device + "--" + Md5Utils.getMd5(device));
        packageManager = this.getPackageManager();
        try {
            info = packageManager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        BaseApplication.cacheFileChange = sp.getBoolean("cacheFileCache", false);
        BaseApplication.version = info.versionName;
        BaseApplication.platform = getAppMetaData(this, "UMENG_CHANNEL");
        BaseApplication.packagename = info.packageName;
        System.out.println("---" + getAppMetaData(this, "UMENG_CHANNEL"));
    }

    private class MyExecptionHandler implements Thread.UncaughtExceptionHandler {
        /**
         * 当线程出现了未捕获异常执行的方法
         * 不能阻止java虚拟机退出，只能在退出之前做点别的事
         *
         * @param thread
         * @param ex
         */
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            L.e("应用程序异常退出了");
            ex.printStackTrace();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
    /**
     * 判断手机是否有SD卡。
     *
     * @return 有SD卡返回true，没有返回false。
     */
    public static boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }


    public static void getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        device = "123456789-777878";
//        try {
//            device = tm.getDeviceId();
//        } catch (Exception e) {
//            device = sp.getString("device", "");
//            if (device == null || device.length() < 5) {
//                device = System.currentTimeMillis() + (int) (Math.random() * 100) + "";
//                System.out.println("---DeviceId获取失败:随机生成：" + device);
//                sp.edit().putString("device", device).commit();
//            } else {
//
//            }
//        }
    }

    private void initMeta() {
        try {

            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null && appInfo.metaData != null) {
                L.mAddLog = appInfo.metaData.getBoolean("DEBUG");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public DisplayMetrics getDeviceDisplayMetrics(Context context) {
        android.view.WindowManager windowsManager = (android.view.WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = windowsManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics;
    }

    /**
     * 获取application中指定的meta-data----UMENG_CHANNEL
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {

                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }
}
