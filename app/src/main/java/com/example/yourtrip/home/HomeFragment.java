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
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private Button btnPopularMore; //인기 코스 더보기 버튼
    private Button btnThemeMore;
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

        Button btnThemeMore = view.findViewById(R.id.btn_theme_course_more);

        btnThemeMore.setOnClickListener(v -> {
            // ⭐ 현재 선택된 태그 찾기
            String selectedTheme = null;

            for (TextView tv : allTags) {
                if (tv.isSelected()) {
                    selectedTheme = tv.getText().toString();
                    break;
                }
            }

            if (selectedTheme == null) return; // 선택된 태그가 없다면 종료

            // ⭐ 이동용 번들 생성
            Bundle bundle = new Bundle();
            bundle.putString("keyword", "");
            bundle.putStringArrayList("tags", new ArrayList<>(List.of(selectedTheme)));
            bundle.putString("mode", "theme");

            // ⭐ 검색결과 페이지로 이동
            HomeSearchResultFragment fragment = new HomeSearchResultFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

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


    // theme 코스 전용 더미데이터
    private void loadAllDummyCourseData() {
        allCourseDummyList = new ArrayList<>();

        allCourseDummyList.add(new HomeCourseItem("홍대 카페 투어", "서울 마포구", R.drawable.dummy2,
                Arrays.asList("맛집탐방", "친구", "주말")));

        allCourseDummyList.add(new HomeCourseItem("부산 힐링 여행", "부산 해운대구", R.drawable.dummy3,
                Arrays.asList("힐링", "장기", "가족")));

        allCourseDummyList.add(new HomeCourseItem("강릉 바다 드라이브", "강원 강릉시", R.drawable.dummy4,
                Arrays.asList("자차", "연인", "자연", "1박 2일")));

        allCourseDummyList.add(new HomeCourseItem("제주 감성 사진 스팟 투어", "제주 제주시", R.drawable.dummy5,
                Arrays.asList("감성", "하루", "친구")));

        allCourseDummyList.add(new HomeCourseItem("서울 숲 산책 힐링 코스", "서울 성동구", R.drawable.dummy4,
                Arrays.asList("힐링", "혼자", "하루")));

        allCourseDummyList.add(new HomeCourseItem("전주 한옥마을 맛집투어", "전북 전주시", R.drawable.dummy1,
                Arrays.asList("맛집탐방", "가성비", "주말")));

        allCourseDummyList.add(new HomeCourseItem("대전 문화예술 전시 투어", "대전 서구", R.drawable.dummy2,
                Arrays.asList("문화/전시", "혼자", "하루")));

        allCourseDummyList.add(new HomeCourseItem("인천 소래습지 자연 여행", "인천 남동구", R.drawable.dummy3,
                Arrays.asList("자연", "가족", "하루")));

        allCourseDummyList.add(new HomeCourseItem("속초 액티비티 체험", "강원 속초시", R.drawable.dummy4,
                Arrays.asList("액티비티", "친구", "1박 2일")));

        allCourseDummyList.add(new HomeCourseItem("부천 쇼핑 데이트 코스", "경기 부천시", R.drawable.dummy5,
                Arrays.asList("쇼핑", "연인", "하루")));

        allCourseDummyList.add(new HomeCourseItem("광주 힐링 사색 여행", "광주 동구", R.drawable.dummy3,
                Arrays.asList("힐링", "혼자", "가성비")));

        allCourseDummyList.add(new HomeCourseItem("울산 대왕암 해안 산책", "울산 동구", R.drawable.dummy1,
                Arrays.asList("자연", "연인", "하루")));

        allCourseDummyList.add(new HomeCourseItem("대구 근교 카페 투어", "대구 수성구", R.drawable.dummy2,
                Arrays.asList("감성", "친구", "주말")));

        allCourseDummyList.add(new HomeCourseItem("여수 낭만 야경 여행", "전남 여수시", R.drawable.dummy3,
                Arrays.asList("감성", "연인", "1박 2일", "프리미엄")));

        allCourseDummyList.add(new HomeCourseItem("제주 액티비티 종합 코스", "제주 서귀포시", R.drawable.dummy4,
                Arrays.asList("액티비티", "친구", "장기")));

        allCourseDummyList.add(new HomeCourseItem("안산 호수공원 산책", "경기 안산시", R.drawable.dummy5,
                Arrays.asList("힐링", "가족", "하루")));

        allCourseDummyList.add(new HomeCourseItem("포항 바다 감성 드라이브", "경북 포항시", R.drawable.dummy5,
                Arrays.asList("자차", "감성", "주말")));

        allCourseDummyList.add(new HomeCourseItem("순천 국가정원 자연 힐링", "전남 순천시", R.drawable.dummy1,
                Arrays.asList("자연", "가족", "하루")));

        allCourseDummyList.add(new HomeCourseItem("서울 종로 문화유산 투어", "서울 종로구", R.drawable.dummy2,
                Arrays.asList("문화/전시", "혼자", "하루")));
        // ⭐ 인기 top5 뽑아서 RecyclerView에 적용
        List<HomeCourseItem> topFive = allCourseDummyList.size() > 5
                ? allCourseDummyList.subList(0, 5)
                : allCourseDummyList;

        popularAdapter.updateList(topFive);
    }


}



