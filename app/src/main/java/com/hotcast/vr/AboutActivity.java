package com.hotcast.vr;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hotcast.vr.bean.User1;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.TokenUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;

import butterknife.InjectView;

/**
 * Created by lostnote on 15/12/2.
 */
public class AboutActivity extends BaseActivity {
    @InjectView(R.id.user_agreement)
    RelativeLayout user_agreement;
    @InjectView(R.id.head)
    RelativeLayout head;
    @InjectView(R.id.feedback)
    RelativeLayout feedback;
    @InjectView(R.id.tv_title)
    TextView tv_title;
    @InjectView(R.id.iv_return)
    ImageView iv_return;
    @InjectView(R.id.translucentview)
    FrameLayout advice;
    @InjectView(R.id.ll_tv)
    LinearLayout ll_tv;
    @InjectView(R.id.ll_advice)
    LinearLayout ll_advice;
    @InjectView(R.id.et_advice)
    EditText et_advice;
    @InjectView(R.id.bt_advice)
    Button bt_advice;
    @InjectView(R.id.bt_cancel)
    Button bt_cancel;
    @InjectView(R.id.tv_version)
    TextView tv_version;
    private boolean isEdet = false;
    private boolean isagreement = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_agreement:
                advice.setVisibility(View.VISIBLE);
                ll_tv.setVisibility(View.VISIBLE);
                ll_advice.setVisibility(View.GONE);
                isagreement = true;
                break;
            case R.id.feedback:
                advice.setVisibility(View.VISIBLE);
                ll_advice.setVisibility(View.VISIBLE);
                isEdet = true;
                InputMethodManager inputMethodManager = (InputMethodManager)et_advice.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(0,InputMethodManager.SHOW_FORCED);
                et_advice.setFocusable(true);
                et_advice.setFocusableInTouchMode(true);
                et_advice.requestLayout();
                break;
//            case R.id.et_advice:
//
//                break;
//            case R.id.bt_advice:
//                System.out.println("****你点击了发送信息");
//                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_about;
    }

    @Override
    public void init() {
        tv_title.setText("关于");
        tv_version.setText(BaseApplication.version);
        iv_return.setVisibility(View.VISIBLE);
        user_agreement.setOnClickListener(this);
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        feedback.setOnClickListener(this);
        bt_advice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendMsg = et_advice.getText().toString();
                if (!TextUtils.isEmpty(sendMsg)){
                    sendAgreement(sendMsg);
                }
                advice.setVisibility(View.GONE);
                ll_advice.setVisibility(View.GONE);
                isEdet = false;
                System.out.println("****你点击了发送信息");
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                advice.setVisibility(View.GONE);
                ll_advice.setVisibility(View.GONE);
                isEdet = false;
            }
        });
    }

    private void sendAgreement(String sendMsg) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(this));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("content", sendMsg);
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
                    JSONObject j = null;
                    try {
                        j = new JSONObject(responseInfo.result);
                        String state = j.getString("state");
                        if (!TextUtils.isEmpty(state) && "successful".equals(state)) {
                            System.out.println("****建议上传成功");
                            et_advice.setText("");
                            showToast("亲，感谢您的建议反馈，我们会更近努力的^_^");
                        } else {
                            showToast("亲，评论上传失败了T_T，请检查网络");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast("亲，建议反馈失败了T_T，请检查网络");
            }
        });
    }

    @Override
    public void getIntentData(Intent intent) {

    }
  private void saveFile(String toSaveString, String filePath){
      File saveFile = new File(filePath);
      if (!saveFile.exists()){
          try {File dir = new File(saveFile.getPath());
              dir.mkdirs();
              saveFile.createNewFile();
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }

    @Override
    public void onBackPressed() {
        if (isEdet || isagreement){
            advice.setVisibility(View.GONE);
            ll_tv.setVisibility(View.GONE);
            isagreement = false;
            isEdet = false;
        }else {
            super.onBackPressed();
        }
        System.out.println("****你点击返回键");
    }
}
