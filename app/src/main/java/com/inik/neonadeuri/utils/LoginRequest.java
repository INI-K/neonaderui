package com.inik.neonadeuri.utils;


import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.request.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    //서버 url 설정(php파일 연동)
    final static  private String URL="http://218.39.138.57/and2/Login2.php";
    private Map<String, String> map;

    public LoginRequest(String userEmail, String userPass, Response.Listener<String>listener){
        super(Method.POST,URL,listener,null);

        map=new HashMap<>();
        map.put("email",userEmail);
        map.put("pass",userPass);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}