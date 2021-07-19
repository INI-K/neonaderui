package com.inik.neonadeuri;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.inik.neonadeuri.models.Feed;
import com.inik.neonadeuri.models.Photo;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.utils.CameraManager;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.FeedListViewAdapter;
import com.inik.neonadeuri.utils.HardwareInformation;
import com.inik.neonadeuri.utils.JSONManager;
import com.inik.neonadeuri.utils.VolleyManager;

import java.util.HashMap;
import java.util.Map;


public class AddFeedFragment extends Fragment implements CameraManager.PhotoReceiver {

    // 클래스 변수
    Context context;
    User currentUser;

    Feed feed;

    int originImageViewWidth;
    int originImageViewHeight;

    ViewTreeObserver viewTreeObserver;

    boolean isFirstCheck;
    boolean isImageSet;

    // 뷰
    Button cancelButton;
    Button saveButton;

    ImageView feedImageView;

    Button setFeedImageButton;

    EditText feedEditText;
    EditText feedTagEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_add_feed, container, false);

        setVariable();
        setView(view);

        return view;
    }

    //
    public void setVariable() {
        context = getActivity().getApplicationContext();
        currentUser = CurrentUserManager.getCurrentUser();
        isFirstCheck = true;
        isImageSet = false;
        CameraManager.photoReceiver = this;

        feed = new Feed();
    }

    // 뷰 설정 메서드
    public void setView(View view) {
        cancelButton = view.findViewById(R.id.cancel_button);
        saveButton = view.findViewById(R.id.save_button);

        // 뷰가 그려시는 시점에 그려진 뷰의 Width 와 Height 를 구한다
        feedImageView = view.findViewById(R.id.feed_image_view);
        viewTreeObserver = feedImageView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 기존 이미지 뷰 크기 저장
                if (isFirstCheck) {
                    originImageViewHeight = feedImageView.getHeight();
                    originImageViewWidth = feedImageView.getWidth();

                    // 이해를 돕기 위한 출력코드
                    System.out.println(Integer.toString(originImageViewHeight));
                    System.out.println(Integer.toString(originImageViewWidth));
                }

                isFirstCheck = false;
            }
        });
        setFeedImageButton = view.findViewById(R.id.set_feed_image_button);

        feedEditText = view.findViewById(R.id.feed_edit_text);
        feedTagEditText = view.findViewById(R.id.feed_tag_edit_text);

        // 리스너 부착
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        setFeedImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage();
            }
        });
    }

    public void cancel() {
        System.out.println("취소 버튼이 클릭되었습니다");

        feedEditText.setText(null);
        feedTagEditText.setText(null);

        isImageSet = false;

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) feedImageView.getLayoutParams();
        layoutParams.width = originImageViewWidth;
        layoutParams.height = originImageViewHeight;

        feedImageView.setLayoutParams(layoutParams);
        feedImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_addphoto));
    }

    public void save() {
        System.out.println("저장 버튼이 클릭되었습니다");

        String feedText = feedEditText.getText().toString();
        String feedTagText = feedTagEditText.getText().toString();

        if (feedText == null || feedText.length() == 0) {
            Toast.makeText(context, "게시글을 입력해주세요", Toast.LENGTH_SHORT).show();

            return;
        }

        if (isImageSet == false) {
            Toast.makeText(context, "사진을 선택해주세요", Toast.LENGTH_SHORT).show();

            return;
        }

        feed.setWriter(currentUser);
        feed.setFeedText(feedText);
        if (feedTagText.length() > 0) {
            String[] feedTagArray = feedTagText.split(" ");

            feedTagText = "";
            for (int i = 0; i < feedTagArray.length; i++) {
                feedTagText += "#" + feedTagArray[i];

                if (i != feedTagArray.length - 1)
                    feedTagText += " ";
            }

            feed.setFeedTag(feedTagText);
        }

        currentUser.addFeed(feed);
        //uploadFeed();
        clickUpload();

        cancel();
        ((HomeActivity) getActivity()).viewPager.setCurrentItem(4, true);


    }


    public void setImage() {
        System.out.println("이미지 변경 버튼이 클릭되었습니다");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("사진 가져오기")
                .setItems(R.array.get_image_method, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                break;
                            case 1:
                                startActivity(new Intent(getActivity(), CameraManager.class));
                                break;
                            default:
                                break;
                        }

                    }
                })
                .show();
    }

    @Override
    public void permissionDenied() {

    }

    @Override
    public void createPhotoSuccess(Photo photo) {
        feed.setPhoto(photo);

        int originBitmapWidth = photo.getBitmapImg().getWidth();
        int originBitmapHeight = photo.getBitmapImg().getHeight();

        float scale = (float) originImageViewWidth / (float) originBitmapWidth;

        int thumbnailBitmapWidth = originImageViewWidth;
        int thumbnailBitmapHeight = (int) (originBitmapHeight * scale);

        Bitmap thumbnailBitmap = photo.getBitmapImg().createScaledBitmap(photo.getBitmapImg(), thumbnailBitmapWidth, thumbnailBitmapHeight, true);
        Bitmap bitmap = Bitmap.createBitmap(thumbnailBitmapWidth, thumbnailBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = HardwareInformation.dpToPx(8);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(thumbnailBitmap, rect, rect, paint);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) feedImageView.getLayoutParams();
        layoutParams.width = thumbnailBitmapWidth;
        layoutParams.height = thumbnailBitmapHeight;
        feedImageView.setLayoutParams(layoutParams);

        feedImageView.setImageBitmap(bitmap);
        isImageSet = true;
    }

    @Override
    public void createPhotoFail() {

    }


    public void clickUpload() {

        //CurrentUserManager.getCurrentUser().addFeed(feed);

        //안드로이드에서 보낼 데이터를 받을 php 서버 주소
        String serverUrl="http://218.39.138.57/and2/SavePhoto.php";

        //Volley plus Library를 이용해서
        //파일 전송하도록..
        //Volley+는 AndroidStudio에서 검색이 안됨 [google 검색 이용]

        //파일 전송 요청 객체 생성[결과를 String으로 받음]
        SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("포토 아이디 : " + response);
               feed.getPhoto().setIdx(response);

                String baseURL = "http://218.39.138.57/and2/";
                String apiURL = "SaveFeed.php";
                RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        baseURL + apiURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                feed.setIdx(response);
                                System.out.println("피드 아이디 : " +feed.getIdx());

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
                        params.put("JSON", JSONManager.feedToJSONObject(feed).toString());
                        return params;
                    }
                };
                request.setShouldCache(false);
                queue.add(request);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //요청 객체에 보낼 데이터를 추가
        smpr.addStringParam("applicationPath", feed.getPhoto().getApplicationPath());
        //이미지 파일 추가
        smpr.addFile("img", feed.getPhoto().getApplicationPath());

        //요청객체를 서버로 보낼 우체통 같은 객체 생성
        RequestQueue requestQueue= Volley.newRequestQueue(context);
        requestQueue.add(smpr);

    }


}