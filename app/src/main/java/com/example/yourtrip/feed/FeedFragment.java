package com.example.yourtrip.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yourtrip.R;

public class FeedFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        Button btnNext = view.findViewById(R.id.btnNext);

        btnNext.setEnabled(false); // 기본 비활성

        // 예시: 3초 뒤 활성화
        new android.os.Handler().postDelayed(() -> {
            btnNext.setEnabled(true);
        }, 3000);

        return view;
    }
}
