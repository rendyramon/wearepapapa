package com.hotcast.vr.bean;

/**
 * Created by lostnote on 15/11/29.
 */
public class Update {
    //版本号ID
    String id;
    //版本号
    String version;
    //更新日志
    String log;
    //是否强制更新，0不强制，1强制
    String is_force;
    //发布时间
    String published_at;
    //更新地址
    String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getIs_force() {
        return is_force;
    }

    public void setIs_force(String is_force) {
        this.is_force = is_force;
    }

    public String getPublished_at() {
        return published_at;
    }

    public void setPublished_at(String published_at) {
        this.published_at = published_at;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Update{" +
                "id='" + id + '\'' +
                ", version='" + version + '\'' +
                ", log='" + log + '\'' +
                ", is_force='" + is_force + '\'' +
                ", published_at='" + published_at + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
