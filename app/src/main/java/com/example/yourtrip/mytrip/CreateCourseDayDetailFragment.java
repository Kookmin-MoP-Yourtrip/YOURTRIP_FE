package com.example.yourtrip.mytrip;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager; // 추가
import androidx.recyclerview.widget.RecyclerView;       // 추가

import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.model.MyCourseDetailResponse;

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

        // 🔵 1. RecyclerView 초기화
        recyclerViewTripDays = view.findViewById(R.id.recyclerViewTripDays);

        // 🔵 2. 데이터 유효성 검사 후 어댑터 설정
        if (daySchedules != null && !daySchedules.isEmpty()) {
            setupDayRecyclerView();
        } else {
            Log.w(TAG, "daySchedules가 비어있어 RecyclerView를 설정할 수 없습니다.");
        }

        // TODO: 아래쪽 장소 목록 RecyclerView(recyclerLocationList) 설정 로직 추가
    }

    /**
     * 상단 일차 탭 RecyclerView를 설정하는 메소드
     */
    private void setupDayRecyclerView() {
        // 🔵 3. 어댑터 생성
        dayAdapter = new DayAdapter(daySchedules);

        // 🔵 4. 레이아웃 매니저 설정 (가로 스크롤)
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTripDays.setLayoutManager(layoutManager);

        // 🔵 5. 어댑터 연결
        recyclerViewTripDays.setAdapter(dayAdapter);
    }
}


