package com.hotcast.vr;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.bean.Classify;
import com.hotcast.vr.bean.Update;
import com.hotcast.vr.pageview.SplashView;
import com.hotcast.vr.services.DownLoadingService;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Md5Utils;
import com.hotcast.vr.tools.TokenUtils;
import com.hotcast.vr.tools.Utils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;

/**
 * Created by lostnote on 16/1/12.
 */
public class SplashScapeActivity extends BaseActivity {
    @InjectView(R.id.container1)
    RelativeLayout container1;
    @InjectView(R.id.container2)
    RelativeLayout container2;
    private SplashView view1,view2;
    SplashActivity splashActivity;
    //下载路径
    private String spec ;
    private String newFeatures;
    //是否强制更新
    private String is_force;
    private PackageInfo info;
    @Override
    public int getLayoutId() {
        try {
            info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            System.out.println("--versioName = " + info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return R.layout.activity_vr_list;
    }

    @Override
    public void init() {
        Intent intent = new Intent(SplashScapeActivity.this, DownLoadingService.class);
        SplashScapeActivity.this.startService(intent);
        getUpDate();
        getNetDate();

        view1 = new SplashView(this);
        view2 = new SplashView(this);

        container1.removeAllViews();
        container2.removeAllViews();
        container1.addView(view1.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container2.addView(view2.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }

    private List<Classify> classifies;
    private void getNetDate() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(this));
        this.httpPost(Constants.URL_CLASSIFY_TITLTE, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.e("ClassifyView  responseInfo:" + responseInfo.result);
                classifies = new Gson().fromJson(responseInfo.result, new TypeToken<List<Classify>>() {
                }.getType());
                startJmp();
            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }

    private void getUpDate() {
//        System.out.println("---"+SplashActivity.getAppMetaData(this, "UMENG_CHANNEL"));
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(this));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform",BaseApplication.platform);
        this.httpPost(Constants.URL_UPDATE, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.e("DetailActivity responseInfo:" + responseInfo.result);
                setViewData(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }
    private String version;
    private void setViewData(String json){
        if (Utils.textIsNull(json)) {
            return;
        }
        Update update = new Gson().fromJson(json, Update.class);
        spec = update.getUrl();
        is_force = update.getIs_force();
        version = update.getVersion();
        System.out.println("--current = " + version);
        newFeatures = update.getLog();
        System.out.println("----SplashActivity spec:" + spec + ",force:" + is_force);
    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    private Timer timer;
    private void startJmp() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                pageJump();
            }

        }, 2500);
    }

    private void pageJump() {
        Intent intent = new Intent(this,LandscapeActivity.class);
        if (!info.versionName.equals(BaseApplication.version)) {
            BaseApplication.isUpdate = true;
            intent.putExtra("spec", spec);
            intent.putExtra("is_force", is_force);
            intent.putExtra("newFeatures",newFeatures);
        }
        intent.putExtra("classifies", BaseApplication.channel);
        startActivity(intent);
        finish();
    }

    @Override
    public void getIntentData(Intent intent) {
    }
}
