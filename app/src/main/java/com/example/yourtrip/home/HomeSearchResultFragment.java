package com.example.yourtrip.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class HomeSearchResultFragment extends Fragment {

    private LinearLayout tagListContainer;
    private ArrayList<String> selectedTags;
    private String keyword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_search_result, container, false);

        tagListContainer = view.findViewById(R.id.tagListContainer);

        // ⭐ 전달받은 검색데이터
        Bundle args = getArguments();
        if (args != null) {
            keyword = args.getString("keyword", "");
            selectedTags = args.getStringArrayList("tags");
        }

        // ⭐ 검색어를 상단 검색창에 세팅 (유지)
        EditText etSearch = view.findViewById(R.id.tvSearch);
        etSearch.setText(keyword);
        etSearch.setFocusable(false);
        etSearch.setClickable(true);

        // 검색창 클릭 시 다시 HomeSearchFragment로 이동
        etSearch.setOnClickListener(v -> {
            Fragment fragment = new HomeSearchFragment();
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // ⭐ 태그 UI 생성
        if (selectedTags != null) {
            addSelectedTagsToContainer();
        }

        RecyclerView rv = view.findViewById(R.id.rvSearchResult);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<HomeCourseItem> dummy = new ArrayList<>();

        dummy.add(new HomeCourseItem("홍대 카페 투어", "서울 마포구", R.drawable.dummy2,
                Arrays.asList("맛집탐방", "친구", "주말")));

        dummy.add(new HomeCourseItem("부산 힐링 여행", "부산 해운대구", R.drawable.dummy3,
                Arrays.asList("힐링", "장기", "가족")));

        dummy.add(new HomeCourseItem("강릉 바다 드라이브", "강원 강릉시", R.drawable.dummy4,
                Arrays.asList("자차", "연인", "자연", "1박 2일")));

        dummy.add(new HomeCourseItem("제주 감성 사진 스팟 투어", "제주 제주시", R.drawable.dummy5,
                Arrays.asList("감성", "하루", "친구")));

        dummy.add(new HomeCourseItem("서울 숲 산책 힐링 코스", "서울 성동구", R.drawable.dummy4,
                Arrays.asList("힐링", "혼자", "하루")));

        dummy.add(new HomeCourseItem("전주 한옥마을 맛집투어", "전북 전주시", R.drawable.dummy1,
                Arrays.asList("맛집탐방", "가성비", "주말")));

        dummy.add(new HomeCourseItem("대전 문화예술 전시 투어", "대전 서구", R.drawable.dummy2,
                Arrays.asList("문화/전시", "혼자", "하루")));

        dummy.add(new HomeCourseItem("인천 소래습지 자연 여행", "인천 남동구", R.drawable.dummy3,
                Arrays.asList("자연", "가족", "하루")));

        dummy.add(new HomeCourseItem("속초 액티비티 체험", "강원 속초시", R.drawable.dummy4,
                Arrays.asList("액티비티", "친구", "1박 2일")));

        dummy.add(new HomeCourseItem("부천 쇼핑 데이트 코스", "경기 부천시", R.drawable.dummy5,
                Arrays.asList("쇼핑", "연인", "하루")));

        dummy.add(new HomeCourseItem("광주 힐링 사색 여행", "광주 동구", R.drawable.dummy3,
                Arrays.asList("힐링", "혼자", "가성비")));

        dummy.add(new HomeCourseItem("울산 대왕암 해안 산책", "울산 동구", R.drawable.dummy1,
                Arrays.asList("자연", "연인", "하루")));

        dummy.add(new HomeCourseItem("대구 근교 카페 투어", "대구 수성구", R.drawable.dummy2,
                Arrays.asList("감성", "친구", "주말")));

        dummy.add(new HomeCourseItem("여수 낭만 야경 여행", "전남 여수시", R.drawable.dummy3,
                Arrays.asList("감성", "연인", "1박 2일", "프리미엄")));

        dummy.add(new HomeCourseItem("제주 액티비티 종합 코스", "제주 서귀포시", R.drawable.dummy4,
                Arrays.asList("액티비티", "친구", "장기")));

        dummy.add(new HomeCourseItem("안산 호수공원 산책", "경기 안산시", R.drawable.dummy5,
                Arrays.asList("힐링", "가족", "하루")));

        dummy.add(new HomeCourseItem("포항 바다 감성 드라이브", "경북 포항시", R.drawable.dummy5,
                Arrays.asList("자차", "감성", "주말")));

        dummy.add(new HomeCourseItem("순천 국가정원 자연 힐링", "전남 순천시", R.drawable.dummy1,
                Arrays.asList("자연", "가족", "하루")));

        dummy.add(new HomeCourseItem("서울 종로 문화유산 투어", "서울 종로구", R.drawable.dummy2,
                Arrays.asList("문화/전시", "혼자", "하루")));


        List<HomeCourseItem> filteredList =
                filterCourses(dummy, keyword, selectedTags);

        UploadCourseAdapter adapter = new UploadCourseAdapter(filteredList);
        rv.setAdapter(adapter);


        return view;
    }

    // 리스트 필터링 함수
    private List<HomeCourseItem> filterCourses(
            List<HomeCourseItem> original,
            String keyword,
            List<String> selectedTags
    ) {
        List<HomeCourseItem> result = new ArrayList<>();

        for (HomeCourseItem item : original) {

            boolean matchKeyword = false;
            boolean matchTags = false;

            // keyword 필터
            if (keyword == null || keyword.isEmpty()) {
                matchKeyword = true; // 검색어 없으면 통과
            } else {
                String lower = keyword.toLowerCase();
                if (item.title.toLowerCase().contains(lower) ||
                        item.location.toLowerCase().contains(lower)) {
                    matchKeyword = true;
                }
            }

            // 태그 필터
            if (selectedTags == null || selectedTags.isEmpty()) {
                matchTags = true; // 태그 없으면 통과
            } else {
                // 모든 선택된 태그가 코스 태그 리스트에 포함되어 있어야 true
                matchTags = item.tags.containsAll(selectedTags);
            }

            if (matchKeyword && matchTags) {
                result.add(item);
            }
        }

        return result;
    }


    private void addSelectedTagsToContainer() {

        tagListContainer.removeAllViews();

        for (String tag : selectedTags) {

            TextView tv = new TextView(requireContext(), null, 0, getStyleForTag(tag));

            tv.setText(tag);
            tv.setPadding(20, 10, 20, 10);

            tagListContainer.addView(tv);
        }
    }

    private int getStyleForTag(String tag) {
        if (tag.equals("뚜벅이") || tag.equals("자차")) return R.style.Tag_Movetype;
        if (tag.equals("혼자") || tag.equals("연인")|| tag.equals("친구")|| tag.equals("가족")) return R.style.Tag_Partner;
        if (tag.equals("하루") || tag.equals("1박 2일")|| tag.equals("주말")|| tag.equals("장기")) return R.style.Tag_Period;
        if (tag.equals("힐링") || tag.equals("액티비티")|| tag.equals("맛집탐방")|| tag.equals("감성")|| tag.equals("문화/전시")
                || tag.equals("자연")|| tag.equals("쇼핑")) return R.style.Tag_Theme;
        if (tag.equals("가성비") || tag.equals("프리미엄")|| tag.equals("보통")) return R.style.Tag_Budget;

        return R.style.Tag;   // 기본
    }
}
