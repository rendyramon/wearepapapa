package com.hotcast.vr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hotcast.vr.adapter.MyPagerAdapter;
import com.hotcast.vr.bean.UserData;
import com.hotcast.vr.pageview.BaseView;
import com.hotcast.vr.pageview.ClassifyView;
import com.hotcast.vr.pageview.HomeView2;

import com.hotcast.vr.pageview.MineView;
import com.hotcast.vr.pageview.MineView2;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.FileUtils;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.TokenUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;


/**
 * Created by lostnote on 15/11/17.
 */
public class MainActivity_new extends BaseActivity {
    @InjectView(R.id.layout_content)
    MyViewPager content;

    @InjectView(R.id.main_radio)
    RadioGroup radioGroup;


    @InjectView(R.id.rl_agreement)
    ScrollView rl_agreement;
    @InjectView(R.id.cb_agreement)
    CheckBox cb_agreement;
    @InjectView(R.id.iv_noNet)
    ImageView nonet;

    private int curTabIndex = -1;

    private MyPagerAdapter adapter;


    private String message = "检测到本程序有新版本发布，建议您更新！";

    private BaseView view0, view1, view2;
    private BaseView[] views = new BaseView[3];
    private List<View> vs;
    private List<String> titles;
    private int[] checkedId = {R.id.page_home, R.id.page_classify, R.id.page_mine};
    private UpdateAppManager updateAppManager;
    String newFeatures;

    @Override
    public int getLayoutId() {
        return R.layout.layout_main;
    }