//package com.example.yourtrip.mytrip;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.yourtrip.R;
//import com.example.yourtrip.mytrip.DayDetailItem;
//import com.example.yourtrip.mytrip.model.PlaceAddResponse;
//import com.example.yourtrip.mytrip.util.DateUtils;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
///**
// * '일차별 장소 목록'을 보여주고, 새로운 장소를 추가하는 화면의 Fragment 입니다.
// * 이 Fragment는 상단 '일차 탭'과 하단 '장소 목록' 두 개의 RecyclerView를 모두 관리합니다.
// */
//public class CreateCourseDayDetailFragment extends Fragment {
//
//    // --- 데이터 관련 멤버 변수 ---
//    private final List<DayDetailItem> locationItemList = new ArrayList<>(); // 하단 장소 목록 데이터
//    private DayAdapter locationAdapter; // 하단 장소 목록 어댑터
//
//    private final List<String> dayTabList = new ArrayList<>(); // 상단 일차 탭 데이터 (예: "1일차", "2일차")
//    private DayTabAdapter dayTabAdapter; // 상단 일차 탭 어댑터
//
//    // --- 현재 상태를 저장하는 변수 ---
//    private long currentCourseId = -1L;
//    private long currentDayId = -1L; // ★★★ 현재 선택된 일차의 ID, 이 값이 중요합니다! ★★★
//    private int currentSelectedDayIndex = 0; // 현재 선택된 탭의 인덱스
//
//    // --- 뷰 관련 멤버 변수 ---
//    private RecyclerView rvDayTabs; // 상단 일차 탭 RecyclerView
//    private RecyclerView rvLocations; // 하단 장소 목록 RecyclerView
//
//    // AddLocationActivity의 결과를 받기 위한 Launcher
//    private ActivityResultLauncher<Intent> addLocationLauncher;
//
//    /**
//     * Fragment가 생성될 때 가장 먼저 호출됩니다.
//     * 여기서는 화면이 복원될 때를 대비한 데이터 처리나, 화면과 상관없는 초기화 작업을 합니다.
//     */
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // 1. 이전 화면(Activity)에서 전달된 코스 ID와 날짜 정보를 받습니다.
//        if (getArguments() != null) {
//            currentCourseId = getArguments().getLong("courseId", -1L);
//            String startDate = getArguments().getString("startDate");
//            String endDate = getArguments().getString("endDate");
//
//            // ★★★ 날짜를 계산하여 동적으로 탭을 생성합니다. ★★★
//            generateDayTabsFromDates(startDate, endDate);
//
//            // TODO: 백엔드에서 실제 dayId 목록을 받아와야 합니다. 지금은 임시로 처리합니다.
//            if (!dayTabList.isEmpty()) {
//                currentDayId = 1L; // 임시로 첫 번째 일차의 ID를 1로 설정합니다.
//            }
//        }
//
//        // 2. AddLocationActivity가 결과를 돌려주면 실행될 콜백을 등록합니다.
//        addLocationLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    // AddLocationActivity가 성공(RESULT_OK) 응답을 보냈는지 확인합니다.
//                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                        // "newPlace" 키로 전달된 API 응답 객체(PlaceAddResponse)를 꺼냅니다.
//                        PlaceAddResponse newPlace = (PlaceAddResponse) result.getData().getSerializableExtra("newPlace");
//
//                        if (newPlace != null) {
//                            Log.d("DayDetailFragment", "새로운 장소 받음: " + newPlace.getPlaceName());
//                            addPlaceToList(newPlace); // 리스트에 아이템을 추가하는 메서드 호출
//                        }
//                    }
//                }
//        );
//    }
//
//    /**
//     * Fragment의 UI(레이아웃)를 생성하는 단계입니다.
//     */
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // 올바른 레이아웃 파일(fragment_trip_create_detail.xml)을 사용합니다.
//        return inflater.inflate(R.layout.fragment_trip_create_detail, container, false);
//    }
//
//    /**
//     * onCreateView에서 만들어진 뷰가 완전히 생성된 후 호출됩니다.
//     * 여기서는 뷰에 대한 초기화 작업을 합니다.
//     */
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        // XML의 뷰 ID를 정확히 참조합니다.
//        rvDayTabs = view.findViewById(R.id.recyclerViewTripDays); // 상단 탭 RecyclerView
//        rvLocations = view.findViewById(R.id.recyclerLocationList); // 하단 장소 목록 RecyclerView
//
//        // 각 RecyclerView를 설정합니다.
//        setupDayTabsRecyclerView();
//        setupLocationsRecyclerView();
//    }
//
//    /**
//     * 상단 '일차 탭' RecyclerView를 설정합니다.
//     */
//    private void setupDayTabsRecyclerView() {
//        dayTabAdapter = new DayTabAdapter(dayTabList, position -> {
//            // 탭을 클릭했을 때 실행될 로직
//            Log.d("DayDetailFragment", (position + 1) + "일차 탭 클릭됨");
//            currentSelectedDayIndex = position;
//            // TODO: 실제로는 position에 맞는 dayId를 백엔드 데이터에서 찾아 currentDayId에 할당해야 합니다.
//            currentDayId = (long) (position + 1); // 임시로 인덱스+1을 dayId로 사용
//            dayTabAdapter.notifyDataSetChanged(); // 선택된 탭의 UI를 업데이트하기 위해 호출
//            // TODO: 해당 일차에 맞는 장소 목록을 서버에서 불러오는 API 호출 필요
//        });
//        rvDayTabs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        rvDayTabs.setAdapter(dayTabAdapter);
//    }
//
//    /**
//     * 하단 '장소 목록' RecyclerView를 설정합니다.
//     */
//    private void setupLocationsRecyclerView() {
//        // 전용 어댑터인 DayDetailAdapter를 생성하고 연결합니다.
//        locationAdapter = new DayAdapter(locationItemList, this);
//        rvLocations.setLayoutManager(new LinearLayoutManager(getContext()));
//        rvLocations.setAdapter(locationAdapter);
//
//        // 초기 데이터 설정: 처음에는 '+ 장소 추가하기' 버튼만 리스트에 추가합니다.
//        if (locationItemList.isEmpty()) {
//            // TODO: 추후 실제 선택된 일차의 장소 목록을 불러오는 API 호출 로직이 여기에 추가되어야 합니다.
//            locationItemList.add(new DayDetailItem(DayDetailItem.TYPE_ADD_BUTTON)); // 타입만 있는 아이템 추가
//            locationAdapter.notifyDataSetChanged();
//        }
//    }
//
//    /**
//     * 어댑터에서 "+ 장소 추가하기" 버튼을 눌렀을 때 호출될 메서드입니다.
//     */
//    public void launchAddLocation() {
//        // ID 값이 유효하지 않으면 토스트 메시지를 보여주고 실행하지 않습니다.
//        if (currentCourseId == -1L || currentDayId == -1L) {
//            Log.e("DayDetailFragment", "courseId 또는 dayId가 유효하지 않아 AddLocationActivity를 실행할 수 없습니다.");
//            Toast.makeText(getContext(), "코스 또는 일차 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // AddLocationActivity로 현재 코스와 '선택된' 일차 ID를 담아 전달하며 실행합니다.
//        Intent intent = new Intent(requireActivity(), AddLocationActivity.class);
//        intent.putExtra("courseId", currentCourseId);
//        intent.putExtra("dayId", currentDayId);
//        addLocationLauncher.launch(intent);
//    }
//
//    /**
//     * Launcher 콜백에서 받은 새로운 장소 정보를 리스트에 추가하고 화면을 갱신합니다.
//     * @param newPlace AddLocationActivity에서 반환된 PlaceAddResponse 객체
//     */
//    private void addPlaceToList(PlaceAddResponse newPlace) {
//        int addBtnPosition = findAddItemPosition();
//        if (addBtnPosition != -1) {
//            DayDetailItem newLocationItem = new DayDetailItem(
//                    DayDetailItem.TYPE_LOCATION,
//                    newPlace.getPlaceName(),
//                    newPlace.getPlaceLocation()
//            );
//            // '+ 장소 추가하기' 버튼 바로 앞에 새로운 장소를 삽입합니다.
//            locationItemList.add(addBtnPosition, newLocationItem);
//            // 어댑터에게 해당 위치에 아이템이 삽입되었음을 알려 화면을 갱신합니다.
//            locationAdapter.notifyItemInserted(addBtnPosition);
//        }
//    }
//
//    /**
//     * 리스트에서 '+ 장소 추가하기' 버튼의 현재 위치(인덱스)를 찾는 헬퍼 메서드입니다.
//     * @return 버튼의 인덱스, 찾지 못하면 -1
//     */
//    private int findAddItemPosition() {
//        for (int i = 0; i < locationItemList.size(); i++) {
//            if (locationItemList.get(i).getViewType() == DayDetailItem.TYPE_ADD_BUTTON) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
//    /**
//     * ★★★ 시작일과 종료일을 바탕으로, 동적으로 '일차' 탭 데이터를 생성합니다. ★★★
//     */
//    private void generateDayTabsFromDates(String start, String end) {
//        dayTabList.clear();
//        if (start == null || end == null || start.isEmpty() || end.isEmpty()) {
//            dayTabList.add("일정 추가하기");
//            return;
//        }
//
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
//            Date startDate = sdf.parse(start);
//            Date endDate = sdf.parse(end);
//            long diffMillis = endDate.getTime() - startDate.getTime();
//            long diffDays = diffMillis / (24 * 60 * 60 * 1000);
//
//            int totalDays = (int) diffDays + 1;
//            for (int i = 1; i <= totalDays; i++) {
//                dayTabList.add(i + "일차");
//            }
//            dayTabList.add("일정 추가하기");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            dayTabList.add("일정 추가하기");
//        }
//    }
//
//
//    // --- 상단 일차 탭을 위한 어댑터와 뷰홀더 ---
//    // (이 코드는 별도의 파일로 분리해도 되고, 편의상 내부 클래스로 둬도 됩니다)
//    // --- 상단 일차 탭을 위한 어댑터와 뷰홀더 ---
//    private class DayTabAdapter extends RecyclerView.Adapter<DayTabAdapter.DayTabViewHolder> {
//        private final List<String> days;
//        private final OnTabClickListener listener;
//
//        public DayTabAdapter(List<String> days, OnTabClickListener listener) {
//            this.days = days;
//            this.listener = listener;
//        }
//
//        @NonNull
//        @Override
//        public DayTabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            // TODO: '일차 탭'을 위한 XML 레이아웃(예: item_day_tab.xml)을 만들어야 합니다.
//            // 지금은 임시로 안드로이드 기본 텍스트뷰 레이아웃을 사용합니다.
//            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
//            return new DayTabViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull DayTabViewHolder holder, int position) {
//            holder.tvDay.setText(days.get(position));
//            // 현재 선택된 탭인지 확인하고 UI를 변경합니다.
//            if (position == currentSelectedDayIndex) {
//                // TODO: 선택된 탭의 UI (예: 배경색, 텍스트 색상)를 변경하는 코드
//            } else {
//                // TODO: 선택되지 않은 탭의 UI
//            }
//            holder.itemView.setOnClickListener(v -> listener.onTabClick(holder.getAdapterPosition()));
//        }
//
//        @Override
//        public int getItemCount() {
//            return days.size();
//        }
//
//        class DayTabViewHolder extends RecyclerView.ViewHolder {
//            TextView tvDay;
//            public DayTabViewHolder(@NonNull View itemView) {
//                super(itemView);
//                tvDay = itemView.findViewById(android.R.id.text1); // 임시 ID
//            }
//        }
//    }
//
//    /**
//     * ★★★ Static 오류 해결: 인터페이스를 DayTabAdapter 밖으로 이동 ★★★
//     * DayTabAdapter의 내부 멤버가 아니므로, static 관련 규칙에 영향을 받지 않습니다.
//     */
//    interface OnTabClickListener {
//        void onTabClick(int position);
//    }
//}



