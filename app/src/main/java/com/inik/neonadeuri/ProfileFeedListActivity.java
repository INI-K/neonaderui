package com.inik.neonadeuri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.inik.neonadeuri.models.Feed;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.FeedListViewAdapter;

import java.util.ArrayList;

public class ProfileFeedListActivity extends AppCompatActivity implements FeedListViewAdapter.FeedDataChanger {

    // 클래스 변수
    Context context;
    User currentUser;
    int position;

    FeedListViewAdapter feedListViewAdapter;

    // 뷰
    TextView userTextView;
    ImageButton backImageButton;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_feed_list);

        setVariable();
        setView();
        initView();
    }

    //
    public void setVariable() {
        context = getApplicationContext();
        currentUser = CurrentUserManager.getCurrentUser();
        position = getIntent().getIntExtra("position", 0);

        feedListViewAdapter = new FeedListViewAdapter(context, this, FeedListViewAdapter.LOAD_FEED_ONE_USER , currentUser.getIdx(),ProfileFeedListActivity.this);
    }

    // 뷰 설정 메서드
    public void setView() {
        // 상태바 제거
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 상태바 색상 변경
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));

        // 액션바 제거
        getSupportActionBar().hide();

        userTextView = (TextView) findViewById(R.id.user_text_view);
        backImageButton = (ImageButton) findViewById(R.id.back_button);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(feedListViewAdapter);
        listView.setSelection(position);

        // 리스너 부착
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndRemoveTask();
            }
        });
    }

    public void initView() {
        userTextView.setText(currentUser.getNickname() + " 의 피드");
    }

    @Override
    public void feedDataSetChanged() {
        feedListViewAdapter.notifyDataSetChanged();
    }
}