package com.example.yourtrip;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
//스플래시
import androidx.core.splashscreen.SplashScreen;

import com.example.yourtrip.auth.LoginActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ✅ 시스템 스플래시 적용
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        // ✅ 로그인 상태 확인 (SharedPreferences 등)
        boolean isLoggedIn = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getBoolean("isLoggedIn", false);

        // ✅ 분기 이동
        if (isLoggedIn) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

        // ✅ LauncherActivity 종료 (뒤로가기시 안돌아오게)
        finish();
    }
}
