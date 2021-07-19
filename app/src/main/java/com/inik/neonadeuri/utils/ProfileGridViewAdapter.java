package com.inik.neonadeuri.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.inik.neonadeuri.R;
import com.inik.neonadeuri.models.Feed;
import com.inik.neonadeuri.models.Photo;

import java.util.ArrayList;

public class ProfileGridViewAdapter extends BaseAdapter {

    private ArrayList<Feed> feeds;
    private Context context;

    CommentListViewAdapter commentListViewAdapter;

    public ProfileGridViewAdapter(Context context) {
        feeds = CurrentUserManager.getCurrentUser().getFeeds();
        this.context = context;
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
        ProfileGridItem profileGridItem = convertView == null ? new ProfileGridItem(context) : (ProfileGridItem) convertView;

        profileGridItem.setImage(feeds.get(position).getPhoto());

        return profileGridItem;
    }

    public class ProfileGridItem extends LinearLayout {

        ImageView imageView;

        public ProfileGridItem(Context context) {
            super(context);

            setView();
        }

        public ProfileGridItem(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);

            setView();
        }

        public ProfileGridItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);

            setView();
        }

        public ProfileGridItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);

            setView();
        }

        public void setVariable() {
        }

        // 뷰 설정 메서드
        public void setView() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.layout_profile_grid_item, this, true);

            imageView = (ImageView) findViewById(R.id.image_view);
            imageView.setImageBitmap(Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888));
        }

        public void setImage(Photo photo) {
            if(photo.getBitmapImg() == null) {
                Glide.with(context).asBitmap().load(photo.getServerPath())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                photo.setBitmapImg(resource);
                                System.out.println("이미지를 로드합니다");
                                imageView.setImageBitmap(cropBitmap(photo));
                            }
                        });
            } else {
                imageView.setImageBitmap(cropBitmap(photo));
            }
        }

        public Bitmap cropBitmap(Photo photo) {
            Bitmap bitmap = photo.getThumbnailBitmapImg();
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            int originX;
            int originY;

            if (height > width) {
                originX = 0;
                originY = (height - width) / 2;

                bitmap = Bitmap.createBitmap(bitmap, originX, originY, width, width);
            } else {
                originY = 0;
                originX = (width - height) / 2;

                bitmap = Bitmap.createBitmap(bitmap, originX, originY, height, height);
            }

            return bitmap;
        }

    }

}

