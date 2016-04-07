package com.hotcast.vr.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hotcast.vr.receiver.UnityReceiver;

public class UnityService extends Service {
    UnityReceiver unityReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        unityReceiver = new UnityReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("UnitySendMessage");
//        filter.addAction("");
        registerReceiver(unityReceiver, filter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(unityReceiver);
        super.onDestroy();
    }
}
