package com.inik.neonadeuri;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.ImageLoadTask;
import com.inik.neonadeuri.utils.ProfileGridViewAdapter;

import javax.xml.transform.URIResolver;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    // 클래스 변수
    Context context;
    User currentUser;

    ProfileGridViewAdapter profileGridViewAdapter;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        setVariable();
        setView(view);

        return view;
    }

    public void setVariable() {
        context = getActivity().getApplicationContext();
        currentUser = CurrentUserManager.getCurrentUser();
        profileGridViewAdapter = new ProfileGridViewAdapter(context);
    }

    // 뷰 설정 메서드
    public void setView(View view) {
        nicknameTextView = (TextView) view.findViewById(R.id.nickname_text_view);
        postCountTextView = (TextView) view.findViewById(R.id.post_count_text_view);
        followersCountTextView = (TextView) view.findViewById(R.id.followers_count_text_view);
        followingCountTextView = (TextView) view.findViewById(R.id.following_count_text_view);

        nameTextView = (TextView) view.findViewById(R.id.name_text_view);
        introductionTextView = (TextView) view.findViewById(R.id.introduction_text_view);
        websiteTextView = (TextView) view.findViewById(R.id.website_text_view);

        moreImageButton = (ImageButton) view.findViewById(R.id.more_image_button);
        profileEditButton = (Button) view.findViewById(R.id.profile_edit_button);

        profileCircleImageView = (CircleImageView) view.findViewById(R.id.profile_circle_image_view);

        gridView = (GridView) view.findViewById(R.id.grid_view);
        gridView.setAdapter(profileGridViewAdapter);

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
                profileEdit();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ProfileFeedListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("position", position);

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
        profileCircleImageView.setImageBitmap(currentUser.getProfileImg().getThumbnailBitmapImg());
        nicknameTextView.setText(currentUser.getNickname());
        postCountTextView.setText(Integer.toString(currentUser.getFeeds().size()));
        followersCountTextView.setText(Integer.toString(currentUser.getFollowers().size()));
        followingCountTextView.setText(Integer.toString(currentUser.getFollowing().size()));

        nameTextView.setText(currentUser.getName());
        introductionTextView.setText(currentUser.getIntroduction());
        websiteTextView.setText(currentUser.getWebSite());
    }

    public void moreOption() {
        System.out.println("더보기 버튼이 클릭되었습니다.");
    }

    public void profileEdit() {
        Intent intent = new Intent(context, ProfileEditActivity.class);
        startActivity(intent);
    }

}