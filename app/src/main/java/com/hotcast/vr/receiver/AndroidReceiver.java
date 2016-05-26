package com.hotcast.vr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.bean.LocalBean2;
import com.hotcast.vr.download.DownLoadService;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

/**
 * Created by liurongzhi on 2016/3/31.
 */
public class AndroidReceiver extends BroadcastReceiver {
    String action;

    @Override
    public void onReceive(Context context, Intent intent) {
        action = intent.getAction();
        String url = intent.getStringExtra("url");
        switch (action) {
            case "startDownLoad":
                String title = intent.getStringExtra("title");
                String imgurl = intent.getStringExtra("imgurl");
                String vid = intent.getStringExtra("vid");
                int qingxidu = intent.getIntExtra("qingxidu", 1);
                String type = intent.getStringExtra("type");//视频种类
                System.out.println("---接收到的种类：" + type);
                DbUtils db = DbUtils.create(context);
                LocalBean2 localBean = new LocalBean2();
                localBean.setTitle(title);
                System.out.println("---接收到的title：" + title);
                localBean.setImage(imgurl);
                localBean.setId(url);
                localBean.setVid(vid);
                localBean.setUrl(url);
                localBean.setQingxidu(qingxidu);
                localBean.setCurState(0);//還沒下載，準備下載
                try {
                    db.saveOrUpdate(localBean);
                    BaseApplication.downLoadManager.addTask(url, url, title + ".mp4", BaseApplication.VedioCacheUrl + title + ".mp4");
                } catch (DbException e) {
                    System.out.println("---新添加的失败：" + e);
                    e.printStackTrace();
                }
                break;
            case "pauseDownLoad":
                BaseApplication.downLoadManager.stopTask(url);
                break;
            case "continueDownLoad":
                BaseApplication.downLoadManager.startTask(url);
                break;
            case "unitywork":
                DownLoadService.unitydoing = false;
                break;
        }
    }
}
