package com.example.yourtrip.mytrip;

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
import com.example.yourtrip.model.MyCourseListItemResponse;

import java.util.ArrayList;
import java.util.List;

public class MyTripFragment extends Fragment {
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
}
