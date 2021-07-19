package com.inik.neonadeuri;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.inik.neonadeuri.models.Feed;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.ProfileEtcGridViewAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEtcActivity extends AppCompatActivity {

    public static FeedEtcProvider feedEtcProvider;

    Context context;
    Feed feed;
    User user;
    User currentUser;

    boolean isfollow;

    ProfileEtcGridViewAdapter profileEtcGridViewAdapter;

    // 뷰
    TextView nicknameTextView;
    TextView postCountTextView;
    TextView followersCountTextView;
    TextView followingCountTextView;

    TextView nameTextView;
    TextView introductionTextView;
    TextView websiteTextView;

    ImageButton moreImageButton;
    Button profileEditButton;

    CircleImageView profileCircleImageView;

    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_etc);

        setVariable();
        setView();

        // Enables Always-on
    }

    public void setVariable() {
        feed = feedEtcProvider.getFeedEtc();
        context = getApplicationContext();
        currentUser = CurrentUserManager.getCurrentUser();
        user = feed.getWriter();

        profileEtcGridViewAdapter = new ProfileEtcGridViewAdapter(context, feed.getWriter());
    }

    // 뷰 설정 메서드
    public void setView() {

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));

        // 액션바 제거
        getSupportActionBar().hide();

        nicknameTextView = (TextView) findViewById(R.id.nickname_text_view);
        postCountTextView = (TextView) findViewById(R.id.post_count_text_view);
        followersCountTextView = (TextView) findViewById(R.id.followers_count_text_view);
        followingCountTextView = (TextView) findViewById(R.id.following_count_text_view);

        nameTextView = (TextView) findViewById(R.id.name_text_view);
        introductionTextView = (TextView) findViewById(R.id.introduction_text_view);
        websiteTextView = (TextView) findViewById(R.id.website_text_view);

        moreImageButton = (ImageButton) findViewById(R.id.more_image_button);
        profileEditButton = (Button) findViewById(R.id.profile_edit_button);

        profileCircleImageView = (CircleImageView) findViewById(R.id.profile_circle_image_view);

        gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(profileEtcGridViewAdapter);



        initView();

        // 리스너 부착
        moreImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOption();
            }
        });

        profileEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followBtn();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ProfileFeedEtcListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("position", position);
                intent.putExtra("userIdx", user.getIdx());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        initView();
    }

    public void initView() {
        // 현재 사용자 정보 설정
        profileCircleImageView.setImageBitmap(feed.getWriter().getProfileImg().getThumbnailBitmapImg());
        nicknameTextView.setText(feed.getWriter().getNickname());
        postCountTextView.setText(Integer.toString(feed.getWriter().getFeeds().size()));
        followersCountTextView.setText(Integer.toString(feed.getWriter().getFollowers().size()));
        followingCountTextView.setText(Integer.toString(feed.getWriter().getFollowing().size()));

        nameTextView.setText(feed.getWriter().getName());
        introductionTextView.setText(feed.getWriter().getIntroduction());
        websiteTextView.setText(feed.getWriter().getWebSite());


        if (user.checkUserInFollowing(currentUser)) {
            isfollow = true;
            profileEditButton.setText("언팔로우");
            System.out.println("진입1");
        } else {
            isfollow = false;
            profileEditButton.setText("팔로우");
            System.out.println("진입2");
        }
    }

    public void moreOption() {
        System.out.println("더보기 버튼이 클릭되었습니다.");
    }

    public void followBtn() {
        if (isfollow) {
            isfollow = false;
            user.removeFollow(currentUser);

            System.out.println("언팔로우 버튼이 눌렸습니다.");
            profileEditButton.setText("팔로우");
            removeFollow();

            initView();

            // 서버에 like 리스트에서 currentUser 를 삭제

        } else {
            isfollow = true;
            profileEditButton.setText("언팔로우");

            user.addFollowing(currentUser);
            System.out.println("팔로우 버튼이 눌렸습니다.");
            addFollow();

            initView();
            // 서버에 like 리스트에서 currentUser 를 추가
        }
    }

    public void addFollow() {
        String serverUrl2 = "http://218.39.138.57/and2/SaveFollow.php";
        SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, serverUrl2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //요청 객체에 보낼 데이터를 추가
        smpr.addStringParam("followers", user.getIdx());
        smpr.addStringParam("followings", currentUser.getIdx());
        //이미지 파일 추가
        //요청객체를 서버로 보낼 우체통 같은 객체 생성
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(smpr);
    }

    public void removeFollow() {
        String serverUrl2 = "http://218.39.138.57/and2/RemoveFollow.php";
        SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, serverUrl2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //요청 객체에 보낼 데이터를 추가
        smpr.addStringParam("followers", user.getIdx());
        smpr.addStringParam("followings", currentUser.getIdx());

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(smpr);
    }


    public interface FeedEtcProvider {
        public Feed getFeedEtc();
        public void updateComment();
    }
}