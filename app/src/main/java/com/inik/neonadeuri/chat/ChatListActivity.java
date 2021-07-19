package com.inik.neonadeuri.chat;


import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.inik.neonadeuri.R;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.JSONManager;
import com.inik.neonadeuri.utils.SaveSharedPreference;
import com.inik.neonadeuri.utils.SearchAdapter;
import com.inik.neonadeuri.utils.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private List<String> list;          // 데이터를 넣은 리스트변수
    private ListView listView;          // 검색을 보여줄 리스트변수
    private EditText editSearch;        // 검색어를 입력할 Input 창
    private ChatListAdapter adapter;      // 리스트뷰에 연결할 아답터
    private Context context;

    public User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // 상태바 색상 변경
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));

        // 액션바 제거
        getSupportActionBar().hide();

        context = getApplicationContext();

        currentUser = CurrentUserManager.getCurrentUser();


        listView = (ListView) findViewById(R.id.listView);

        // 리스트를 생성한다.
        list = new ArrayList<String>();

        // 검색에 사용할 데이터을 미리 저장한다.
        settingList();

        // 리스트의 모든 데이터를 arraylist에 복사한다.// list 복사본을 만든다.

        // 리스트에 연동될 아답터를 생성한다.
        adapter = new ChatListAdapter(list, context, null);

        // 리스트뷰에 아답터를 연결한다.
        listView.setAdapter(adapter);

        // input창에 검색어를 입력시 "addTextChangedListener" 이벤트 리스너를 정의한다.


    }


    // 검색에 사용될 데이터를 리스트에 추가한다.
    private void settingList() {
        String serverUrl2 = "http://218.39.138.57/and2/ChatList.php";
        SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, serverUrl2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("닉네임 확인 :"  + currentUser.getNickname());

                System.out.println("리스폰스2222" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = (JSONArray) jsonObject.get("room");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject userJSONObject = (JSONObject) jsonArray.get(i);
                        String targetId = String.valueOf(userJSONObject.get("receiver"));
                        list.add(targetId);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        //요청 객체에 보낼 데이터를 추가
        smpr.addStringParam("idx", String.valueOf(currentUser.getNickname()));

        //이미지 파일 추가
        //요청객체를 서버로 보낼 우체통 같은 객체 생성
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(smpr);
    }
}