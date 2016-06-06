package com.hotcast.vr.bean;

/**
 * Created by liurongzhi on 2016/6/1.
 * 向播放器传递地址的类
 */
public class LocalPlay {
    String id = "LocalPlay";
    String nowplayUrl;//当前默认播放地址
    String qingxidu;
    String sdurl;
    String hdrul;
    String uhdrul;
    String type;
    boolean unityJump;
    String param1;
    String param2;
    String param3;

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    public String getNowplayUrl() {
        return nowplayUrl;
    }

    public void setNowplayUrl(String nowplayUrl) {
        this.nowplayUrl = nowplayUrl;
    }

    public String getQingxidu() {
        return qingxidu;
    }

    public void setQingxidu(String qingxidu) {
        this.qingxidu = qingxidu;
    }

    public String getSdurl() {
        return sdurl;
    }

    public void setSdurl(String sdurl) {
        this.sdurl = sdurl;
    }

    public String getHdrul() {
        return hdrul;
    }

    public void setHdrul(String hdrul) {
        this.hdrul = hdrul;
    }

    public String getUhdrul() {
        return uhdrul;
    }

    public void setUhdrul(String uhdrul) {
        this.uhdrul = uhdrul;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isUnityJump() {
        return unityJump;
    }

    public void setUnityJump(boolean unityJump) {
        this.unityJump = unityJump;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }
}
