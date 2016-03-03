package com.hotcast.vr;

import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.event.OnClick;

import butterknife.InjectView;

/**
 * Created by lostnote on 16/3/3.
 */
public class LoginActivity extends BaseActivity {
    boolean islook = false;
    @InjectView(R.id.tv_login)
    TextView tv_login;
    @InjectView(R.id.iv_look)
    ImageView iv_look;
    @InjectView(R.id.et_password)
    EditText et_password;
    @InjectView(R.id.et_username)
    EditText et_username;
    @InjectView(R.id.bt_next)
    Button bt_next;
    @InjectView(R.id.iv_return)
    ImageView iv_return;

    String username;
    String password;


    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void init() {
        bt_next.setOnClickListener(this);
        iv_look.setOnClickListener(this);
        iv_return.setOnClickListener(this);
        tv_login.setText("登录");
    }

    @Override
    public void getIntentData(Intent intent) {

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_next:
                username = et_username.getText().toString().trim();
                password = et_password.getText().toString().trim();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                    showToast("亲，手机号或密码不能为空为空哦^_^");
                }
                finish();
                BaseApplication.isLogin = true;
                System.out.println("---点击了登录按钮");

                break;
            case R.id.iv_look:
                if (islook){
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    iv_look.setImageResource(R.mipmap.look);
                    System.out.println("---点击了密码不可见");
                    islook = false;
                }else {
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    iv_look.setImageResource(R.mipmap.unlook);
                    System.out.println("---点击了密码可见");
                    islook = true;
                }
                break;
            case R.id.iv_return:
                finish();
                break;
        }
    }

}
