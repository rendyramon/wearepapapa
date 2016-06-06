package com.hotcast.vr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.hotcast.vr.services.UnityService;
import com.hotcast.vr.tools.SharedPreUtil;
import com.unity3d.player.UnityPlayer;

/**
 * Created by liurongzhi on 2016/3/19.
 */
public class UnityReceiver extends BroadcastReceiver {
    String action;

    @Override
    public void onReceive(Context context, Intent intent) {
        action = intent.getAction();
        switch (action) {
            case "UnitySendMessage":
                String url = intent.getStringExtra("url");
                String state = intent.getStringExtra("state");
                String pecnet = intent.getStringExtra("pecnet");
                String speed = intent.getStringExtra("speed");
                UnityPlayer.UnitySendMessage("HuanCunYe", "ShuaXin", url + "H_U_3D" + state + "H_U_3D" + pecnet + "H_U_3D" + speed);
                break;
            case "finishUnity":
                if (UnityPlayer.currentActivity != null) {
                    System.out.println("---关闭Unity");
                    UnityPlayer.currentActivity.finish();
                }
                break;
            case "landscape":
                UnityPlayer.UnitySendMessage("Canvas", "SwitchLandscape", "");
                break;
        }

    }
}
