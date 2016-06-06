package com.hotcast.vr;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hotcast.vr.bean.User1;
import com.hotcast.vr.bean.UserData;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.TokenUtils;
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
    @InjectView(R.id.et_oldpassword)
    EditText et_oldpassword;
    @InjectView(R.id.et_newpassword)
    EditText et_newpassword;


    String data;
    UserData userData;
    String title;
    String mUrl;
    String username;
    String phone;
    String oldPassword;
    String newPassword;


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
        switch (title) {
            case "Change the password":
            case "修改密码":
                et_oldpassword.setVisibility(View.VISIBLE);
                et_newpassword.setVisibility(View.VISIBLE);
                et_username.setVisibility(View.GONE);
                bt_save.setEnabled(false);
                et_oldpassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() >= 6) {
                            et_newpassword.setEnabled(true);
                        }else {
                            et_newpassword.setEnabled(false);
                        }
                    }
                });
                et_newpassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() >= 6) {
                            bt_save.setEnabled(true);
                        }else {
                            bt_save.setEnabled(false);
//                            showToast(getResources().getString(R.string.et_password));
                        }
                    }
                });
                break;
            case "Change the username":
            case "更改用户名":
                et_username.setVisibility(View.VISIBLE);
                et_oldpassword.setVisibility(View.GONE);
                et_newpassword.setVisibility(View.GONE);
                et_username.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                       if (s.length()>0 && s.length()<15){
                           bt_save.setEnabled(true);
                       }else {
                           showToast("亲这样的用户名朋友们会记不住的呀#_#");
                           bt_save.setEnabled(false);
                       }
                    }
                });
                break;
        }
        data = sp.select("userData", "");
        if (!TextUtils.isEmpty(data)) {
            userData = new Gson().fromJson(data, UserData.class);
        }
    }


    @Override
    public void getIntentData(Intent intent) {
        title = intent.getStringExtra("title");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                finish();
                break;
            case R.id.bt_save:
                switch (title) {
                    case "修改密码":
                        oldPassword = et_oldpassword.getText().toString().trim();
                        newPassword = et_newpassword.getText().toString().trim();
                        if (!TextUtils.isEmpty(oldPassword) && !TextUtils.isEmpty(newPassword)){
                                saveNewPassword(oldPassword,newPassword);
                            bt_save.setEnabled(false);
                        }else {

                        }
                        break;
                    case "更改用户名":
                        username = et_username.getText().toString().trim();
                        if (!TextUtils.isEmpty(username)) {
                            saveNewUsername(username);
                            bt_save.setEnabled(false);
                        } else {
                            showToast("亲，用户名不能为空哦^_^");
                        }
                        break;
                }
                break;
        }
    }

    private void saveNewPassword(String oldPassword, String newPassword) {
        mUrl = Constants.CHANGPASSWORD;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(this));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("login_token", sp.select("login_token", ""));
        params.addBodyParameter("old_password", oldPassword);
        params.addBodyParameter("new_password", newPassword);
        this.httpPost(mUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                System.out.println("---responseInfo.result = " + responseInfo.result);
                if (!TextUtils.isEmpty(responseInfo.result)) {
                    User1 user1 = new Gson().fromJson(responseInfo.result, User1.class);
                    if ("success".equals(user1.getMessage()) || 0 <= user1.getCode() && user1.getCode() <= 10) {
                        showToast("亲，您的密码已经修改该成功了哟^_^");
                        finish();
                    }else {
                        bt_save.setEnabled(true);
                        showToast("亲，" + user1.getMessage() + "*_*");
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                bt_save.setEnabled(true);
                showToast("亲，修改该用户名失败了T_T，请检查网络");
            }
        });
    }

    private void saveNewUsername(final String username) {
        mUrl = Constants.RENAME;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(this));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("login_token", sp.select("login_token", ""));
        params.addBodyParameter("username", username);
        System.out.println("---login_token=" + sp.select("login_token","" ));
        this.httpPost(mUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                System.out.println("---responseInfo.result = " + responseInfo.result);
                if (!TextUtils.isEmpty(responseInfo.result)) {
                    User1 user1 = new Gson().fromJson(responseInfo.result, User1.class);
                    if ("success".equals(user1.getMessage()) || 0 <= user1.getCode() && user1.getCode() <= 10) {
                        sp.add("username", username);
                        showToast("亲，您的昵称已经修改该成功了哟^_^");
                        finish();
                    }else {
                        bt_save.setEnabled(true);
                        showToast("亲，"+user1.getMessage()+"^_^");
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                bt_save.setEnabled(true);
                showToast("亲，修改该用户名失败了T_T，请检查网络");
            }
        });
    }
}
