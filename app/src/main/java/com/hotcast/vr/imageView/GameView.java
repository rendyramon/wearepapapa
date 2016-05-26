package com.hotcast.vr.imageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hotcast.vr.R;

/**
 * Created by zhangjunjun on 2016/5/24.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback,
        Runnable {

    private SurfaceHolder holder;
    private Canvas canvas;
    private Bitmap bitmap;
    private boolean isRunning = true;
    private int dx;        //用于背景移动
    private int shebeiHight,shebeiWidth;
    private int alphe = 1;

    public GameView(Context context) {
        super(context);
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        shebeiHight = display.getHeight();
        shebeiWidth=display.getWidth();
        this.setFocusable(true);
        holder = this.getHolder();//这个this指的是这个Surface
        holder.addCallback(this); //这个this表示实现了Callback接口
        bitmap=getBitmap();

    }

    private Bitmap getBitmap(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//不加载bitmap到内存中
        BitmapFactory.decodeResource(getResources(),
                R.mipmap.guidepic, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 1;
        float width = (float) shebeiHight / (float) outHeight * outWidth;
        int imageWidth = Math.round(width);
        if (outWidth != 0 && outHeight != 0 && imageWidth != 0 && shebeiHight != 0) {
            int sampleSize = (outWidth / imageWidth + outHeight / shebeiHight) / 2;
            options.inSampleSize = sampleSize;
        }
        options.inJustDecodeBounds = false;
        dx = -(imageWidth-shebeiWidth);
        return BitmapFactory.decodeResource(getResources(),
                R.mipmap.guidepic, options);
    }


    @Override
    public void run() {
        while (isRunning) {
            drawView();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawView() {

        try {
            if (holder != null) {
                canvas = holder.lockCanvas();
                canvas.drawColor(Color.BLACK);
                //画背景, 并使其移动
                dx += 5;
                canvas.drawBitmap(bitmap, dx, 0, null);
                //判断是否到达屏幕底端, 到达了则使其回到屏幕上端
                if (dx >= 0)
                    isRunning = false;

                //画飞机
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null)
                holder.unlockCanvasAndPost(canvas);
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        new Thread(this).start();
        isRunning = true;
        //初始值必须放到这里才行, 因为SurfaceView创建成功后才能获取高宽

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            isRunning = false;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }


}
