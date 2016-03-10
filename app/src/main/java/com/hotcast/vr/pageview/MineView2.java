package com.hotcast.vr.pageview;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hotcast.vr.AboutActivity;
import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.HelpActivity;
import com.hotcast.vr.ListLocalActivity;
import com.hotcast.vr.LoginActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.ReNameActivity;
import com.hotcast.vr.RegistActivity;
import com.hotcast.vr.UpdateAppManager;
import com.hotcast.vr.bean.User1;
import com.hotcast.vr.bean.User2;
import com.hotcast.vr.bean.UserData;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.Md5Utils;
import com.hotcast.vr.tools.TokenUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.InjectView;

/**
 * Created by lostnote on 15/11/17.
 */
public class MineView2 extends BaseView implements View.OnClickListener {
    @InjectView(R.id.rl_cache)
    RelativeLayout rl_cache;
    @InjectView(R.id.rl_about)
    RelativeLayout rl_about;
    @InjectView(R.id.rl_version)
    RelativeLayout rl_version;
    @InjectView(R.id.rl_help)
    RelativeLayout rl_help;
    @InjectView(R.id.tv_title)
    TextView title;
    @InjectView(R.id.iv_head)
    ImageView iv_head;
    @InjectView(R.id.ll_login)
    LinearLayout ll_login;
    @InjectView(R.id.login)
    Button login;
    @InjectView(R.id.regist)
    Button regist;
    @InjectView(R.id.tv_username)
    TextView tv_username;
    Handler handler;
    @InjectView(R.id.rl_change)
    RelativeLayout rl_change;

    private UpdateAppManager updateAppManager;
    String spec;
    String is_force;
    String newFeatures;
    boolean ishowPopupWindow = false;

    public MineView2(BaseActivity activity, Handler mhandler) {
        super(activity, R.layout.layout_mine2);
        handler = mhandler;
    }


    @Override
    public void init() {
//        bitmapUtils = new BitmapUtils(activity);
//        date = activity.sp.select("userData","");
        iv_head.setOnClickListener(this);
        refreshView();
        title.setText("注销");
        spec = activity.sp.select("spec", "");
        is_force = activity.sp.select("is_force", "");
        newFeatures = activity.sp.select("newFeatures", "");
        if (bFirstInit) {
            initListView();
        }
        super.init();
    }

    UserData userDate;
    BitmapUtils bitmapUtils;
    String date;
    String username;
    String avatar;

    private void showMasseg() {
        if (!TextUtils.isEmpty(date)) {
            userDate = new Gson().fromJson(date, UserData.class);
            username = activity.sp.select("username", "");
            avatar = activity.sp.select("avatar", "");
            if (!TextUtils.isEmpty(username)) {
                tv_username.setText(username);
            } else {
                tv_username.setText(userDate.getUsername());
            }
            if (!TextUtils.isEmpty(avatar)) {
                bitmapUtils.display(iv_head, avatar);

            } else {
                bitmapUtils.display(iv_head, userDate.getAvatar());
            }
        }

//        tv_username.setText();
    }

    public void refreshView() {
        date = activity.sp.select("userData", "");
        bitmapUtils = new BitmapUtils(activity);
        if (!TextUtils.isEmpty(date)) {
            BaseApplication.isLogin = true;
        }
        if (BaseApplication.isLogin) {
            ll_login.setVisibility(View.GONE);
            tv_username.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            title.setOnClickListener(this);
            tv_username.setOnClickListener(this);
            showMasseg();
        } else {
            ll_login.setVisibility(View.VISIBLE);
            tv_username.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
        }
    }
    private void initListView() {
        login.setOnClickListener(this);
        regist.setOnClickListener(this);
        rl_cache.setOnClickListener(this);
        rl_about.setOnClickListener(this);
        rl_version.setOnClickListener(this);
        rl_help.setOnClickListener(this);
        rl_change.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rl_change:
                // TODO 点击跳转修改该密码
                intent = new Intent(activity, ReNameActivity.class);
                intent.putExtra("title","修改密码");
                activity.startActivity(intent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                System.out.println("---你点击了修改该密码");
                break;
            case R.id.tv_username:
                intent = new Intent(activity, ReNameActivity.class);
                intent.putExtra("title", "更改用户名");
                activity.startActivity(intent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                System.out.println("---您点击了用户名，你可以修改用户名");
                break;
            case R.id.login:
                intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                System.out.println("---您点击了登录按钮");
                break;
            case R.id.regist:
                intent = new Intent(activity, RegistActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                System.out.println("---您点击了注册按钮");
                break;

            case R.id.rl_cache:
                intent = new Intent(activity, ListLocalActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.rl_about:
                intent = new Intent(activity, AboutActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.rl_version:
                System.out.println("---你点击了监测版本更新");
                if (BaseApplication.isUpdate) {
                    updateAppManager = new UpdateAppManager(activity, spec, is_force, newFeatures);
                    updateAppManager.checkUpdateInfo();
                } else {
                    activity.showToast("您已经是最新版本");
                }
                break;
            case R.id.rl_help:
                intent = new Intent(activity, HelpActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.tv_title:
                logout(userDate.getLogin_token());
                break;
            case R.id.iv_head:
                if (BaseApplication.isLogin) {
                    showPopupWinow();
                } else {
                    Toast.makeText(activity, "亲，您还没有登陆哟~", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_pictue:
                //打开本地相册
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    System.out.println("---隐藏");
                    ishowPopupWindow = popupWindow.isShowing();
                    handler.sendEmptyMessage(0);
                    break;
                }
                break;
            case R.id.bt_camera:
                //打开相机
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    ishowPopupWindow = popupWindow.isShowing();
                    handler.sendEmptyMessage(1);
                }
                break;
        }
    }

    PopupWindow popupWindow;
    Button bt_camera;
    Button bt_pictue;

    public void showPopupWinow() {
        ishowPopupWindow = true;
        View view = LayoutInflater.from(activity).inflate(
                R.layout.popopwindow_mineview, null);
        bt_camera = (Button) view.findViewById(R.id.bt_camera);
        bt_pictue = (Button) view.findViewById(R.id.bt_pictue);
        bt_camera.setOnClickListener(this);
        bt_pictue.setOnClickListener(this);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(this.getRootView(), Gravity.CENTER | Gravity.BOTTOM, 0, 0);
    }

    private void logout(String login_token) {
        String mUrl = Constants.LOGOUT;
        RequestParams params = new RequestParams();
        System.out.println("--token= " + TokenUtils.createToken(activity));
        params.addBodyParameter("token", TokenUtils.createToken(activity));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("login_token", login_token);
        activity.httpPost(mUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                System.out.println("---responseInfo.result = " + responseInfo.result);
                User1 user1 = new Gson().fromJson(responseInfo.result, User1.class);
                if ("user1".equals(user1.getMessage()) || 0 <= user1.getCode() && 10 >= user1.getCode()) {
                    BaseApplication.isLogin = false;
                    iv_head.setImageResource(R.mipmap.head2);
                    activity.sp.delete("userData");
                    activity.sp.delete("username");
                    activity.sp.delete("avatar");
                    title.setVisibility(View.GONE);
                    refreshView();
                } else {
                    activity.showToast("亲，" + user1.getMessage() + "T_T");
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                activity.showToast("亲,注销失败了T_T，请检查一下网络");
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setHeadBitmap(Bitmap bt) {
        iv_head.setBackground(new BitmapDrawable(bt));
    }

}