//package com.example.yourtrip.mytrip;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.example.yourtrip.R;
//import com.example.yourtrip.mytrip.model.LocationItem;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CreateCourseDayDetailFragment extends Fragment {
//    private ArrayList<LocationItem> locationList  = new ArrayList<>();
//    private LocationAdapter locationAdapter;
//    private RecyclerView rvLocations;
//
//
//    // 🔵 AddLocationActivity → 결과 받는 Launcher
//    private ActivityResultLauncher<Intent> addLocationLauncher =
//            registerForActivityResult(
//                    new ActivityResultContracts.StartActivityForResult(),
//                    result -> {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            Intent data = result.getData();
//                            if (data != null) {
//
//                                // 반환된 값 받기
//                                LocationItem item = new LocationItem();
//                                item.setPlaceName(data.getStringExtra("placeName"));
//                                item.setAddress(data.getStringExtra("address"));
//                                item.setLat(data.getDoubleExtra("lat", 0));
//                                item.setLng(data.getDoubleExtra("lng", 0));
//
//                                // 리스트에 추가
//                                locationList.add(item);
//                                locationAdapter.notifyItemInserted(locationList.size());
//                            }
//                        }
//                    }
//            );
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.fragment_trip_create_detail, container, false);
//
//        // ===============================
//        // 1) 상단 일차 스크롤 (dayList)
//        // ===============================
//        ArrayList<String> dayList = getArguments().getStringArrayList("dayList");
//        Log.d("CreateCourseDayDetail", "받은 dayList = " + dayList);
//
//        RecyclerView rvDays = view.findViewById(R.id.recyclerViewTripDays);
//        rvDays.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        rvDays.setAdapter(new TripAdapter(dayList));
//
//
//        // ===============================
//        // 2) 장소 리스트 RecyclerView
//        // ===============================
//        locationList = new ArrayList<>();
//
//        RecyclerView rvLocations = view.findViewById(R.id.recyclerLocationList);
//        rvLocations.setLayoutManager(new LinearLayoutManager(getContext()));
//        locationAdapter = new LocationAdapter(locationList);
//        rvLocations.setAdapter(locationAdapter);
//
//
//        // ===============================
//        // 3) 장소 추가하기 버튼 → Activity 이동
//        // ===============================
//        locationAdapter.setOnAddClickListener(() -> {
//            Intent intent = new Intent(getActivity(), AddLocationActivity.class);
//            addLocationLauncher.launch(intent);
//        });
//
//
//        // ===============================
//        // 4) 장소 삭제하기 버튼
//        // ===============================
//        locationAdapter.setOnDeleteClickListener(position -> {
//            locationList.remove(position);
//            locationAdapter.notifyItemRemoved(position);
//            locationAdapter.notifyItemRangeChanged(position, locationList.size());
//        });
//
//        return view;
//    }
//
//
//    // ===============================
//    // TripAdapter (일차 스크롤 영역)
//    // ===============================
//    public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
//
//        private List<String> tripList;
//
//        public TripAdapter(List<String> tripList) {
//            this.tripList = tripList;
//        }
//
//        @Override
//        public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View itemView = LayoutInflater.from(parent.getContext())
//                    .inflate(android.R.layout.simple_list_item_1, parent, false);
//            return new TripViewHolder(itemView);
//        }
//
//        @Override
//        public void onBindViewHolder(TripViewHolder holder, int position) {
//            String item = tripList.get(position);
//            holder.textView.setText(item);
//        }
//
//        @Override
//        public int getItemCount() {
//            return tripList.size();
//        }
//
//        // ViewHolder
//        public class TripViewHolder extends RecyclerView.ViewHolder {
//            public TextView textView;
//
//            public TripViewHolder(View view) {
//                super(view);
//                textView = view.findViewById(android.R.id.text1);
//            }
//        }
//    }
//}
//
