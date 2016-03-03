package com.hotcast.vr;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by lostnote on 16/3/3.
 */
public class RegistActivity extends BaseActivity {
    @InjectView(R.id.bt_verificationcode)
    Button bt_verificationcode;
    @InjectView(R.id.ll_regist1)
    LinearLayout ll_regist1;
    @InjectView(R.id.ll_regist2)
    LinearLayout ll_regist2;



    @OnClick({R.id.iv_return,R.id.bt_verificationcode,R.id.bt_next})
    void clickType(View v) {
        switch (v.getId()){
            case R.id.iv_return :
                finish();
                break;
            case R.id.bt_verificationcode :
                //发送手机号到服务器
                bt_verificationcode.setEnabled(false);
                break;
            case R.id.bt_next :
                //跳转到第二个regist页面
                ll_regist1.setVisibility(View.GONE);
                ll_regist2.setVisibility(View.VISIBLE);
                break;


        }
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_regist;
    }

    @Override
    public void init() {
        ll_regist1.setVisibility(View.VISIBLE);
        ll_regist2.setVisibility(View.GONE);

    }

    @Override
    public void getIntentData(Intent intent) {

    }
}
