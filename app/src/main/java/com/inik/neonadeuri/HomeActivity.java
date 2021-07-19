package com.inik.neonadeuri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.inik.neonadeuri.models.Comment;
import com.inik.neonadeuri.models.Feed;
import com.inik.neonadeuri.models.Photo;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.HardwareInformation;
import com.inik.neonadeuri.utils.HomeViewPagerAdapter;
import com.inik.neonadeuri.utils.JSONManager;
import com.inik.neonadeuri.utils.SaveSharedPreference;
import com.inik.neonadeuri.utils.SharedPreferencesManager;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    public ViewPager viewPager;
    // 클래스 변수
    private User currentUser;

    // 뷰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);




        setVariable();
//        generateTestData();

        setView();
    }

    // 클래스 변수 설정 메서드
    public void setVariable() {
        currentUser = CurrentUserManager.getCurrentUser();
    }



        // 뷰 설정 메서드
    public void setView() {
        // 상태바 제거
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 상태바 색상 변경
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));

        // 액션바 제거
        getSupportActionBar().hide();

        // 뷰페이저 설정
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new HomeViewPagerAdapter(getSupportFragmentManager()));

        // 탭레이아웃 설정
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().clearColorFilter();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // 탭 레이아웃 아이콘 이미지 설정
        ArrayList<Integer> tabIconImgs = new ArrayList<>();
        tabIconImgs.add(R.drawable.tab_icon_feedlist);
        tabIconImgs.add(R.drawable.tab_icon_search);
        tabIconImgs.add(R.drawable.tab_icon_addfeed);
        tabIconImgs.add(R.drawable.tab_icon_profile);

        // 나중에 프로필 이미지가 탭 아이콘으로 뜨도록 수정할 것
