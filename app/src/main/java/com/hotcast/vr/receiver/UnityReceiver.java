package com.hotcast.vr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.hotcast.vr.services.UnityService;
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
            case "Unitystart":
                String nowplayUrl = intent.getStringExtra("nowplayUrl");
                if (TextUtils.isEmpty(nowplayUrl)) {
                    nowplayUrl = "";
                    UnityService.setUrls(nowplayUrl, "", "", "", "", "");
                } else {
                    UnityService.setUrls(intent.getStringExtra("nowplayUrl"), intent.getStringExtra("qingxidu"), intent.getStringExtra("sdurl"), intent.getStringExtra("hdrul"), intent.getStringExtra("uhdrul"), intent.getStringExtra("type"));
                }
                UnityPlayer.UnitySendMessage("Canvas", "Succ", "");
                break;
            case "finishUnity":
                if (UnityPlayer.currentActivity != null) {
                    UnityService.unityWork = intent.getBooleanExtra("Unitisdoing", true);
                    UnityPlayer.currentActivity.finish();
                }
                break;
            case "UnitWork":
                UnityService.unityWork = intent.getBooleanExtra("Unitisdoing", true);
                break;
        }
    }
}
