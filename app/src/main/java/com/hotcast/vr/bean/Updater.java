package com.hotcast.vr.bean;

/**
 * Created by lostnote on 16/3/23.
 */
public class Updater {
    int code;
    String message;
    Update data;

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

    public Update getData() {
        return data;
    }

    public void setData(Update data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Updater{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
