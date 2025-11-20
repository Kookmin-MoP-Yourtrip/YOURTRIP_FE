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

import com.example.yourtrip.R;

import java.util.ArrayList;

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

        return view;
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
