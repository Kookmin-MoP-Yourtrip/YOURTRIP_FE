package com.example.yourtrip.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    // ⭐ 태그 관련
    private TextView tagHealing, tagActivity, tagFood, tagSensibility, tagCulture, tagNature, tagShopping;
    private List<TextView> allTags = new ArrayList<>();
    private List<UploadCourseItem> allCourseList = new ArrayList<>();

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

        // 검색창 → 검색 화면 이동
        EditText tvSearch = view.findViewById(R.id.tvSearch);
        tvSearch.setFocusable(false);
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
        setupTagClickListeners();

        // Default 태그 = 힐링
        tagHealing.post(() -> tagHealing.performClick());

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

        // 테마 코스 더보기
        btnThemeMore.setOnClickListener(v -> {
            String selectedTag = null;
            for (TextView tv : allTags) {
                if (tv.isSelected()) {
                    selectedTag = tv.getText().toString();
                    break;
                }
            }
            if (selectedTag == null) return;

            Bundle bundle = new Bundle();
            bundle.putString("mode", "theme");
            bundle.putString("keyword", "");
            bundle.putStringArrayList("tags", new ArrayList<>(List.of(selectedTag)));

            HomeSearchResultFragment fragment = new HomeSearchResultFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // API 호출 → 인기 코스 로드 (전체 리스트 저장)
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

        // 태그 바인딩
        tagHealing = view.findViewById(R.id.tag_healing);
        tagActivity = view.findViewById(R.id.tag_activity);
        tagFood = view.findViewById(R.id.tag_food);
        tagSensibility = view.findViewById(R.id.tag_sensibility);
        tagCulture = view.findViewById(R.id.tag_culture);
        tagNature = view.findViewById(R.id.tag_nature);
        tagShopping = view.findViewById(R.id.tag_shopping);

        allTags = List.of(
                tagHealing, tagActivity, tagFood, tagSensibility,
                tagCulture, tagNature, tagShopping
        );
    }

    // ⭐ 태그 클릭 리스너 등록
    private void setupTagClickListeners() {
        for (TextView tag : allTags) {
            tag.setOnClickListener(v -> {
                String selectedTag = tag.getText().toString();
                applyTagSelection(tag);
                filterCourseByTag(selectedTag);
            });
        }
    }

    // ⭐ 태그 단일 선택 UI
    private void applyTagSelection(TextView selectedTag) {
        for (TextView tag : allTags) {
            boolean isSelected = (tag == selectedTag);
            tag.setSelected(isSelected);

            tag.setTextColor(isSelected ?
                    getResources().getColor(android.R.color.white) :
                    getResources().getColor(R.color.gray_600)
            );
        }
    }

    // ⭐ 태그 필터링
    private void filterCourseByTag(String tag) {
        List<UploadCourseItem> matched = new ArrayList<>();

        for (UploadCourseItem item : allCourseList) {
            if (item.keywords != null && item.keywords.contains(tag)) {
                matched.add(item);
            }
        }

        List<UploadCourseItem> topFive =
                matched.size() > 5 ? matched.subList(0, 5) : matched;

        themeAdapter.setItems(topFive);
    }

    // RecyclerView 기본 설정
    private void setupPopularRecycler() {
        rvPopular.setLayoutManager(new LinearLayoutManager(getContext()));
        popularAdapter = new UploadCourseAdapter(new ArrayList<>());
        rvPopular.setAdapter(popularAdapter);
    }

    private void setupThemeRecycler() {
        rvThemeCourse.setLayoutManager(new LinearLayoutManager(getContext()));
        themeAdapter = new UploadCourseAdapter(new ArrayList<>());
        rvThemeCourse.setAdapter(themeAdapter);
    }

    // ⭐ 장소별 클릭
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

    // ⭐ API 호출
    private void loadPopularCourses() {
        ApiService api = RetrofitClient.getInstance(getContext()).create(ApiService.class);

        api.getUploadCourses(null, null, "POPULAR")
                .enqueue(new Callback<UploadCourseListResponse>() {
                    @Override
                    public void onResponse(Call<UploadCourseListResponse> call,
                                           Response<UploadCourseListResponse> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Log.e("API", "❌ 서버 응답 오류");
                            return;
                        }

                        List<UploadCourseItem> list = response.body().uploadCourses;

                        // ⭐ 전체 저장 (태그 필터링용)
                        allCourseList = list;

                        // 상위 5개만 추림
                        List<UploadCourseItem> topFive =
                                list.size() > 5 ? list.subList(0, 5) : list;

                        popularAdapter.setItems(topFive);

                        // 디폴트: theme 도 인기 top5
                        themeAdapter.setItems(topFive);
                    }

                    @Override
                    public void onFailure(Call<UploadCourseListResponse> call, Throwable t) {
                        Log.e("API", "❌ 통신 실패: " + t.getMessage());
                    }
                });
    }
}
