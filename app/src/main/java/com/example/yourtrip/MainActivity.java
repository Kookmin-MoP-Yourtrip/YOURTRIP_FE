package com.example.yourtrip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.yourtrip.commonUtil.NotLoggedInActivity;
import com.example.yourtrip.feed.FeedFragment;
import com.example.yourtrip.home.HomeFragment;
import com.example.yourtrip.mypage.MypageFragment;
import com.example.yourtrip.mytrip.list.MyTripListFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private MaterialToolbar topNav;
    private LinearLayout logoContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);

        //앱이 처음 실행될 때 어떤 프래그먼트를 보여줄지 여기서 결정
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            // "navigateTo" 정보가 있고, 로그인이 되어 있으며, "MyTripListFragment"로 이동하라는 요청이 있을 경우
            if (intent != null && "MyTripListFragment".equals(intent.getStringExtra("navigateTo")) && isLoggedIn()) {
                switchFragment(new MyTripListFragment(), false);
                bottomNav.setSelectedItemId(R.id.nav_trip);
                // 이미 처리한 Intent 정보는 제거
                intent.removeExtra("navigateTo");
            } else {
                // 일반적인 첫 실행의 경우, '홈' 프래그먼트
                switchFragment(new HomeFragment(), false);
            }
        }

        // 하단 네비게이션 바 클릭 리스너
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment target = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) target = new HomeFragment();
            else if (id == R.id.nav_trip) {
                if (!isLoggedIn()) {
                    Intent intent = new Intent(MainActivity.this, NotLoggedInActivity.class);
                    startActivity(intent);
                    return false;
                }
                target = new MyTripListFragment();
            }
            else if (id == R.id.nav_feed) target = new FeedFragment();
            else if (id == R.id.nav_my) {
                if (!isLoggedIn()) {
                    Intent intent = new Intent(MainActivity.this, NotLoggedInActivity.class);
                    startActivity(intent);
                    return false;
                }
                target = new MypageFragment();
            }

            if (target != null) {
                switchFragment(target, false);
                bottomNav.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });

        // 뒤로가기 처리
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackPress();
            }
        });
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);
        return token != null;
    }

    /** 공통 뒤로가기 처리 */
    private void handleBackPress() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(); // 프래그먼트 뒤로가기
        } else {
            finish(); // 앱 종료
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }
    public void switchFragment(@NonNull Fragment fragment, boolean isSubPage) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(isSubPage ? fragment.getClass().getSimpleName() : null)
                .commit();

        if (isSubPage) {
            bottomNav.setVisibility(View.GONE); // 하단바 숨김
        } else {
            bottomNav.setVisibility(View.VISIBLE); // 하단바 표시
        }
    }
}
