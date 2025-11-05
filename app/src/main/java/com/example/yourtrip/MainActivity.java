package com.example.yourtrip;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.yourtrip.feed.FeedFragment;
import com.example.yourtrip.home.HomeFragment;
import com.example.yourtrip.mypage.MypageFragment;
import com.example.yourtrip.mytrip.MytripFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);

        // 처음 실행 시 홈화면 표시
        if (savedInstanceState == null) {
            switchFragment(new HomeFragment());
        }

        // 네비게이션 바 클릭 리스너
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment target = null;
            int id = item.getItemId();
            if (id == R.id.nav_home) target = new HomeFragment();
            else if (id == R.id.nav_trip) target = new MytripFragment();
            else if (id == R.id.nav_feed) target = new FeedFragment();
            else if (id == R.id.nav_my) target = new MypageFragment();

            if (target != null) {
                switchFragment(target);
                return true;
            }
            return false;
        });
    }

    private void switchFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}