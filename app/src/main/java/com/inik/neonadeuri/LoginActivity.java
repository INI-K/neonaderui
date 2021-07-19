package com.inik.neonadeuri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.inik.neonadeuri.models.Photo;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.JSONManager;
import com.inik.neonadeuri.utils.SaveSharedPreference;
import com.inik.neonadeuri.utils.VolleyManager;
import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    // 클래스 변수
    Context context;
    User currentUser;

    // 뷰
    EditText emailEditText;
    EditText passwordEditText;

    Button basicLoginButton;
    Button googleLoginButton;
    Button kakaoLoginButton;
    Button signUpButton;


    // 구글api클라이언트
    private GoogleSignInClient googleSignInClient;

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;


    private GoogleSignInClient mGoogleSignInClient;
    private String TAG = "mainTag";
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 카카오톡 로그인을 위해서 해시키를 출력하는 메서드
        detectHashKey();

        // 클래스 변수 및 뷰 설정
        setVariable();
        setView();


        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signin]

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]


    }

    // 해시키 출력 메서드
    public void detectHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());

                System.out.println("앱 해시키 : " + Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // 클래스 변수 설정 메서드
    public void setVariable() {
        context = getApplicationContext();
        currentUser = CurrentUserManager.getCurrentUser();
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
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);

        basicLoginButton = (Button) findViewById(R.id.basic_login_button);
        googleLoginButton = (Button) findViewById(R.id.google_login_button);
        kakaoLoginButton = (Button) findViewById(R.id.kakao_login_button);
        signUpButton = (Button) findViewById(R.id.sign_up_button);

        // 리스너 설정
        basicLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();

            }
        });

        kakaoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KakaoLogin();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);

            }
        });
    }

    // 카카오 로그인 토큰을 받아오는 메서드
    public void KakaoLogin() {
        // 카카오 로그인 SDK 초기화
        KakaoSdk.init(this, getString(R.string.kakao_app_key));

        // 카카오톡이 설치되어 있다면
        if (LoginClient.getInstance().isKakaoTalkLoginAvailable(context)) {
            System.out.println("SYSTEM : 카카오톡이 설치되어 있습니다");
            System.out.println("SYSTEM : 카카오톡 어플리케이션으로 연결합니다");

            LoginClient.getInstance().loginWithKakaoTalk(context, (token, error) -> {
                if (error != null) {
                    System.out.println("ERROR : 카카오 로그인에 실패하였습니다");
                    System.out.println("ERROR : " + error);
                } else if (token != null) {
                    System.out.println("SYSTEM : 카카오 로그인에 성공했습니다");
                    System.out.println("SYSTEM : 카카오 토큰 정보 - " + token.getAccessToken());

                    saveKakaoLoginData();
                }

                return null;
            });
        } else {
            // 카카오톡이 설치되어 있지 않다면
            System.out.println("SYSTEM : 카카오톡이 설치되어 있지 않습니다");
            System.out.println("SYSTEM : 브라우저로 연결합니다.");

            LoginClient.getInstance().loginWithKakaoAccount(context, (token, error) -> {
                if (error != null) {
                    System.out.println("ERROR : 카카오 로그인에 실패하였습니다");
                    System.out.println("ERROR : " + error);
                } else if (token != null) {
                    System.out.println("SYSTEM : 카카오 로그인에 성공했습니다");
                    System.out.println("SYSTEM : 카카오 토큰 정보 - " + token.getAccessToken());

                    saveKakaoLoginData();
                }

                return null;
            });
        }
    }

    // 카카오 토큰을 이용하여 이메일 정보를 받아오는 메서드
    public void saveKakaoLoginData() {
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                System.out.println("ERROR : 카카오 프로필 정보 로딩에 실패했습니다");
                System.out.println("ERROR : " + error);
            } else if (user != null) {
                System.out.println("SYSTEM : 프로필 닉네임 정보 - " + user.getKakaoAccount().getProfile().getNickname());
                System.out.println("SYSTEM : 이메일 정보 - " + user.getKakaoAccount().getEmail());
                System.out.println("SYSTEM : 휴대폰 정보 - " + user.getKakaoAccount().getPhoneNumber());

                currentUser.setEmail(user.getKakaoAccount().getEmail());
                currentUser.setNickname(user.getKakaoAccount().getProfile().getNickname());
                currentUser.setPhone(user.getKakaoAccount().getPhoneNumber());
                currentUser.printInfo();

                checkLoginAndMoveActivity();
            }

            return null;
        });
    }

    // 로그인이 정상적으로 처리되었는지 확인하고 화면을 전환하는 메서드
    public void checkLoginAndMoveActivity() {
        Intent intent = new Intent(context, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
    // [END on_start_check_user]

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(getApplicationContext(), "Google sign in Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            System.out.println("이메일 확인 : " + user.getEmail());
                            System.out.println("이름 확인 : " + user.getDisplayName());
                            System.out.println("닉네임 확인 : " + user.getDisplayName());
                            System.out.println("번호 확인 : " + user.getPhoneNumber());
                            System.out.println("이미지 주소 확인 : " + user.getPhotoUrl());

                            currentUser.setEmail(user.getEmail());
                            currentUser.setNickname(user.getDisplayName());
                            currentUser.setName(user.getDisplayName());
                            currentUser.setPhone("0000");
                            currentUser.setPassword("패스워드 없음");
                            currentUser.setWebSite("웹사이트 없음");
                            currentUser.setIntroduction("자기소개 없음");


                            //updateUI(user);
                            Toast.makeText(getApplicationContext(), "구글 로그인 성공", Toast.LENGTH_LONG).show();


                            String serverUrl2 = "http://218.39.138.57/and2/InsertGoogle.php";
                            SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, serverUrl2, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    currentUser.setIdx(response);
                                    SaveSharedPreference.setUserId(context, response);

                                    System.out.println("아이디확인 : " + response);


                                    String serverUrl="http://218.39.138.57/and2/InsertToken.php";

                                    SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    });

                                    //요청 객체에 보낼 데이터를 추가
                                    smpr.addStringParam("userIdx", currentUser.getIdx());

                                    //이미지 파일 추가
                                    smpr.addStringParam("token", SaveSharedPreference.getUserToken(context));

                                    //요청객체를 서버로 보낼 우체통 같은 객체 생성
                                    RequestQueue requestQueue= Volley.newRequestQueue(context);
                                    requestQueue.add(smpr);





                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });

                            //요청 객체에 보낼 데이터를 추가
                            smpr.addStringParam("email", user.getEmail());
                            smpr.addStringParam("password", "패스워드 없음");
                            smpr.addStringParam("name", user.getDisplayName());
                            smpr.addStringParam("nickname", user.getDisplayName());
                            smpr.addStringParam("phone", user.getPhoneNumber());
                            smpr.addStringParam("website", "웹사이트 없음");
                            smpr.addStringParam("introduction", "자기 소개 없음");
                            //이미지 파일 추가
                            //요청객체를 서버로 보낼 우체통 같은 객체 생성
                            RequestQueue requestQueue = Volley.newRequestQueue(context);
                            requestQueue.add(smpr);


                            Intent intent = new Intent(getApplicationContext(), FirstLogin.class);

                            intent.putExtra("nickName", user.getDisplayName());
                            intent.putExtra("profileImageUrl", user.getPhotoUrl());
                            startActivity(intent);


                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();


                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();
                    }
                });
    }
}