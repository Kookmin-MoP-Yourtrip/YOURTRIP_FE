package com.example.yourtrip.mypage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yourtrip.MainActivity;
import com.example.yourtrip.R;
import com.example.yourtrip.feed.FeedFragment;
import com.example.yourtrip.mytrip.MyTripListFragment;

public class MypageFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        // 버튼 참조 가져오기
        LinearLayout btnMyCourse = view.findViewById(R.id.btnMyCourse);
        LinearLayout btnMyFeed = view.findViewById(R.id.btnMyFeed);

        // 코스 보기
        btnMyCourse.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).switchFragment(new MyTripListFragment(), true);
        });

        // 피드 보기
        btnMyFeed.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).switchFragment(new FeedFragment(), true);
        });

        return view;
    }
}