package com.example.yourtrip.mytrip.list;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // ★★★ 올바른 Log 클래스를 import 합니다. ★★★
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.create_ai.CreateAICourseDateActivity;
import com.example.yourtrip.mytrip.create_direct.CreateCourseBasicActivity;
import com.example.yourtrip.mytrip.create_direct.CreateCourseDetailActivity;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;
import com.example.yourtrip.mytrip.model.MyCourseListResponse; //목록 전체
import com.example.yourtrip.mytrip.model.MyCourseListItemResponse; // 목록 아이템 하나만

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTripListFragment extends Fragment {

    // --- 뷰 관련 변수들 ---
    private ImageView btnAddTrip;
    private LinearLayout fabMenuLayout;
    private View dimLayer;
    private LinearLayout btnAIMake;
    private LinearLayout btnManualMake;
    private boolean isMenuOpen = false;

    // --- 데이터 관련 변수들 ---
    private RecyclerView recyclerView;
    private TripAdapter adapter;
    private final List<MyCourseListItemResponse> courseList = new ArrayList<>();
    private ApiService apiService;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupFabMenu();

        // Fragment가 생성될 때 API 서비스 초기화
        apiService = RetrofitClient.getAuthService(requireContext());
    }

    // 화면이 사용자에게 다시 보일 때마다 호출되는 메서드
    @Override
    public void onResume() {
        super.onResume();
        // 최신 코스 목록을 서버에서 불러옵니다.
        loadMyCourses();
    }

    private void initViews(@NonNull View view) {
        recyclerView = view.findViewById(R.id.trip_recycler);
        btnAddTrip = view.findViewById(R.id.btn_add_trip);
        fabMenuLayout = view.findViewById(R.id.fab_menu);
        dimLayer = view.findViewById(R.id.fab_dim);
        btnAIMake = view.findViewById(R.id.btn_upload);
        btnManualMake = view.findViewById(R.id.btn_manual_make);
    }

    private void setupRecyclerView() {
        adapter = new TripAdapter(courseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // 어댑터에 아이템 클릭 리스너를 설정
        adapter.setOnItemClickListener(myTrip -> {

            // 더미 데이터는 courseId가 null이므로, 실제 데이터(서버 응답)에만 클릭 이벤트가 동작
            if (myTrip != null && myTrip.getCourseId() != null) {
                Intent intent = new Intent(requireActivity(), CreateCourseDetailActivity.class);

                intent.putExtra("courseId", myTrip.getCourseId());

                Log.d("MyTripListFragment", "CreateCourseDetailActivity로 이동. 전달하는 courseId: " + myTrip.getCourseId());

                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "상세 정보를 볼 수 없는 코스입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 서버에서 내 코스 목록을 불러오는 메서드
    private void loadMyCourses() {
        Log.d("MyTripListFragment", "서버에서 코스 목록을 불러옵니다...");
        // getMyCourses()는 Call<MyCourseListResponse>를 반환하므로, Callback도 동일한 타입이어야 합니다.
        apiService.getMyCourses().enqueue(new Callback<MyCourseListResponse>() {
            @Override
            public void onResponse(Call<MyCourseListResponse> call, Response<MyCourseListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("MyTripListFragment", "코스 목록 API 호출 성공");
                    courseList.clear(); //기존 리스트를 깨끗하게 비움
                    courseList.addAll(response.body().getMyCourses());
                    courseList.addAll(getDummyCourses());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("MyTripListFragment", "코스 목록 API 호출 실패: " + response.code());
                    Toast.makeText(getContext(), "목록을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyCourseListResponse> call, Throwable t) {
                Log.e("MyTripListFragment", "코스 목록 API 네트워크 오류: " + t.getMessage());
                Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFabMenu() {
        dimLayer.setVisibility(View.GONE);
        fabMenuLayout.setVisibility(View.GONE);
        btnAddTrip.setOnClickListener(v -> {
            if (isMenuOpen) closeMenu();
            else openMenu();
        });
        dimLayer.setOnClickListener(v -> closeMenu());
        btnAIMake.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), CreateAICourseDateActivity.class);
            startActivity(intent);
            closeMenu();
        });
        btnManualMake.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), CreateCourseBasicActivity.class);
            startActivity(intent);
            closeMenu();
        });
    }

    private List<MyCourseListItemResponse> getDummyCourses() {
        List<MyCourseListItemResponse> list = new ArrayList<>();
        // API 응답과 형식을 맞추기 위해 courseId는 null로 설정합니다.
        list.add(new MyCourseListItemResponse(null, "대전 빵집 투어", "대전 중구, 서구", "2025-11-09", "2025-11-10", 1));
        list.add(new MyCourseListItemResponse(null, "강릉 힐링 바다 여행", "강원도 강릉", "2025-08-14", "2025-08-16", 1));
        list.add(new MyCourseListItemResponse(null,"부산 야경 맛집 코스", "부산 해운대구", "2025-05-01", "2025-05-02", 1));
        list.add(new MyCourseListItemResponse(null,"제주도 감성 카페 일주", "제주 제주시", "2025-03-12", "2025-03-14", 1));
        list.add(new MyCourseListItemResponse(null,"서울 종로 하루 산책", "서울 종로구", "2025-10-03", "2025-10-03", 1));
        list.add(new MyCourseListItemResponse(null,"전주 한옥마을 먹방 여행", "전북 전주", "2025-09-20", "2025-09-21", 1));
        list.add(new MyCourseListItemResponse(null,"속초 해변 드라이브", "강원도 속초", "2025-07-11", "2025-07-12", 1));
        list.add(new MyCourseListItemResponse(null,"울산 고래문화마을 하루 코스", "울산 남구", "2025-02-22", "2025-02-22", 1));
        list.add(new MyCourseListItemResponse(null,"수원 화성 당일 여행", "경기도 수원", "2025-04-18", "2025-04-18", 1));
        list.add(new MyCourseListItemResponse(null,"경주 야간 사적지 투어", "경북 경주", "2025-09-08", "2025-09-09", 1));
        return list;
    }

    private void openMenu() {
        fabMenuLayout.setVisibility(View.VISIBLE);
        dimLayer.setVisibility(View.VISIBLE);
        dimLayer.setAlpha(0f);
        dimLayer.animate().alpha(1f).setDuration(150).start();
        fabMenuLayout.setAlpha(0f);
        fabMenuLayout.setTranslationY(40f);
        fabMenuLayout.animate().alpha(1f).translationY(0f).setDuration(150).start();
        btnAddTrip.setImageResource(R.drawable.fab_close_menu);
        isMenuOpen = true;
    }

    private void closeMenu() {
        dimLayer.animate().alpha(0f).setDuration(150).withEndAction(() -> dimLayer.setVisibility(View.GONE)).start();
        fabMenuLayout.animate().alpha(0f).translationY(40f).setDuration(150).withEndAction(() -> fabMenuLayout.setVisibility(View.GONE)).start();
        btnAddTrip.setImageResource(R.drawable.fab_add_course);
        isMenuOpen = false;
    }
}

