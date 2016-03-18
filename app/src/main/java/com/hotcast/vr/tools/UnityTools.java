package com.hotcast.vr.tools;

import com.hotcast.vr.BaseApplication;

/**
 * Created by liurongzhi on 2016/3/16.
 */
public class UnityTools {
    public void startActivity() {

    }

    /**
     * 获取设备的ID
     *
     * @return
     */
    public String getDeviceID() {
        return BaseApplication.device;
    }

    /**
     * 获取应用的版本号
     *
     * @return
     */
    public String getAppversion() {
        return BaseApplication.version;
    }

    /**
     * 获取应用的包名
     *
     * @return
     */
    public String getPackagename() {
        return BaseApplication.packagename;
    }
}
