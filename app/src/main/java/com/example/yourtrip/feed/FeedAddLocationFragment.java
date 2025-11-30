package com.example.yourtrip.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yourtrip.R;

public class FeedAddLocationFragment extends Fragment {

    private EditText editLocation;
    private Button btnAddLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_add_location, container, false);

        // XML 연결
        editLocation = view.findViewById(R.id.tv_feed_add_loaction);
        btnAddLocation = view.findViewById(R.id.btn_feed_add_location);

        // “취소하기” 버튼 클릭 → 뒤로가기
        view.findViewById(R.id.btn_feed_cancel_location)
                .setOnClickListener(v -> requireActivity().onBackPressed());

        // “이 장소 추가하기” 버튼 클릭 처리
        btnAddLocation.setOnClickListener(v -> {
            String location = editLocation.getText().toString().trim();

            if (location.isEmpty()) {
                return; // 빈 값이면 무시
            }

            // ★ 입력 장소 FeedUploadFragment로 전달
            Bundle result = new Bundle();
            result.putString("selected_location", location);

            getParentFragmentManager().setFragmentResult("location_request", result);

            // 이전 화면으로 이동
            requireActivity().onBackPressed();
        });

        return view;
    }
}
