package com.example.yourtrip.mypage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yourtrip.MainActivity;
import com.example.yourtrip.R;
import com.example.yourtrip.mytrip.list.MyTripListFragment;

public class MyCourseViewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_course_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 뒤로가기 버튼 설정
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).switchFragment(new MypageFragment(), false);
        });

        // MyTripListFragment를 child fragment로 표시
        if (savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.my_course_container, new MyTripListFragment())
                    .commit();
        }

        // child fragment의 상단바를 숨기기 위해 약간의 딜레이 후 처리
        view.post(() -> {
            View childTopBar = view.findViewById(R.id.my_course_container).findViewById(R.id.topBar);
            if (childTopBar != null) {
                childTopBar.setVisibility(View.GONE);
            }
        });
    }

}
