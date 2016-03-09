package com.hotcast.vr;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hotcast.vr.bean.User1;
import com.hotcast.vr.bean.User2;
import com.hotcast.vr.bean.UserData;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.Md5Utils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;


import butterknife.InjectView;

/**
 * Created by lostnote on 16/3/8.
 */
public class ReNameActivity extends BaseActivity {
    @InjectView(R.id.tv_title)
    TextView tv_title;
    @InjectView(R.id.iv_return)
    ImageView iv_return;
    @InjectView(R.id.et_username)
    EditText et_username;
    @InjectView(R.id.bt_save)
    Button bt_save;

    String data;
    UserData userData;
    @Override
    public int getLayoutId() {
        return R.layout.rename_activity;
    }

    @Override
    public void init() {
        tv_title.setText(title);
        iv_return.setVisibility(View.VISIBLE);
        iv_return.setOnClickListener(this);
        bt_save.setOnClickListener(this);
        data = sp.select("userData","");
        if (!TextUtils.isEmpty(data)){
            userData = new Gson().fromJson(data, UserData.class);
        }
    }

    String title;
    String mUrl;
    String username;
    @Override
    public void getIntentData(Intent intent) {
        title = intent.getStringExtra("title");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return:
                finish();
                break;
            case R.id.bt_save:

                username = et_username.getText().toString();
                reName(username);
                break;
        }
    }

    private void reName(final String username) {
        mUrl = Constants.RENAME;
        RequestParams params = new RequestParams();
        String str = format.format(System.currentTimeMillis());
        params.addBodyParameter("token", Md5Utils.getMd5("hotcast-"+str+"-hotcast"));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("login_token", userData.getLogin_token());
        params.addBodyParameter("username", username);
        this.httpPost(mUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                System.out.println("---responseInfo.result = " + responseInfo.result);
                if (!TextUtils.isEmpty(responseInfo.result)){
                    User1 user1 = new Gson().fromJson(responseInfo.result,User1.class);
                    if ("success".equals(user1.getMessage()) || 0<=user1.getCode() && user1.getCode()<=10){
                        sp.add("username",username);
                        showToast("亲，您的昵称已经修改该成功了哟^_^");
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast("亲，修改该用户名失败了T_T，请检查网络");
            }
        });
    }
}
