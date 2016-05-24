package com.hotcast.vr.bean;

/**
 * Created by lostnote on 16/3/23.
 */
public class Channeler {
    int code;
    String message;
    Channel data;

    public int getCode() {
        return code;
    }

    public void setCode(int  code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Channel getData() {
        return data;
    }

    public void setData(Channel data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Channeler{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
