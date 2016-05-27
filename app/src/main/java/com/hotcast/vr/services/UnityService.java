package com.hotcast.vr.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hotcast.vr.receiver.UnityReceiver;

public class UnityService extends Service {
    UnityReceiver unityReceiver;
    public static String[] urls;
    public static boolean unityWork = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("---UnityService创建onStartCommand");
        if (unityReceiver == null) {
            unityReceiver = new UnityReceiver();
        }
        if (urls == null) {
            urls = new String[6];
            urls[0] = "";
            urls[1] = "";
            urls[2] = "";
            urls[3] = "";
            urls[4] = "";
            urls[5] = "";
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("UnitySendMessage");
        filter.addAction("Unitystart");
        filter.addAction("UnitWork");//表示Unity初始化后要不要跳转
        filter.addAction("finishUnity");
        registerReceiver(unityReceiver, filter);
        return super.onStartCommand(intent, flags, startId);
    }

    public static void setUrls(String urlnow, String qingxidu, String sdurl, String hdrul, String uhdruls, String type) {
        if (urls == null) {
            urls = new String[6];
        }
        if (!urlnow.contains("http") && !urlnow.contains("file") && urlnow.length() > 1) {
            urlnow = "file://" + urlnow;
        }
        urls[0] = urlnow;
        urls[1] = qingxidu;
        urls[2] = sdurl;
        urls[3] = hdrul;
        urls[4] = uhdruls;
        urls[5] = type;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(unityReceiver);
        super.onDestroy();
    }
}
