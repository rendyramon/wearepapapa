package com.hotcast.vr.bean;

import java.util.List;

/**
 * Created by liurongzhi on 2016/3/19.
 */
public class Glass {
    int code;
    String message;
    List<GlassesData> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<GlassesData> getData() {
        return data;
    }

    public void setData(List<GlassesData> data) {
        this.data = data;
    }

    public class GlassesData {
        String id;
        String title;
        String price;
        String desc;
        String image;
        String buy_url;
        String time;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getBuy_url() {
            return buy_url;
        }

        public void setBuy_url(String buy_url) {
            this.buy_url = buy_url;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
