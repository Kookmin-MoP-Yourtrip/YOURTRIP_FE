//시연 코드 -> 시연 후 밑에 주석 코드로 변경하기
package com.example.yourtrip;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.content.Intent;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.yourtrip.feed.FeedFragment;
import com.example.yourtrip.home.HomeFragment;
import com.example.yourtrip.mypage.MypageFragment;
import com.example.yourtrip.mytrip.MyTripListFragment;
import com.example.yourtrip.model.MyCourseListItemResponse;  // MyCourseListItemResponse import 추가
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

        // 처음 실행 시 홈화면 표시
        if (savedInstanceState == null) {
            switchFragment(new HomeFragment(), false);
        }

        // 하단 네비게이션 바 클릭 리스너
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment target = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) target = new HomeFragment();
            else if (id == R.id.nav_trip) target = new MyTripListFragment(); // 기존 코드에서 MyTripListFragment 연결
            else if (id == R.id.nav_feed) target = new FeedFragment();
            else if (id == R.id.nav_my) target = new MypageFragment();

            if (target != null) {
                switchFragment(target, false);
                bottomNav.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });

        // ✅ 새 방식의 뒤로가기 처리
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackPress();
            }
        });

        // MainActivity에서 전달받은 데이터를 처리
        handleIntentData();
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

    // Intent로 전달된 여행 코스 데이터 처리
    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("newCourse")) {
            // MyCourseListItemResponse 객체를 Intent에서 가져오기
            MyCourseListItemResponse newCourse = (MyCourseListItemResponse) intent.getSerializableExtra("newCourse");

            // MyTripListFragment에 새로운 코스를 추가할 수 있도록 처리
            if (newCourse != null) {
                MyTripListFragment tripListFragment = new MyTripListFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("newCourse", newCourse); // 코스 데이터를 전달
                tripListFragment.setArguments(bundle);

                // MyTripListFragment를 동적으로 추가
                switchFragment(tripListFragment, true);
            }
        }
    }
}


//package com.example.yourtrip;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.LinearLayout;
//
//import androidx.activity.OnBackPressedCallback;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//
//import com.example.yourtrip.feed.FeedFragment;
//import com.example.yourtrip.home.HomeFragment;
//import com.example.yourtrip.mypage.MypageFragment;
//import com.example.yourtrip.mytrip.MyTripListFragment;
//import com.google.android.material.appbar.MaterialToolbar;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//
//
//public class MainActivity extends AppCompatActivity {
//    private BottomNavigationView bottomNav;
//    private MaterialToolbar topNav;
//    private LinearLayout logoContainer;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        bottomNav = findViewById(R.id.bottomNav);
//
//        // 처음 실행 시 홈화면 표시
//        if (savedInstanceState == null) {
//            switchFragment(new HomeFragment(), false);
//        }
//
//        // 하단 네비게이션 바 클릭 리스너
//        bottomNav.setOnItemSelectedListener(item -> {
//            Fragment target = null;
//            int id = item.getItemId();
//
//            if (id == R.id.nav_home) target = new HomeFragment();
//            else if (id == R.id.nav_trip) target = new MyTripListFragment();
//            else if (id == R.id.nav_feed) target = new FeedFragment();
//            else if (id == R.id.nav_my) target = new MypageFragment();
//
//            if (target != null) {
//                switchFragment(target, false);
//                bottomNav.setVisibility(View.VISIBLE);
//                return true;
//            }
//            return false;
//        });
//
//        // ✅ 새 방식의 뒤로가기 처리
//        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                handleBackPress();
//            }
//        });
//    }
//    /** 공통 뒤로가기 처리 */
//    private void handleBackPress() {
//        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            getSupportFragmentManager().popBackStack(); // 프래그먼트 뒤로가기
//        } else {
//            finish(); // 앱 종료
//        }
//
//        // 뒤로가기로 복귀할 때 상단바·하단바 상태 복원
//        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//            bottomNav.setVisibility(View.VISIBLE);
//        }
//    }
//    public void switchFragment(@NonNull Fragment fragment, boolean isSubPage) {
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragmentContainer, fragment)
//                .addToBackStack(isSubPage ? fragment.getClass().getSimpleName() : null)
//                .commit();
//
//        if (isSubPage) {
//            bottomNav.setVisibility(View.GONE); // 하단바 숨김
//        } else {
//            bottomNav.setVisibility(View.VISIBLE); // 하단바 표시
//        }
//    }
//
//}