//        if(currentUser.getThumbnailProfileImg() != null) {
//
//        } else {
//
//        }

        // 각각의 탭에 이미지 세팅
        for (int i = 0; i < tabIconImgs.size(); i++) {
            tabLayout.getTabAt(i).setIcon(tabIconImgs.get(i));
        }

        // 최초 선택 탭 색상 설정
        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
    }

    // 테스팅을 위한 코드 (나중에 지울 것)
    public void generateTestData() {
        Context context = getApplicationContext();

        if(currentUser.getIdx() == null) {
            currentUser.generateTestingData(0);

            Photo testPhoto = new Photo();
            testPhoto.setBitmapImg(BitmapFactory.decodeResource(context.getResources(), R.drawable.test_1));
            currentUser.setProfileImg(testPhoto);

            User testUserOne = new User();
            testUserOne.generateTestingData(1);
            User testUserTwo = new User();
            testUserTwo.generateTestingData(2);
            User testUserThree = new User();
            testUserThree.generateTestingData(3);

            currentUser.addFollowing(testUserOne);
            currentUser.addFollowing(testUserTwo);
            currentUser.addFollowing(testUserThree);

            currentUser.addFollower(testUserOne);
            currentUser.addFollower(testUserTwo);

            String text = JSONManager.userToJSONObject(currentUser).toString();
            System.out.println(text);
            User user = JSONManager.jsonObjectToUser(text);
            user.printInfo();
            System.out.println(JSONManager.userToJSONObject(user));

            Feed feedOne = new Feed();
            feedOne.setWriter(currentUser);
            Photo photoOne = new Photo();
            photoOne.setBitmapImg(BitmapFactory.decodeResource(context.getResources(), R.drawable.test_0));
            feedOne.setPhoto(photoOne);
            feedOne.setFeedText("첫 번째 피드입니다.");
            feedOne.setFeedTag("#태그1 #태그2 #태그3");



            Feed feedTwo = new Feed();
            Photo photoTwo = new Photo();
            photoTwo.setBitmapImg(BitmapFactory.decodeResource(context.getResources(), R.drawable.test_1));
            feedTwo.setPhoto(photoTwo);
            feedTwo.setFeedText("두 번째 피드입니다.");
            feedTwo.setFeedTag("#태그1 #태그2 #태그3");

            Feed feedThree = new Feed();
            feedThree.setWriter(currentUser);
            feedThree.setIdx("1");
            Photo photoThree = new Photo("01");
            photoThree.setBitmapImg(BitmapFactory.decodeResource(context.getResources(), R.drawable.test_2));
            feedThree.setPhoto(photoThree);
            feedThree.setFeedText("세 번째 피드입니다.");
            feedThree.setFeedTag("#태그1 #태그2 #태그3");
            feedThree.addLike(currentUser);
            feedThree.addLike(testUserOne);
            feedThree.addLike(testUserTwo);
            feedThree.addLike(testUserThree);

            String feedText = JSONManager.feedToJSONObject(feedThree).toString();
            System.out.println(feedText);
            Feed newFeed = JSONManager.jsonObjectToFeed(feedText);
            System.out.println(JSONManager.feedToJSONObject(newFeed).toString());

            Comment testCommentOne = new Comment();
            testCommentOne.setWriter(currentUser);
            testCommentOne.setComment("따봉 놓고 갑니다 00 따봉 놓고 갑니다 00 따봉 놓고 갑니다 00 따봉 놓고 갑니다 00 따봉 놓고 갑니다 00 따봉 놓고 갑니다 00 따봉 놓고 갑니다 00 따봉 놓고 갑니다 00 따봉 놓고 갑니다 00 따봉 놓고 갑니다 00 따봉 놓고 갑니다 00 따봉 놓고 갑니다 00 따봉 놓고 갑니다 00");
            testCommentOne.setWriter(currentUser);

            Comment testCommentTwo = new Comment();
            testCommentTwo.setWriter(currentUser);
            testCommentTwo.setComment("따봉 놓고 갑니다 01");
            testCommentTwo.setWriter(currentUser);

            Comment testCommentThree = new Comment();
            testCommentThree.setWriter(currentUser);
            testCommentThree.setComment("따봉 놓고 갑니다 02");
            testCommentThree.setWriter(currentUser);

            feedThree.addComment(testCommentOne);
            feedThree.addComment(testCommentTwo);
            feedThree.addComment(testCommentThree);

            Feed feedFour = new Feed();
            Photo photoFour = new Photo();
            photoFour.setBitmapImg(BitmapFactory.decodeResource(context.getResources(), R.drawable.test_3));
            feedFour.setPhoto(photoFour);
            feedFour.setFeedText("네 번째 피드입니다.");
            feedFour.setFeedTag("#태그1 #태그2 #태그3");

            Feed feedFive = new Feed();
            Photo photoFive = new Photo();
            photoFive.setBitmapImg(BitmapFactory.decodeResource(context.getResources(), R.drawable.test_4));
            feedFive.setPhoto(photoFive);
            feedFive.setFeedText("다섯 번째 피드입니다.");
            feedFive.setFeedTag("#태그1 #태그2 #태그3");

            Feed feedSix = new Feed();
            Photo photoSix = new Photo();
            photoSix.setBitmapImg(BitmapFactory.decodeResource(context.getResources(), R.drawable.test_5));
            feedSix.setPhoto(photoSix);
            feedSix.setFeedText("여섯 번째 피드입니다.");
            feedSix.setFeedTag("#태그1 #태그2 #태그3");

            Feed feedSeven = new Feed();
            Photo photoSeven = new Photo();
            photoSeven.setBitmapImg(BitmapFactory.decodeResource(context.getResources(), R.drawable.test_6));
            feedSeven.setPhoto(photoSeven);
            feedSeven.setFeedText("일곱 번째 피드입니다.");
            feedSeven.setFeedTag("#태그1 #태그2 #태그3");

            currentUser.addFeed(feedOne);
            currentUser.addFeed(feedTwo);
            currentUser.addFeed(feedThree);
            currentUser.addFeed(feedFour);
            currentUser.addFeed(feedFive);
            currentUser.addFeed(feedSix);
            currentUser.addFeed(feedSeven);
        }
    }
}