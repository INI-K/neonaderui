package com.inik.neonadeuri.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.inik.neonadeuri.R;
import com.inik.neonadeuri.models.Comment;
import com.inik.neonadeuri.models.Feed;
import com.inik.neonadeuri.models.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListViewAdapter extends BaseAdapter {

    private ArrayList<Comment> comments;
    private Context context;
    private User currentUser;

    public CommentListViewAdapter(Context context, ArrayList<Comment> comments) {
        this.comments = comments;
        this.context = context;
        this.currentUser = CurrentUserManager.getCurrentUser();
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentListViewItem commentListViewItem;
        Comment comment = comments.get(position);

        if (convertView == null) {
            commentListViewItem = new CommentListViewItem(context);
        } else {
            commentListViewItem = (CommentListViewItem) convertView;
        }

        commentListViewItem.setVariable(comment);
        commentListViewItem.initView();

        return commentListViewItem;
    }

    public class CommentListViewItem extends LinearLayout {

        // 클래스 변수
        boolean isLiked;
        Comment comment;
        Context context;
        User writer;

        // 뷰
        CircleImageView circleImageView;

        TextView userTextView;
        TextView commentTextView;
        TextView commentTimePostedTextView;
        TextView commentLikedTextView;

        ImageButton commentLikedImageButton;

        public CommentListViewItem(Context context) {
            super(context);
            this.context = context;

            setView();
        }

        public CommentListViewItem(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.context = context;

            setView();
        }

        public CommentListViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            this.context = context;

            setView();
        }

        public CommentListViewItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            this.context = context;

            setView();
        }

        public void setVariable(Comment comment) {
            this.comment = comment;
            writer = comment.getWriter();
        }

        // 뷰 설정 메서드
        public void setView() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.layout_comment_item, this, true);

            circleImageView = (CircleImageView) findViewById(R.id.comment_profile_image);

            userTextView = (TextView) findViewById(R.id.comment_user_text_view);
            commentTextView = (TextView) findViewById(R.id.comment_text_view);
            commentTimePostedTextView = (TextView) findViewById(R.id.comment_time_posted_text_view);
            commentLikedTextView = (TextView) findViewById(R.id.comment_liked_text_view);

            commentLikedImageButton = (ImageButton) findViewById(R.id.comment_liked_image_button);

            // 리스너 설정
            commentLikedImageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    liked();
                }
            });
        }

        // 뷰 초기화 메서드
        public void initView() {



            if(writer == null) {
                System.out.println("Load Data");
                loadComment(context);
            } else {
                circleImageView.setImageBitmap(writer.getProfileImg().getThumbnailBitmapImg());

                userTextView.setText(writer.getNickname());
                commentTextView.setText(comment.getComment());
                commentTimePostedTextView.setText(comment.getDateString());
                commentLikedTextView.setText(comment.getLikedString());
            }

        }

        public void liked() {
            if (isLiked) {
                isLiked = false;
                commentLikedImageButton.setColorFilter(Color.parseColor("#BBBBBB"), PorterDuff.Mode.SRC_IN);

                comment.getLikedPeoples().remove(currentUser);
                removeLike();
            } else {
                isLiked = true;
                commentLikedImageButton.setColorFilter(Color.parseColor("#E31B23"), PorterDuff.Mode.SRC_IN);

                comment.addLike(currentUser);
                addLike();
            }

            commentLikedTextView.setText(comment.getLikedString());
        }

        public void loadComment(Context context) {
            String baseURL = "http://218.39.138.57/and2/";
            String apiURL = "loadComment.php";

            RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();

            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    baseURL + apiURL + "?idx=" + comment.getIdx(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Comment responseComment = JSONManager.jsonObjectToComment(response);
                            comment = responseComment;

                            loadUser();
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

        public void loadUser() {
            String baseURL = "http://218.39.138.57/and2/";
            String apiURL = "loadUser.php";
            String idx = comment.getWriter().getIdx();

            RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    baseURL + apiURL + "?idx=" + idx,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            comment.setWriter(JSONManager.jsonObjectToUser(response));
                            loadImage();
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

        public void loadImage() {
            String baseURL = "http://218.39.138.57/and2/";
            String apiURL = "loadPhoto.php";

            RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    baseURL + apiURL + "?idx=" + comment.getWriter().getProfileImg().getIdx(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            comment.getWriter().setProfileImg(JSONManager.jsonObjectToPhoto(response));

                            String url = comment.getWriter().getProfileImg().getServerPath();
                            Glide.with(context).asBitmap().load(url)
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                            comment.getWriter().getProfileImg().setBitmapImg(resource);
                                            circleImageView.setImageBitmap(comment.getWriter().getProfileImg().getThumbnailBitmapImg());

                                            userTextView.setText(comment.getWriter().getNickname());
                                            commentTextView.setText(comment.getComment());
                                            commentTimePostedTextView.setText(comment.getDateString());
                                            commentLikedTextView.setText(comment.getLikedString());



                                            // 따봉 버튼 초기화
                                            if (comment.checkUserInLikePeople(currentUser)) {
                                                isLiked = true;
                                                commentLikedImageButton.setColorFilter(Color.parseColor("#E31B23"), PorterDuff.Mode.SRC_IN);
                                            } else {
                                                isLiked = false;
                                                commentLikedImageButton.setColorFilter(Color.parseColor("#BBBBBB"), PorterDuff.Mode.SRC_IN);
                                            }

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

        public void addLike() {
            String serverUrl2 = "http://218.39.138.57/and2/SaveCommentLike.php";
            SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, serverUrl2, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println("라이크 리스폰스 확인" + response);
                    initView();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            //요청 객체에 보낼 데이터를 추가
            smpr.addStringParam("comment", comment.getIdx());
            smpr.addStringParam("likedpeople", currentUser.getIdx());
            //이미지 파일 추가
            //요청객체를 서버로 보낼 우체통 같은 객체 생성
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(smpr);
        }

        public void removeLike() {
            String serverUrl2 = "http://218.39.138.57/and2/RemoveCommentLike.php";
            SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, serverUrl2, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    initView();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            //요청 객체에 보낼 데이터를 추가
            smpr.addStringParam("comment", comment.getIdx());
            smpr.addStringParam("likedpeople", currentUser.getIdx());
            //이미지 파일 추가
            //요청객체를 서버로 보낼 우체통 같은 객체 생성
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(smpr);
        }


    }
}
