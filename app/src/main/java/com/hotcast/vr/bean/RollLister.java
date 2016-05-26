package com.hotcast.vr.bean;

import java.util.List;

/**
 * Created by lostnote on 16/3/23.
 */
public class RollLister {
    int code;
    String message;
    List<RollBean> data;

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

    public List<RollBean> getData() {
        return data;
    }

    public void setData(List<RollBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RollLister{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
