package com.inik.neonadeuri;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.inik.neonadeuri.videoChat.RtcActivity;

public class PopupActivity extends Activity {

    TextView txtText;
    String target;
    PowerManager powerManager;

    PowerManager.WakeLock wakeLock;
     Context context;



    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED


                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD


                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON


                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.popup_activity);

        context = getApplicationContext();







        //UI 객체생성
        txtText = (TextView)findViewById(R.id.callName);

        //데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
         target = intent.getStringExtra("caller");
        txtText.setText(data);
    }


    public void mOnClose(View v){

        Intent intent = new Intent(getApplicationContext(), RtcActivity.class);
        intent.putExtra("callerX", target);
        startActivity(intent);


        finish();
    }
    public void moveRtc(View v){
        System.out.println("진입");
        System.out.println("진입 타겟 : " + target );

        Intent intent = new Intent(getApplicationContext(), RtcActivity.class);
        intent.putExtra("caller", target);
        startActivity(intent);


        finish();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
