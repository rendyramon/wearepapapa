package com.hotcast.vr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.dlodlo.dvr.sdk.unity.DvrUnityActivity;
import com.hotcast.vr.tools.SharedPreUtil;
import com.hotcast.vr.tools.UnityTools;
import com.hotcast.vr.tools.VideoDBUtils;
import com.hotcast.vr.u3d.UnityPlayerActivity;

/**
 * Created by liurongzhi on 2016/5/9.
 */
public class PlayLocalActivity extends Activity {
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlocalactivity);
        path = getIntent().getData().getPath();
        if (!path.endsWith(".mp4")) {
            String strs[] = path.split("/");
            path = VideoDBUtils.getLocalVideoPathById(this, Long.parseLong(strs[strs.length - 1]));
        }
        goToUnity();
    }

    protected void goToUnity() {
        System.out.println("---解析到的路径：" + path);
        Intent intent;
        if (UnityTools.getGlasses().equals("1")) {
            intent = new Intent(PlayLocalActivity.this, DvrUnityActivity.class);
        } else {
            intent = new Intent(PlayLocalActivity.this, UnityPlayerActivity.class);
        }
        SharedPreUtil.getInstance(this).add("nowplayUrl", path);
        SharedPreUtil.getInstance(this).add("qingxidu", "0");
        SharedPreUtil.getInstance(this).add("sdurl", "");
        SharedPreUtil.getInstance(this).add("hdrul", "");
        SharedPreUtil.getInstance(this).add("uhdrul", "");
        if (path.contains("_3d_interaction")) {
            SharedPreUtil.getInstance(this).add("type", "3d");
        } else if (path.contains("_vr_interaction")) {
            SharedPreUtil.getInstance(this).add("type", "vr_interaction");
        } else if (path.contains("_3d_noteraction")) {
            SharedPreUtil.getInstance(this).add("type", "3d_noteraction");
        } else {
            SharedPreUtil.getInstance(this).add("type", "vr");
        }
        this.startActivity(intent);
        finish();
    }
}
