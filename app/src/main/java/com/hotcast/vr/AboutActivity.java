package com.hotcast.vr;

import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.io.File;
import java.io.IOException;

import butterknife.InjectView;

/**
 * Created by lostnote on 15/12/2.
 */
public class AboutActivity extends BaseActivity {
    @InjectView(R.id.user_agreement)
    RelativeLayout user_agreement;
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
                isagreement = true;
                break;
            case R.id.feedback:
                isEdet = true;
                Intent intent = new Intent(AboutActivity.this,FeedBackActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_about;
    }

    @Override
    public void init() {
        tv_title.setText(getResources().getString(R.string.mine_about));
        tv_version.setText(BaseApplication.version);
        iv_return.setVisibility(View.VISIBLE);
        user_agreement.setOnClickListener(this);
        iv_return.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        feedback.setOnClickListener(this);

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
