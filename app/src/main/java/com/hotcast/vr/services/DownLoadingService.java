package com.hotcast.vr.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.bean.Details;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DownLoadingService extends Service {
    HttpUtils httpUtils;
    final String START = "START";
    final String DOWNLOADING = "DOWNLOADING";
    final String FINISH = "FINISH";
    final String PAUSE = "PAUSE";
    Map<String, HttpHandler> handlers;

    public DownLoadingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DownLoadingReceiver receiver = new DownLoadingReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(START);
        filter.addAction(DOWNLOADING);
        filter.addAction(FINISH);
        filter.addAction(PAUSE);
        registerReceiver(receiver, filter);
        httpUtils = new HttpUtils();
        handlers = new HashMap<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class DownLoadingReceiver extends BroadcastReceiver {
        HttpHandler handler;

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equals(START) && BaseApplication.detailsList.size() > 0) {
//                    Details details = (Details) intent.getSerializableExtra("Details");
                final Details details = BaseApplication.detailsList.get(0);
                final String localUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hostcast/vr/" + details.getTitle() + ".mp4";
//                final String play_url = intent.getStringExtra("play_url");
                final String play_url = BaseApplication.playUrls.get(0);
                handler = httpUtils.download(play_url, localUrl, true, true, new RequestCallBack<File>() {
                    String nowurl = play_url;
                    String localurl = localUrl;
                    Details nowDetali = details;

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        BaseApplication.playUrls.remove(nowurl);
                        BaseApplication.detailsList.remove(nowDetali);
                        Intent intent = new Intent(FINISH);
                        intent.putExtra("play_url", nowurl);
                        intent.putExtra("localurl", localurl);
                        sendBroadcast(intent);
                        Intent intent2 = new Intent(START);
                        sendBroadcast(intent2);

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStart() {
                        Toast.makeText(context, "开始下载", Toast.LENGTH_SHORT).show();
                        super.onStart();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        Intent intent = new Intent(DOWNLOADING);
                        intent.putExtra("total", total);
                        intent.putExtra("current", current);
                        intent.putExtra("play_url", play_url);
                        sendBroadcast(intent);
//                            Toast.makeText(context,current+"/"+total,Toast.LENGTH_SHORT).show();
                    }
                });
                handlers.put(play_url, handler);
            } else if (intent.getAction().equals(PAUSE)) {
//                    String play_url = intent.getStringExtra("play_url");
//                    HttpHandler handler = handlers.get(play_url);
                //停止下載
                if (handler!=null){
                    handler.cancel();
                }
            }
        }
    }
}
