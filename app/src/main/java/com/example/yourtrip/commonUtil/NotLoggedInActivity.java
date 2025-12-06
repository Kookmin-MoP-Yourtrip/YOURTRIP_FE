package com.example.yourtrip.commonUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;
import com.example.yourtrip.auth.LoginActivity;

public class NotLoggedInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_logged_in);

        ImageView btnBack = findViewById(R.id.btn_back);
        Button btnGoLogin = findViewById(R.id.btn_go_login);

        // 뒤로가기
        btnBack.setOnClickListener(v -> finish());

        // 로그인 하러가기 → LoginActivity로 이동
        btnGoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
