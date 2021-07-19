package com.inik.neonadeuri;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.FeedListViewAdapter;
import com.inik.neonadeuri.utils.JSONManager;
import com.inik.neonadeuri.utils.SaveSharedPreference;
import com.inik.neonadeuri.utils.VolleyManager;

public class ProfileFeedEtcListActivity extends AppCompatActivity implements FeedListViewAdapter.FeedDataChanger {

    // 클래스 변수
    Context context;
    User user;
    int position;
    String userIdx;

    FeedListViewAdapter feedListViewAdapter;

    // 뷰
    TextView userTextView;
    ImageButton backImageButton;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_feed_etc_list);

        setVariable();
        setView();

    }


    public void setVariable() {
        context = getApplicationContext();

        position = getIntent().getIntExtra("position", 0);
        userIdx = getIntent().getStringExtra("userIdx");

        userLoad();


        feedListViewAdapter = new FeedListViewAdapter(context, this, FeedListViewAdapter.LOAD_FEED_ONE_USER ,userIdx,ProfileFeedEtcListActivity.this);
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

    public void userLoad(){
        String baseURL = "http://218.39.138.57/and2/";
        String apiURL = "loadUser.php";

        RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
        StringRequest request = new StringRequest(
                Request.Method.GET,
                baseURL + apiURL + "?idx=" + userIdx,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        user = JSONManager.jsonObjectToUser(response);

                        userTextView.setText(user.getNickname() + " 의 피드");

                        System.out.println("유저 확인 : " + user.getNickname());

                        initView();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                });
        request.setShouldCache(false);
        queue.add(request);
    }

    public void initView() {

    }

    @Override
    public void feedDataSetChanged() {
        feedListViewAdapter.notifyDataSetChanged();
    }
}