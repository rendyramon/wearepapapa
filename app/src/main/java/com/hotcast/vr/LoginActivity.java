package com.hotcast.vr;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.event.OnClick;

import butterknife.InjectView;

/**
 * Created by lostnote on 16/3/3.
 */
public class LoginActivity extends BaseActivity {
    @InjectView(R.id.tv_login)
    TextView tv_login;
    @OnClick(R.id.bt_next)
    void clickConfirm(View v){
        //确认登录返回我的页面并将页面改成登录状态
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void init() {
        tv_login.setText("登录");
    }

    @Override
    public void getIntentData(Intent intent) {

    }
}
