package com.inik.neonadeuri;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.inik.neonadeuri.models.Comment;
import com.inik.neonadeuri.models.Feed;
import com.inik.neonadeuri.models.Photo;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.utils.CommentListViewAdapter;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.JSONManager;
import com.inik.neonadeuri.utils.VolleyManager;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    // 클래스 변수
    private Context context;
    private User currentUser;

    private CommentListViewAdapter commentListViewAdapter;

    public static FeedProvider feedProvider;

    private Feed feed;

    private InputMethodManager inputMethodManager;

    // 뷰
    private ImageButton backImageButton;
    private ImageButton sendImageButton;

    private CircleImageView profileCircleImageView;

    private TextView userTextView;
    private TextView feedTextView;
    private TextView feedTimePostedTextView;

    private ListView listView;

    private EditText commentEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        setVariable();
        setView();
        initView();
    }

    // 클래스 변수 설정 메서드
    public void setVariable() {
        context = getApplicationContext();
        currentUser = CurrentUserManager.getCurrentUser();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        feed = feedProvider.getFeed();

        commentListViewAdapter = new CommentListViewAdapter(context, feed.getComments());
    }

    // 뷰 설정 메서드
    public void setView() {
        // 상태바 제거
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 상태바 색상 변경
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));

        // 액션바 제거
        getSupportActionBar().hide();

        backImageButton = findViewById(R.id.back_button);
        sendImageButton = findViewById(R.id.send_button);

        profileCircleImageView = findViewById(R.id.feed_profile_image);

        userTextView = findViewById(R.id.feed_user_text_view);
        feedTextView = findViewById(R.id.feed_text_view);
        feedTimePostedTextView = findViewById(R.id.feed_time_posted_text_view);

        listView = findViewById(R.id.list_view);
        listView.setAdapter(commentListViewAdapter);

        commentEditText = findViewById(R.id.comment_edit_text);
        saveButton = findViewById(R.id.save_button);

        backImageButton.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
        sendImageButton.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);

        // 리스너 부착
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndRemoveTask();
            }
        });

        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    // 뷰 초기화 메서드
    public void initView() {
        String baseURL = "http://218.39.138.57/and2/";
        String apiURL = "loadUser.php";
        String idx = feed.getWriter().getIdx();

        RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
        StringRequest request = new StringRequest(
                Request.Method.GET,
                baseURL + apiURL + "?idx=" + idx,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        feed.setWriter(JSONManager.jsonObjectToUser(response));

                        loadImage(context);
                        userTextView.setText(feed.getWriter().getNickname());
                        feedTextView.setText(feed.getFeedText());
                        feedTimePostedTextView.setText(feed.getDateString());
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

    public void loadImage(Context context) {
        String baseURL = "http://218.39.138.57/and2/";
        String apiURL = "loadPhoto.php";

        RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
        StringRequest request = new StringRequest(
                Request.Method.GET,
                baseURL + apiURL + "?idx=" +  feed.getWriter().getProfileImg().getIdx(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Photo photo = JSONManager.jsonObjectToPhoto(response);
                        feed.getWriter().setProfileImg(photo);

                        String url = photo.getServerPath();
                        Glide.with(getApplicationContext()).asBitmap().load(url)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                        feed.getWriter().getProfileImg().setBitmapImg(resource); // 유저 이미지 세팅
                                        profileCircleImageView.setImageBitmap(feed.getWriter().getProfileImg().getThumbnailBitmapImg());
                                    }
                                });
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

    public void send() {
        System.out.println("보내기 버튼이 클릭되었습니다.");
    }

    public void save() {
        System.out.println("저장 버튼이 클릭되었습니다.");
        String commentText = commentEditText.getText().toString();

        if(commentText.length() > 0) {
            Comment comment = new Comment(currentUser);
            comment.setComment(commentText);
            feed.addComment(comment);
            uploadComment(comment);
            commentEditText.setText("");
        }

        inputMethodManager.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
        commentListViewAdapter.notifyDataSetChanged();
        feedProvider.updateComment();
    }

    public void uploadComment(Comment comment) {
        String baseURL = "http://218.39.138.57/and2/";
        String apiURL = "SaveComment.php";
        RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                baseURL + apiURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("업로드 코멘트 리스폰스 : " + response);
                        comment.setIdx(response);

                        commentSubUpload(comment);
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

                params.put("JSON", JSONManager.commentToJSONObject(comment).toString());

                return params;
            }
        };
        request.setShouldCache(false);
        queue.add(request);
    }


    public void commentSubUpload(Comment comment){
        String serverUrl2="http://218.39.138.57/and2/SaveComment_sub.php";

        SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CommentActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        });

        //요청 객체에 보낼 데이터를 추가
        smpr.addStringParam("feed", feed.getIdx());
        smpr.addStringParam("comment", comment.getIdx());
        //이미지 파일 추가


        //요청객체를 서버로 보낼 우체통 같은 객체 생성
        RequestQueue requestQueue= Volley.newRequestQueue(context);
        requestQueue.add(smpr);
    }



    public interface FeedProvider {
        public Feed getFeed();
        public void updateComment();
    }



}