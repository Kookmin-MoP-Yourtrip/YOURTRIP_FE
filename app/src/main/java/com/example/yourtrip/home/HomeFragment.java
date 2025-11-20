package com.example.yourtrip.home;

import android.os.Bundle;
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
import com.example.yourtrip.model.HomeCourseItem;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private Button btnPopularMore; //인기 코스 더보기 버튼
    private TextView tagHealing, tagActivity, tagFood, tagSensibility, tagCulture, tagNature, tagShopping;
    private List<TextView> allTags = new ArrayList<>();
    private View location0, location1, location2, location3, location4;
    private RecyclerView rvPopular;
    private RecyclerView rvThemeCourse;
    private UploadCourseAdapter popularAdapter;
    private UploadCourseAdapter themeAdapter;
    private List<HomeCourseItem> allCourseDummyList = new ArrayList<>();


    // 1. onCreateView(): 화면 XML 초기화 -
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_home_main, container, false);

        initViews(view);       // 뷰 초기화

        Button btnPopularMore = view.findViewById(R.id.btn_popular_course_more); //인기코스 더보기 버튼
        this.btnPopularMore = btnPopularMore;

        // 🔹 검색창 클릭 → 검색 화면으로 이동
        EditText tvSearch = view.findViewById(R.id.tvSearch);

        tvSearch.setFocusable(false);   // 클릭 시 키보드가 뜨지 않도록
        tvSearch.setClickable(true);

        tvSearch.setOnClickListener(v -> {
            Fragment fragment = new HomeSearchFragment();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });


        setupPopularRecycler(); // 인기 코스 RecyclerView 셋업
        setupThemeRecycler(); // 테마 코스 RecyclerView 셋업

        loadDummyPopularData(); // 더미 데이터 주입
        loadAllDummyCourseData();

        setupLocationClickEvents();   // ⭐ 장소 버튼 클릭 이벤트 적용

        setupTagClickListeners(); // 태그 클릭 이벤트 적용

        tagHealing.performClick();

        btnPopularMore.setOnClickListener(v -> {
            // ⭐ 검색 결과 화면으로 이동 + 필터 완전 비활성(default 전체 리스트)
            Bundle bundle = new Bundle();
            bundle.putString("mode", "all");          // 전체 코스 모드
            bundle.putString("keyword", "");          // 검색어 없음
            bundle.putStringArrayList("tags", null);  // 태그 없음

            HomeSearchResultFragment fragment = new HomeSearchResultFragment();
            fragment.setArguments(bundle);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }


    //  뷰 초기화
    private void initViews(View view) {
        rvPopular = view.findViewById(R.id.rv_popular_course);
        rvThemeCourse = view.findViewById(R.id.rv_theme_course);


        // 🔹 장소별 코스 버튼들 가져오기
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
                tagHealing, tagActivity, tagFood,
                tagSensibility, tagCulture,
                tagNature, tagShopping
        );


    }

    // 태그 클릭 리스너
    private void setupTagClickListeners() {
        for (TextView tag : allTags) {
            tag.setOnClickListener(v -> {
                String selectedTag = tag.getText().toString();

                applyTagSelection(tag);     // 단일 선택 UI 적용
                filterCourseByTag(selectedTag); // 코스 5개 필터링
            });
        }
    }
    // 태그는 단일 선택만 가능
    private void applyTagSelection(TextView selectedTag) {
        for (TextView tag : allTags) {
            tag.setSelected(tag == selectedTag);   // 하나만 true, 나머지는 false

            boolean isSelected = (tag == selectedTag);
            tag.setSelected(isSelected);

            if (isSelected) {
                tag.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                tag.setTextColor(getResources().getColor(R.color.gray_600));
            }
        }
    }

    // 태그별 코스 필터링 함수 (추후 구현 - 더미로 확인)
    private void filterCourseByTag(String tag) {

        List<HomeCourseItem> matched = new ArrayList<>();

        for (HomeCourseItem item : allCourseDummyList) {
            if (item.tags.contains(tag)) {
                matched.add(item);
            }
        }

        // 인기순 상위 5개만
        List<HomeCourseItem> topFive =
                matched.size() > 5 ? matched.subList(0, 5) : matched;

        themeAdapter.updateList(topFive);
    }


    // 인기 코스 RecyclerView 설정
    private void setupPopularRecycler() {
        rvPopular.setLayoutManager(new LinearLayoutManager(getContext()));

        popularAdapter = new UploadCourseAdapter(new ArrayList<>());
        rvPopular.setAdapter(popularAdapter);

        // 클릭 이벤트 예시
        popularAdapter.setOnItemClickListener(item -> {
            // TODO: 상세 페이지 이동
            // Toast.makeText(getContext(), item.title, Toast.LENGTH_SHORT).show();
        });
    }
    // 테마별 인기코스
    private void setupThemeRecycler() {
        rvThemeCourse.setLayoutManager(new LinearLayoutManager(getContext()));
        themeAdapter = new UploadCourseAdapter(new ArrayList<>());
        rvThemeCourse.setAdapter(themeAdapter);
    }

    // 장소별 코스 클릭 리스너
    private void setupLocationClickEvents() {
        View.OnClickListener listener = v -> {
            String keyword = "";

            int id = v.getId();

            if (id == R.id.location0) keyword = "홍대";
            else if (id == R.id.location1) keyword = "성수";
            else if (id == R.id.location2) keyword = "제주도";
            else if (id == R.id.location3) keyword = "강릉";
            else if (id == R.id.location4) keyword = "서촌";

            // 🔹 검색창에 자동 입력
            EditText tvSearch = requireView().findViewById(R.id.tvSearch);
            tvSearch.setText(keyword);

            // 🔹 검색 실행 (추후 구현)
             runHomeSearch(keyword);
        };

        location0.setOnClickListener(listener);
        location1.setOnClickListener(listener);
        location2.setOnClickListener(listener);
        location3.setOnClickListener(listener);
        location4.setOnClickListener(listener);
    }

    private void runHomeSearch(String keyword) {
        // TODO: 홈 검색 로직 추후 구현
        // 예: 검색 API 호출, 결과 리스트 페이지 이동 등
    }

    // 인기순 코스 더미 데이터 주입
    private void loadDummyPopularData() {
        List<HomeCourseItem> dummy = new ArrayList<>();

        dummy.add(new HomeCourseItem(
                "대전 맛도리 빵집 투어",
                "대전 유성구, 중구",
                R.drawable.dummy1,
                List.of("자차", "쇼핑")
        ));
        dummy.add(new HomeCourseItem(
                "춘천 감성 카페 투어",
                "강원 춘천시",
                R.drawable.dummy3,
                List.of("감성", "하루")
        ));

        dummy.add(new HomeCourseItem(
                "성수 카페 투어",
                "서울 성동구",
                R.drawable.dummy2,
                List.of("혼자", "쇼핑", "프리미엄", "힐링")

        ));

        dummy.add(new HomeCourseItem(
                "여수 가족 여행 루트",
                "전남 여수시",
                R.drawable.dummy4,
                List.of("가족", "힐링")
        ));

        dummy.add(new HomeCourseItem(
                "부산 야경 드라이브 코스",
                "부산 해운대구",
                R.drawable.dummy5,
                List.of("자차", "가성비")
        ));

        dummy.add(new HomeCourseItem(
                "부산 야경 드라이브 코스",
                "부산 해운대구",
                R.drawable.dummy5,
                List.of("자차", "가성비")
        ));

        // ⭐ 여기서 5개로 제한해서 Adapter에 전달
        List<HomeCourseItem> topFive = dummy.size() > 5
                ? dummy.subList(0, 5)
                : dummy;

        // Adapter에 데이터 전달
        popularAdapter.updateList(topFive);
    }

    // theme 코스 전용 더미데이터
    private void loadAllDummyCourseData() {
        allCourseDummyList = new ArrayList<>();

        allCourseDummyList.add(new HomeCourseItem(
                "대전 맛도리 빵집 투어",
                "대전 유성구, 중구",
                R.drawable.dummy1,
                List.of("자차", "쇼핑", "힐링")
        ));

        allCourseDummyList.add(new HomeCourseItem(
                "춘천 감성 카페 투어",
                "강원 춘천시",
                R.drawable.dummy3,
                List.of("감성", "하루", "힐링")
        ));

        allCourseDummyList.add(new HomeCourseItem(
                "성수 카페 투어",
                "서울 성동구",
                R.drawable.dummy2,
                List.of("혼자", "쇼핑", "프리미엄")
        ));

        allCourseDummyList.add(new HomeCourseItem(
                "여수 가족 여행 루트",
                "전남 여수시",
                R.drawable.dummy4,
                List.of("가족", "힐링")
        ));

        allCourseDummyList.add(new HomeCourseItem(
                "부산 야경 드라이브 코스",
                "부산 해운대구",
                R.drawable.dummy5,
                List.of("자차", "가성비")
        ));

        // 필요한 만큼 계속 추가
    }

}

