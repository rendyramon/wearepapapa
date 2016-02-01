package com.hotcast.vr.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lostnote on 16/1/28.
 */
public class Channel implements Serializable {
    //频道数量
    int count;
    List<ChanelData> data;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ChanelData> getData() {
        return data;
    }

    public void setData(List<ChanelData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "count=" + count +
                ", data=" + data +
                '}';
    }
}
