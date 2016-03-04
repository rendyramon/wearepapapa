package com.hotcast.vr.download;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;

import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.bean.LocalBean1;
import com.hotcast.vr.download.dbcontrol.FileHelper;
import com.hotcast.vr.download.dbcontrol.bean.SQLDownLoadInfo;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 类功能描述：下载器后台服务</br>
 *
 * @author zhuiji7  (470508081@qq.com)
 * @version 1.0
 *          </p>
 */

public class DownLoadService extends Service {
    private static DownLoadManager downLoadManager;
    DbUtils db;
    List<String> fileUrls;
    List<File> fileList;

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
        downLoadManager = new DownLoadManager(DownLoadService.this);
        BaseApplication.downLoadManager = downLoadManager;
        db = DbUtils.create(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放downLoadManager
        downLoadManager.stopAllTask();
        downLoadManager = null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (downLoadManager == null) {
            downLoadManager = new DownLoadManager(DownLoadService.this);
        }
        downLoadManager.setSupportBreakpoint(true);
        downLoadManager.setAllTaskListener(new DownloadManagerListener());
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
            try {
                LocalBean1 localBean = db.findById(LocalBean1.class, taskID);
                if (localBean != null) {
                    System.out.println("---接收到刷新信息onStart");
                    localBean.setCurState(1);
                    localBean.setDownloading(true);
                    localBean.setTotal(sqlDownLoadInfo.getFileSize());
                    localBean.setLocalurl(BaseApplication.VedioCacheUrl + localBean.getTitle() + ".mp4");
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
            System.out.println("---2：onProgress:" + sqlDownLoadInfo.getDownloadSize()+"总大小"+sqlDownLoadInfo.getFileSize());
            Intent intent = new Intent(DOWNLOADING);
            intent.putExtra("play_url", sqlDownLoadInfo.getTaskID());
            intent.putExtra("current", sqlDownLoadInfo.getDownloadSize());
            intent.putExtra("total", sqlDownLoadInfo.getFileSize());
            sendBroadcast(intent);
        }

        @Override
        public void onStop(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
            System.out.println("---onStop");
            try {
                LocalBean1 localBean = db.findById(LocalBean1.class, sqlDownLoadInfo.getTaskID());
                if (localBean != null) {
                    localBean.setCurState(4);
                    localBean.setLocalurl(BaseApplication.VedioCacheUrl + localBean.getTitle() + ".mp4");
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
            System.out.println("---接收到刷新信息onSuccess");
            String taskID = sqlDownLoadInfo.getTaskID();//taskID是网络地址
            try {
                LocalBean1 localBean = db.findById(LocalBean1.class, taskID);
                localBean.setCurState(3);
                localBean.setDownloading(false);
                localBean.setLocalurl(BaseApplication.VedioCacheUrl + localBean.getTitle() + ".mp4");
                db.saveOrUpdate(localBean);
            } catch (DbException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(FINISH);
            intent.putExtra("play_url", sqlDownLoadInfo.getTaskID());
            intent.putExtra("localurl", sqlDownLoadInfo.getFilePath());
            sendBroadcast(intent);
        }

        @Override
        public void onError(SQLDownLoadInfo sqlDownLoadInfo) {
            //根据监听到的信息查找列表相对应的任务，停止该任务
            System.out.println("---接收到刷新信息onError");
            String taskID = sqlDownLoadInfo.getTaskID();//taskID是网络地址
            try {
                LocalBean1 localBean = db.findById(LocalBean1.class, taskID);
                localBean.setCurState(2);
                localBean.setLocalurl(BaseApplication.VedioCacheUrl + localBean.getTitle() + ".mp4");
                db.saveOrUpdate(localBean);
            } catch (DbException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(PAUSE);
            intent.putExtra("play_url", sqlDownLoadInfo.getTaskID());
            sendBroadcast(intent);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };
}
