package com.hotcast.vr.bean;

/**
 * Created by lostnote on 16/3/23.
 */
public class Roller {
    int code;
    String message;
    RollBean data;

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

    public RollBean getData() {
        return data;
    }

    public void setData(RollBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Roller{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
