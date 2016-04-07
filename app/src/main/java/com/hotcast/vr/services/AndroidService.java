package com.hotcast.vr.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hotcast.vr.receiver.AndroidReceiver;

public class AndroidService extends Service {
    AndroidReceiver androidReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("---启动android服务");
        androidReceiver = new AndroidReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("startDownLoad");
        filter.addAction("pauseDownLoad");
        filter.addAction("continueDownLoad");
        filter.addAction("unitywork");
        registerReceiver(androidReceiver, filter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(androidReceiver);
        super.onDestroy();
    }
}
