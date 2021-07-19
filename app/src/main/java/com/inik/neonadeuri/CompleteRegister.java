package com.inik.neonadeuri;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CompleteRegister extends AppCompatActivity {

    TextView textView;
    Button loginBtn;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CompleteRegister.this, LoginActivity.class);
        startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_register);

       textView = findViewById(R.id.textView);
       loginBtn = findViewById(R.id.loginBtn);

       loginBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(CompleteRegister.this, LoginActivity.class);
               startActivity(intent);
           }
       });
    }
}
