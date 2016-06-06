package com.hotcast.vr.asynctask;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.bean.LocalBean2;
import com.hotcast.vr.tools.HotVedioCacheUtils;
import com.hotcast.vr.tools.SaveBitmapUtils;
import com.hotcast.vr.tools.VedioBitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.BitmapFactory.*;

/**
 * Created by liurongzhi on 2016/2/2.
 */
public class LocalVideosAsynctask extends AsyncTask<Integer, Integer, List<LocalBean2>> {
    Context context;
    private List<LocalBean2> dbList;

    public LocalVideosAsynctask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List doInBackground(Integer... params) {
        saveLocalVideoImage();
        DbUtils db = DbUtils.create(context);
        try {
            dbList = db.findAll(LocalBean2.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (dbList == null) {
            dbList = new ArrayList<>();
        }
        if (dbList != null) {
            System.out.println("---数据库原始尺寸：" + dbList.size());
        }
        String[] localNames = HotVedioCacheUtils.getVedioCache(BaseApplication.VedioCacheUrl);
        if (localNames != null) {
            int size = dbList.size();
            List<String> titles = new ArrayList<>();
            for (LocalBean2 localBean : dbList) {
                titles.add(localBean.getTitle());
            }
            for (int i = 0; i < localNames.length; i++) {
                String title = localNames[i];
                String title1 = title.replace(".mp4", "");

                if (size == 0) {
                    System.out.println("---title1" + title1);
                    LocalBean2 localBean = new LocalBean2();
                    localBean.setLocalurl(BaseApplication.VedioCacheUrl + localNames[i]);
                    localBean.setCurState(3);
                    SaveBitmapUtils.saveMyBitmap(title1, VedioBitmapUtils.getMiniVedioBitmap(BaseApplication.VedioCacheUrl + localNames[i]));
//                        System.out.println("---数据库没有数据。添加本地bitmap：" + VedioBitmapUtils.getMiniVedioBitmap(BaseApplication.VedioCacheUrl + localNames[i]));
                    localBean.setImage(BaseApplication.ImgCacheUrl + title1 + ".jpg");
                    localBean.setUrl("");
                    localBean.setId(BaseApplication.VedioCacheUrl + localNames[i]);
                    localBean.setTitle(title1);
//                    dbList.add(localBean);
                    try {
                        db.saveOrUpdate(localBean);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!titles.contains(title1)) {
                        System.out.println("---不为空title1" + title1);
                        LocalBean2 localBean = new LocalBean2();
                        localBean.setLocalurl(BaseApplication.VedioCacheUrl + localNames[i]);
                        localBean.setCurState(3);
                        SaveBitmapUtils.saveMyBitmap(title.replace(".mp4", ""), VedioBitmapUtils.getMiniVedioBitmap(BaseApplication.VedioCacheUrl + localNames[i]));
                        localBean.setImage(BaseApplication.ImgCacheUrl + title1 + ".jpg");
                        localBean.setUrl("");
                        localBean.setTitle(title1);
                        localBean.setId(BaseApplication.VedioCacheUrl + localNames[i]);
//                        dbList.add(localBean);
                        try {
                            db.saveOrUpdate(localBean);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        System.out.println("--" + title);
                    }
                }
            }
        }
        System.out.println("----数据处理完毕");
        BaseApplication.doAsynctask = true;
        return dbList;
    }

    public void saveLocalVideoImage() {
        File file = new File(BaseApplication.ImgCacheUrl);
        if (!file.exists()) {
            file.mkdirs();
        }
        final Options options = new Options();
        options.inDither = false;
        switch (options.inPreferredConfig = Bitmap.Config.ARGB_8888) {
        }
        final ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME};
        final Cursor cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Video.Media.MIME_TYPE + "='video/mp4'",
                null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();
        int fileNum = cursor.getCount();
        for (int counter = 0; counter < fileNum; counter++) {
            final String path = cursor.getString(0);
            long size = Long.parseLong(cursor.getString(1)) / (1024 * 1024);
            if (size > 10 && !path.contains("/hostcast/vr/")) {
                final String VideoName = cursor.getString(3);
                final long videoId = Long.parseLong(cursor.getString(2));

                final Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, videoId,
                        MediaStore.Images.Thumbnails.MINI_KIND, options);
                saveBitmap(VideoName.replace(".mp4", ".jpg"), bitmap);
            }
            cursor.moveToNext();
        }

        cursor.close();

    }

    public void saveBitmap(String name, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        System.out.println("---保存图片：" + name);
        File f = new File(BaseApplication.ImgCacheUrl, name);
        if (f.exists()) {
            return;
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    @Override
    protected void onPostExecute(List<LocalBean2> s) {
//            super.onPostExecute(s);
    }
}