    @Override
    public void init() {
        L.e("是否有网络" + isNetworkConnected(this) + "---" + isWifiConnected(this) + "---" + isMobileConnected(this));
        if ((isWifiConnected(this) || isMobileConnected(this)) && isNetworkConnected(this)) {
            nonet.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(spec)) {
                updateAppManager = new UpdateAppManager(this, spec, is_force, newFeatures);
                updateAppManager.checkUpdateInfo();
            }
            if (isFrist1) {
                System.out.println("***显示免责声明同时提示用户操作帮助");
                rl_agreement.setVisibility(View.VISIBLE);

            }
            view0 = new HomeView2(this);
            view1 = new ClassifyView(this);
            view2 = new MineView2(this, mhandler);
            views[0] = view0;
            views[1] = view1;
            views[2] = view2;

            vs = new ArrayList<View>();
            vs.add(view0.getRootView());
            vs.add(view1.getRootView());
            vs.add(view2.getRootView());
            adapter = new MyPagerAdapter(vs);
            titles = new ArrayList<String>();
            titles.add("热播");
            titles.add("分类");
            titles.add("我的");
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    switch (checkedId) {
                        case R.id.page_home:
//                            title.setText(titles.get(0));
                            content.setCurrentItem(0);
                            break;
                        case R.id.page_classify:
//                            title.setText(titles.get(1));
                            content.setCurrentItem(1);
                            break;
                        case R.id.page_mine:
//                            title.setText(titles.get(2));
                            content.setCurrentItem(2);
                            break;
                    }
                }
            });
            radioGroup.check(R.id.page_home);
            content.setAdapter(adapter);

            content.setOnPageChangeListener(new LazyViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    BaseView baseView = views[position];
                    baseView.getRootView();

                }

                @Override
                public void onPageSelected(int position) {
//                    title.setText(titles.get(position));
                    radioGroup.check(checkedId[position]);
                    clickTab(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            clickTab(0);
            cb_agreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rl_agreement.setVisibility(View.GONE);
                }
            });
        } else {
            nonet.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            nonet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    getLayoutId();
//                    getIntent();
                    init();
                }
            });
        }
    }

    private void clickTab(int index) {
        if (index != curTabIndex) {
            changeViewIndex(index);
            curTabIndex = index;
        }
    }


    private void changeViewIndex(int index) {
        views[index].init();
    }

    //下载路径
    private String spec;
    //是否强制更新
    private String is_force;
    private boolean isFrist1;

    @Override
    public void getIntentData(Intent intent) {
        spec = getIntent().getStringExtra("spec");
        is_force = getIntent().getStringExtra("is_force");
        newFeatures = getIntent().getStringExtra("newFeatures");
        isFrist1 = getIntent().getBooleanExtra("isFrist1", isFrist1);
        System.out.println("***isFrist = " + isFrist1);
        sp.add("spec", spec);
        sp.add("is_force", is_force);
        System.out.println("---spec = " + spec + "is_force = " + is_force);
    }

    @Override
    protected void onStart() {
        ((MineView2) view2).refreshView();
        super.onStart();
    }

    private boolean bExitting;

    @Override
    public void onBackPressed() {
        if (!bExitting) {
            bExitting = true;
            showToast("再按一次退出");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    bExitting = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();

        }
    }

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                    startActivityForResult(intent, ALBUM_REQUEST_CODE);
                    break;
                case 1:
                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.
                            getExternalStorageDirectory(), "vrhotcastuser.jpg")));
                    startActivityForResult(intent2, CAMERA_REQUEST_CODE);
                    break;
                case 2:
                    ((MineView2) view2).refreshView();
                    break;
            }
        }
    };
    private static final String TAG = "---";
    private static final int ALBUM_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int CROP_REQUEST_CODE = 4;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ALBUM_REQUEST_CODE:
                Log.i(TAG, "相册，开始裁剪");
                Log.i(TAG, "相册 [ " + data + " ]");
                if (data == null) {
                    return;
                }
                startCrop(data.getData());
                break;
            case CAMERA_REQUEST_CODE:
                Log.i(TAG, "相机, 开始裁剪");
                File picture = new File(Environment.getExternalStorageDirectory()
                        + "/vrhotcastuser.jpg");
                startCrop(Uri.fromFile(picture));
                break;
            case CROP_REQUEST_CODE:
                Log.i(TAG, "相册裁剪成功");
                Log.i(TAG, "裁剪以后 [ " + data + " ]");
                if (data == null) {
                    // TODO 如果之前以后有设置过显示之前设置的图片 否则显示默认的图片
                    return;
                }
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0-100)压缩文件
                    //此处可以把Bitmap保存到sd卡中，具体请看：http://www.cnblogs.com/linjiqin/archive/2011/12/28/2304940.html
                    bitmap = photo;
                    ((MineView2) view2).setHeadBitmap(photo);
                    new SaveAsyncTask().execute();
                    //把图片显示在ImageView控件上
                }
                break;
            default:
                break;
        }
    }

    private Bitmap bitmap;
    private static final String IMAGE_UNSPECIFIED = "image/*";

    /**
     * 开始裁剪
     *
     * @param uri
     */
    private void startCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");//调用Android系统自带的一个图片剪裁页面,
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");//进行修剪
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    class SaveAsyncTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
            FileUtils.saveBitmap(bitmap, "vrhotcastuser");
            String login_token = new Gson().fromJson(sp.select("userData", ""), UserData.class).getLogin_token();
            RequestParams params1 = new RequestParams();
            params1.addBodyParameter("token", TokenUtils.createToken(MainActivity_new.this));
            params1.addBodyParameter("version", BaseApplication.version);
            params1.addBodyParameter("platform", BaseApplication.platform);
            params1.addBodyParameter("login_token", login_token);
            params1.addBodyParameter("UploadAvatarForm[avatar]", new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "vrhotcastuser.jpg"));
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.send(HttpRequest.HttpMethod.POST, Constants.UPHEAD, params1, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    System.out.println("----------上传头像成功" + responseInfo);
                    try {
                        JSONObject j = new JSONObject(responseInfo.result);
                        JSONObject data = j.getJSONObject("data");
                        String avatar = data.getString("avatar_url");
                        sp.add("avatar",avatar);
                        mhandler.sendEmptyMessage(2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException error, String msg) {
                    System.out.println("------" + error + "----上传头像失败：" + msg);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }
}
