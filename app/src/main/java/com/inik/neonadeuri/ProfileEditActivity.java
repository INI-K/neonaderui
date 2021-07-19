package com.inik.neonadeuri;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


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

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditActivity extends AppCompatActivity implements CameraManager.PhotoReceiver {

    // 클래스 변수
    Context context;
    User currentUser;

    Photo editProfilePhoto;

    // 뷰
    CircleImageView profileImgView;

    EditText nameEditText;
    EditText nicknameEditText;
    EditText websiteEditText;
    EditText introductionEditText;
    EditText emailEditText;
    EditText phoneEditText;

    Button cancelButton;
    Button saveButton;
    Button profileImgChangeButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);



        setVariable();
        setView();
    }

    // 클래스 변수 설정 메서드
    public void setVariable() {
        context = getApplicationContext();
        currentUser = CurrentUserManager.getCurrentUser();

        CameraManager.photoReceiver = this;
    }

    // 뷰 설정 메서드
    public void setView() {
        // 상태바 제거
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 상태바 색상 변경
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));

        // 액션바 제거
        getSupportActionBar().hide();

        // 뷰 인스턴스 연결
        profileImgView = (CircleImageView) findViewById(R.id.profile_circle_image_view);

        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        nicknameEditText = (EditText) findViewById(R.id.nickname_edit_text);
        websiteEditText = (EditText) findViewById(R.id.website_edit_text);
        introductionEditText = (EditText) findViewById(R.id.introduction_edit_text);
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        phoneEditText = (EditText) findViewById(R.id.phone_edit_text);

        cancelButton = (Button) findViewById(R.id.cancel_button);
        saveButton = (Button) findViewById(R.id.save_button);
        profileImgChangeButton = (Button) findViewById(R.id.change_profile_img_button);

        initViewText();

        // 리스너 연결
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        profileImgChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfileImg();
            }
        });
    }

    // 초기 정보 입력
    public void initViewText() {
        Bitmap profileImg = currentUser.getProfileImg().getBitmapImg() == null ?
                BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_user) :
                currentUser.getProfileImg().getBitmapImg();
        editProfilePhoto = new Photo();
        editProfilePhoto.setBitmapImg(profileImg);
        profileImgView.setImageBitmap(profileImg);

        String name = currentUser.getName();
        if (name.length() > 0) {
            nameEditText.setText(name);
        }

        String nickName = currentUser.getNickname();
        if (nickName.length() > 0) {
            nicknameEditText.setText(nickName);
        }

        String website = currentUser.getWebSite();
        if (website.length() > 0) {
            websiteEditText.setText(website);
        }
        ;

        String introduction = currentUser.getIntroduction();
        if (introduction.length() > 0) {
            introductionEditText.setText(introduction);
        }

        String email = currentUser.getEmail();
        if (email.length() > 0) {
            emailEditText.setText(email);
        }

        String phone = currentUser.getPhone();
        if (phone.length() > 0) {
            phoneEditText.setText(phone);
        }
    }

    // 취소 메서드
    public void cancel() {
        Intent intent = new Intent(context, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    // 저장 메서드
    public void save() {


        currentUser.setProfileImg(editProfilePhoto);
        currentUser.setName(String.valueOf(nameEditText.getText()));
        currentUser.setNickname(String.valueOf(nicknameEditText.getText()));
        currentUser.setWebSite(String.valueOf(websiteEditText.getText()));
        currentUser.setIntroduction(String.valueOf(introductionEditText.getText()));
        currentUser.setEmail(String.valueOf(emailEditText.getText()));
        currentUser.setPhone(String.valueOf(phoneEditText.getText()));

        currentUser.printInfo();


        clickUpload();

    }

    // 사진 변경 메서드
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

        profileImgView.setImageBitmap(editProfilePhoto.getBitmapImg());
    }

    @Override
    public void createPhotoFail() {

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

}