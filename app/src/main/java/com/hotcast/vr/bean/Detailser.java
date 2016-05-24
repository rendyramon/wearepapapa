package com.hotcast.vr.bean;

/**
 * Created by lostnote on 16/3/23.
 */
public class Detailser {
    int code;
    String message;
    Details data;

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

    public Details getData() {
        return data;
    }

    public void setData(Details data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Detailser{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
