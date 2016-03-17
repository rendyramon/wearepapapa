package com.hotcast.vr.bean;

import java.util.List;

/**
 * Created by liurongzhi on 2016/3/16.
 */
public class Pinglun {
    int count;
    int total_page;
    List<Data> data;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public class Data {
        String user;
        String picture;
        String videoset_id;
        String content;
        long time;
        String com_id;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public String getVideoset_id() {
            return videoset_id;
        }

        public void setVideoset_id(String videoset_id) {
            this.videoset_id = videoset_id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getCom_id() {
            return com_id;
        }

        public void setCom_id(String com_id) {
            this.com_id = com_id;
        }
    }
}
