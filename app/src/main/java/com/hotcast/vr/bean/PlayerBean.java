package com.hotcast.vr.bean;

/**
 * Created by lostnote on 16/3/23.
 */
public class PlayerBean {
    int code;
    String message;
    Play data;

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

    public Play getData() {
        return data;
    }

    public void setData(Play data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PlayerBean{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
