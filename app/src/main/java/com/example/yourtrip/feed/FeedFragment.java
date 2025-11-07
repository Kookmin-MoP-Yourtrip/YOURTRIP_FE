package com.example.yourtrip.feed;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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

        // 처음에는 비활성 상태
        btnNext.setEnabled(false);

        // 3초 뒤 활성화 예시
        new Handler().postDelayed(() -> btnNext.setEnabled(true), 3000);

        // 클릭 리스너
        btnNext.setOnClickListener(v ->
                Toast.makeText(getContext(), "다음 버튼 클릭됨!", Toast.LENGTH_SHORT).show()
        );

        return view;
    }
}
