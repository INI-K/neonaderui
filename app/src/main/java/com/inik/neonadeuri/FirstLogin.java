package com.inik.neonadeuri;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class FirstLogin extends AppCompatActivity {

    TextView firstText;
    TextView firstTextTwo;
    TextView firstTextThree;

    EditText profileName;


    ImageView nunaImage;
    ImageView profileImage;

    Button profileBtn;

    String nickName;
    String profileImageUrl;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));

        // 액션바 제거
        getSupportActionBar().hide();


        Intent intent = getIntent();

        nickName = intent.getStringExtra("nickName");
        profileImageUrl = intent.getStringExtra("profileImageUrl");
        email = intent.getStringExtra("email");


        firstTextTwo = findViewById(R.id.firstTextTwo);
        firstTextThree = findViewById(R.id.firstTextThree);

        profileName = findViewById(R.id.profileName);
        profileImage = findViewById(R.id.profileImage);
        profileBtn = findViewById(R.id.profileSetBtn);

        nunaImage = findViewById(R.id.nunaImage);

        Animation ani = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation ani2 = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
        Animation ani3 = AnimationUtils.loadAnimation(this, R.anim.fade_in3);


        nunaImage.setAnimation(ani2);
        firstTextTwo.setAnimation(ani3);


        ani2.setAnimationListener(new AnimationAdapter());
        ani3.setAnimationListener(new AnimationAdapter());


        nunaImage.startAnimation(ani2);
        firstTextTwo.startAnimation(ani3);


    }

    private final class AnimationAdapter implements Animation.AnimationListener {
        public void onAnimationStart(Animation animation) {

        }

        public void onAnimationEnd(Animation animation) {
            Animation ani7 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);


            nunaImage.startAnimation(ani7);
            firstTextTwo.startAnimation(ani7);

            Intent intent = new Intent(FirstLogin.this, SetProfile.class);
            intent.putExtra("email", email);
            intent.putExtra("nickName", nickName);
            intent.putExtra("profileImageUrl", profileImageUrl);


            startActivity(intent);


        }

        public void onAnimationRepeat(Animation animation) {

        }
    }
}