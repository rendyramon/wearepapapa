package com.hotcast.vr;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hotcast.vr.bean.User1;
import com.hotcast.vr.bean.User2;
import com.hotcast.vr.tools.Constants;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

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
    @InjectView(R.id.bt_next)
    Button bt_next;
    @InjectView(R.id.et_phone)
    EditText et_phone;
    @InjectView(R.id.et_verificationcode)
    EditText et_verificationcode;
    @InjectView(R.id.et_password)
    EditText et_password;
    @InjectView(R.id.bt_yes)
    Button bt_yes;

    String phone;
    String password;
    String verificationcode;
    String mUrl =null;
    User1 user1;




    @OnClick({R.id.iv_return,R.id.bt_verificationcode,R.id.bt_next,R.id.bt_yes})
    void clickType(View v) {
        switch (v.getId()){
            case R.id.bt_yes :
                password = et_password.getText().toString();
                if (!TextUtils.isEmpty(password) && password.length() >= 6){
                    System.out.println("---password = " + password);
                    rePassword(password,phone);
                }
                break;
            case R.id.iv_return :
                finish();
                break;
            case R.id.bt_verificationcode :
                //发送手机号到服务器
                phone = et_phone.getText().toString();
                System.out.println("---phone = " + phone.length());
                if (!TextUtils.isEmpty(phone) && phone.length() == 11 || isMobileNo(phone)){
                    getVerificationCode(phone);
                }else {
                    bt_verificationcode.setEnabled(false);
                    showToast("亲，请输入正确的手机号码哟^_^");
                }

                break;
            case R.id.bt_next :
                //跳转到第二个regist页面
                verificationcode = et_verificationcode.getText().toString();
                if (!TextUtils.isEmpty(verificationcode) && verificationcode.length() == 6 ){
                    sendVerify(verificationcode,phone);
                }else {
                    showToast("亲，请输入正确的验证^_^，请重输入");
                }

                break;


        }
    }
    User2 user2;
    private void rePassword(String password, String phone) {
        mUrl = Constants.REGIST;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", "123");
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("phone",phone);
        params.addBodyParameter("password",password);
        this.httpPost(mUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                JSONObject j = null;
                try {
                    j = new JSONObject(responseInfo.result);
                    JSONObject data = j.getJSONObject("data");
                    user2 = new Gson().fromJson(responseInfo.result,User2.class);
                    System.out.println("---user2="+user2);
                    if ("success".equals(user2.getMessage())|| 0<=user2.getCode() && 10>= user2.getCode()){
                        BaseApplication.isLogin = true;
                        sp.add("userData", data.toString());
                        showToast("亲，注册成功了哟^_^，快去看片儿吧");
                        finish();
                    }else {
                        showToast("亲，密码不要输入特殊字符哦*_*");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast("亲，密码不要输入特殊字符哦*_*");
//                bt_verificationcode.setEnabled(true);
            }
        });
    }

    private void sendVerify(String verificationcode,String phone) {
        mUrl = Constants.CHECKMESSAG;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", "123");
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("phone",phone);
        params.addBodyParameter("code",verificationcode);
        this.httpPost(mUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                user1 = new Gson().fromJson(responseInfo.result,User1.class);
                if ("success".equals(user1.getMessage())|| 0<=user1.getCode() && 10>= user1.getCode()){
                    ll_regist1.setVisibility(View.GONE);
                    ll_regist2.setVisibility(View.VISIBLE);
                }else {
                    showToast("亲，请输入正确的验证^_^，请重输入");
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast("亲，请输入正确的验证^_^，请重输入");
            }
        });
    }

    private void getVerificationCode(String phone) {
        mUrl = Constants.SENDMESSAG;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", "123");
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("phone",phone);
        this.httpPost(mUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                user1 = new Gson().fromJson(responseInfo.result, User1.class);
                System.out.println("---user1 = " + user1);
                if ("success".equals(user1.getMessage()) || 0 <= user1.getCode() && 10 >= user1.getCode()) {
                    bt_verificationcode.setEnabled(false);

                } else {
                    showToast("亲," + user1.getMessage() +"^_^");
                    bt_verificationcode.setEnabled(true);
                }
            }
            @Override
            public void onFailure(HttpException e, String s) {
                showToast("亲，验证码获取失败T_T，请重新获取");
                bt_verificationcode.setEnabled(true);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_regist;
    }

    @Override
    public void init() {
        ll_regist1.setVisibility(View.VISIBLE);
        ll_regist2.setVisibility(View.GONE);
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6){
                    bt_yes.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_verificationcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6){
                    bt_next.setEnabled(true);
                }else {
                    bt_next.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                System.out.println("---CharSequence="+s+" start="+start+" after="+after+" count="+count);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 11){
                    bt_verificationcode.setEnabled(true);
                }else {
                    bt_verificationcode.setEnabled(false);
                }
//                System.out.println("---CharSequence="+s+" start="+start+" before="+before+" count="+count);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                System.out.println("---CharSequence="+s);
            }
        });

    }

    @Override
    public void getIntentData(Intent intent) {

    }
}
