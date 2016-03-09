package com.hotcast.vr.tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.hotcast.vr.BaseApplication;

import java.text.SimpleDateFormat;

/**
 * Created by liurongzhi on 2016/3/9.
 */
public class TokenUtils {
    public static SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH");

    public static String createToken(Context context) {
        String token = null;
        if (BaseApplication.platform == null || BaseApplication.platform.equals("")) {
            BaseApplication.platform = getAppMetaData(context, "UMENG_CHANNEL");
        }
        if ("android".equals(BaseApplication.platform)) {
            //主线版
            token = Md5Utils.getMd5("hotcast-" + format.format(System.currentTimeMillis()) + "-hotcast");
        } else {
            //其他渠道包
            token = Md5Utils.getMd5(BaseApplication.platform + "-" + format.format(System.currentTimeMillis()));
        }
        return token;
    }

    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {

                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }
}
