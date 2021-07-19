package com.inik.neonadeuri;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;
import com.inik.neonadeuri.models.Comment;
import com.inik.neonadeuri.models.Feed;
import com.inik.neonadeuri.models.Photo;
import com.inik.neonadeuri.utils.CurrentUserManager;

import com.inik.neonadeuri.utils.FeedListViewAdapter;
import com.inik.neonadeuri.utils.HardwareInformation;
import com.inik.neonadeuri.utils.JSONManager;
import com.inik.neonadeuri.utils.SaveSharedPreference;
import com.inik.neonadeuri.utils.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setHardwareInformation();
        getToken();


        if (SaveSharedPreference.getUserid(getApplication()).length() > 0) {
            Handler handler = new Handler();
            handler.postDelayed(new SplashHandler(getApplicationContext()), 0);
        } else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

    }

    public void setHardwareInformation() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);

        HardwareInformation.displayHeight = point.y;
        HardwareInformation.displayWidth = point.x;
        HardwareInformation.dpToPxDensity = getResources().getDisplayMetrics().density;
    }

    @Override
    public void onBackPressed() {
    }

    private class SplashHandler implements Runnable {
        Context context;

        public SplashHandler(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            String baseURL = "http://218.39.138.57/and2/";
            String apiURL = "loadUser.php";
            String idx = SaveSharedPreference.getUserid(context);

            RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    baseURL + apiURL + "?idx=" + idx,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            CurrentUserManager.setCurrentUser(JSONManager.jsonObjectToUser(response));

                            loadFeeds(context);
                            loadImage(context);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error);
                        }
                    });
            request.setShouldCache(false);
            queue.add(request);
        }
    }

    public void loadImage(Context context) {
        String baseURL = "http://218.39.138.57/and2/";
        String apiURL = "loadPhoto.php";

        RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
        StringRequest request = new StringRequest(
                Request.Method.GET,
                baseURL + apiURL + "?idx=" + CurrentUserManager.getCurrentUser().getProfileImg().getIdx(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        CurrentUserManager.getCurrentUser().setProfileImg(JSONManager.jsonObjectToPhoto(response));

                        String url = CurrentUserManager.getCurrentUser().getProfileImg().getServerPath();
                        Glide.with(getApplicationContext()).asBitmap().load(url)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                        CurrentUserManager.getCurrentUser().getProfileImg().setBitmapImg(resource); // 유저 이미지 세팅
                                        startActivity(new Intent(context, HomeActivity.class));
                                    }
                                });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                });
        request.setShouldCache(false);
        queue.add(request);
    }

    public void loadFeeds(Context context) {
        for (int i = 0; i < CurrentUserManager.getCurrentUser().getFeeds().size(); i++) {
            String baseURL = "http://218.39.138.57/and2/";
            String apiURL = "loadFeed.php";

            RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    baseURL + apiURL + "?idx=" + CurrentUserManager.getCurrentUser().getFeeds().get(i).getIdx(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Feed feed = JSONManager.jsonObjectToFeed(response);

                            CurrentUserManager.getCurrentUser().setFeed(feed); //피드 로딩

                            String baseURL = "http://218.39.138.57/and2/";
                            String apiURL = "loadPhoto.php";


                            RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
                            StringRequest request = new StringRequest(
                                    Request.Method.GET,
                                    baseURL + apiURL + "?idx=" + feed.getPhoto().getIdx(),
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Photo photo = JSONManager.jsonObjectToPhoto(response);
                                            feed.setPhoto(photo);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            System.out.println(error);
                                        }
                                    });
                            request.setShouldCache(false);
                            queue.add(request);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error);
                        }
                    });

            request.setShouldCache(false);
            queue.add(request);
        }
    }
    public void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String Token  = instanceIdResult.getToken();
                        SaveSharedPreference.setUserToken(getApplicationContext(), Token);
                        Log.e("Token", instanceIdResult.getToken());
                    }
                });
    }
}