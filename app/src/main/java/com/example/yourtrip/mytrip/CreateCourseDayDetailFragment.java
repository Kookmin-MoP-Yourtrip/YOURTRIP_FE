package com.example.yourtrip.mytrip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import com.example.yourtrip.mytrip.model.LocationItem;

import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.MyCourseDetailResponse;
import com.example.yourtrip.mytrip.model.PlaceAddResponse;

import java.io.Serializable;
import java.util.List;

public class CreateCourseDayDetailFragment extends Fragment {

    private static final String TAG = "CourseDayDetailFragment";
    private static final String ARG_COURSE_ID = "courseId";
    private static final String ARG_DAY_SCHEDULES = "daySchedules";
    
    // 데이터
    private long courseId;
    private List<MyCourseDetailResponse.DaySchedule> daySchedules;

    // UI 컴포넌트
    private RecyclerView recyclerViewTripDays;
    private DayAdapter dayAdapter;
    private RecyclerView recyclerLocationList;
    private LocationAdapter locationAdapter;
    // 🟡 1. AddLocationActivity의 결과를 받을 Launcher 변수 선언
    private ActivityResultLauncher<Intent> addLocationLauncher;
    
    //프래그먼트는 반드시 비어있는 기본 생성자를 가져야 함
    public CreateCourseDayDetailFragment() {
        // Required empty public constructor
    }

