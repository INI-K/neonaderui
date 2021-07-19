package com.inik.neonadeuri;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.inik.neonadeuri.models.Photo;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.utils.CameraManager;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.ImageLoadTask;
import com.inik.neonadeuri.utils.JSONManager;
import com.inik.neonadeuri.utils.VolleyManager;

import java.util.HashMap;
import java.util.Map;

public class SetProfile extends AppCompatActivity implements CameraManager.PhotoReceiver {


    User currentUser;

    Photo editProfilePhoto;

    ImageView profileImage;
    EditText profileName;
    Button setProfile;

    Context context;

    //업로드할 이미지의 절대경로(실제 경로)
    String imgPath;

    String email;
    String nickName;
    String insertNickname;
    String profileImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));

        // 액션바 제거
        getSupportActionBar().hide();


        setVariable();

        Intent intent = getIntent();
        nickName = intent.getStringExtra("nickName");
        profileImageUrl = intent.getStringExtra("profileImageUrl");

        currentUser.setName(nickName);


        profileName = findViewById(R.id.profileName);

        if(nickName != null){
            profileName.setText(currentUser.getNickname());
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int permissionResult = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(permissionResult == PackageManager.PERMISSION_DENIED){
                String[] permissions = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,10);
            }
        }else{
            //cv.setVisibility(View.VISIBLE);
        }

        profileImage = findViewById(R.id.profileImage);
            if(profileImageUrl != null){
                ImageLoadTask task = new ImageLoadTask(profileImageUrl,profileImage);
                task.execute();
            }


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtn();
            }
        });


            setProfile  = findViewById(R.id.profileSetBtn);
            setProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentUser.setName(profileName.getText().toString());
                    currentUser.setNickname(profileName.getText().toString());
                    clickUpload();
                }
            });

    }//onCreate() ..

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 10 :
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED) //사용자가 허가 했다면
                {
                    Toast.makeText(this, "외부 메모리 읽기/쓰기 사용 가능", Toast.LENGTH_SHORT).show();

                }else{//거부했다면
                    Toast.makeText(this, "외부 메모리 읽기/쓰기 제한", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    public void setVariable() {
        context = getApplicationContext();
        currentUser = CurrentUserManager.getCurrentUser();

        CameraManager.photoReceiver = this;
    }

    public void clickBtn() {
         changeProfileImg();
    }

    public void clickUpload() {

        //안드로이드에서 보낼 데이터를 받을 php 서버 주소
        String serverUrl="http://218.39.138.57/and2/SavePhoto.php";

        //Volley plus Library를 이용해서
        //파일 전송하도록..
        //Volley+는 AndroidStudio에서 검색이 안됨 [google 검색 이용]

        //파일 전송 요청 객체 생성[결과를 String으로 받음]
        SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("포토 아이디 : " + response);
                currentUser.getProfileImg().setIdx(response);

                System.out.println("가져온 포토 아이디 : "  + currentUser.getProfileImg().getIdx());

                String baseURL = "http://218.39.138.57/and2/";
                String apiURL = "SaveUser.php";

                RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        baseURL + apiURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Intent intent = new Intent(SetProfile.this, SplashActivity.class);
                                startActivity(intent);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println(error);
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();

                        params.put("JSON", JSONManager.userToJSONObject(currentUser).toString());

                        return params;
                    }
                };
                request.setShouldCache(false);
                queue.add(request);

                finishAndRemoveTask();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        //요청 객체에 보낼 데이터를 추가
        smpr.addStringParam("applicationPath", editProfilePhoto.getApplicationPath());

        //이미지 파일 추가
        smpr.addFile("img", editProfilePhoto.getApplicationPath());

        //요청객체를 서버로 보낼 우체통 같은 객체 생성
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(smpr);

    }


    public void changeProfileImg() {
        startActivity(new Intent(context, CameraManager.class));
    }

    @Override
    public void permissionDenied() {
        Toast.makeText(context, "하드웨어 권한을 획득할 수 없습니다", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void createPhotoSuccess(Photo photo) {
        this.editProfilePhoto = photo;

        profileImage.setImageBitmap(editProfilePhoto.getBitmapImg());
    }

    @Override
    public void createPhotoFail() {

    }
}
