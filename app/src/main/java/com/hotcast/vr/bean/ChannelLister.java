package com.hotcast.vr.bean;

import java.util.List;

/**
 * Created by lostnote on 16/3/23.
 */
public class ChannelLister {
    int code;
    String message;
    List<ChannelList> data;

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

    public List<ChannelList> getData() {
        return data;
    }

    public void setData(List<ChannelList> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ChannelLister{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
