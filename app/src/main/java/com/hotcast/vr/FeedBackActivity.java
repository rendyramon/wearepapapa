package com.hotcast.vr;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hotcast.vr.bean.User1;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.TokenUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import butterknife.InjectView;

/**
 * Created by lostnote on 16/3/18.
 */
public class FeedBackActivity extends BaseActivity {
    @InjectView(R.id.et_advice)
    EditText et_advice;
    @InjectView(R.id.bt_advice)
    Button bt_advice;
    @InjectView(R.id.bt_cancel)
    Button bt_cancel;
    @InjectView(R.id.et_contact)
    EditText et_contact;
    @InjectView(R.id.iv_return)
    ImageView iv_return;
    @InjectView(R.id.tv_title)
    TextView tv_title;

    String contact;

    @Override
    public int getLayoutId() {
        return R.layout.layout_feedback;
    }

    @Override
    public void init() {
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_title.setText("反馈");
        InputMethodManager inputMethodManager = (InputMethodManager)et_advice.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0,InputMethodManager.SHOW_FORCED);
        bt_advice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendMsg = et_advice.getText().toString();
                contact = et_contact.getText().toString();
                if (!TextUtils.isEmpty(sendMsg)){
                    sendAgreement(sendMsg,contact);
                }
//                advice.setVisibility(View.GONE);
//                ll_advice.setVisibility(View.GONE);
//                isEdet = false;
                System.out.println("****你点击了发送信息");
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void getIntentData(Intent intent) {

    }
    private void sendAgreement(String sendMsg,String contact) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(this));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("content", "评论内容："+sendMsg+"\n联系方式："+contact);
        params.addBodyParameter("app_version", BaseApplication.version);
        params.addBodyParameter("package", BaseApplication.packagename);
        params.addBodyParameter("device", BaseApplication.device);
        this.httpPost(Constants.FEEDBACK, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                System.out.println("---responseInfo.result = " + responseInfo.result);
                if (!TextUtils.isEmpty(responseInfo.result)) {
                    User1 user1 = new Gson().fromJson(responseInfo.result, User1.class);
                    if ("successful".equals(user1.getMessage()) || 0 <= user1.getCode() && user1.getCode() <= 10) {
                        System.out.println("****建议上传成功");
                        et_advice.setText("");
                        et_contact.setText("");
                        showToast("亲，感谢您的建议反馈，我们会更加努力的^_^");
                    } else {
                        showToast("亲，评论上传失败了T_T，请重新上传一下下哟");
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast("亲，建议反馈失败了T_T，请检查网络");
            }
        });
    }

}
