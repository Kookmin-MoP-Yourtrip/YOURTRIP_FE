package com.example.yourtrip.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yourtrip.R;

public class HomeSearchResultFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_search_result, container, false);

        EditText etSearch = view.findViewById(R.id.tvSearch);

        // 🔹 전달받은 검색어 세팅
        Bundle args = getArguments();
        if (args != null) {
            String keyword = args.getString("keyword", "");
            etSearch.setText(keyword);   // ⭐ 검색어 유지
        }

        // 🔹 HomeFragment처럼 클릭 전용 설정
        etSearch.setFocusable(false);
        etSearch.setClickable(true);

        etSearch.setOnClickListener(v -> {
            Fragment fragment = new HomeSearchFragment();
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
