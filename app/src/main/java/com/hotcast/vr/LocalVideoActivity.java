package com.hotcast.vr;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dlodlo.dvr.sdk.unity.DvrUnityActivity;
import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.asynctask.LocalVideosAsynctask;
import com.hotcast.vr.bean.ListBean;
import com.hotcast.vr.bean.LocalBean2;
import com.hotcast.vr.bean.LocalVideoBean;
import com.hotcast.vr.tools.SharedPreUtil;
import com.hotcast.vr.tools.UnityTools;
import com.hotcast.vr.u3d.UnityPlayerActivity;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zhangjunjun on 2016/5/9.
 */
public class LocalVideoActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    @InjectView(R.id.tv_title)
    TextView title;
    @InjectView(R.id.iv_return)
    ImageView iv_return;
    @InjectView(R.id.lv)
    GridView lv;
    @InjectView(R.id.bt_editor)
    Button bt_editor;
    ArrayList<LocalVideoBean> list;
    BitmapUtils bitmapUtils;

    @OnClick(R.id.iv_return)
    void onReturn() {
        finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_local_list;
    }

    @Override
    public void init() {
        bitmapUtils = new BitmapUtils(this);
        list = new ArrayList<>();
        bt_editor.setVisibility(View.GONE);
        title.setText(getResources().getString(R.string.local_video));
        iv_return.setVisibility(View.VISIBLE);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    100);
        } else {
            getLocalVideo();
            initListView();
        }


    }


    public void getLocalVideo() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        switch (options.inPreferredConfig = Bitmap.Config.ARGB_8888) {
        }
        final ContentResolver contentResolver = getContentResolver();
        String[] projection = new String[]{MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME};


        final Cursor cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Video.Media.MIME_TYPE + "='video/mp4'",
                null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();
        int fileNum = cursor.getCount();

        for (int counter = 0; counter < fileNum; counter++) {
            final LocalVideoBean localBean2 = new LocalVideoBean();
            final String path = cursor.getString(0);
            long size = Long.parseLong(cursor.getString(1)) / (1024 * 1024);
            if (size > 10 && !path.contains(BaseApplication.VedioCacheUrl)) {
                localBean2.setVideoPath(path);
                final String VideoName = cursor.getString(3);
                localBean2.setVideoName(VideoName.replace(".mp4", ""));
                localBean2.setImagePath(BaseApplication.ImgCacheUrl + VideoName.replace(".mp4", ".jpg"));
                list.add(localBean2);
            }

            cursor.moveToNext();
        }

        cursor.close();

    }

    private void initListView() {
        lv.setAdapter(new LocalVideoAdapter());
        lv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String localurl = list.get(i).getVideoPath();

                File file;
                if (localurl != null) {
                    file = new File(localurl);
                } else {
                    file = new File(" ");
                }
                System.out.println("===" + localurl);
                if (file.exists()) {
                    Intent intent;
                    if (UnityTools.getGlasses().equals("1")) {
                        intent = new Intent(LocalVideoActivity.this, DvrUnityActivity.class);
                    } else {
                        intent = new Intent(LocalVideoActivity.this, UnityPlayerActivity.class);
                    }
                    SharedPreUtil.getInstance(LocalVideoActivity.this).add("nowplayUrl", "file://" + localurl);
                    SharedPreUtil.getInstance(LocalVideoActivity.this).add("qingxidu", "0");
                    SharedPreUtil.getInstance(LocalVideoActivity.this).add("sdurl", "");
                    SharedPreUtil.getInstance(LocalVideoActivity.this).add("hdrul", "");
                    SharedPreUtil.getInstance(LocalVideoActivity.this).add("uhdrul", "");
                    if (localurl.contains("_3d_interaction")) {
                        SharedPreUtil.getInstance(LocalVideoActivity.this).add("type", "3d");
                    } else if (localurl.contains("_vr_interaction")) {
                        SharedPreUtil.getInstance(LocalVideoActivity.this).add("type", "vr_interaction");
                    } else if (localurl.contains("_3d_noteraction")) {
                        SharedPreUtil.getInstance(LocalVideoActivity.this).add("type", "3d_noteraction");
                    } else {
                        SharedPreUtil.getInstance(LocalVideoActivity.this).add("type", "vr");
                    }
                    LocalVideoActivity.this.startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocalVideo();
                    initListView();

                }

                return;
            }


        }
    }

    class LocalVideoAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final LocalVideoBean bean = list.get(position);
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(LocalVideoActivity.this, R.layout.huancun_grid_item, null);
                holder.tv_finish = (TextView) convertView.findViewById(R.id.tv_finish);
                holder.iv_huancun_img = (ImageView) convertView.findViewById(R.id.iv_huancun_img);
                holder.iv_huancun_sd = (ImageView) convertView.findViewById(R.id.iv_huancun_sd);
                holder.ib_delete = (ImageView) convertView.findViewById(R.id.ib_delete);
                holder.tv_huancun_downpecent = (TextView) convertView.findViewById(R.id.tv_huancun_downpecent);
                holder.tv_huancun_downspeed = (TextView) convertView.findViewById(R.id.tv_huancun_downspeed);
                holder.tv_huancun_moviename = (TextView) convertView.findViewById(R.id.tv_huancun_moviename);
                holder.ll_huancun_down = (LinearLayout) convertView.findViewById(R.id.ll_huancun_down);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.ll_huancun_down.setVisibility(View.INVISIBLE);
            holder.tv_huancun_moviename.setText(bean.getVideoName());
            holder.tv_finish.setVisibility(View.GONE);
            holder.iv_huancun_sd.setVisibility(View.INVISIBLE);
            holder.iv_huancun_sd.setBackgroundResource(R.mipmap.huancun_sb);
            holder.tv_huancun_downspeed.setVisibility(View.INVISIBLE);
            holder.tv_huancun_downpecent.setVisibility(View.INVISIBLE);
            holder.iv_huancun_img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            bitmapUtils.display(holder.iv_huancun_img, bean.getImagePath());
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_finish;
        ImageView iv_huancun_img;//預覽圖
        ImageView iv_huancun_sd;//暫停或下載中
        TextView tv_huancun_downpecent;//下載進度
        TextView tv_huancun_downspeed;//下載速度
        TextView tv_huancun_moviename;//影片名稱
        ImageView ib_delete;
        LinearLayout ll_huancun_down;
    }

    @Override
    public void getIntentData(Intent intent) {

    }


}
