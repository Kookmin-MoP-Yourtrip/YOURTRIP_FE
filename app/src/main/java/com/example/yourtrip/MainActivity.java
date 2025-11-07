package com.example.yourtrip;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.yourtrip.feed.FeedFragment;
import com.example.yourtrip.home.HomeFragment;
import com.example.yourtrip.mypage.MypageFragment;
import com.example.yourtrip.mytrip.MytripFragment;
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
        topNav=findViewById(R.id.topNav);
        logoContainer = findViewById(R.id.logoContainer);

        // 로고 클릭 시 홈 이동
        logoContainer.setOnClickListener(v ->
                bottomNav.setSelectedItemId(R.id.nav_home)
        );

        // 처음 실행 시 홈화면 표시
        if (savedInstanceState == null) {
            switchFragment(new HomeFragment(), false);
            showLogoBar();
        }

        // 하단 네비게이션 바 클릭 리스너
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment target = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) target = new HomeFragment();
            else if (id == R.id.nav_trip) target = new MytripFragment();
            else if (id == R.id.nav_feed) target = new FeedFragment();
            else if (id == R.id.nav_my) target = new MypageFragment();

            if (target != null) {
                switchFragment(target, false);
                showLogoBar(); // 탭 전환 시 항상 로고바
                bottomNav.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });
        // 🔹 상단 뒤로가기 버튼 동작
        topNav.setNavigationOnClickListener(v -> onBackPressed());

        // ✅ 새 방식의 뒤로가기 처리
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackPress();
            }
        });
    }
    /** 공통 뒤로가기 처리 */
    private void handleBackPress() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(); // 프래그먼트 뒤로가기
        } else {
            finish(); // 앱 종료
        }

        // 뒤로가기로 복귀할 때 상단바·하단바 상태 복원
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            showLogoBar();
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
            showBackBar(); // 세부화면이면 뒤로가기바로 변경
            bottomNav.setVisibility(View.GONE); // 하단바 숨김
        } else {
            showLogoBar(); // 기본 탭 화면은 로고바
            bottomNav.setVisibility(View.VISIBLE); // 하단바 표시
        }
    }

    /** 🔹 기본 탭 화면: 로고바 */
    private void showLogoBar() {
        topNav.setNavigationIcon(null); // 뒤로가기 제거
        logoContainer.setVisibility(View.VISIBLE); // 로고 표시
    }

    /** 🔹 세부화면: 뒤로가기바 */
    private void showBackBar() {
        topNav.setNavigationIcon(R.drawable.top_bar_go_back);
        logoContainer.setVisibility(View.GONE); // 로고 숨김
    }
}