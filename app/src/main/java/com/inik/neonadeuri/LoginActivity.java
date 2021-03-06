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

    // ????????? ??????
    Context context;
    User currentUser;

    // ???
    EditText emailEditText;
    EditText passwordEditText;

    Button basicLoginButton;
    Button googleLoginButton;
    Button kakaoLoginButton;
    Button signUpButton;


    // ??????api???????????????
    private GoogleSignInClient googleSignInClient;

    // ?????????????????? ?????? ?????? ??????
    private FirebaseAuth firebaseAuth;


    private GoogleSignInClient mGoogleSignInClient;
    private String TAG = "mainTag";
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ???????????? ???????????? ????????? ???????????? ???????????? ?????????
        detectHashKey();

        // ????????? ?????? ??? ??? ??????
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

    // ????????? ?????? ?????????
    public void detectHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());

                System.out.println("??? ????????? : " + Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // ????????? ?????? ?????? ?????????
    public void setVariable() {
        context = getApplicationContext();
        currentUser = CurrentUserManager.getCurrentUser();
    }

    // ??? ?????? ?????????
    public void setView() {
        // ????????? ??????
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // ????????? ?????? ??????
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));

        // ????????? ??????
        getSupportActionBar().hide();

        // ??? ???????????? ??????
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);

        basicLoginButton = (Button) findViewById(R.id.basic_login_button);
        googleLoginButton = (Button) findViewById(R.id.google_login_button);
        kakaoLoginButton = (Button) findViewById(R.id.kakao_login_button);
        signUpButton = (Button) findViewById(R.id.sign_up_button);

        // ????????? ??????
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

    // ????????? ????????? ????????? ???????????? ?????????
    public void KakaoLogin() {
        // ????????? ????????? SDK ?????????
        KakaoSdk.init(this, getString(R.string.kakao_app_key));

        // ??????????????? ???????????? ?????????
        if (LoginClient.getInstance().isKakaoTalkLoginAvailable(context)) {
            System.out.println("SYSTEM : ??????????????? ???????????? ????????????");
            System.out.println("SYSTEM : ???????????? ???????????????????????? ???????????????");

            LoginClient.getInstance().loginWithKakaoTalk(context, (token, error) -> {
                if (error != null) {
                    System.out.println("ERROR : ????????? ???????????? ?????????????????????");
                    System.out.println("ERROR : " + error);
                } else if (token != null) {
                    System.out.println("SYSTEM : ????????? ???????????? ??????????????????");
                    System.out.println("SYSTEM : ????????? ?????? ?????? - " + token.getAccessToken());

                    saveKakaoLoginData();
                }

                return null;
            });
        } else {
            // ??????????????? ???????????? ?????? ?????????
            System.out.println("SYSTEM : ??????????????? ???????????? ?????? ????????????");
            System.out.println("SYSTEM : ??????????????? ???????????????.");

            LoginClient.getInstance().loginWithKakaoAccount(context, (token, error) -> {
                if (error != null) {
                    System.out.println("ERROR : ????????? ???????????? ?????????????????????");
                    System.out.println("ERROR : " + error);
                } else if (token != null) {
                    System.out.println("SYSTEM : ????????? ???????????? ??????????????????");
                    System.out.println("SYSTEM : ????????? ?????? ?????? - " + token.getAccessToken());

                    saveKakaoLoginData();
                }

                return null;
            });
        }
    }

    // ????????? ????????? ???????????? ????????? ????????? ???????????? ?????????
    public void saveKakaoLoginData() {
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                System.out.println("ERROR : ????????? ????????? ?????? ????????? ??????????????????");
                System.out.println("ERROR : " + error);
            } else if (user != null) {
                System.out.println("SYSTEM : ????????? ????????? ?????? - " + user.getKakaoAccount().getProfile().getNickname());
                System.out.println("SYSTEM : ????????? ?????? - " + user.getKakaoAccount().getEmail());
                System.out.println("SYSTEM : ????????? ?????? - " + user.getKakaoAccount().getPhoneNumber());

                currentUser.setEmail(user.getKakaoAccount().getEmail());
                currentUser.setNickname(user.getKakaoAccount().getProfile().getNickname());
                currentUser.setPhone(user.getKakaoAccount().getPhoneNumber());
                currentUser.printInfo();

                checkLoginAndMoveActivity();
            }

            return null;
        });
    }

    // ???????????? ??????????????? ?????????????????? ???????????? ????????? ???????????? ?????????
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

                            System.out.println("????????? ?????? : " + user.getEmail());
                            System.out.println("?????? ?????? : " + user.getDisplayName());
                            System.out.println("????????? ?????? : " + user.getDisplayName());
                            System.out.println("?????? ?????? : " + user.getPhoneNumber());
                            System.out.println("????????? ?????? ?????? : " + user.getPhotoUrl());

                            currentUser.setEmail(user.getEmail());
                            currentUser.setNickname(user.getDisplayName());
                            currentUser.setName(user.getDisplayName());
                            currentUser.setPhone("0000");
                            currentUser.setPassword("???????????? ??????");
                            currentUser.setWebSite("???????????? ??????");
                            currentUser.setIntroduction("???????????? ??????");


                            //updateUI(user);
                            Toast.makeText(getApplicationContext(), "?????? ????????? ??????", Toast.LENGTH_LONG).show();


                            String serverUrl2 = "http://218.39.138.57/and2/InsertGoogle.php";
                            SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, serverUrl2, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    currentUser.setIdx(response);
                                    SaveSharedPreference.setUserId(context, response);

                                    System.out.println("??????????????? : " + response);


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

                                    //?????? ????????? ?????? ???????????? ??????
                                    smpr.addStringParam("userIdx", currentUser.getIdx());

                                    //????????? ?????? ??????
                                    smpr.addStringParam("token", SaveSharedPreference.getUserToken(context));

                                    //??????????????? ????????? ?????? ????????? ?????? ?????? ??????
                                    RequestQueue requestQueue= Volley.newRequestQueue(context);
                                    requestQueue.add(smpr);





                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });

                            //?????? ????????? ?????? ???????????? ??????
                            smpr.addStringParam("email", user.getEmail());
                            smpr.addStringParam("password", "???????????? ??????");
                            smpr.addStringParam("name", user.getDisplayName());
                            smpr.addStringParam("nickname", user.getDisplayName());
                            smpr.addStringParam("phone", user.getPhoneNumber());
                            smpr.addStringParam("website", "???????????? ??????");
                            smpr.addStringParam("introduction", "?????? ?????? ??????");
                            //????????? ?????? ??????
                            //??????????????? ????????? ?????? ????????? ?????? ?????? ??????
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