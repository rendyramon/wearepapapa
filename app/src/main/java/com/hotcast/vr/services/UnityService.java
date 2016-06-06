package com.hotcast.vr.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hotcast.vr.bean.LocalPlay;
import com.hotcast.vr.receiver.UnityReceiver;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

public class UnityService extends Service {
    UnityReceiver unityReceiver;
    public static String[] urls;
    public static boolean unityWork = true;
    DbUtils db;
    LocalPlay localPlay;

    @Override
    public void onCreate() {
        System.out.println("---onCreate");
        db = DbUtils.create(this);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("---onStartCommand");
        if (unityReceiver == null) {
            unityReceiver = new UnityReceiver();
        }
        if (urls == null) {
            urls = new String[6];
        }
        try {
            localPlay = db.findById(LocalPlay.class, "LocalPlay");
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (localPlay == null) {
            urls[0] = "";
            urls[1] = "0";
            urls[2] = "";
            urls[3] = "";
            urls[4] = "";
            urls[5] = "";
            unityWork = true;
        } else {
            System.out.println("---播放地址："+urls[0]);
            urls[0] = localPlay.getNowplayUrl();
            urls[1] = localPlay.getQingxidu();
            urls[2] = localPlay.getSdurl();
            urls[3] = localPlay.getHdrul();
            urls[4] = localPlay.getUhdrul();
            urls[5] = localPlay.getType();
            unityWork = localPlay.isUnityJump();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("UnitySendMessage");
        filter.addAction("finishUnity");
        filter.addAction("landscape");
        registerReceiver(unityReceiver, filter);
        return super.onStartCommand(intent, flags, startId);
    }

    public static void setUrls(String urlnow, String qingxidu, String sdurl, String hdrul, String uhdruls, String type) {
        if (urls == null) {
            urls = new String[6];
            urls[0] = "";
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
}
