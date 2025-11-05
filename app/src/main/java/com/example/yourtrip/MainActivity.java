package com.example.yourtrip;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.yourtrip.ui.FeedFragment;
import com.example.yourtrip.ui.HomeFragment;
import com.example.yourtrip.ui.MyFragment;
import com.example.yourtrip.ui.TripFragment;
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
            else if (id == R.id.nav_trip) target = new TripFragment();
            else if (id == R.id.nav_feed) target = new FeedFragment();
            else if (id == R.id.nav_my) target = new MyFragment();

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