//MyTripFragment -> MyTripListFragment 로 이름 변경
package com.example.yourtrip.mytrip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.model.MyCourseListItemResponse;

import java.util.ArrayList;
import java.util.List;


public class MyTripListFragment extends Fragment {
    private ImageView btnAddTrip;         // + 버튼
    private LinearLayout fabMenuLayout;   // 메뉴 레이아웃
    private boolean isMenuOpen = false;   // 토글 상태 저장
    private View dimLayer;

    // 새로 추가: 메뉴 안의 버튼들
    private LinearLayout btnAIMake;       // AI로 만들기
    private LinearLayout btnManualMake;   // 직접 만들기


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.trip_recycler);

        // 1) 더미데이터 생성
        List<MyCourseListItemResponse> dummyList = getDummyCourses();

        // 2) 어댑터 생성 및 설정
        TripAdapter adapter = new TripAdapter(dummyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        // Floating Action Button + 메뉴 초기화
        btnAddTrip = view.findViewById(R.id.btn_add_trip);
        fabMenuLayout = view.findViewById(R.id.fab_menu);
        dimLayer = view.findViewById(R.id.fab_dim);

        // fab 누르기 전 초기상태:
        dimLayer.setVisibility(view.GONE);
        fabMenuLayout.setVisibility(View.GONE);

        btnAddTrip.setOnClickListener(v -> {
            if (isMenuOpen) {
                closeMenu();
            } else {
                openMenu();
            }
        });
        dimLayer.setOnClickListener(v -> closeMenu());


        //  추가: 메뉴 안의 버튼 연결
        btnAIMake = view.findViewById(R.id.btn_upload);          // AI로 만들기
        btnManualMake = view.findViewById(R.id.btn_manual_make); // 직접 만들기


        //  추가: "AI로 만들기" 클릭 이벤트
        btnAIMake.setOnClickListener(v -> {
            Toast.makeText(getContext(), "AI 제작 기능 준비 중입니다.", Toast.LENGTH_SHORT).show();
        });

        //  추가: "직접 만들기" 클릭 이벤트 → CreateCourseBasicFragment 이동
        btnManualMake.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), CreateCourseBasicActivity.class);
            startActivity(intent);
            closeMenu();
        });
    }


    // ---------------------------
    // 더미데이터 생성 함수
    // ---------------------------
    private List<MyCourseListItemResponse> getDummyCourses() {
        List<MyCourseListItemResponse> list = new ArrayList<>();

        list.add(new MyCourseListItemResponse("대전 빵집 투어", "대전 중구, 서구", "2025-11-09", "2025-11-10", 3));
        list.add(new MyCourseListItemResponse("강릉 힐링 바다 여행", "강원도 강릉", "2025-08-14", "2025-08-16", 2));
        list.add(new MyCourseListItemResponse("부산 야경 맛집 코스", "부산 해운대구", "2025-05-01", "2025-05-02", 4));
        list.add(new MyCourseListItemResponse("제주도 감성 카페 일주", "제주 제주시", "2025-03-12", "2025-03-14", 1));
        list.add(new MyCourseListItemResponse("서울 종로 하루 산책", "서울 종로구", "2025-10-03", "2025-10-03", 2));
        list.add(new MyCourseListItemResponse("전주 한옥마을 먹방 여행", "전북 전주", "2025-09-20", "2025-09-21", 3));
        list.add(new MyCourseListItemResponse("속초 해변 드라이브", "강원도 속초", "2025-07-11", "2025-07-12", 2));
        list.add(new MyCourseListItemResponse("울산 고래문화마을 하루 코스", "울산 남구", "2025-02-22", "2025-02-22", 4));
        list.add(new MyCourseListItemResponse("수원 화성 당일 여행", "경기도 수원", "2025-04-18", "2025-04-18", 2));
        list.add(new MyCourseListItemResponse("경주 야간 사적지 투어", "경북 경주", "2025-09-08", "2025-09-09", 5));

        return list;
    }

    // ---------------------------
    // 메뉴 열기
    // ---------------------------
    private void openMenu() {
        // dim 나타남
        fabMenuLayout.setVisibility(View.VISIBLE);
        dimLayer.setAlpha(0f);
        dimLayer.animate().alpha(1f).setDuration(150).start();

        // menu 나타남
        dimLayer.setVisibility(View.VISIBLE);
        fabMenuLayout.setAlpha(0f);
        fabMenuLayout.setTranslationY(40f);

        fabMenuLayout.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(150)
                .start();

        // + → X 아이콘
        btnAddTrip.setImageResource(R.drawable.fab_close_menu);
        isMenuOpen = true;
    }

    // ---------------------------
    // 메뉴 닫기
    // ---------------------------
    private void closeMenu() {
        // dim 서서히 사라짐
        dimLayer.animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction(() -> dimLayer.setVisibility(View.GONE))
                .start();

        // 메뉴 사라짐
        fabMenuLayout.animate()
                .alpha(0f)
                .translationY(40f)
                .setDuration(150)
                .withEndAction(() -> fabMenuLayout.setVisibility(View.GONE))
                .start();

        // X → + 아이콘 복귀
        btnAddTrip.setImageResource(R.drawable.fab_add_course);
        isMenuOpen = false;
    }
}
