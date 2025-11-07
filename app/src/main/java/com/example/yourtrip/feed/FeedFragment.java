package com.example.yourtrip.feed;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
        EditText editDynamic = view.findViewById(R.id.editDynamic);

        btnNext.setEnabled(false); // 기본 비활성

        // 예시: 3초 뒤 활성화
        new android.os.Handler().postDelayed(() -> {
            btnNext.setEnabled(true);
        }, 3000);





        return view;
    }
}
