package com.hotcast.vr;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.hotcast.vr.asynctask.LocalVideosAsynctask;
import com.hotcast.vr.asynctask.TestMediaAsynctask;
import com.hotcast.vr.bean.LocalBean;
import com.hotcast.vr.bean.LocalBean1;
import com.hotcast.vr.bean.LocalBean2;
import com.hotcast.vr.bean.Update;
import com.hotcast.vr.bean.Updater;
import com.hotcast.vr.bean.User2;
import com.hotcast.vr.bean.UserData;
import com.hotcast.vr.download.DownLoadService;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Md5Utils;
import com.hotcast.vr.tools.SharedPreUtil;
import com.hotcast.vr.tools.TokenUtils;
import com.hotcast.vr.tools.Utils;
import com.hotcast.vr.u3d.UnityPlayerActivity;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.AnalyticsConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;

/**
 * Created by joey on 8/6/15.
 */
public class SplashActivity extends BaseActivity {
    @InjectView(R.id.vp_guide)
    ViewPager vp_guide;
    @InjectView(R.id.iv_start)
    ImageView iv_start;

    //    显示下载更新对话框
    protected static final int SHOW_UPDATE_DIALOG = 1;
    //    加载主UI界面
    private static final int LOAD_MAINUI = 2;
    //    包管理器
    private PackageManager packageManager;

    private String requestUrl;
    //下载路径
    private String spec;
    //更新日志
    private String newFeatures;
    //是否强制更新
    private String is_force;
    private int[] images = {R.mipmap.guide_1, R.mipmap.guide_2, R.mipmap.guide_3, R.mipmap.guide_4};

    private Timer timer;
    boolean isFrist;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    private PackageInfo info;

