package com.hotcast.vr.bean;

import java.util.List;

/**
 * Created by lostnote on 16/3/23.
 */
public class Relationer {
    int code;
    String message;
    List<Relation> data;

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

    public List<Relation> getData() {
        return data;
    }

    public void setData(List<Relation> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Relationer{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
