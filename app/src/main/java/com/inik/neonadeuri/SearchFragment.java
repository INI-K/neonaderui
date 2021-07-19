package com.inik.neonadeuri;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.inik.neonadeuri.models.Feed;
import com.inik.neonadeuri.models.User;
import com.inik.neonadeuri.utils.CurrentUserManager;
import com.inik.neonadeuri.utils.JSONManager;
import com.inik.neonadeuri.utils.SearchAdapter;
import com.inik.neonadeuri.utils.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment implements SearchAdapter.UserDataChanger {

    private List<User> list;          // 데이터를 넣은 리스트변수
    private ListView listView;          // 검색을 보여줄 리스트변수
    private EditText editSearch;        // 검색어를 입력할 Input 창
    private SearchAdapter adapter;      // 리스트뷰에 연결할 아답터
    private ArrayList<User> arraylist;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        context = getActivity().getApplicationContext();


        editSearch = (EditText) view.findViewById(R.id.editSearch);
        listView = (ListView) view.findViewById(R.id.listView);

        // 리스트를 생성한다.
        list = new ArrayList<User>();
        arraylist = new ArrayList<User>();

        // 검색에 사용할 데이터을 미리 저장한다.
        settingList();

        // 리스트의 모든 데이터를 arraylist에 복사한다.// list 복사본을 만든다.

        // 리스트에 연동될 아답터를 생성한다.
        adapter = new SearchAdapter(list, context, this);

        // 리스트뷰에 아답터를 연결한다.
        listView.setAdapter(adapter);

        // input창에 검색어를 입력시 "addTextChangedListener" 이벤트 리스너를 정의한다.
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editSearch.getText().toString();
                search(text);
            }
        });

        return view;
    }

    public void search(String charText) {
        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        list.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            list.addAll(arraylist);
        }
        // 문자 입력을 할때..
        else {
            // 리스트의 모든 데이터를 검색한다.
            for (int i = 0; i < arraylist.size(); i++) {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (arraylist.get(i).getNickname().contains(charText)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    list.add(arraylist.get(i));
                    System.out.println("계속 도냐 33 : " + arraylist.size());
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }

    // 검색에 사용될 데이터를 리스트에 추가한다.
    private void settingList() {
        String baseURL = "http://218.39.138.57/and2/";
        String apiURL = "loadUserAll.php";

        RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
        StringRequest request = new StringRequest(
                Request.Method.GET,
                baseURL + apiURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = (JSONArray) jsonObject.get("user");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                User user =  new User();

                                JSONObject userJSONObject = (JSONObject) jsonArray.get(i);
                                String userIdx = String.valueOf(userJSONObject.get("idx"));

                                String baseURL = "http://218.39.138.57/and2/";
                                String apiURL = "loadUser.php";

                                RequestQueue queue = VolleyManager.getInstance(context).getRequestQueue();
                                StringRequest request = new StringRequest(
                                        Request.Method.GET,
                                        baseURL + apiURL + "?idx=" + userIdx,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                try {
                                                    User user =  new User();
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    user = JSONManager.jsonObjectToUser(response);


                                                    System.out.println("계속 도냐2" );

                                                    list.add(user);

                                                    arraylist.add(user);
                                                    adapter.notifyDataSetChanged();

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

    public void setUser(User user) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIdx().equals(user.getIdx())) {
                list.set(i, user);
            }
        }
    }
    @Override
    public void userDataSetChanged() {
        adapter.notifyDataSetChanged();
    }


}