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
import com.example.yourtrip.mytrip.model.DayPlacesResponse;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private ApiService apiService;
    
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
                // 🟡 디버깅 로그 1: Fragment가 처음 데이터를 받았을 때
                if (daySchedules != null) {
                    Log.d("DEBUG_DAY_ID", "[Fragment onCreate] 프래그먼트가 처음 받은 daySchedules:");
                    for (MyCourseDetailResponse.DaySchedule schedule : daySchedules) {
                        Log.d("DEBUG_DAY_ID", "  >> Day: " + schedule.getDay() + ", Day ID: " + schedule.getDayId());
                    }
                }
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

        // ApiService 초기화
        apiService = RetrofitClient.getAuthService(requireContext());

        // Launcher 초기화: 결과를 어떻게 처리할지 미리 정의
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

                            if (locationAdapter != null) {
                                // 1. 장소를 추가했던 바로 그 dayId를 어댑터로부터 가져옵니다.
                                long currentDayId = locationAdapter.getCurrentDayId();
                                if (currentDayId != -1L) {
                                    Log.d(TAG, "장소 추가 성공 후, dayId " + currentDayId + "의 목록을 새로고침합니다.");
                                    // 2. 해당 dayId의 최신 목록을 서버에서 다시 불러옵니다.
                                    fetchPlacesForDay(currentDayId);
                                }
                            }

//                            long currentDayId = locationAdapter.getCurrentDayId();
//
//                            // 3. 받은 API 응답(PlaceAddResponse)을 RecyclerView 아이템(LocationItem)으로 변환합니다.
//                            LocationItem newItem = new LocationItem(
//                                    newPlaceResponse.getPlaceId(),
//                                    newPlaceResponse.getPlaceName(),
//                                    newPlaceResponse.getPlaceLocation(),
//                                    newPlaceResponse.getMemo(),
//                                    newPlaceResponse.getStartTime()
//                            );
//
//                            // 4. 어댑터에 새로운 아이템 추가를 요청하고, 화면을 갱신
//                            if (locationAdapter != null) {
//                                locationAdapter.addItem(newItem);
//                            }
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment_trip_create_detail.xml 레이아웃을 인플레이트
        View view = inflater.inflate(R.layout.fragment_trip_create_detail, container, false);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //--- 상단 일차 탭 RecyclerView 설정 ---
        // RecyclerView 초기화
        recyclerViewTripDays = view.findViewById(R.id.recyclerViewTripDays);

        // 데이터 유효성 검사 후 어댑터 설정
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
        // 메서드 시작 시 한 번 더 확인하여 안정성 강화
        if (daySchedules == null || daySchedules.isEmpty()) {
            Log.w(TAG, "daySchedules가 없어 DayAdapter를 설정할 수 없습니다.");
            return;
        }

        // DayAdapter를 생성할 때, 클릭 리스너를 함께 전달
        dayAdapter = new DayAdapter(daySchedules, (position, dayId) -> {
            // 이 람다(lambda) 표현식은 OnDayTabClickListener의 onDayTabClick 메서드를 구현한 것
            // 탭이 클릭될 때마다 이 안의 코드가 실행
            // 🟡 디버깅 로그 2: 상단 탭이 클릭되었을 때
            Log.d("DEBUG_DAY_ID", "[DayAdapter Click] " + (position + 1) + "일차 탭 클릭됨. 전달된 dayId: " + dayId);

            // 1. LocationAdapter에 새로운 dayId를 알려줌
            if (locationAdapter != null) {
                locationAdapter.updateDayId(dayId);
            }

            // 탭 클릭 시 API 호출 활성화
             fetchPlacesForDay(dayId);
        });
        //레이아웃 매니저 설정 (가로 스크롤) , 어뎁터 연결
        recyclerViewTripDays.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTripDays.setAdapter(dayAdapter);

    }

    //하단 장소 목록 RecyclerView 설정 메서드
