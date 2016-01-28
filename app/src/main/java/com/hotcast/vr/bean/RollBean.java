package com.hotcast.vr.bean;

import java.util.List;

/**
 * Created by lostnote on 16/1/27.
 */
public class RollBean {
    //轮播位置ID
    String id;
    //标题
    String title;
    //更新时间
    String updated_at;
    //图标
    String logo;
    //描述
    List<Datas> data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Datas> getData() {
        return data;
    }

    public void setData(List<Datas> data) {
        this.data = data;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "RollBean{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", logo='" + logo + '\'' +
                ", data=" + data +
                '}';
    }
}
