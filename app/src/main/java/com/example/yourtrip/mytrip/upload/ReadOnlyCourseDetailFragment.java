package com.example.yourtrip.mytrip.upload;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.UploadCourseResponse;
import com.example.yourtrip.mytrip.upload.ReadOnlyDayAdapter;
import com.example.yourtrip.mytrip.upload.ReadOnlyLocationAdapter;


import java.io.Serializable;
import java.util.List;



public class ReadOnlyCourseDetailFragment extends Fragment  {

    private static final String TAG = "ReadOnlyFragment";
    private static final String ARG_DAY_SCHEDULES = "daySchedules";

    // 데이터
    private List<UploadCourseResponse.DaySchedule> daySchedules;

    // UI 컴포넌트
    private RecyclerView rvReadOnlyDays;
    private ReadOnlyDayAdapter dayAdapter;
    private RecyclerView rvReadOnlyLocations;
    private ReadOnlyLocationAdapter locationAdapter; // 읽기 전용 어댑터로 변경 필요


    //프래그먼트는 반드시 비어있는 기본 생성자를 가져야 함
    public ReadOnlyCourseDetailFragment() {
        // Required empty public constructor
    }

    /**
     * [수정] 읽기 전용 프래그먼트 인스턴스를 생성하는 정적 팩토리 메서드
     *
     * @param daySchedules 서버에서 받은 '읽기 전용' 일정 데이터 리스트
     * @return 데이터가 포함된 새로운 ReadOnlyCourseDetailFragment 인스턴스
     */
    public static ReadOnlyCourseDetailFragment newInstance(List<UploadCourseResponse.DaySchedule> daySchedules) {
        ReadOnlyCourseDetailFragment fragment = new ReadOnlyCourseDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DAY_SCHEDULES, (Serializable) daySchedules);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 이전 액티비티/프래그먼트로부터 전달받은 데이터 처리
        if (getArguments() != null) {
            // [주의] ClassCastException 방지를 위해 안전하게 형변환
            try {
                daySchedules = (List<UploadCourseResponse.DaySchedule>) getArguments().getSerializable(ARG_DAY_SCHEDULES);
            } catch (ClassCastException e) {
                daySchedules = null;
                Log.e(TAG, "daySchedules 데이터를 변환하는 데 실패했습니다.", e);
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_readonly_course_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // '읽기 전용' 레이아웃의 ID로 UI 컴포넌트를 찾음
        rvReadOnlyDays = view.findViewById(R.id.rv_recyclerViewTripDays);
        rvReadOnlyLocations = view.findViewById(R.id.rv_recyclerLocationList);

        // RecyclerView 설정
        setupDayRecyclerView();
        setupLocationRecyclerView();

        // 화면이 처음 보일 때, 첫 번째 일차의 장소 목록을 표시
        if (daySchedules != null && !daySchedules.isEmpty()) {
            // 첫 번째 탭의 장소들을 표시
            updateLocationList(daySchedules.get(0).getPlaces());
        }
    }


    /**
     * 상단 일차 탭 RecyclerView를 설정하는 메서드.
     */
    private void setupDayRecyclerView() {
        if (daySchedules == null || daySchedules.isEmpty()) return;

        dayAdapter = new ReadOnlyDayAdapter(daySchedules, position -> {
            if (daySchedules.get(position) != null) {
                updateLocationList(daySchedules.get(position).getPlaces());
            }
        });
        rvReadOnlyDays.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvReadOnlyDays.setAdapter(dayAdapter);
    }

    /**
     * 하단 장소 목록 RecyclerView를 설정하는 메서드.
     */
    private void setupLocationRecyclerView() {
        // 어댑터 내부에서는 R.layout.item_readonly_location_card를 사용함
        // '장소 추가' 버튼 아이템을 리스트에 추가하지 않음

        locationAdapter = new ReadOnlyLocationAdapter();
        rvReadOnlyLocations.setLayoutManager(new LinearLayoutManager(getContext()));
        rvReadOnlyLocations.setAdapter(locationAdapter);
    }

    /**
     * 장소 목록 RecyclerView를 새로운 데이터로 업데이트하는 메서드
     */
    private void updateLocationList(List<UploadCourseResponse.Place> places) {
        if (locationAdapter != null && places != null) {
            // TODO: 어댑터의 updateItems 메서드가 UploadedCourseDetailResponse.Place 타입을 받도록 수정 필요
            locationAdapter.updatePlaces(places);
        }
    }
}