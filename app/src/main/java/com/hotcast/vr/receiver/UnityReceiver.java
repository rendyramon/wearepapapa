package com.hotcast.vr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
        }
    }
}
