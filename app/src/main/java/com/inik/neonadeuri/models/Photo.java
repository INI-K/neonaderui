package com.inik.neonadeuri.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.inik.neonadeuri.utils.HardwareInformation;

import java.io.Serializable;

public class Photo implements Serializable {

    private String idx;
    private Bitmap bitmapImg;
    private Bitmap thumbnailBitmapImg;
    private String applicationPath; // 안드로이드 하드웨어에 저장된 주소 (yyyyMMdd_HHmmss_idx_suffix.jpg)
    private String serverPath; // 서버에 저장된 주소

    public Photo() {

    }

    public Photo(String idx) {
        this.idx = idx;
        applicationPath = new String();
        serverPath = new String();
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }


    public Bitmap getBitmapImg() {
        return bitmapImg;
    }

    public void setBitmapImg(Bitmap bitmapImg) {
        float scaleRate = (float) HardwareInformation.displayWidth / (float) bitmapImg.getWidth();
        this.bitmapImg = bitmapImg.createScaledBitmap(bitmapImg, HardwareInformation.displayWidth, (int) (bitmapImg.getHeight() * scaleRate), true);

        setThumbnailBitmapImg(bitmapImg.createScaledBitmap(bitmapImg, 500, 500, true));
    }

    public Bitmap getThumbnailBitmapImg() {
        return thumbnailBitmapImg;
    }

    public void setThumbnailBitmapImg(Bitmap thumbnailBitmapImg) {
        this.thumbnailBitmapImg = thumbnailBitmapImg;
    }

    public String getApplicationPath() {
        return applicationPath;
    }

    public void setApplicationPath(String applicationPath) {
        this.applicationPath = applicationPath;
    }

    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }
}
