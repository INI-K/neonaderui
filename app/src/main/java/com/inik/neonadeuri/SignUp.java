package com.inik.neonadeuri;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.SaveSharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUp extends AppCompatActivity {

    private DatePickerDialog.OnDateSetListener callbackMethod;
    private boolean validate = false;
    private AlertDialog dialog;


    Context context;
    User currentUser;

    LinearLayout verfi;

    Button dateBtn;
    Button signupBtn;
    Button checkEmailBtn;
    Button otpBtn;
    Button verifyBtn;

    EditText emailEditText;
    EditText pwEditText;
    EditText pwCheckEditText;
    EditText phoneNumEditText;
    EditText verifyText;


    CheckBox uCheck;
    CheckBox iCheck;
    CheckBox mCheck;
    CheckBox aCheck;

    String checkVerify;

    private void linkXML() {

    }

    private void setAttribute() {

    }

    SimpleDateFormat birthDate = new SimpleDateFormat("yyyy/MM/dd");


    private EditText emailId, pass, phoneNum, birth, gender, checkM;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_activity);

        this.InitializeListener();

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));

        // 액션바 제거
        getSupportActionBar().hide();

        setVariable();


        emailEditText = findViewById(R.id.emailInput);
        pwEditText = (EditText) findViewById(R.id.passwordInput);
        pwCheckEditText = (EditText) findViewById(R.id.rePasswordInput);
        phoneNumEditText = (EditText) findViewById(R.id.phoneNumEditText);


        mCheck = (CheckBox) findViewById(R.id.Mcheck);
        iCheck = (CheckBox) findViewById(R.id.Icheck);
        uCheck = (CheckBox) findViewById(R.id.Ucheck);
        aCheck = (CheckBox) findViewById(R.id.Acheck);
        aCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aCheck.isChecked()) {
                    if (mCheck.isChecked()) {
                        mCheck.setChecked(false);
                    }
                    if (iCheck.isChecked()) {
                        iCheck.setChecked(false);
                    }
                    if (uCheck.isChecked()) {
                        uCheck.setChecked(false);
                    }
                    mCheck.setChecked(true);
                    iCheck.setChecked(true);
                    uCheck.setChecked(true);
                }
                if (!aCheck.isChecked()) {
                    if (mCheck.isChecked()) {
                        mCheck.setChecked(false);
                    }
                    if (iCheck.isChecked()) {
                        iCheck.setChecked(false);
                    }
                    if (uCheck.isChecked()) {
                        uCheck.setChecked(false);
                    }
                    mCheck.setChecked(false);
                    iCheck.setChecked(false);
                    uCheck.setChecked(false);
                }


            }
        });


        checkEmailBtn = (Button) findViewById(R.id.checkEmail);
        checkEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                if (validate) {
                    return;
                }
                if (!isEmail(email)) {
                    System.out.println("이메일 형식 확인");
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    dialog = builder.setMessage("이메일의 형식이 아닙니다. 이메일을 입력해주세요")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                if (email.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    dialog = builder.setMessage("아이디는 빈 칸일 수 없습니다")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                                dialog = builder.setMessage("사용할 수 없는 아이디입니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(email, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SignUp.this);
                queue.add(validateRequest);

            }
        });

        otpBtn = (Button) findViewById(R.id.otpBtn);
        verifyText = (EditText) findViewById(R.id.verifyText);
        verifyBtn = (Button) findViewById(R.id.verifyBtn);


        otpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                             // otp
                if (phoneNumEditText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUp.this, "휴대전화 번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                otpBtn.setText("재전송");
                verifyBtn.setVisibility(View.VISIBLE);
                verifyText.setVisibility(View.VISIBLE);


                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+82" + phoneNumEditText.getText().toString(),
                        60,
                        TimeUnit.SECONDS,
                        SignUp.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                System.out.println("전화번호  :  " + phoneNumEditText.getText().toString());
                                Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                checkVerify = verificationId;
                                System.out.println("넘어온 테스트 코드 : " + checkVerify);

                            }
                        }
                );

            }
        });


        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("인증 시작!!!!");
                if (verifyText.getText().toString().trim().isEmpty()) {
                    System.out.println("넘어온코드  : " + checkVerify);
                    Toast.makeText(SignUp.this, "인증번호를 입력해주세요", Toast.LENGTH_SHORT).show();

                    return;
                }
                if (checkVerify != null) {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            checkVerify,
                            verifyText.getText().toString()

                    );
                    System.out.println("인증 시작");
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUp.this, "전화 인증 성공", Toast.LENGTH_SHORT).show();
                                        verifyBtn.setText("전화 인증 성공");
                                    } else {
                                        Toast.makeText(SignUp.this, "전화 인증 실패 - 제대로 입력해주세요", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });


        //emailId, pass, name, phoneNum, birth, gender, checkM;
        signupBtn = findViewById(R.id.btn_register);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pwEditText.getText().toString().equals(pwCheckEditText.getText().toString())) {
                    Toast.makeText(SignUp.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    pwEditText.setText("");
                    pwCheckEditText.setText("");
                    pwEditText.requestFocus();
                    return;
                }
                if (!uCheck.isChecked()) {
                    Toast.makeText(SignUp.this, "이용정보 동의를 해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!iCheck.isChecked()) {
                    Toast.makeText(SignUp.this, "개인정보 취급방침 동의를 해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }


                String userEmail = emailEditText.getText().toString();
                System.out.println("서버로간다 이메일 : " + userEmail);


                final String userPass = pwEditText.getText().toString();
                System.out.println("서버로간다 패스워드 : " + userPass);
                String userName = "닉네임";
                System.out.println("서버로간다 이름 : " + userName);
                String userPhoneNum = phoneNumEditText.getText().toString();
                System.out.println("서버로간다 폰번호 : " + userPhoneNum);


                String serverUrl2 = "http://218.39.138.57/and2/Register2.php";
                SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, serverUrl2, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        currentUser.setIdx(response);
                        SaveSharedPreference.setUserId(context, response);

                        System.out.println("아이디확인 : " + response);

                        currentUser.setEmail(userEmail);
                        currentUser.setNickname(userName);
                        currentUser.setName(userName);
                        currentUser.setPhone("0000");
                        currentUser.setPassword("패스워드 없음");
                        currentUser.setWebSite("웹사이트 없음");
                        currentUser.setIntroduction("자기소개 없음");

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                //요청 객체에 보낼 데이터를 추가
                smpr.addStringParam("email", userEmail);
                smpr.addStringParam("password", userPass);
                smpr.addStringParam("name", userName);
                smpr.addStringParam("nickname", userName);
                smpr.addStringParam("phone", userPhoneNum);
                smpr.addStringParam("website", "웹사이트 없음");
                smpr.addStringParam("introduction", "자기 소개 없음");
                //이미지 파일 추가
                //요청객체를 서버로 보낼 우체통 같은 객체 생성
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(smpr);


                Intent intent = new Intent(getApplicationContext(), FirstLogin.class);

                intent.putExtra("nickName", userName);

                startActivity(intent);
            }
        });

    }

    public void InitializeListener() {
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateBtn.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
            }
        };
    }


    public void OnClickHandler(View view) {
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, 2020, 11, 27);

        dialog.show();
    }

    public static boolean isEmail(String email) {
        boolean returnValue = false;
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            returnValue = true;
        }
        return returnValue;
    }

    public void setVariable() {
        context = getApplicationContext();
        currentUser = CurrentUserManager.getCurrentUser();
    }

}