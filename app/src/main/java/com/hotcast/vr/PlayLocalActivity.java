package com.hotcast.vr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.hotcast.vr.bean.LocalPlay;
import com.hotcast.vr.services.UnityService;
import com.hotcast.vr.tools.VideoDBUtils;
import com.hotcast.vr.u3d.UnityPlayerActivity;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

/**
 * Created by liurongzhi on 2016/5/9.
 */
public class PlayLocalActivity extends Activity {
    String path;
    DbUtils db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlocalactivity);
        db = DbUtils.create(this);
        path = getIntent().getData().getPath();
        if (!path.endsWith(".mp4")) {
            String strs[] = path.split("/");
            path = VideoDBUtils.getLocalVideoPathById(this, Long.parseLong(strs[strs.length - 1]));
        }
        if (!path.contains("file://")) {
            path = "file://" + path;
        }
        goToUnity();
    }

    protected void goToUnity() {
        System.out.println("---解析到的路径：" + path);
        LocalPlay localPlay = new LocalPlay();
        localPlay.setNowplayUrl(path);
        localPlay.setQingxidu("0");
        localPlay.setSdurl("");
        localPlay.setHdrul("");
        localPlay.setUhdrul("");

        if (path.contains("_3d_interaction")) {
            localPlay.setType("3d");
        } else if (path.contains("_vr_interaction")) {
            localPlay.setType("vr_interaction");
        } else if (path.contains("_3d_noteraction")) {
            localPlay.setType("3d_noteraction");
        } else {
            localPlay.setType("vr");
        }
        localPlay.setUnityJump(false);
        try {
            db.saveOrUpdate(localPlay);
        } catch (DbException e) {
            e.printStackTrace();
        }
        Intent intent;
        intent = new Intent(PlayLocalActivity.this, UnityPlayerActivity.class);
        startService(new Intent(this, UnityService.class));
        this.startActivity(intent);
        finish();
    }

}
