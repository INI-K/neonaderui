package com.inik.neonadeuri.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageHelper {

    public static Bitmap getImage(String strUrl){
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        Bitmap bm = null;


        try {
            url = new URL(strUrl);
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.connect();

            is = conn.getInputStream();
            bis = new BufferedInputStream(is);

            bm = BitmapFactory.decodeStream(bis);

        } catch (Exception e) {
            //throw e;
            return null;
        }finally{
            try{
                if(is != null) is.close();
                if(bis != null) bis.close();
            }catch(Exception e){}
        }

        return bm;
    }
}