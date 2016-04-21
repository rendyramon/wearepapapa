package com.hotcast.vr.asynctask;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;

import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.R;
import com.hotcast.vr.bean.LocalBean2;
import com.hotcast.vr.tools.HotVedioCacheUtils;
import com.hotcast.vr.tools.SaveBitmapUtils;
import com.hotcast.vr.tools.SharedPreUtil;
import com.hotcast.vr.tools.VedioBitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liurongzhi on 2016/2/2.
 */
public class TestMediaAsynctask extends AsyncTask<Integer, Integer, Bitmap> {
    Context context;

    public TestMediaAsynctask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
        System.out.println("---开始监测手机兼容信息");
        //SD卡路径
        String out = BaseApplication.ImgCacheUrl
                + "/" + "1.mp4";//图片名称
        File outfile = new File(out);
        OutputStream outputStream = null;
        InputStream in = null;
        if (!outfile.exists()) {
            System.out.println("---不存在，创建并开始写入");
            try {
                outfile.createNewFile();
                outputStream = new FileOutputStream(outfile);
                in = context.getResources().openRawResource(R.raw.test);
                byte[] buffer = new byte[1024 * 2];
                int b = 0;
                while ((b = in.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, b);
                    outputStream.flush();
                }
            } catch (IOException e) {
                System.out.println("---异常" + e);
                e.printStackTrace();
            } finally {
                try {
                    outputStream.close();
                    in.close();
                    System.out.println("---结束");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return VedioBitmapUtils.getMiniVedioBitmap(out);

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
//        super.onPostExecute(bitmap);
        if (bitmap != null && bitmap.getRowBytes() > 0) {
            System.out.println("---手机不错哟");
            SharedPreUtil.getInstance(context).add("islow", false);
        } else {
            System.out.println("---手机太渣了");
            SharedPreUtil.getInstance(context).add("islow", true);
        }
    }
}

