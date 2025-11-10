package com.example.yourtrip.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.R;

public class SignupCompleteActivity extends AppCompatActivity {

    private TextView tvSuccessMessage;
    private Button btnGoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_complete);

        // View 초기화
        tvSuccessMessage = findViewById(R.id.tvSuccessMessage);
        btnGoLogin = findViewById(R.id.btnGoLogin);

        // 로그인 화면으로 이동
        btnGoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupCompleteActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // 회원가입 플로우 완전히 종료
        });
    }
}
