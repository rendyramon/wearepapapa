package com.hotcast.vr;

import android.app.Activity;
import android.os.Bundle;

import com.hotcast.vr.tools.UnityTools;

/**
 * Created by liurongzhi on 2016/5/9.
 */
public class PlayLocalActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlocalactivity);
        System.out.println("获取到的地址：" + getIntent().getData().getPath());
//        System.out.println(UnityTools.getlocal());
    }
}
