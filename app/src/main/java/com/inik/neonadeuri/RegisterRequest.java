package com.inik.neonadeuri;


import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.request.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    //서버 url 설정(php파일 연동)
    final static  private String URL="http://218.39.138.57/and2/Register2.php";
    private Map<String,String>map;

    //emailId, pass, name, phoneNum, birth, gender, checkM;
    public RegisterRequest(String emailId, String pass, String name,String nickname , String phoneNum, String photoIdx,  String webSite, String introduction, Response.Listener<String>listener){
        super(Method.POST,URL,listener,null);//위 url에 post방식으로 값을 전송

        map=new HashMap<>();
        map.put("email",emailId);
        map.put("pass", pass);
        map.put("name", name);
        map.put("nickname", nickname);
        map.put("phoneNum",phoneNum);
        map.put("photoIdx",photoIdx);



        map.put("webSite",webSite);
        map.put("introduction",introduction);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}