    @Override
    public void init() {
        AnalyticsConfig.enableEncrypt(true);
        packageManager = this.getPackageManager();
        try {
            info = packageManager.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        DbdateSave();
        new LocalVideosAsynctask(this).execute();
        new TestMediaAsynctask(this).execute();
        L.e("PackageName:" + getPackageName());
        getNetDate();
        String userData = sp.select("userData", "");
        System.out.println("---userData=" + userData);
        if (!TextUtils.isEmpty(userData)) {
            UserData userData1 = new Gson().fromJson(userData, UserData.class);
            if (userData1 != null) {
                System.out.println("---login_token=" + userData1.getLogin_token());
                getUserData(sp.select("login_token", ""));
            }
        }

        System.out.println("***sp=" + sp);

    }

    private void getUserData(String login_token) {
        requestUrl = Constants.INFO;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(this));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("login_token", login_token);
        System.out.println("---login_token=" + login_token);
        this.httpPost(requestUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                System.out.println("---responseInfo.result = " + responseInfo.result);
                try {
                    JSONObject j = new JSONObject(responseInfo.result);
                    String data = j.getString("data");
                    if (data.length() > 5) {
                        User2 user2 = new Gson().fromJson(responseInfo.result, User2.class);
                        System.out.println("---user2 = " + user2);
                        if ("success".equals(user2.getMessage()) || 0 <= user2.getCode() && 10 >= user2.getCode()) {
                            sp.add("userData", data);
//                            System.out.println("---add userData=" + data);
//                            System.out.println("---select userData=" + sp.select("userData", "**"));

                        } else {
                            showToast("亲," + user2.getMessage() + "^_^");
                            System.out.println("---message=" + user2.getMessage());
                        }
                    } else {
                        sp.delete("userData");
                        BaseApplication.isLogin = false;
                        String message = j.getString("message");
                        showToast("亲," + message + "^_^");
                        System.out.println("---message=" + message);
                    }
                } catch (JSONException e) {
                    System.out.println("--解析失败：" + e);
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast("亲，获取信息失败了T_T，请重检查网络");
            }
        });
    }

    public void DbdateSave() {
        DbUtils db = DbUtils.create(this);
        try {
            List<LocalBean> localBeens = db.findAll(LocalBean.class);
            List<LocalBean1> localBeens1 = db.findAll(LocalBean1.class);
            if (localBeens != null && localBeens.size() > 0) {
                for (int i = 0; i < localBeens.size(); i++) {
                    LocalBean2 l = new LocalBean2();
                    l.setQingxidu(1);
                    l.setTitle(localBeens.get(i).getTitle());
                    l.setLocalurl(localBeens.get(i).getLocalurl());
                    l.setId(localBeens.get(i).getId());
                    l.setCurState(3);
                    l.setVid("localbean");
                    l.setUrl(localBeens.get(i).getUrl());
                    l.setImage(localBeens.get(i).getImage());
                    if (localBeens.get(i).getLocalurl() != null) {
                        db.save(l);
                    }
                    System.out.println("***同步数据库：" + localBeens.get(i).getLocalurl() + "--" + localBeens.get(i).getUrl());
                }
                db.deleteAll(LocalBean.class);
            }
            if (localBeens1 != null && localBeens1.size() > 0) {
                for (int i = 0; i < localBeens1.size(); i++) {
                    LocalBean2 l = new LocalBean2();
                    l.setQingxidu(localBeens1.get(i).getQingxidu());
                    l.setTitle(localBeens1.get(i).getTitle());
                    l.setLocalurl(localBeens1.get(i).getLocalurl());
                    l.setId(localBeens1.get(i).getId());
                    l.setCurState(localBeens1.get(i).getCurState());
                    l.setVid("localbean");
                    l.setUrl(localBeens1.get(i).getUrl());
                    l.setImage(localBeens1.get(i).getImage());
                    if (localBeens1.get(i).getLocalurl() != null) {
                        db.save(l);
                    }
                    System.out.println("***同步数据库：" + localBeens1.get(i).getLocalurl() + "--" + localBeens1.get(i).getUrl());
                }
                db.deleteAll(LocalBean1.class);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    private void getNetDate() {
        requestUrl = Constants.URL_UPDATE;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(this));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        this.httpPost(requestUrl, params, new RequestCallBack<String>() {
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

    private void setViewData(String json) {
        if (Utils.textIsNull(json)) {
            return;
        } else {
            Updater updater = new Gson().fromJson(json, Updater.class);
            if ("success".equals(updater.getMessage()) || 0 <= updater.getCode() && updater.getCode() <= 10) {
                Update update = updater.getData();
                spec = update.getUrl();
                is_force = update.getIs_force();
                version = update.getVersion();
                System.out.println("--update = " + update);
                newFeatures = update.getLog();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void getIntentData(Intent intent) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        startJmp();
    }

    Handler shanler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            System.out.println("---第一次运行，显示引导页");
            vp_guide.setAdapter(new MyAdapter());
            vp_guide.setVisibility(View.VISIBLE);
        }
    };

    private void startJmp() {
        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("---启动unity");
//                startGoInUnity();
//            }
//        }, 2500);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isFrist = sp.select("isFrist", true);
                if (isFrist) {
                    shanler.sendEmptyMessage(0);

                } else {
                    vp_guide.setVisibility(View.GONE);
                    System.out.println("---启动mainactivity");
                    pageJump();
                }
            }
        }, 3000);
    }

    public void startGoInUnity() {
        Intent intent = new Intent(this, UnityPlayerActivity.class);
        SharedPreUtil.getInstance(this).add("nowplayUrl", "");
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void pageJump() {
        boolean isFrist1 = true;
        Intent intent = new Intent(this, MainActivity_new.class);
        if (isFrist) {
//            sp.getBooleanData(SplashActivity.this,"isFrist",false);
            L.e("***第一次运行" + isFrist + "显示声明");
            sp.add("isFrist", false);
            isFrist1 = true;
            if (!BaseApplication.version.equals(version)) {
                BaseApplication.isUpdate = true;
                intent.putExtra("spec", spec);
                intent.putExtra("is_force", is_force);
                intent.putExtra("newFeatures", newFeatures);
            }

        } else {
            L.e("***不是第一次运行" + isFrist + "不显示");
            isFrist1 = false;
            if (!BaseApplication.version.equals(version)) {

                BaseApplication.isUpdate = true;
                intent.putExtra("spec", spec);
                intent.putExtra("is_force", is_force);
                intent.putExtra("newFeatures", newFeatures);
            }
        }
        intent.putExtra("isFrist1", isFrist1);
        startActivity(intent);
        finish();
    }

    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = (ImageView) View.inflate(SplashActivity.this, R.layout.splash_item, null);
            System.out.println("--创建iv");
            iv.setBackgroundResource(images[position]);
            vp_guide.addView(iv);
            if (position == images.length - 1) {
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageJump();
                    }
                });
            }
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            object = null;

        }
    }

}
