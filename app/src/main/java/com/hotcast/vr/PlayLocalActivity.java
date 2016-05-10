package com.hotcast.vr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hotcast.vr.tools.SharedPreUtil;
import com.hotcast.vr.tools.UnityTools;
import com.hotcast.vr.u3d.UnityPlayerActivity;

/**
 * Created by liurongzhi on 2016/5/9.
 */
public class PlayLocalActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlocalactivity);
        String path = getIntent().getData().getPath();
        System.out.println("获取到的地址：" + getIntent().getData().getPath());
        SharedPreUtil.getInstance(this).add("sence", 1);
        SharedPreUtil.getInstance(this).add("sdplayUrl",path);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, UnityPlayerActivity.class);
        startActivity(intent);
        finish();
    }
}
