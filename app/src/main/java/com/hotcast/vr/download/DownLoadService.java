package com.hotcast.vr.download;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.bean.LocalBean2;
import com.hotcast.vr.download.dbcontrol.bean.SQLDownLoadInfo;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.unity3d.player.UnityPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类功能描述：下载器后台服务</br>
 *
 * @author zhuiji7  (470508081@qq.com)
 * @version 1.0
 *          </p>
 */

public class DownLoadService extends Service {
    public static boolean unitydoing = false;

    public static Map<String, String> speeds;//下载速度的集合;
    public static List<String> errorTaskId = new ArrayList<>();
    private static DownLoadManager downLoadManager;
    DbUtils db;
    List<String> fileUrls;
    List<File> fileList;
    NetStateReceiver netStateReceiver;

    @Override

    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public static DownLoadManager getDownLoadManager() {
        return downLoadManager;
    }
//    TEMP_FILEPATH + "/(" + FileHelper.filterIDChars(sqlDownLoadInfo.getTaskID()) + ")" + sqlDownLoadInfo.getFileName()

    @Override
    public void onCreate() {
        super.onCreate();
        speeds = new HashMap<>();
        System.out.println("---后台服务创建");
        downLoadManager = new DownLoadManager(DownLoadService.this);
        BaseApplication.downLoadManager = downLoadManager;
        db = DbUtils.create(this);
        BaseApplication.downLoadings.clear();
        try {
            List<LocalBean2> ls = db.findAll(Selector.from(LocalBean2.class).where("curState", "=", 1));
            if (ls != null) {
                for (int i = 0; i < ls.size(); i++) {
                    BaseApplication.downLoadings.add(ls.get(i).getUrl());
                    downLoadManager.startTask(ls.get(i).getUrl());
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放downLoadManager
        System.out.println("---后台服务停止");
        downLoadManager.stopAllTask();
        downLoadManager = null;
        unregisterReceiver(netStateReceiver);
        netStateReceiver = null;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("---后台服务onStartCommand" + BaseApplication.downLoadings.size());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (downLoadManager == null) {
            downLoadManager = new DownLoadManager(DownLoadService.this);
        }
        downLoadManager.setSupportBreakpoint(true);
        downLoadManager.setAllTaskListener(new DownloadManagerListener());
        netStateReceiver = new NetStateReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(netStateReceiver, filter);
    }

    final String START = "START";
    final String DOWNLOADING = "DOWNLOADING";
    final String FINISH = "FINISH";
    final String PAUSE = "PAUSE";
    final String ERROR = "ERROR";

    private class DownloadManagerListener implements DownLoadListener {
        @Override
        public void onStart(SQLDownLoadInfo sqlDownLoadInfo) {
            String taskID = sqlDownLoadInfo.getTaskID();//taskID是网络地址
            BaseApplication.downLoadings.add(taskID);
            System.out.println("---start：" + BaseApplication.downLoadings.size());
            if (speeds.get(taskID) == null) {
                speeds.put(sqlDownLoadInfo.getTaskID(), "1");
                sendMessageToUnity(sqlDownLoadInfo.getTaskID(), "1", "0", 1);
            } else if (sqlDownLoadInfo.getFileSize() != 0) {
                int p = (int) ((sqlDownLoadInfo.getDownloadSize() * 100 / sqlDownLoadInfo.getFileSize()));
                long s = sqlDownLoadInfo.getDownloadSize() - Integer.parseInt(speeds.get(sqlDownLoadInfo.getTaskID()));
                sendMessageToUnity(sqlDownLoadInfo.getTaskID(), "1", p + "", s);
                speeds.put(sqlDownLoadInfo.getTaskID(), sqlDownLoadInfo.getDownloadSize() + "");
            }
            try {
                LocalBean2 localBean = db.findById(LocalBean2.class, taskID);
                if (localBean != null) {
                    System.out.println("---接收到刷新信息onStart");
                    localBean.setCurState(1);
                    localBean.setDownloading(true);
                    localBean.setTotal(sqlDownLoadInfo.getFileSize());
                    localBean.setLocalurl(BaseApplication.VedioCacheUrl + localBean.getTitle() + ".mp4");
                    if (TextUtils.isEmpty(localBean.getPecent())) {
                        localBean.setSpeed("100");
                        localBean.setPecent("0");
                    }
                    db.saveOrUpdate(localBean);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(START);
            intent.putExtra("play_url", sqlDownLoadInfo.getTaskID());
            intent.putExtra("current", sqlDownLoadInfo.getDownloadSize());
            intent.putExtra("total", sqlDownLoadInfo.getFileSize());
            sendBroadcast(intent);
        }

        @Override
        public void onProgress(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
            //根据监听到的信息查找列表相对应的任务，更新相应任务的进度
            System.out.println("---2：onProgress:" + sqlDownLoadInfo.getDownloadSize() + "总大小" + sqlDownLoadInfo.getFileSize());
            int p = (int) ((sqlDownLoadInfo.getDownloadSize() * 100 / sqlDownLoadInfo.getFileSize()));
            System.out.println("---p" + p);
            long s = sqlDownLoadInfo.getDownloadSize() - Integer.parseInt(speeds.get(sqlDownLoadInfo.getTaskID()));
            sendMessageToUnity(sqlDownLoadInfo.getTaskID(), "1", p + "", s);
            speeds.put(sqlDownLoadInfo.getTaskID(), sqlDownLoadInfo.getDownloadSize() + "");
            Intent intent = new Intent(DOWNLOADING);
            intent.putExtra("play_url", sqlDownLoadInfo.getTaskID());
            intent.putExtra("current", sqlDownLoadInfo.getDownloadSize());
            intent.putExtra("total", sqlDownLoadInfo.getFileSize());
            sendBroadcast(intent);
        }

        @Override
        public void onStop(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
            BaseApplication.downLoadings.remove(sqlDownLoadInfo.getTaskID());
            System.out.println("---onStop");
            int p = (int) ((sqlDownLoadInfo.getDownloadSize() * 100 / sqlDownLoadInfo.getFileSize()));
            long s = sqlDownLoadInfo.getDownloadSize() - Integer.parseInt(speeds.get(sqlDownLoadInfo.getTaskID()));
            sendMessageToUnity(sqlDownLoadInfo.getTaskID(), "4", p + "", s);
            try {
                LocalBean2 localBean = db.findById(LocalBean2.class, sqlDownLoadInfo.getTaskID());
                if (localBean != null) {
                    localBean.setCurState(4);
                    localBean.setDownloading(false);
                    localBean.setLocalurl(BaseApplication.VedioCacheUrl + localBean.getTitle() + ".mp4");
                    localBean.setPecent(p + "");
                    localBean.setSpeed(s + "");
                    db.saveOrUpdate(localBean);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(PAUSE);
            intent.putExtra("play_url", sqlDownLoadInfo.getTaskID());
            intent.putExtra("current", sqlDownLoadInfo.getDownloadSize());
            intent.putExtra("total", sqlDownLoadInfo.getFileSize());
            sendBroadcast(intent);
        }

        @Override
        public void onSuccess(SQLDownLoadInfo sqlDownLoadInfo) {
            BaseApplication.downLoadings.remove(sqlDownLoadInfo.getTaskID());
            System.out.println("---接收到刷新信息onSuccess");
            sendMessageToUnity(sqlDownLoadInfo.getTaskID(), "3", "100", 0);
            speeds.remove(sqlDownLoadInfo.getTaskID());
            Intent intent = new Intent(FINISH);
            intent.putExtra("play_url", sqlDownLoadInfo.getTaskID());
            intent.putExtra("localurl", sqlDownLoadInfo.getFilePath());
            sendBroadcast(intent);
            String taskID = sqlDownLoadInfo.getTaskID();//taskID是网络地址
            try {
                LocalBean2 localBean = db.findById(LocalBean2.class, taskID);
                localBean.setCurState(3);
                localBean.setDownloading(false);
                localBean.setLocalurl(BaseApplication.VedioCacheUrl + localBean.getTitle() + ".mp4");
                db.saveOrUpdate(localBean);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(SQLDownLoadInfo sqlDownLoadInfo) {
            BaseApplication.downLoadings.remove(sqlDownLoadInfo.getTaskID());
            //根据监听到的信息查找列表相对应的任务，停止该任务
            System.out.println("---接收到刷新信息onError");
            String taskID = sqlDownLoadInfo.getTaskID();//taskID是网络地址
            LocalBean2 localBean = null;
            try {
                localBean = db.findById(LocalBean2.class, taskID);
                localBean.setCurState(2);
                localBean.setLocalurl(BaseApplication.VedioCacheUrl + localBean.getTitle() + ".mp4");
                db.saveOrUpdate(localBean);
            } catch (DbException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(PAUSE);
            intent.putExtra("play_url", sqlDownLoadInfo.getTaskID());
            sendBroadcast(intent);
            if (isNetworkConnected(DownLoadService.this) && localBean != null) {
                System.out.println("---下载失败，开始重新下载");
                BaseApplication.downLoadManager.startTask(taskID);
            } else {
                System.out.println("---下载失败，失败线程保留");
                if (errorTaskId == null) {
                    errorTaskId = new ArrayList<>();
                }
                errorTaskId.add(taskID);
                sendMessageToUnity(sqlDownLoadInfo.getTaskID(), "2", "0", 0);
                UnityPlayer.UnitySendMessage("HuanCunYe", "ShuaXin", sqlDownLoadInfo.getTaskID() + "H_U_3D" + "2" + "H_U_3D" + "0" + "H_U_3D" + "0");
            }
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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    /**
     * @param url    下载地址
     * @param state  状态
     * @param pecnet 百分比
     * @param s      速度
     */
    public void sendMessageToUnity(String url, String state, String pecnet, long s) {
        String speed = (s / 1024) + "";
        System.out.println("---发送消息到unity1");
        System.out.println("---发送消息到unity2");
        Intent intent = new Intent("UnitySendMessage");
        intent.putExtra("url", url);
        intent.putExtra("state", state);
        intent.putExtra("pecnet", pecnet);
        intent.putExtra("speed", speed);
        sendBroadcast(intent);
    }

    public class NetStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo.State wifiState = null;
            NetworkInfo.State mobileState = null;
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            if (wifiState != null && mobileState != null
                    && NetworkInfo.State.CONNECTED != wifiState && NetworkInfo.State.CONNECTED == mobileState) {
                // 手机网络连接成功
                System.out.println("---移动网络连接成功");
                for (int i = 0; i < errorTaskId.size(); i++) {
                    BaseApplication.downLoadManager.startTask(errorTaskId.get(i));
                }
                errorTaskId.clear();
            } else if (wifiState != null && mobileState != null
                    && NetworkInfo.State.CONNECTED != wifiState
                    && NetworkInfo.State.CONNECTED != mobileState) {
                System.out.println("---网络连接断开");
                // 手机没有任何的网络
            } else if (wifiState != null && NetworkInfo.State.CONNECTED == wifiState) {
                // 无线网络连接成功
                System.out.println("---wifi网络连接成功");
                for (int i = 0; i < errorTaskId.size(); i++) {
                    BaseApplication.downLoadManager.startTask(errorTaskId.get(i));
                }
                errorTaskId.clear();
            }
        }
    }
}
