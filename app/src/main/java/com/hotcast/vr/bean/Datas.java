package com.hotcast.vr.bean;

/**
 * Created by lostnote on 16/1/27.
 */
public class Datas {
    //轮播内容ID
    String id;
    //轮播位置ID
    String rec_id;
    //资源ID（配合type定位资源）
    String media_id;
    //资源类型
    String type;
    //排序
    String sort;
    //图片地址
    String image;
    //标题
    String title;
    //描述
    String desc;
    //推荐URL，当type为web时使用
    String rec_url;

    public String getRec_url() {
        return rec_url;
    }

    public void setRec_url(String rec_url) {
        this.rec_url = rec_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRec_id() {
        return rec_id;
    }

    public void setRec_id(String rec_id) {
        this.rec_id = rec_id;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Datas{" +
                "id='" + id + '\'' +
                ", rec_id='" + rec_id + '\'' +
                ", media_id='" + media_id + '\'' +
                ", type='" + type + '\'' +
                ", sort='" + sort + '\'' +
                ", image='" + image + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", rec_ur='" + rec_url + '\'' +
                '}';
    }
}
