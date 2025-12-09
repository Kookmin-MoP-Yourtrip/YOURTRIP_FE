package com.example.yourtrip;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.SharedPreferences;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yourtrip.auth.LoginActivity;


public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
//
//        View root = findViewById(android.R.id.content);
//        root.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_splash));
//
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            // 로그인 상태 확인 -> 화면 전환
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            String accessToken = prefs.getString("access_token", null);

            Intent intent;
            if (accessToken != null && !accessToken.isEmpty()) {
                intent = new Intent(this, MainActivity.class);
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
            startActivity(intent);
            //애니메이션 - fade 효과
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            
                finish();
        }, 2000); // 2초
    }

}
