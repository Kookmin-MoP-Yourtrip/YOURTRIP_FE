package com.example.yourtrip.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yourtrip.R;

import java.util.ArrayList;
import java.util.List;

public class HomeSearchFragment extends Fragment {

    private EditText etSearch;
    private ImageView btnSearch;

    private final List<TextView> tagViews = new ArrayList<>();
    private final ArrayList<String> selectedTags = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_home_search, container, false);

        initViews(view);
        // 모든 태그 초기 텍스트 색상 지정
        for (TextView tag : tagViews) {
            tag.setTextColor(getResources().getColor(R.color.gray_500));
        }
        setupTagClickListeners();
        setupSearchButton();

        return view;
    }

    private void initViews(View view) {

        etSearch = view.findViewById(R.id.tvSearch);
        btnSearch = view.findViewById(R.id.btnSearch);

        // 🔹 태그 뷰 수집 (XML에 있는 태그 id 등록)
        addTagView(view, R.id.tag_search_walking);
        addTagView(view, R.id.tag_search_car);

        addTagView(view, R.id.tag_search_solo);
        addTagView(view, R.id.tag_search_couple);
        addTagView(view, R.id.tag_search_friends);
        addTagView(view, R.id.tag_search_family);

        addTagView(view, R.id.tag_search_one_day);
        addTagView(view, R.id.tag_search_one_night_two_days);
        addTagView(view, R.id.tag_search_weekend);
        addTagView(view, R.id.tag_search_long_term);

        addTagView(view, R.id.tag_search_healing);
        addTagView(view, R.id.tag_search_activity);
        addTagView(view, R.id.tag_search_food_tour);
        addTagView(view, R.id.tag_search_emotional);
        addTagView(view, R.id.tag_search_culture_exhibition);
        addTagView(view, R.id.tag_search_nature);
        addTagView(view, R.id.tag_search_shopping);

        addTagView(view, R.id.tag_search_budget);
        addTagView(view, R.id.tag_search_normal);
        addTagView(view, R.id.tag_search_premium);


    }

    private void addTagView(View view, int id) {
        TextView tv = view.findViewById(id);
        tagViews.add(tv);
    }


    // 🔹 태그 다중선택 로직
    private void setupTagClickListeners() {
        for (TextView tag : tagViews) {
            tag.setOnClickListener(v -> {

                boolean newState = !tag.isSelected();
                tag.setSelected(newState);

                // UI: 선택되면 white, 아니면 gray
                tag.setTextColor(getResources().getColor(
                        newState ? android.R.color.black : R.color.gray_500
                ));

                // 데이터: 선택 리스트에 추가/제거
                String tagText = tag.getText().toString();

                if (newState) {
                    if (!selectedTags.contains(tagText))
                        selectedTags.add(tagText);
                } else {
                    selectedTags.remove(tagText);
                }
            });
        }
    }

    // 🔹 검색 버튼 클릭 → 검색 결과 페이지로 이동
    private void setupSearchButton() {
        btnSearch.setOnClickListener(v -> {

            String keyword = etSearch.getText().toString().trim();

            // ⭐ 검색 조건(키워드 + 태그)을 전달하기 위해 Bundle 생성
            Bundle bundle = new Bundle();
            bundle.putString("keyword", keyword);
            bundle.putStringArrayList("tags", selectedTags);

            // ⭐ 이동할 Fragment 생성
            HomeSearchResultFragment fragment = new HomeSearchResultFragment();
            fragment.setArguments(bundle);

            // ⭐ Fragment 이동
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
}
