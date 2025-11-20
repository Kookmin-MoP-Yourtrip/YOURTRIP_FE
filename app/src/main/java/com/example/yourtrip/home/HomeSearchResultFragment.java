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

        setupSearchBarClick(view);

        return view;
    }

    private void setupSearchBarClick(View view) {
        EditText etSearch = view.findViewById(R.id.tvSearch);

        // 🔹 HomeFragment와 동일하게 클릭 전용으로 설정
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
    }
}