    /**
     * 프래그먼트 인스턴스를 생성하고 초기 데이터를 전달하는 정적 팩토리 메서드(Factory Method)입니다.
     * 이 방법을 사용하면 데이터 전달 과정을 캡슐화하고, Activity와의 결합도를 낮춰 오류를 줄일 수 있습니다.
     * @param courseId 코스 ID
     * @param daySchedules 일차 정보 리스트
     * @return 데이터가 포함된 새로운 CreateCourseDayDetailFragment 인스턴스
     */
    public static CreateCourseDayDetailFragment newInstance(long courseId, List<MyCourseDetailResponse.DaySchedule> daySchedules) {
        CreateCourseDayDetailFragment fragment = new CreateCourseDayDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_COURSE_ID, courseId);
        args.putSerializable(ARG_DAY_SCHEDULES, (Serializable) daySchedules);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            courseId = getArguments().getLong(ARG_COURSE_ID, -1L);
            // 형변환 시 ClassCastException을 방지하기 위해 try-catch 사용 가능
            try {
                daySchedules = (List<MyCourseDetailResponse.DaySchedule>) getArguments().getSerializable(ARG_DAY_SCHEDULES);
            } catch (ClassCastException e) {
                Log.e(TAG, "DaySchedules 리스트를 변환하는 데 실패했습니다.", e);
                daySchedules = null;
            }
        }

        // 데이터 유효성 검사 및 로그 출력
        if (courseId != -1L && daySchedules != null && !daySchedules.isEmpty()) {
            Log.d(TAG, "프래그먼트 데이터 로드 성공. Course ID: " + courseId + ", 총 일차: " + daySchedules.size());
        } else {
            Log.e(TAG, "프래그먼트 데이터가 유효하지 않습니다.");
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "일정 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        // 🟡 2. Launcher 초기화: 결과를 어떻게 처리할지 미리 정의합니다.
        addLocationLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // AddLocationActivity에서 돌아왔을 때 이 코드가 실행됩니다.

                    // 결과가 성공(OK)이고, 데이터가 비어있지 않은지 확인
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        // "newPlace" 라는 키로 전달된 응답 객체(PlaceAddResponse)를 꺼냅니다.
                        PlaceAddResponse newPlaceResponse = (PlaceAddResponse) result.getData().getSerializableExtra("newPlace");

                        if (newPlaceResponse != null) {
                            Log.d(TAG, "새로운 장소 받음: " + newPlaceResponse.getPlaceName());

                            // 3. 받은 API 응답(PlaceAddResponse)을 RecyclerView 아이템(LocationItem)으로 변환합니다.
                            LocationItem newItem = new LocationItem(
                                    newPlaceResponse.getPlaceId(),
                                    newPlaceResponse.getPlaceName(),
                                    newPlaceResponse.getPlaceLocation(),
                                    newPlaceResponse.getMemo(),
                                    newPlaceResponse.getStartTime()
                            );

                            // 4. 어댑터에 새로운 아이템 추가를 요청하고, 화면을 갱신합니다.
                            if (locationAdapter != null) {
                                locationAdapter.addItem(newItem);
                            }
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 🔵 요청하신 로직: fragment_trip_create_detail.xml 레이아웃을 인플레이트
        View view = inflater.inflate(R.layout.fragment_trip_create_detail, container, false);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //--- 상단 일차 탭 RecyclerView 설정 ---
        // 🔵 1. RecyclerView 초기화
        recyclerViewTripDays = view.findViewById(R.id.recyclerViewTripDays);

        // 🔵 2. 데이터 유효성 검사 후 어댑터 설정
        if (daySchedules != null && !daySchedules.isEmpty()) {
            setupDayRecyclerView();
        } else {
            Log.w(TAG, "daySchedules가 비어있어 RecyclerView를 설정할 수 없습니다.");
        }

        //--- 하단 장소 목록 RecyclerView 설정 ---

        recyclerLocationList = view.findViewById(R.id.recyclerLocationList);
        setupLocationRecyclerView(); // 새로운 메서드 호출
    }

    //상단 일차 탭 RecyclerView 설정 메서드
    private void setupDayRecyclerView() {
        // 🟡 수정: DayAdapter를 생성할 때, 클릭 리스너를 함께 전달합니다.
        dayAdapter = new DayAdapter(daySchedules, (position, dayId) -> {
            // 이 람다(lambda) 표현식은 OnDayTabClickListener의 onDayTabClick 메서드를 구현한 것입니다.
            // 탭이 클릭될 때마다 이 안의 코드가 실행됩니다.

            Log.d(TAG, (position + 1) + "일차 탭 클릭됨. 새로운 dayId: " + dayId);

            // 1. LocationAdapter에 새로운 dayId를 알려줍니다.
            if (locationAdapter != null) {
                locationAdapter.updateDayId(dayId);
            }

            // 2. TODO: 여기에서 새로운 dayId로 실제 장소 목록을 불러오는 API를 호출해야 합니다.
            // fetchPlacesForDay(dayId);
        });

        // 🔵 4. 레이아웃 매니저 설정 (가로 스크롤)
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTripDays.setLayoutManager(layoutManager);

        // 🔵 5. 어댑터 연결
        recyclerViewTripDays.setAdapter(dayAdapter);
    }

    //하단 장소 목록 RecyclerView 설정 메서드
    private void setupLocationRecyclerView() {
        // --- 1. 화면에 보여줄 초기 데이터 생성 ---
        List<Object> initialList = new ArrayList<>();
        initialList.add("ADD_BUTTON");

        // --- 2. 어댑터 생성 및 RecyclerView에 연결 ---
        // 🟡 수정: 초기 dayId 값을 설정하여 어댑터를 생성합니다.
        // daySchedules 리스트가 비어있지 않다는 가정 하에, 첫 번째(1일차) dayId를 가져옵니다.
        long initialDayId = (daySchedules != null && !daySchedules.isEmpty()) ? daySchedules.get(0).getDayId() : -1L;

        // courseId와 '초기' dayId를 전달하여 LocationAdapter를 생성합니다.
        locationAdapter = new LocationAdapter(initialList, courseId, initialDayId,this);

        recyclerLocationList.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerLocationList.setAdapter(locationAdapter);
        
        
        
        
        // --- 화면 확인용 임시 데이터 생성 ---
//        List<Object> tempList = new ArrayList<>();
//        // 시나리오 1: 장소가 1개 추가된 상태를 시뮬레이션
//        LocationItem sampleLocation = new LocationItem("성심당 본점", "대전광역시 중구 은행동 145-1");
//        tempList.add(sampleLocation);
//
//        // 시나리오 2: 장소가 2개 추가된 상태
//        // LocationItem sampleLocation2 = new LocationItem("카이스트", "대전 유성구 대학로 291");
//        // tempList.add(sampleLocation2);
//
//        // 리스트의 맨 마지막에는 항상 '추가 버튼'을 위한 데이터를 넣어줍니다.
//        // "ADD_BUTTON" 문자열은 어떤 값이든 상관없지만, LocationItem 객체가 아니어야 합니다.
//        tempList.add("ADD_BUTTON");
//
//
//        // --- 2. 어댑터 생성 및 RecyclerView에 연결 ---
//        // 생성한 임시 데이터 리스트로 어댑터를 만듭니다.
//        locationAdapter = new LocationAdapter(tempList);
//
//        // RecyclerView에 LayoutManager와 Adapter를 설정합니다.
//        recyclerLocationList.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerLocationList.setAdapter(locationAdapter);
    }


    // Adapter로부터 Activity 실행 요청 받아서 화면 전환 처리
    public void launchAddLocationActivity(long courseId, long dayId) {
        Intent intent = new Intent(requireActivity(), AddLocationActivity.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("dayId", dayId);

        // ActivityResultLauncher를 사용하여 결과를 받음
//        requireActivity().startActivity(intent);
        // 🟡 5. 'startActivity' 대신 'launch'를 사용하여 Activity를 실행하고 결과를 기다립니다.
        addLocationLauncher.launch(intent);
    }


}


