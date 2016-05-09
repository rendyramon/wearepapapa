package com.hotcast.vr.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by zhangjunjun on 2016/5/9.
 */
public class LocalVideoBean implements Serializable {
    private String videoPath;
    private String videoName;
    private long videoSize;
    private Bitmap videoImage;

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }

    public Bitmap getVideoImage() {
        return videoImage;
    }

    public void setVideoImage(Bitmap videoImage) {
        this.videoImage = videoImage;
    }
}
