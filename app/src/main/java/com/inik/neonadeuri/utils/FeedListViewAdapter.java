package com.inik.neonadeuri.utils;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.inik.neonadeuri.CommentActivity;
import com.inik.neonadeuri.FeedListFragment;
import com.inik.neonadeuri.HomeActivity;
import com.inik.neonadeuri.ProfileEtcActivity;
import com.inik.neonadeuri.ProfileFragment;
import com.inik.neonadeuri.ProfileSearchActivity;
import com.inik.neonadeuri.R;
import com.inik.neonadeuri.chat.ChatActivity;
import com.inik.neonadeuri.chat.ChatMainActivity;
import com.inik.neonadeuri.models.Feed;
import com.inik.neonadeuri.models.Photo;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.videoChat.RtcActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedListViewAdapter extends BaseAdapter {

    public static final int LOAD_FEED_ALL = 0;
    public static final int LOAD_FEED_ONE_USER = 1;


    private ArrayList<Feed> feeds;
    private Context context;
    private User currentUser;
    private CommentListViewAdapter commentListViewAdapter;
    private String userIdx;
    public FeedDataChanger feedDataChanger;
    public Activity activity;


    public FeedListViewAdapter(Context context, FeedDataChanger feedDataChanger, int loadMode, String UserIdx , Activity activity) {
        this.context = context;
        this.currentUser = CurrentUserManager.getCurrentUser();
        this.feedDataChanger = feedDataChanger;
        this.userIdx = UserIdx;
        feeds = new ArrayList<>();
        this.activity = activity;

        if (loadMode == LOAD_FEED_ALL) {

            String baseURL = "http://218.39.138.57/and2/";
            String apiURL = "loadFeedAll.php";
            String idx  = currentUser.getIdx();

            RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    baseURL + apiURL + "?idx=" + idx,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = (JSONArray) jsonObject.get("feed");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject feedIdJSONObject = (JSONObject) jsonArray.get(i);
                                    String feedIdx = String.valueOf(feedIdJSONObject.get("idx"));

                                    feeds.add(new Feed(feedIdx));
                                    feedDataChanger.feedDataSetChanged();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

        } else if (loadMode == LOAD_FEED_ONE_USER) {

            String baseURL = "http://218.39.138.57/and2/";
            String apiURL = "loadFeedOne.php";
            String idx = userIdx;

            RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    baseURL + apiURL + "?idx=" + idx,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = (JSONArray) jsonObject.get("feed");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject feedIdJSONObject = (JSONObject) jsonArray.get(i);
                                    String feedIdx = String.valueOf(feedIdJSONObject.get("idx"));

                                    feeds.add(new Feed(feedIdx));
                                    feedDataChanger.feedDataSetChanged();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


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
    }

    @Override
    public int getCount() {
        return feeds.size();
    }

    @Override
    public Object getItem(int position) {
        return feeds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedListViewItem feedListViewItem;
        Feed feed = feeds.get(position);

        commentListViewAdapter = new CommentListViewAdapter(context, feed.getComments());

        if (convertView != null && position != 0) {
            feedListViewItem = (FeedListViewItem) convertView;
        } else {
            feedListViewItem = new FeedListViewItem(context);
        }
        feedListViewItem.setVariable(feed, commentListViewAdapter);
        feedListViewItem.loadFeed();

        return feedListViewItem;
    }

    public void setFeed(Feed feed) {
        for (int i = 0; i < feeds.size(); i++) {
            if (feeds.get(i).getIdx().equals(feed.getIdx())) {
                feeds.set(i, feed);
            }
        }
    }

    public interface FeedDataChanger {
        void feedDataSetChanged();
    }

    public class FeedListViewItem extends RelativeLayout implements CommentActivity.FeedProvider, ProfileEtcActivity.FeedEtcProvider {

        // 클래스 변수
        boolean isliked;
        Feed feed;

        CommentListViewAdapter commentListViewAdapter;

        // 뷰
        CircleImageView profileCircleImageView;

        TextView userTextView;
        ImageButton moreImageButton;

        ImageView feedImageView;

        ImageButton likedImageButton;
        ImageButton commentImageButton;
        ImageButton sendImageButton;

        TextView feedLikeTextView;
        TextView feedTextView;
        TextView tagTextView;
        TextView postTimePostedTextView;
        TextView showCommentButton;

        public FeedListViewItem(Context context) {
            super(context);

            setView();
        }

        public FeedListViewItem(Context context, AttributeSet attrs) {
            super(context, attrs);

            setView();
        }

        public FeedListViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);

            setView();
        }

        public FeedListViewItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);

            setView();
        }

        public void setVariable(Feed feed, CommentListViewAdapter commentListViewAdapter) {
            this.feed = feed;
            this.commentListViewAdapter = commentListViewAdapter;
        }

        // 뷰 설정 메서드
        public void setView() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.layout_profile_item, this, true);

            profileCircleImageView = (CircleImageView) findViewById(R.id.profile_circle_image_view);

            userTextView = (TextView) findViewById(R.id.profile_user_text_view);
            moreImageButton = (ImageButton) findViewById(R.id.more_image_button);

            feedImageView = (ImageView) findViewById(R.id.feed_image_view);

            likedImageButton = (ImageButton) findViewById(R.id.liked_image_button);
            commentImageButton = (ImageButton) findViewById(R.id.comment_image_button);
            sendImageButton = (ImageButton) findViewById(R.id.send_image_button);

            feedLikeTextView = (TextView) findViewById(R.id.feed_like_text_view);
            feedTextView = (TextView) findViewById(R.id.feed_text_view);
            tagTextView = (TextView) findViewById(R.id.feed_tag_text_view);
            postTimePostedTextView = (TextView) findViewById(R.id.feed_time_posted_text_view);
            showCommentButton = (TextView) findViewById(R.id.show_comment_button);

            // 리스너 연결
            moreImageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    moreOption();
                }
            });

            likedImageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    liked();
                }
            });

            commentImageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    comment();
                }
            });

            sendImageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    show();

                    //sent();
                }
            });

            showCommentButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showComment();
                }
            });
            userTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser.getNickname().equals(feed.getWriter().getNickname())) {
                        HomeActivity h = (HomeActivity) ((FeedListFragment) feedDataChanger).getActivity();
                        h.viewPager.setCurrentItem(4, true);
                    } else {
                        moveToProfileEtcActivity();
                    }
                }
            });
        }

        public void initView(Feed feed) {
            loadWriter();


            feedImageView.setImageBitmap(Bitmap.createBitmap(500, 800, Bitmap.Config.ARGB_8888));
            loadImage(feed);

            feedTextView.setText(feed.getFeedText());
            tagTextView.setText(feed.getFeedTag());

            postTimePostedTextView.setText(feed.getDateString());

            // 더보기 버튼 색상 변경
            moreImageButton.setColorFilter(Color.parseColor("#064274"), PorterDuff.Mode.SRC_IN);

            // 좋아요 텍스트 초기화
            setNumberOfLikeText();

            // 댓글 더보기 버튼 초기화
            int numOfComments = feed.getComments().size();
            if (numOfComments != 0) {
                showCommentButton.setText(Integer.toString(numOfComments) + " 개의 댓글 보기");
                showCommentButton.setEnabled(true);
            } else {
                showCommentButton.setText("댓글 없음");
                showCommentButton.setEnabled(false);
            }

            // 따봉 버튼 초기화
            if (feed.checkUserInLikePeople(currentUser)) {
                isliked = true;
                likedImageButton.setColorFilter(Color.parseColor("#E31B23"), PorterDuff.Mode.SRC_IN);
            } else {
                isliked = false;
                likedImageButton.setColorFilter(Color.parseColor("#BBBBBB"), PorterDuff.Mode.SRC_IN);
            }

        }

        public void loadFeed() {
            String baseURL = "http://218.39.138.57/and2/";
            String apiURL = "loadFeed.php";

            RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    baseURL + apiURL + "?idx=" + feed.getIdx(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Feed loadedFeed = JSONManager.jsonObjectToFeed(response);

                            setFeed(loadedFeed);

                            String baseURL = "http://218.39.138.57/and2/";
                            String apiURL = "loadPhoto.php";

                            RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
                            StringRequest request = new StringRequest(
                                    Request.Method.GET,
                                    baseURL + apiURL + "?idx=" + loadedFeed.getPhoto().getIdx(),
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Photo photo = JSONManager.jsonObjectToPhoto(response);
                                            loadedFeed.setPhoto(photo);
                                            feed = loadedFeed;

                                            initView(loadedFeed);
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

        public void loadImage(Feed profilefeed) {
            if (profilefeed.getPhoto().getBitmapImg() == null) {
                Glide.with(context).asBitmap().load(profilefeed.getPhoto().getServerPath())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                profilefeed.getPhoto().setBitmapImg(resource);
                                feedImageView.setImageBitmap(profilefeed.getPhoto().getBitmapImg());
                            }
                        });
            } else {
                feedImageView.setImageBitmap(feed.getPhoto().getBitmapImg());
            }
        }

        public void moreOption() {
            System.out.println("더보기 버튼이 클릭되었습니다");
        }

        public void liked() {
            System.out.println("따봉 버튼이 클릭되었습니다");

            if (isliked) {
                isliked = false;
                likedImageButton.setColorFilter(Color.parseColor("#BBBBBB"), PorterDuff.Mode.SRC_IN);

                feed.getLikedPeople().remove(currentUser);
                setNumberOfLikeText();
                removeLike();
                // 서버에 like 리스트에서 currentUser 를 삭제

            } else {
                isliked = true;
                likedImageButton.setColorFilter(Color.parseColor("#E31B23"), PorterDuff.Mode.SRC_IN);


                feed.addLike(currentUser);
                setNumberOfLikeText();
                addLike();
                // 서버에 like 리스트에서 currentUser 를 추가
            }
        }

        public void setNumberOfLikeText() {
            ArrayList<User> likedPeople = feed.getLikedPeople();

            if (likedPeople.size() == 0) {
                feedLikeTextView.setText("좋아요 없음");
            } else {
                loadUser(likedPeople.get(0).getIdx(), likedPeople.size());
            }
        }

        public void comment() {
            System.out.println("댓글 버튼이 클릭되었습니다");
            moveToCommentActivity();
        }

        public void sent() {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("userSender", currentUser.getNickname());
            intent.putExtra("userReceiver", feed.getWriter().getNickname());
            intent.putExtra("userReceiverId", feed.getWriter().getIdx());
            context.startActivity(intent);

        }

        public void videoSent() {
            Intent intent = new Intent(context, RtcActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("userSender", currentUser.getNickname());
            intent.putExtra("userReceiver", feed.getWriter().getNickname());
            intent.putExtra("userReceiverId", feed.getWriter().getIdx());
            context.startActivity(intent);

        }
        public void showComment() {
            moveToCommentActivity();
        }

        public void moveToCommentActivity() {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            CommentActivity.feedProvider = this;

            context.startActivity(intent);
        }

        public void moveToProfileEtcActivity() {
            Intent intent = new Intent(context, ProfileEtcActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ProfileEtcActivity.feedEtcProvider = this;

            context.startActivity(intent);
        }

        @Override
        public Feed getFeed() {
            System.out.println("feed 를 넘겨줍니다.");
            return feed;
        }

        @Override
        public Feed getFeedEtc() {
            System.out.println("feedEtc 를 넘겨줍니다.");
            return feed;
        }

        @Override
        public void updateComment() {
            initView(feed);
        }

        public void addLike() {
            String serverUrl2 = "http://218.39.138.57/and2/SaveFeedLike.php";
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
            smpr.addStringParam("feed", feed.getIdx());
            smpr.addStringParam("likedpeople", currentUser.getIdx());
            //이미지 파일 추가
            //요청객체를 서버로 보낼 우체통 같은 객체 생성
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(smpr);
        }

        public void loadUser(String index, int numOfLiked) {
            String baseURL = "http://218.39.138.57/and2/";
            String apiURL = "loadUser.php";
            String idx = index;

            RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    baseURL + apiURL + "?idx=" + idx,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            User user = JSONManager.jsonObjectToUser(response);

                            if (numOfLiked == 1) {
                                feedLikeTextView.setText(user.getNickname() + " 님이 좋아함");
                            } else {
                                feedLikeTextView.setText(user.getNickname() + " 님 외 " + Integer.toString(numOfLiked - 1) + " 명이 좋아함");
                            }

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

        public void removeLike() {
            String serverUrl2 = "http://218.39.138.57/and2/RemoveFeedLike.php";
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
            smpr.addStringParam("feed", feed.getIdx());
            smpr.addStringParam("likedpeople", currentUser.getIdx());

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(smpr);
        }

        public void loadWriter() {
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

                            User user = JSONManager.jsonObjectToUser(response);
                            feed.setWriter(user);

                            if (currentUser.getIdx() == feed.getWriter().getIdx()) {
                                userTextView.setText(currentUser.getNickname());
                            } else {
                                userTextView.setText(feed.getWriter().getNickname());
                            }

                            String baseURL = "http://218.39.138.57/and2/";
                            String apiURL = "loadPhoto.php";


                            RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
                            StringRequest request = new StringRequest(
                                    Request.Method.GET,
                                    baseURL + apiURL + "?idx=" + feed.getWriter().getProfileImg().getIdx(),
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            System.out.println("포토 리스폰스 확인" + response);

                                            Photo photo = JSONManager.jsonObjectToPhoto(response);
                                            feed.getWriter().setProfileImg(photo);

                                            String url = feed.getWriter().getProfileImg().getServerPath();
                                            Glide.with(context).asBitmap().load(url)
                                                    .into(new SimpleTarget<Bitmap>() {
                                                        @Override
                                                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                                            profileCircleImageView.setImageBitmap(resource);//
                                                            feed.getWriter().getProfileImg().setBitmapImg(resource);// 유저 이미지 세팅

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

        public void show()
        {
            final List<String> ListItems = new ArrayList<>();
            ListItems.add("채팅");
            ListItems.add("영상통화");
            final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

            final List SelectedItems  = new ArrayList();
            int defaultItem = 0;
            SelectedItems.add(defaultItem);

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("다음중 하나를 선택해주세요");
            builder.setSingleChoiceItems(items, defaultItem,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SelectedItems.clear();
                            SelectedItems.add(which);
                        }
                    });
            builder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String msg="";

                            if (!SelectedItems.isEmpty()) {
                                int index = (int) SelectedItems.get(0);
                                msg = ListItems.get(index);
                            }

                            if(msg.equals("채팅")){
                                sent();
                            }
                            if(msg.equals("영상통화")){
                                videoSent();
                            }
                        }
                    });
            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
        }
    }

}