//    private void setupLocationRecyclerView() {
//        // --- 1. 화면에 보여줄 초기 데이터 생성 ---
//        List<Object> initialList = new ArrayList<>();
//        initialList.add("ADD_BUTTON");
//
//        // --- 2. 어댑터 생성 및 RecyclerView에 연결 ---
//        // 🟡 수정: 초기 dayId 값을 설정하여 어댑터를 생성합니다.
//        // daySchedules 리스트가 비어있지 않다는 가정 하에, 첫 번째(1일차) dayId를 가져옵니다.
//        long initialDayId = (daySchedules != null && !daySchedules.isEmpty()) ? daySchedules.get(0).getDayId() : -1L;
//
//        // courseId와 '초기' dayId를 전달하여 LocationAdapter를 생성합니다.
//        locationAdapter = new LocationAdapter(initialList, courseId, initialDayId,this);
//
//        recyclerLocationList.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerLocationList.setAdapter(locationAdapter);
//    }
    private void setupLocationRecyclerView() {
        // --- 1. 화면에 보여줄 초기 데이터 생성 ---
        List<Object> initialList = new ArrayList<>();
        // 초기 상태에서도 '추가 버튼'이 보이도록 데이터를 추가
        initialList.add("ADD_BUTTON");

        // --- 2. 어댑터 생성 및 RecyclerView에 연결 ---
        long initialDayId = (daySchedules != null && !daySchedules.isEmpty()) ? daySchedules.get(0).getDayId() : -1L;
        locationAdapter = new LocationAdapter(initialList, courseId, initialDayId, this);

        recyclerLocationList.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerLocationList.setAdapter(locationAdapter);

        // -화면이 처음 보일 때, 첫 번째 일차의 장소 목록을 가져옴
        if (initialDayId != -1L) {
            fetchPlacesForDay(initialDayId);
        }
    }


    // 일차별 장소 목록 호출 메서드
    private void fetchPlacesForDay(long dayId) {
        // courseId 유효성 검사
        if (courseId == -1L) {
            Log.e(TAG, "courseId가 유효하지 않아 API를 호출할 수 없습니다.");
            return;
        }

        Log.d(TAG, "장소 목록 조회 API 호출 시작. dayId: " + dayId);
        apiService.getPlacesForDay(courseId, dayId).enqueue(new Callback<DayPlacesResponse>() {
            @Override
            public void onResponse(Call<DayPlacesResponse> call, Response<DayPlacesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // API 응답에서 장소 리스트(places)를 추출
                    List<LocationItem> places = response.body().getPlaces();
                    Log.d(TAG, "장소 " + places.size() + "개 조회 성공.");

                    // 어댑터에 데이터를 업데이트하는 새로운 메서드를 호출
                    if (locationAdapter != null) {
                        locationAdapter.updateItems(places);
                    }

                } else {
                    Log.e(TAG, "장소 목록 조회 API 응답 실패: " + response.code());
                    // 오류 발생 시, 리스트를 비우고 '추가 버튼'만 보여주도록 처리
                    if (locationAdapter != null) {
                        locationAdapter.updateItems(new ArrayList<>());
                    }
                }
            }

            @Override
            public void onFailure(Call<DayPlacesResponse> call, Throwable t) {
                Log.e(TAG, "장소 목록 조회 네트워크 오류", t);
                if (locationAdapter != null) {
                    locationAdapter.updateItems(new ArrayList<>());
                }
            }
        });
    }


    // Adapter로부터 Activity 실행 요청 받아서 화면 전환 처리
    public void launchAddLocationActivity(long courseId, long dayId) {
        // 🟡 디버깅 로그 3: AddLocationActivity를 실행하기 직전
        Log.d("DEBUG_DAY_ID", "[Fragment launch] AddLocationActivity 실행 요청. 전달할 dayId: " + dayId);

        Intent intent = new Intent(requireActivity(), AddLocationActivity.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("dayId", dayId);

        // ActivityResultLauncher를 사용하여 결과를 받음
//        requireActivity().startActivity(intent);
        // 🟡 5. 'startActivity' 대신 'launch'를 사용하여 Activity를 실행하고 결과를 기다립니다.
        addLocationLauncher.launch(intent);
    }


}


