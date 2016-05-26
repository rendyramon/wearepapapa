package com.hotcast.vr.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.FileObserver;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.bean.LocalBean2;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

/**
 * Created by liurongzhi on 2016/1/21.
 */
public class FileCacheService extends Service {
    FileCacheLisenter fileCacheLisenter;
    SharedPreferences sp;
    DbUtils db;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = DbUtils.create(this);
        sp = getSharedPreferences("cache_config",Context.MODE_PRIVATE);
        fileCacheLisenter = new FileCacheLisenter(BaseApplication.VedioCacheUrl);
        fileCacheLisenter.startWatching();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    class FileCacheLisenter extends FileObserver {


        public FileCacheLisenter(String path) {
            super(path);
        }

        @Override
        public void onEvent(int event, String path) {
            switch (event) {
                case FileObserver.DELETE:
                    System.out.println("---DELETE:" + path);
                    try {
                        LocalBean2 localBean = db.findFirst(Selector.from(LocalBean2.class).where("localurl","=", BaseApplication.VedioCacheUrl+path));
                        if (localBean!=null){
                            db.delete(localBean);
                        }
                        System.out.println("---DELETE:" + localBean);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    break;
                case FileObserver.ALL_EVENTS:
                    if (!BaseApplication.cacheFileChange) {
                        BaseApplication.cacheFileChange = true;
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("cacheFileCache",true);
                        editor.commit();
                    }
                    break;
                case FileObserver.MODIFY:
                    break;
                case FileObserver.CREATE:
                    break;
            }

        }
    }

    @Override
    public void onDestroy() {
        if (fileCacheLisenter != null){
            fileCacheLisenter.stopWatching();
        }
        super.onDestroy();
    }
}
