package com.inik.neonadeuri.chat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.inik.neonadeuri.R;
import com.inik.neonadeuri.databinding.ActivityChatBinding;
import com.inik.neonadeuri.models.MessageData;
import com.inik.neonadeuri.models.RoomData;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.retrofit.Result;
import com.inik.neonadeuri.retrofit.RetrofitClient;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatImageActivity extends AppCompatActivity {

    private ImageView imageView;
    public Context context;
    public String imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_image);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));
        getSupportActionBar().hide();

        imageView = findViewById(R.id.chatImage);
        context = getApplicationContext();

        Intent intent = getIntent();

        imageUrl = intent.getStringExtra("imageUrl");

        MultiTransformation option = new MultiTransformation(new CenterCrop(), new RoundedCorners(8));

        Glide.with(context)
                .load(imageUrl)
                .apply(RequestOptions.bitmapTransform(option))
                .into(imageView);

    }
}
