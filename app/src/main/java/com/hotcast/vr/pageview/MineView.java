package com.hotcast.vr.pageview;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotcast.vr.AboutActivity;
import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.DetailActivity;
import com.hotcast.vr.HelpActivity;
import com.hotcast.vr.ListLocalActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.UpdateAppManager;
import com.hotcast.vr.tools.SharedPreUtil;

import butterknife.InjectView;

/**
 * Created by lostnote on 15/11/17.
 */
public class MineView extends BaseView implements View.OnClickListener {
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

    private UpdateAppManager updateAppManager;
    String spec;
    String is_force;
    String newFeatures;

    public MineView(BaseActivity activity) {
        super(activity, R.layout.layout_mine);
    }

    @Override
    public void init() {
        title.setText("注销");
        spec = activity.sp.select("spec", "");
        is_force = activity.sp.select("is_force", "");
        newFeatures = activity.sp.select("newFeatures", "");
        if (bFirstInit) {
            initListView();
        }
        super.init();
    }

    private void initListView() {
        rl_cache.setOnClickListener(this);
        rl_about.setOnClickListener(this);
        rl_version.setOnClickListener(this);
        rl_help.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rl_cache:

                intent = new Intent(activity, ListLocalActivity.class);
                activity.startActivity(intent);
                break;
            case R.id.rl_about:

                intent = new Intent(activity, AboutActivity.class);
                activity.startActivity(intent);
                break;
            case R.id.rl_version:
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
                break;
        }
    }
}
