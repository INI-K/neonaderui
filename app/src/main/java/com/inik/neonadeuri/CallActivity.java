package com.inik.neonadeuri;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.inik.neonadeuri.models.Comment;
import com.inik.neonadeuri.models.Feed;
import com.inik.neonadeuri.models.Photo;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.HomeViewPagerAdapter;
import com.inik.neonadeuri.utils.JSONManager;

import java.util.ArrayList;

public class CallActivity extends AppCompatActivity {


    // 클래스 변수
    private User currentUser;

    // 뷰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);




        setVariable();


    }

    // 클래스 변수 설정 메서드
    public void setVariable() {
        currentUser = CurrentUserManager.getCurrentUser();
    }



    // 뷰 설정 메서드
    public void setView() {

    }
}