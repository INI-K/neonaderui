package com.inik.neonadeuri;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.inik.neonadeuri.chat.ChatListActivity;
import com.inik.neonadeuri.chat.ChatMainActivity;
import com.inik.neonadeuri.models.Feed;
import com.inik.neonadeuri.models.Photo;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.FeedListViewAdapter;
import com.inik.neonadeuri.utils.JSONManager;
import com.inik.neonadeuri.utils.ProfileGridViewAdapter;
import com.inik.neonadeuri.utils.SaveSharedPreference;
import com.inik.neonadeuri.utils.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FeedListFragment extends Fragment implements FeedListViewAdapter.FeedDataChanger  {

    // 클래스 변수

    Context context;
    User currentUser;

    FeedListViewAdapter feedListViewAdapter;

    ImageButton button;

    // 뷰
    ListView listView;
    SwipeRefreshLayout swipe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_list, container, false);

        setVariable();
        setView(view);



        return view;
    }

    public void setVariable() {
        context = getActivity().getApplicationContext();
        currentUser = CurrentUserManager.getCurrentUser();
    }

    // 뷰 설정 메서드
    public void setView(View view) {
        button = (ImageButton) view.findViewById(R.id.send_image_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        listView.setAdapter(feedListViewAdapter);
        feedListViewAdapter = new FeedListViewAdapter(context, this, FeedListViewAdapter.LOAD_FEED_ALL, currentUser.getIdx(),getActivity());
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView = (ListView) view.findViewById(R.id.list_view);
                        listView.setAdapter(feedListViewAdapter);
                        feedListViewAdapter = new FeedListViewAdapter(context, FeedListFragment.this, FeedListViewAdapter.LOAD_FEED_ALL, currentUser.getIdx(),getActivity());

                        swipe.setRefreshing(false);
                    }
                }, 500);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.detach(this).attach(this).commit();
    }

    @Override
    public void feedDataSetChanged() {
        feedListViewAdapter.notifyDataSetChanged();
        listView.setAdapter(feedListViewAdapter);
    }
}