package com.example.yourtrip.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.model.HomePopularCourseItem;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvPopular;
    private PopularCourseAdapter popularAdapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_home_main, container, false);

        initViews(view);       // 뷰 초기화
        setupPopularRecycler(); // 인기 코스 RecyclerView 셋업
        loadDummyPopularData(); // 더미 데이터 주입

        return view;
    }

    // ----------------------------
    // 1. 뷰 초기화
    // ----------------------------
    private void initViews(View view) {
        rvPopular = view.findViewById(R.id.rv_popular_course);
    }

    // ----------------------------
    // 2. 인기 코스 RecyclerView 설정
    // ----------------------------
    private void setupPopularRecycler() {
        rvPopular.setLayoutManager(new LinearLayoutManager(getContext()));

        popularAdapter = new PopularCourseAdapter(new ArrayList<>());
        rvPopular.setAdapter(popularAdapter);

        // 클릭 이벤트 예시
        popularAdapter.setOnItemClickListener(item -> {
            // TODO: 상세 페이지 이동
            // Toast.makeText(getContext(), item.title, Toast.LENGTH_SHORT).show();
        });
    }

    // ----------------------------
    // 3. 더미 데이터 주입
    // ----------------------------
    private void loadDummyPopularData() {
        List<HomePopularCourseItem> dummy = new ArrayList<>();

        dummy.add(new HomePopularCourseItem(
                "대전 맛도리 빵집 투어",
                "대전 유성구, 중구",
                R.drawable.dummy1,
                List.of("자차", "쇼핑")
        ));
        dummy.add(new HomePopularCourseItem(
                "춘천 감성 카페 투어",
                "강원 춘천시",
                R.drawable.dummy3,
                List.of("감성", "하루")
        ));

        dummy.add(new HomePopularCourseItem(
                "성수 카페 투어",
                "서울 성동구",
                R.drawable.dummy2,
                List.of("혼자", "쇼핑", "프리미엄")

        ));

        dummy.add(new HomePopularCourseItem(
                "여수 가족 여행 루트",
                "전남 여수시",
                R.drawable.dummy4,
                List.of("가족", "힐링")
        ));

        dummy.add(new HomePopularCourseItem(
                "부산 야경 드라이브 코스",
                "부산 해운대구",
                R.drawable.dummy5,
                List.of("자차", "가성비")
        ));

        dummy.add(new HomePopularCourseItem(
                "부산 야경 드라이브 코스",
                "부산 해운대구",
                R.drawable.dummy5,
                List.of("자차", "가성비")
        ));

        // ⭐ 여기서 5개로 제한해서 Adapter에 전달
        List<HomePopularCourseItem> topFive = dummy.size() > 5
                ? dummy.subList(0, 5)
                : dummy;

        // Adapter에 데이터 전달
        popularAdapter.updateList(topFive);
    }
}
