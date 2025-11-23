package com.example.yourtrip.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourtrip.R;
import com.example.yourtrip.model.UploadCourseItem;
import com.example.yourtrip.model.UploadCourseListResponse;
import com.example.yourtrip.network.ApiService;
import com.example.yourtrip.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private Button btnPopularMore;
    private Button btnThemeMore;

    private RecyclerView rvPopular;
    private RecyclerView rvThemeCourse;

    private UploadCourseAdapter popularAdapter;
    private UploadCourseAdapter themeAdapter;

    // 장소 버튼들
    private View location0, location1, location2, location3, location4;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_home_main, container, false);

        initViews(view);

        // 인기/테마 more 버튼
        btnPopularMore = view.findViewById(R.id.btn_popular_course_more);
        btnThemeMore = view.findViewById(R.id.btn_theme_course_more);

        // 검색창 클릭 → 검색 화면
        EditText tvSearch = view.findViewById(R.id.tvSearch);
        tvSearch.setFocusable(false);
        tvSearch.setClickable(true);
        tvSearch.setOnClickListener(v -> {
            Fragment fragment = new HomeSearchFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        setupPopularRecycler();
        setupThemeRecycler();
        setupLocationClickEvents();

        // 인기 코스 더보기
        btnPopularMore.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("mode", "all");
            bundle.putString("keyword", "");
            bundle.putStringArrayList("tags", null);

            HomeSearchResultFragment fragment = new HomeSearchResultFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // 테마 코스 더보기 (현재는 전체 리스트 이동)
        btnThemeMore.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("mode", "theme");
            bundle.putString("keyword", "");
            bundle.putStringArrayList("tags", null);

            HomeSearchResultFragment fragment = new HomeSearchResultFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // API 로 인기 코스 로드
        loadPopularCourses();

        return view;
    }

    private void initViews(View view) {
        rvPopular = view.findViewById(R.id.rv_popular_course);
        rvThemeCourse = view.findViewById(R.id.rv_theme_course);

        location0 = view.findViewById(R.id.location0);
        location1 = view.findViewById(R.id.location1);
        location2 = view.findViewById(R.id.location2);
        location3 = view.findViewById(R.id.location3);
        location4 = view.findViewById(R.id.location4);
    }

    // 장소 클릭 → 해당 장소 검색
    private void setupLocationClickEvents() {
        View.OnClickListener listener = v -> {
            String keyword = "";

            int id = v.getId();
            if (id == R.id.location0) keyword = "홍대";
            else if (id == R.id.location1) keyword = "성수";
            else if (id == R.id.location2) keyword = "제주";
            else if (id == R.id.location3) keyword = "강릉";
            else if (id == R.id.location4) keyword = "서촌";

            Bundle bundle = new Bundle();
            bundle.putString("mode", "location");
            bundle.putString("keyword", keyword);
            bundle.putStringArrayList("tags", null);

            HomeSearchResultFragment fragment = new HomeSearchResultFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        };

        location0.setOnClickListener(listener);
        location1.setOnClickListener(listener);
        location2.setOnClickListener(listener);
        location3.setOnClickListener(listener);
        location4.setOnClickListener(listener);
    }

    // 인기 코스 RecyclerView
    private void setupPopularRecycler() {
        rvPopular.setLayoutManager(new LinearLayoutManager(getContext()));
        popularAdapter = new UploadCourseAdapter(new ArrayList<>());
        rvPopular.setAdapter(popularAdapter);
    }

    // 테마별 RecyclerView
    private void setupThemeRecycler() {
        rvThemeCourse.setLayoutManager(new LinearLayoutManager(getContext()));
        themeAdapter = new UploadCourseAdapter(new ArrayList<>());
        rvThemeCourse.setAdapter(themeAdapter);
    }

    // API → 인기 코스 가져오기
    private void loadPopularCourses() {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);

        api.getUploadCourses(null, null, "POPULAR")
                .enqueue(new Callback<UploadCourseListResponse>() {
                    @Override
                    public void onResponse(Call<UploadCourseListResponse> call,
                                           Response<UploadCourseListResponse> response) {

                        if (!response.isSuccessful()) {
                            Log.e("API_TEST", "❌ 서버 응답 실패 (코드: " + response.code() + ")");
                            return;
                        }

                        if (response.body() == null) {
                            Log.e("API_TEST", "❌ response.body() == null");
                            return;
                        }

                        List<UploadCourseItem> list = response.body().uploadCourses;

                        // ====== ⭐ API 응답 확인 로그 ======
                        Log.d("API_TEST", "✅ 응답 성공! 리스트 개수 = " + list.size());
                        for (UploadCourseItem item : list) {
                            Log.d("API_TEST", "• " + item.title + " | " + item.location);
                        }
                        // ==================================

                        // 기존 코드 그대로 유지
                        popularAdapter.setItems(list);
                        themeAdapter.setItems(list);
                    }

                    @Override
                    public void onFailure(Call<UploadCourseListResponse> call, Throwable t) {
                        Log.e("API_TEST", "❌ onFailure: " + t.getMessage());
                    }
                });


    }
}
